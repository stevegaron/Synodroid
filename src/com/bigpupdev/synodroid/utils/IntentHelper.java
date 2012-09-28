package com.bigpupdev.synodroid.utils;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;

public class IntentHelper {

	@TargetApi(16)
	public static Uri getClipDataUri(Intent intentP) {
		Uri uri = null;
		ClipData cd = intentP.getClipData();
		if (cd != null) {
			for (int i =0; i< cd.getItemCount(); i++){
				uri = cd.getItemAt(i).getUri();
			}
	    }
		
		return uri;
	}
}
