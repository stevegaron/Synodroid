package com.bigpupdev.synodroid.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.action.AddTaskAction;
import com.bigpupdev.synodroid.ui.BrowserFragment;
import com.bigpupdev.synodroid.ui.HomeActivity;
import com.bigpupdev.synodroid.utils.ServiceHelper;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class DownloadIntentService extends IntentService{
	public static String URL = "URL";
	public static String COOKIE = "COOKIE";
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
		String cookie = intent.getStringExtra(COOKIE);
		boolean dbg = intent.getBooleanExtra(DEBUG, false);
		
		String fname = "SYNODROID_" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
		Notification notification = ServiceHelper.getNotificationProgress(this, uri, progress, DL_ID, R.drawable.dl_download);
		
		try {
			URL url = new URL(uri); // you can write here any link
			File path = Environment.getExternalStorageDirectory();
			path = new File(path, "Android/data/com.bigpupdev.synodroid/cache/");
			path.mkdirs();
			if (!fname.toLowerCase().endsWith(".torrent") && !fname.toLowerCase().endsWith(".nzb")) {
				fname += ".torrent";
			}
			File file = new File(path, fname);

			long startTime = System.currentTimeMillis();
			try{
				if (dbg) Log.v(Synodroid.DS_TAG, "DownloadIntentService: Downloading " + uri + " to temp folder...");
			}catch (Exception ex){/*DO NOTHING*/}
			try{
				if (dbg) Log.v(Synodroid.DS_TAG, "DownloadIntentService: Temp file destination: " + file.getAbsolutePath());
			}catch (Exception ex){/*DO NOTHING*/}
			/* Open a connection to that URL. */
			HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
			
			if (cookie != null && !cookie.equals("")){
				ucon.setRequestProperty("Cookie", cookie);
			}
			
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
	        		ServiceHelper.updateProgress(this, notification, progress, DL_ID);
	        	}
	        }
	        
	        fos.write(out.toByteArray());
	        fos.flush();
			fos.close();
			
			try{
				if (dbg) Log.v(Synodroid.DS_TAG, "DownloadIntentService: Download completed. Elapsed time: " + ((System.currentTimeMillis() - startTime) / 1000) + " sec(s)");
			}catch (Exception ex){/*DO NOTHING*/}
			uri = Uri.fromFile(file).toString();
		} catch (Exception e) {
			try{
				if (dbg) Log.e(Synodroid.DS_TAG, "DownloadIntentService: Download Error.", e);
			}catch (Exception ex){/*DO NOTHING*/}
		} finally{
			ServiceHelper.cancelNotification(this, DL_ID);
		}

		boolean out_url = false;
		if (!uri.startsWith("file")){
			out_url = true;
		}
		
		AddTaskAction addTask = new AddTaskAction(Uri.parse(uri), out_url, false);
		Synodroid app = (Synodroid) getApplication();
		app.executeAsynchronousAction(app.getServer().getResponseHandler(), addTask, false);
	}
}
