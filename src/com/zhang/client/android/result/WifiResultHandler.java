package com.zhang.client.android.result;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.client.android.R;
import com.zhang.client.android.CaptureActivity;
import com.zhang.client.android.wifi.WifiConfigManager;
import com.zhang.client.result.ParsedResult;
import com.zhang.client.result.WifiParsedResult;

public final class WifiResultHandler extends ResultHandler {

	private static final String TAG = WifiResultHandler.class.getSimpleName();

	private final CaptureActivity parent;

	public WifiResultHandler(CaptureActivity activity, ParsedResult result) {
		super(activity, result);
		parent = activity;
	}

	@Override
	public int getButtonCount() {
		// We just need one button, and that is to configure the wireless. This
		// could change in the future.
		return 1;
	}

	@Override
	public int getButtonText(int index) {
		return R.string.button_wifi;
	}

	@Override
	public void handleButtonPress(int index) {
		if (index == 0) {
			WifiParsedResult wifiResult = (WifiParsedResult) getResult();
			WifiManager wifiManager = (WifiManager) getActivity()
					.getSystemService(Context.WIFI_SERVICE);
			if (wifiManager == null) {
				Log.w(TAG, "No WifiManager available from device");
				return;
			}
			final Activity activity = getActivity();
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(activity.getApplicationContext(),
							R.string.wifi_changing_network, Toast.LENGTH_SHORT)
							.show();
				}
			});
			new WifiConfigManager(wifiManager).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, wifiResult);
			parent.restartPreviewAfterDelay(0L);
		}
	}

	// Display the name of the network and the network type to the user.
	@Override
	public CharSequence getDisplayContents() {
		WifiParsedResult wifiResult = (WifiParsedResult) getResult();
		StringBuilder contents = new StringBuilder(50);
		String wifiLabel = parent.getString(R.string.wifi_ssid_label);
		ParsedResult.maybeAppend(wifiLabel + '\n' + wifiResult.getSsid(),
				contents);
		String typeLabel = parent.getString(R.string.wifi_type_label);
		ParsedResult.maybeAppend(
				typeLabel + '\n' + wifiResult.getNetworkEncryption(), contents);
		return contents.toString();
	}

	@Override
	public int getDisplayTitle() {
		return R.string.result_wifi;
	}
}