package com.bigpupdev.synodroid.adapter;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.utils.SlidingMenuItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SlidingMenuAdapter extends ArrayAdapter<SlidingMenuItem> {
	
	public SlidingMenuAdapter(Context context) {
		super(context, 0);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.sliding_menu_item, null);
		}
		ImageView icon = (ImageView) convertView.findViewById(R.id.ivMenuIcon);
		icon.setImageResource(getItem(position).iconRes);
		TextView title = (TextView) convertView.findViewById(R.id.tvMenuText);
		title.setText(getItem(position).tag);

		return convertView;
	}
}
