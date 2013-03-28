package com.bigpupdev.synodroid.adapter;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.utils.BookmarkMenuItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookmarkMenuAdapter extends ArrayAdapter<BookmarkMenuItem> {
	
	public BookmarkMenuAdapter(Context context) {
		super(context, 0);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.bookmark_item, null);
		}
		BookmarkMenuItem cur_item = getItem(position);
		
		if (cur_item.favicon != null){
			ImageView icon = (ImageView) convertView.findViewById(R.id.ivBookmarkIcon);
			icon.setImageBitmap(cur_item.favicon);
		}
		
		TextView title = (TextView) convertView.findViewById(R.id.tvBookmarkText);
		title.setText(cur_item.title);
		
		TextView url = (TextView) convertView.findViewById(R.id.tvBookmarkUrl);
		url.setText(cur_item.url);

		return convertView;
	}
}
