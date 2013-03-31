/**
 * Copyright 2010 Eric Taix Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.bigpupdev.synodroid;

import java.util.ArrayList;
import java.util.List;

import com.bigpupdev.synodroid.server.SynoServer;
import com.bigpupdev.synodroid.server.SynoServerConnection;
import com.bigpupdev.synodroid.protocol.ResponseHandler;
import com.bigpupdev.synodroid.ui.DetailFiles;
import com.bigpupdev.synodroid.ui.DetailMain;
import com.bigpupdev.synodroid.ui.DownloadFragment;
import com.bigpupdev.synodroid.ui.SearchFragment;
import com.bigpupdev.synodroid.utils.Utils;
import com.bigpupdev.synodroid.R;

import com.bigpupdev.synodroid.action.DeleteMultipleTaskAction;
import com.bigpupdev.synodroid.action.DeleteTaskAction;
import com.bigpupdev.synodroid.action.GetAllAndOneDetailTaskAction;
import com.bigpupdev.synodroid.action.SynoAction;
import com.bigpupdev.synodroid.data.TaskStatus;

import de.keyboardsurfer.android.widget.crouton.Style;
import de.keyboardsurfer.android.widget.crouton.Style.Builder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;

/**
 * The application (single instance) which implements utility methods to access to the current server
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class Synodroid extends Application {

	private static final String PREFERENCE_DEBUG = "debug_cat";
	private static final String PREFERENCE_DEBUG_LOG = "debug_cat.debug_logging";
	
	public static final String DS_TAG = "Synodroid";
	public boolean DEBUG;
	// The current server
	private SynoServer currentServer = null;
	private String curBrowserUrl = null;

	public static final Style CROUTON_ERROR;
	public static final Style CROUTON_ALERT;
    public static final Style CROUTON_CONFIRM;
    public static final Style CROUTON_INFO;

    public static final int holoRedLight = 0xddff4444;
    public static final int holoGreenLight = 0xdd99cc00;
  	public static final int holoBlueLight = 0xdd33b5e5;

	static {
		CROUTON_ERROR = new Builder().setDuration(-1).setBackgroundColorValue(holoRedLight).setHeight(LayoutParams.WRAP_CONTENT).build();
		CROUTON_ALERT = new Builder().setDuration(1300).setBackgroundColorValue(holoRedLight).setHeight(LayoutParams.WRAP_CONTENT).build();
		CROUTON_CONFIRM = new Builder().setDuration(1300).setBackgroundColorValue(holoGreenLight).setHeight(LayoutParams.WRAP_CONTENT).build();
	    CROUTON_INFO = new Builder().setDuration(1300).setBackgroundColorValue(holoBlueLight).setHeight(LayoutParams.WRAP_CONTENT).build();
	  }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences preferences = getSharedPreferences(PREFERENCE_DEBUG, Activity.MODE_PRIVATE);
		DEBUG = preferences.getBoolean(PREFERENCE_DEBUG_LOG, false);	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public void setBrowserUrl(String url){
		curBrowserUrl = url;
	}
	
	public String getBrowserUrl(){
		return curBrowserUrl;
	}
	
	public void enableDebugLog(){
		DEBUG = true;
		if (currentServer != null) currentServer.setDebugLvl(DEBUG);
	}
	
	public void disableDebugLog(){
		DEBUG = false;
		if (currentServer != null) currentServer.setDebugLvl(DEBUG);
	}

	public boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	/**
	 * Set the current server. An attempt to connect to the server is only done if this is different server
	 * 
	 * @param activityP
	 * @param serverP
	 */
	
	public synchronized void connectServer(DownloadFragment activityP, SynoServer serverP, List<SynoAction> actionQueueP, boolean automated) {
		connectServer(activityP, serverP, actionQueueP, automated, null);
	}

	public boolean shouldUsePublicConnection(){
		boolean pub = true;
		// Determine the current network access
		WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		boolean wifiOn = wifiMgr.isWifiEnabled();
		if (DEBUG){
			if (wifiOn){
				Log.v(Synodroid.DS_TAG, "Synodroid: Wifi is: ENABLED.");	
			}
			else{
				Log.v(Synodroid.DS_TAG, "Synodroid: Wifi is: DISABLED.");
			}
		}
		
		final WifiInfo currentWifi = wifiMgr.getConnectionInfo();
		final boolean wifiConnected = (wifiOn && currentWifi.getNetworkId() != -1);
		
		if (DEBUG){
			if (wifiConnected){
				Log.v(Synodroid.DS_TAG, "Synodroid: Wifi is: CONNECTED.");
			}
			else{
				Log.v(Synodroid.DS_TAG, "Synodroid: Wifi is: DISCONNECTED.");
			}
		}
		// If we are connected to a WIFI network, verify if SSID match
		String cur_ssid = Utils.validateSSID(currentWifi.getSSID());
		if (wifiConnected && cur_ssid != null) {
			if (DEBUG) Log.v(Synodroid.DS_TAG, "Synodroid: Wifi current SSID is: '" + cur_ssid+"'");
			SynoServerConnection sc = currentServer.getLocalConnection();
			if (sc != null) {
				List<String> ssids = sc.wifiSSID;
				if (ssids != null) {
					if (DEBUG) Log.v(Synodroid.DS_TAG, "Synodroid: Local connection has an SSID list! Checking to find the right SSID...");
					for (String ssid : ssids) {
						ssid = Utils.validateSSID(ssid);
						if (DEBUG) Log.v(Synodroid.DS_TAG, "Synodroid: Comparing '"+ssid+"' with '" + cur_ssid+"' ...");
						if (cur_ssid.equals(ssid)) {
							pub = false;
							if (DEBUG) Log.v(Synodroid.DS_TAG, "Synodroid: SSIDs are equal! Connecting using local connection...");
							break;
						}
					}
				}
			}
		}
		return pub;
	}
	
	public synchronized void connectServer(DownloadFragment activityP, SynoServer serverP, List<SynoAction> actionQueueP, boolean automated, String otp) {
		// if (currentServer == null || !currentServer.isAlive() || !currentServer.equals(serverP)) {
		// First disconnect the old server
		if (currentServer != null && !automated) {
			currentServer.disconnect();
		}
		// Set the recurrent action
		GetAllAndOneDetailTaskAction recurrentAction = new GetAllAndOneDetailTaskAction(serverP.getSortAttribute(), serverP.isAscending(), activityP.getTaskAdapter());
		serverP.setRecurrentAction(activityP, recurrentAction);
		// Then connect the new one
		currentServer = serverP;
		currentServer.connect(activityP, actionQueueP, shouldUsePublicConnection(), otp);
		// }
	}

	/**
	 * Get the current server
	 * 
	 * @return currentServer
	 */
	public SynoServer getServer() {
		return currentServer;
	}

	/**
	 * Bind an activity to the current server
	 * 
	 * @param handlerP
	 */
	public boolean bindResponseHandler(ResponseHandler handlerP) {
		if (currentServer == null) {
			return false;
		} else {
			currentServer.bindResponseHandler(handlerP);
			return true;
		}
	}

	/**
	 * Change the recurrent action
	 * 
	 * @param actionP
	 */
	public void setRecurrentAction(ResponseHandler handlerP, SynoAction actionP) {
		if (currentServer != null) {
			currentServer.setRecurrentAction(handlerP, actionP);
		}
	}

	/**
	 * Force a refresh
	 */
	public void forceRefresh() {
		if (currentServer != null) {
			currentServer.forceRefresh();
		}
	}

	/**
	 * Pause the current server if exist
	 */
	public void pauseServer() {
		if (currentServer != null) {
			currentServer.pause();
		}
	}

	/**
	 * Resume the current server if exist
	 */
	public void resumeServer() {
		if (currentServer != null) {
			currentServer.resume();
		}
	}

	/**
	 * Execute an action and connect to the server or display the connection dialog if needed
	 * 
	 * @param activityP
	 * @param actionP
	 * @param forceRefreshP
	 */
	public void executeAction(final DetailMain fragmentP, final SynoAction actionP, final boolean forceRefreshP) {
		if (currentServer != null) {
			// First verify if it is a DeleteTaskAction and if the task is not finished
			TaskStatus status = null;
			if (actionP.getTask() != null && actionP.getTask().status != null) {
				status = actionP.getTask().getStatus();
			}
			if ((actionP instanceof DeleteTaskAction) && (status != TaskStatus.TASK_FINISHED)) {
				Activity a = fragmentP.getActivity();
				Dialog d = new AlertDialog.Builder(a).setTitle(actionP.getTask().fileName).setMessage(R.string.dialog_message_confirm).setNegativeButton(android.R.string.no, null).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						currentServer.setRecurrentAction(fragmentP, null);
						currentServer.executeAsynchronousAction(fragmentP, actionP, forceRefreshP);
						fragmentP.finish();
					}
				}).create();
				// d.setOwnerActivity(this); // why can't the builder do this?
				d.show();
			}
			else if (actionP instanceof DeleteTaskAction) {
				currentServer.setRecurrentAction(fragmentP, null);
				currentServer.executeAsynchronousAction(fragmentP, actionP, forceRefreshP);
				fragmentP.finish();
			}
			// Ok no problem do it
			else {
				currentServer.executeAsynchronousAction(fragmentP, actionP, forceRefreshP);
			}
		}
		// If an action have to be executed but with no current connection
		else {
			ArrayList<SynoAction> actionQueue = new ArrayList<SynoAction>();
			actionQueue.add(actionP);
		}
	}

	
	/**
	 * Execute an action and connect to the server or display the connection dialog if needed
	 * 
	 * @param activityP
	 * @param actionP
	 * @param forceRefreshP
	 */
	public void executeAction(final SearchFragment fragmentP, final SynoAction actionP, final boolean forceRefreshP) {
		if (currentServer != null) {
			// First verify if it is a DeleteTaskAction and if the task is not finished
			currentServer.executeAsynchronousAction(fragmentP, actionP, forceRefreshP);
		}
		// If an action have to be executed but with no current connection
		else {
			ArrayList<SynoAction> actionQueue = new ArrayList<SynoAction>();
			actionQueue.add(actionP);
		}
	}
	
	/**
	 * Execute an action and connect to the server or display the connection dialog if needed
	 * 
	 * @param activityP
	 * @param actionP
	 * @param forceRefreshP
	 */
	public void executeAction(final DetailFiles fragmentP, final SynoAction actionP, final boolean forceRefreshP) {
		if (currentServer != null) {
			// First verify if it is a DeleteTaskAction and if the task is not finished
			currentServer.executeAsynchronousAction(fragmentP, actionP, forceRefreshP);
		}
		// If an action have to be executed but with no current connection
		else {
			ArrayList<SynoAction> actionQueue = new ArrayList<SynoAction>();
			actionQueue.add(actionP);
		}
	}
	
	/**
	 * Execute an action and connect to the server or display the connection dialog if needed
	 * 
	 * @param activityP
	 * @param actionP
	 * @param forceRefreshP
	 */
	public void executeAction(final DownloadFragment fragmentP, final SynoAction actionP, final boolean forceRefreshP) {
		if (currentServer != null && currentServer.isConnected()) {
			// First verify if it is a DeleteTaskAction and if the task is not finished
			TaskStatus status = null;
			if (actionP.getTask() != null && actionP.getTask().status != null) {
				status = actionP.getTask().getStatus();
			}
			if ((actionP instanceof DeleteTaskAction) && (status != TaskStatus.TASK_FINISHED)) {
				Activity a = fragmentP.getActivity();
				Dialog d = new AlertDialog.Builder(a).setTitle(actionP.getTask().fileName).setMessage(R.string.dialog_message_confirm).setNegativeButton(android.R.string.no, null).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						currentServer.executeAsynchronousAction(fragmentP, actionP, forceRefreshP);
					}
				}).create();
				// d.setOwnerActivity(this); // why can't the builder do this?
				d.show();
			}
			else if (actionP instanceof DeleteMultipleTaskAction) {
				Activity a = fragmentP.getActivity();
				Dialog d = new AlertDialog.Builder(a).setTitle(R.string.delete_multiple).setMessage(R.string.dialog_multiple_confirm).setNegativeButton(android.R.string.no, null).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						currentServer.executeAsynchronousAction(fragmentP, actionP, forceRefreshP);
					}
				}).create();
				// d.setOwnerActivity(this); // why can't the builder do this?
				d.show();
			} 
			// Ok no problem do it
			else {
				currentServer.executeAsynchronousAction(fragmentP, actionP, forceRefreshP);
			}
		}
		// If an action have to be executed but with no current connection
		else {
			ArrayList<SynoAction> actionQueue = new ArrayList<SynoAction>();
			actionQueue.add(actionP);
			fragmentP.alreadyCanceled = false;
			fragmentP.showDialogToConnect(true, actionQueue, true);
		}
	}

	/**
	 * Change the sort
	 * 
	 * @param sorAttrP
	 * @param ascendingP
	 */
	public void setServerSort(String sorAttrP, boolean ascendingP) {
		if (currentServer != null) {
			currentServer.setSortAttribute(sorAttrP);
			currentServer.setAscending(ascendingP);
			currentServer.forceRefresh();
		}
	}

	public String getServerSort(){
		return currentServer.getSortAttribute();
	}
	/**
	 * Execute an asynchronous action if the server is currently connected
	 * 
	 * @param handlerP
	 * @param actionP
	 * @param forceRefreshP
	 * @param showToastP
	 */

	public void executeAsynchronousAction(ResponseHandler handlerP, SynoAction actionP, final boolean forceRefreshP, final boolean showToastP) {
		if (currentServer != null) {
			currentServer.executeAsynchronousAction(handlerP, actionP, forceRefreshP, showToastP);
		}
	}

	/**
	 * Execute an asynchronous action if the server is currently connected
	 * 
	 * @param handlerP
	 * @param actionP
	 * @param forceRefreshP
	 */
	public void executeAsynchronousAction(ResponseHandler handlerP, SynoAction actionP, final boolean forceRefreshP) {
		if (currentServer != null) {
			currentServer.executeAsynchronousAction(handlerP, actionP, forceRefreshP);
		}
		else{
			fireMessage(handlerP, ResponseHandler.MSG_ALERT, ((Fragment) handlerP).getText(R.string.empty_not_connected));
		}
	}

	/**
	 * Send a message
	 */
	public void fireMessage(ResponseHandler handlerP, int msgP) {
		fireMessage(handlerP, msgP, null);
	}

	/**
	 * Send a message
	 */
	public void fireMessage(ResponseHandler handlerP, int msgP, Object objP) {
		// Send the connecting message
		Message msg = new Message();
		msg.what = msgP;
		msg.obj = objP;
		handlerP.handleReponse(msg);

	}
}
