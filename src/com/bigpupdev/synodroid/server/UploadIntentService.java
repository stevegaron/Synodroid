package com.bigpupdev.synodroid.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.json.JSONObject;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.protocol.DSMHandlerFactory;
import com.bigpupdev.synodroid.protocol.https.AcceptAllHostNameVerifier;
import com.bigpupdev.synodroid.protocol.https.AcceptAllTrustManager;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class UploadIntentService extends IntentService{
	public static String URL = "URL";
	public static String DEBUG = "DEBUG";
	public static String DSM_VERSION = "DSM_VERSION";
	public static String COOKIES = "COOKIES";
	public static String PATH = "PATH";
	private int UL_ID = 43;
	
	int progress = 0;

	static {
		SSLContext sc;
		try {
			sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new AcceptAllTrustManager() }, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new AcceptAllHostNameVerifier());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/** 
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public UploadIntentService() {
		super("UploadIntentService");
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
		String dsm_version = intent.getStringExtra(DSM_VERSION);
		String cookie = intent.getStringExtra(COOKIES);
		Uri uri = Uri.parse(intent.getStringExtra(URL));
		String path = intent.getStringExtra(PATH);
		boolean dbg = intent.getBooleanExtra(DEBUG, false);
		
		DSMVersion vers = DSMVersion.titleOf(dsm_version);
		if (vers == null) {
			vers = DSMVersion.VERSION2_2;
		}
		DSMHandlerFactory dsm = DSMHandlerFactory.getFactory(vers, null, dbg);
		
		String url = dsm.getDSHandler().getMultipartUri();
		byte[] content = null;
		try {
			content = dsm.getDSHandler().generateMultipart(uri);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (content != null){
			final Notification notification = new Notification(R.drawable.icon_phone, uri.getPath(), System
	                .currentTimeMillis());
	        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
	        notification.contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
	        notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.dl_upload);
	        notification.contentView.setTextViewText(R.id.status_text, uri.getPath());
	        notification.contentView.setProgressBar(R.id.status_progress, 100, progress, false);
	        getApplicationContext();
			final NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(
	                Context.NOTIFICATION_SERVICE);
	
	        notificationManager.notify(UL_ID, notification);
			
			HttpURLConnection conn = null;
			JSONObject respJSO = null;
			int retry = 0;
			int MAX_RETRY = 2;
			try {
				while (retry <= MAX_RETRY) {
					try {
						// Create the connection
						conn = createConnection(url, "", "POST", dbg, cookie, path);
						conn.setRequestProperty("Connection", "keep-alive");
						conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + dsm.getDSHandler().getBoundary());
						conn.setFixedLengthStreamingMode(content.length);
						
						// Write the multipart
						int offset = 0;
						int size = 1024;
						int lenBytes = Math.min(size, content.length);
						long lastUpdate = 0;
						while (content.length > offset){
							lenBytes = Math.min(content.length-offset, size);
							conn.getOutputStream().write(content, offset, lenBytes);
							offset += lenBytes;
							progress = (int) ((float) offset / (float )content.length * 100);
							if (((lastUpdate + 250) < System.currentTimeMillis()) || offset == content.length){
								lastUpdate = System.currentTimeMillis();
				                //this is where you would do something to report the prgress, like this maybe
				                notification.contentView.setProgressBar(R.id.status_progress, 100, progress, false);
				                // inform the progress bar of updates in progress
				                notificationManager.notify(UL_ID, notification);
							}
			                conn.getOutputStream().flush();
							   
						}
						conn.getOutputStream().close();
						
						// Now read the reponse and build a string with it
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						StringBuffer sb = new StringBuffer();
						String line;
						while ((line = br.readLine()) != null) {
							sb.append(line);
						}
						br.close();
			
						if (conn.getResponseCode() == -1) {
							retry++;
							if (dbg) Log.d(Synodroid.DS_TAG, "Response code is -1 (retry: " + retry + ")");
						} else {
							if (dbg) Log.d(Synodroid.DS_TAG, "Response is: " + sb.toString());
							respJSO = new JSONObject(sb.toString());
							boolean success = respJSO.getBoolean("success");
							// If successful then build details list
							if (!success) {
								String reason = "";
								if (respJSO.has("reason")) {
									reason = respJSO.getString("reason");
								} else if (respJSO.has("errno")) {
									JSONObject err = respJSO.getJSONObject("errno");
									reason = err.getString("key");
								}
								//throw new DSMException(reason);
							}
							return;
						}
					} catch (Exception e) {
						if (dbg) Log.e(Synodroid.DS_TAG, "Caught exception while contacting the server, retying...", e);
						retry ++;
					}
				}
			}
			finally {
				if (conn != null) {
					conn.disconnect();
				}
				conn = null;
				notificationManager.cancel(UL_ID);
			}
		}
		//throw new Exception("Failed to read response from server. Please reconnect!");
	}
	
	private HttpURLConnection createConnection(String uriP, String requestP, String methodP, boolean dbg, String cookie, String url) throws MalformedURLException, IOException {
		// Prepare the connection
		HttpURLConnection con = (HttpURLConnection) new URL(url + Uri.encode(uriP, "/")).openConnection();

		// Add cookies if exist
		if (cookie != null) {
			con.addRequestProperty("Cookie", cookie);
			if (dbg) Log.d(Synodroid.DS_TAG, "Added cookie: " + cookie);
		}
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setRequestMethod(methodP);
		con.setConnectTimeout(20000);
		if (dbg) Log.d(Synodroid.DS_TAG, methodP + ": " + uriP + "?" + requestP);
		return con;
	}
}
