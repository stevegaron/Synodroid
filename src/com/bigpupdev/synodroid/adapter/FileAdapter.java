package com.bigpupdev.synodroid.adapter;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.utils.FileItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdapter extends ArrayAdapter<FileItem> {
	
	public FileAdapter(Context context) {
		super(context, 0);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.file_item, null);
		}
		FileItem curItem = getItem(position);
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.ivFileIcon);
		if (curItem.iconRes != -1){
			icon.setImageResource(curItem.iconRes);
			icon.setVisibility(View.VISIBLE);
		}
		else{
			icon.setVisibility(View.GONE);
		}
		TextView title = (TextView) convertView.findViewById(R.id.tvFileText);
		title.setText(curItem.tag);

		return convertView;
	}
}
