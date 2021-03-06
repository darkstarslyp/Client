
package com.zhang.book;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;

import com.zhang.client.android.LocaleManager;

import java.util.List;

final class BrowseBookListener implements AdapterView.OnItemClickListener {

  private final SearchBookContentsActivity activity;
  private final List<SearchBookContentsResult> items;

  BrowseBookListener(SearchBookContentsActivity activity, List<SearchBookContentsResult> items) {
    this.activity = activity;
    this.items = items;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    if (position < 1) {
      // Clicked header, ignore it
      return;
    }
    int itemOffset = position - 1;
    if (itemOffset >= items.size()) {
      return;
    }
    String pageId = items.get(itemOffset).getPageId();
    String query = SearchBookContentsResult.getQuery();
    if (LocaleManager.isBookSearchUrl(activity.getISBN()) && !pageId.isEmpty()) {
      String uri = activity.getISBN();
      int equals = uri.indexOf('=');
      String volumeId = uri.substring(equals + 1);
      String readBookURI = "http://books.google." +
          LocaleManager.getBookSearchCountryTLD(activity) +
          "/books?id=" + volumeId + "&pg=" + pageId + "&vq=" + query;
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(readBookURI));
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);                    
      activity.startActivity(intent);
    }
  }
}
