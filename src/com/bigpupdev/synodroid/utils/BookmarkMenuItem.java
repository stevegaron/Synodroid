package com.bigpupdev.synodroid.utils;

import android.graphics.Bitmap;

public class BookmarkMenuItem {
	public String title;
	public String url;
	public Bitmap favicon;
	public BookmarkMenuItem(String title, String url, Bitmap favicon) {
		this.title = title; 
		this.url = url;
		this.favicon = favicon;
	}
}
