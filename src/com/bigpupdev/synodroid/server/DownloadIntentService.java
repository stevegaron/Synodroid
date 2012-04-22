package com.bigpupdev.synodroid.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.ui.HomeActivity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

public class DownloadIntentService extends IntentService{
	public static String URL = "URL";
	public static String DEBUG = "DEBUG";
	private int DL_ID = 42;
	
	int progress = 0;

	/** 
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public DownloadIntentService() {
		super("DownloadIntentService");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent,flags,startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns, IntentService
	 * stops the service, as appropriate.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		String uri = intent.getStringExtra(URL);
		boolean dbg = intent.getBooleanExtra(DEBUG, false);
		
		String temp[] = uri.split("/");
		String fname = temp[(temp.length) - 1];
		final Notification notification = new Notification(R.drawable.icon_phone, fname, System
                .currentTimeMillis());
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notification.contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        notification.contentView.setTextViewText(R.id.status_text, fname);
        notification.contentView.setProgressBar(R.id.status_progress, 100, progress, false);
        getApplicationContext();
		final NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(
                Context.NOTIFICATION_SERVICE);

        notificationManager.notify(DL_ID, notification);
        
		try {
			URL url = new URL(uri); // you can write here any link
			File path = Environment.getExternalStorageDirectory();
			path = new File(path, "Android/data/com.bigpupdev.synodroid/");
			path.mkdirs();
			if (!fname.toLowerCase().endsWith(".torrent") && !fname.toLowerCase().endsWith(".nzb")) {
				fname += ".torrent";
			}
			File file = new File(path, fname);

			long startTime = System.currentTimeMillis();
			try{
				if (dbg) Log.d(Synodroid.DS_TAG, "Downloading " + uri + " to temp folder...");
			}catch (Exception ex){/*DO NOTHING*/}
			try{
				if (dbg) Log.d(Synodroid.DS_TAG, "Temp file destination: " + file.getAbsolutePath());
			}catch (Exception ex){/*DO NOTHING*/}
			/* Open a connection to that URL. */
			HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			
			while (ucon.getResponseCode() == 302){
				ucon = (HttpURLConnection) ucon.getURL().openConnection();
				is = ucon.getInputStream();
			}
			int contentLength = ucon.getContentLength();
			
			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(file);
			
			BufferedInputStream bis = new BufferedInputStream(is);
			
	        byte[] buf = new byte[1024];
	        int count = 0;
	        int downloadedSize = 0;
	        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
	        long lastUpdate = 0;
			while ((count = bis.read(buf)) != -1){
	        	out.write(buf, 0, count);
	        	downloadedSize += count;
	        	progress = (int) (((float) downloadedSize/ ((float )contentLength)) * 100);
	        	if (((lastUpdate + 250) < System.currentTimeMillis()) || downloadedSize == contentLength){
					//this is where you would do something to report the prgress, like this maybe
	                notification.contentView.setProgressBar(R.id.status_progress, 100, progress, false);
	                // inform the progress bar of updates in progress
	                notificationManager.notify(DL_ID, notification);
	        	}
	        }
	            
	        
			
	        fos.write(out.toByteArray());
	        fos.flush();
			fos.close();
			
			try{
				if (dbg) Log.d(Synodroid.DS_TAG, "Download completed. Elapsed time: " + ((System.currentTimeMillis() - startTime) / 1000) + " sec(s)");
			}catch (Exception ex){/*DO NOTHING*/}
			uri = Uri.fromFile(file).toString();
		} catch (Exception e) {
			try{
				if (dbg) Log.d(Synodroid.DS_TAG, "Download Error: " + e);
			}catch (Exception ex){/*DO NOTHING*/}
			try{
				if (dbg) Log.d(Synodroid.DS_TAG, "Letting the NAS do the heavy lifting...");
			}catch (Exception ex){/*DO NOTHING*/}
		} finally{
			notificationManager.cancel(DL_ID);
		}

		Intent broadcastIntent = new Intent();
		if (uri.startsWith("file")){
			broadcastIntent.setAction(Intent.ACTION_VIEW);
			broadcastIntent.setData(Uri.parse(uri));
		}
		else{
			broadcastIntent.setAction(Intent.ACTION_SEND);
			broadcastIntent.putExtra(Intent.EXTRA_TEXT, uri);
		}
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		broadcastIntent.setClass(this, HomeActivity.class);
		getApplication().startActivity(broadcastIntent);
	}
}
