package com.zhang.client.android.result;

import com.google.zxing.client.android.R;
import com.zhang.client.Result;
import com.zhang.client.result.ParsedResult;

import android.app.Activity;

public final class TextResultHandler extends ResultHandler {

	private static final int[] buttons = { R.string.button_web_search,
			R.string.button_share_by_email, R.string.button_share_by_sms,
			R.string.button_custom_product_search, };

	public TextResultHandler(Activity activity, ParsedResult result,
			Result rawResult) {
		super(activity, result, rawResult);
	}

	@Override
	public int getButtonCount() {
		return hasCustomProductSearch() ? buttons.length : buttons.length - 1;
	}

	@Override
	public int getButtonText(int index) {
		return buttons[index];
	}

	@Override
	public void handleButtonPress(int index) {
		String text = getResult().getDisplayResult();
		switch (index) {
		case 0:
			webSearch(text);
			break;
		case 1:
			shareByEmail(text);
			break;
		case 2:
			shareBySMS(text);
			break;
		case 3:
			openURL(fillInCustomSearchURL(text));
			break;
		}
	}

	@Override
	public int getDisplayTitle() {
		return R.string.result_text;
	}
}
