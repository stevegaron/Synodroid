package com.bigpupdev.synodroid.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.ui.HomeActivity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class ServiceHelper {
	private static int ERROR_ID = 13;
	private static int INFO_ID = 14;
	
	public static Notification getNotificationProgress(IntentService self, String text, int curProgress, int ID, int icon){
		final Notification notification = new Notification(R.drawable.status_icon, text, System
                .currentTimeMillis());
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notification.contentView = new RemoteViews(self.getApplicationContext().getPackageName(), R.layout.notification);
        notification.contentView.setImageViewResource(R.id.status_icon, icon);
        notification.contentView.setTextViewText(R.id.status_text, text);
        notification.contentView.setProgressBar(R.id.status_progress, 100, curProgress, false);
        Intent pending = new Intent(self, HomeActivity.class);
        pending.putExtra("com.bigpupdev.synodroid.notifyId", ID);
        notification.contentIntent = PendingIntent.getActivity(self, 1, pending, 0);
        final NotificationManager notificationManager = (NotificationManager) self.getApplicationContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
        
        notificationManager.notify(ID, notification);
		
        return notification;
	}
	
	public static void updateProgress(IntentService self, Notification notification, int curProgress, int ID){
		//this is where you would do something to report the progress, like this maybe
        notification.contentView.setProgressBar(R.id.status_progress, 100, curProgress, false);
        // inform the progress bar of updates in progress
        final NotificationManager notificationManager = (NotificationManager) self.getApplicationContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
        
        notificationManager.notify(ID, notification);
	}
	
	public static void cancelNotification(IntentService self, int ID){
		final NotificationManager notificationManager = (NotificationManager) self.getApplicationContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
        
        notificationManager.cancel(ID);
	}
	
	public static void showNotificationError(IntentService self, String action, String text, int icon){
		final Notification notification = new Notification(R.drawable.status_icon, text, System
                .currentTimeMillis());
		notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
        notification.contentView = new RemoteViews(self.getApplicationContext().getPackageName(), R.layout.notification_error);
        notification.contentView.setImageViewResource(R.id.status_icon, icon);
        notification.contentView.setTextViewText(R.id.status_text, action);
        notification.contentView.setTextViewText(R.id.status_cancel, text);
        Intent pending = new Intent(self, HomeActivity.class);
        pending.putExtra("com.bigpupdev.synodroid.notifyId", ERROR_ID);
        notification.contentIntent = PendingIntent.getActivity(self, 1, pending, 0);
        final NotificationManager notificationManager = (NotificationManager) self.getApplicationContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
        
        notificationManager.notify(ERROR_ID, notification);
	}
	
	public static void showNotificationInfo(IntentService self, String action, String text, int icon){
		final Notification notification = new Notification(R.drawable.status_icon, text, System
                .currentTimeMillis());
		notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
        notification.contentView = new RemoteViews(self.getApplicationContext().getPackageName(), R.layout.notification_error);
        notification.contentView.setImageViewResource(R.id.status_icon, icon);
        notification.contentView.setTextViewText(R.id.status_text, action);
        notification.contentView.setTextViewText(R.id.status_cancel, text);
        Intent pending = new Intent(self, HomeActivity.class);
        pending.putExtra("com.bigpupdev.synodroid.notifyId", ERROR_ID);
        notification.contentIntent = PendingIntent.getActivity(self, 1, pending, 0);
        final NotificationManager notificationManager = (NotificationManager) self.getApplicationContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
        
        notificationManager.notify(INFO_ID, notification);
	}
	
	public static HttpURLConnection createConnection(String uriP, String requestP, String methodP, boolean dbg, String cookie, String url) throws MalformedURLException, IOException {
		// Prepare the connection
		HttpURLConnection con = (HttpURLConnection) new URL(url + Uri.encode(uriP, "/")).openConnection();

		// Add cookies if exist
		if (cookie != null) {
			con.addRequestProperty("Cookie", cookie);
			if (dbg) Log.v(Synodroid.DS_TAG, "Added cookie to request: " + cookie);
		}
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setRequestMethod(methodP);
		con.setConnectTimeout(20000);
		if (dbg) Log.i(Synodroid.DS_TAG, methodP + ": " + uriP + "?" + requestP);
		return con;
	}
}
