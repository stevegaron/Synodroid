/**
 * Copyright 2010 Eric Taix
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 */
package com.bigpupdev.synodroid.action;

import com.bigpupdev.synodroid.server.DownloadIntentService;
import com.bigpupdev.synodroid.server.SynoServer;
import com.bigpupdev.synodroid.server.UploadIntentService;
import com.bigpupdev.synodroid.protocol.ResponseHandler;
import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;

import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.data.Task;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * This action upload a file
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class AddTaskAction implements SynoAction {

	private Uri uri;
	private Task task;
	private boolean use_safe;
	private boolean toast = true;
	
	/**
	 * Constructor to upload a file defined by an Uri
	 * 
	 * @param uriP
	 * @param outside_url
	 */
	public AddTaskAction(Uri uriP, boolean outside_url, boolean use_safeP) {
		uri = uriP;
		task = new Task();
		task.fileName = uri.getLastPathSegment();
		if (task.fileName == null){
			task.fileName = uri.toString();
		}
		task.outside_url = outside_url;
		use_safe = use_safeP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.ds.action.TaskAction#execute(com.bigpupdev.synodroid.ds .DownloadActivity, com.bigpupdev.synodroid.common.SynoServer)
	 */
	public void execute(ResponseHandler handlerP, SynoServer serverP) throws Exception {
		if (use_safe && serverP.getDsmVersion().smallerThen(DSMVersion.VERSION3_1)){
			//new TorrentDownloadAndAdd((Fragment)handlerP).execute(uri.toString());
			Activity a = ((Fragment)handlerP).getActivity();
			Intent msgIntent = new Intent(a, DownloadIntentService.class);
			msgIntent.putExtra(DownloadIntentService.URL, uri.toString());
			msgIntent.putExtra(DownloadIntentService.DEBUG, ((Synodroid)a.getApplication()).DEBUG);
			a.startService(msgIntent);
		}
		else{
			if (task.outside_url) {
				// Start task using url instead of reading file
				serverP.getDSMHandlerFactory().getDSHandler().uploadUrl(uri);
			} else {
				//serverP.getDSMHandlerFactory().getDSHandler().upload((Fragment) handlerP, uri);
				Activity a = ((Fragment)handlerP).getActivity();
				Intent msgIntent = new Intent(a, UploadIntentService.class);
				msgIntent.putExtra(UploadIntentService.URL, uri.toString());
				if (serverP.getDsmVersion().smallerThen(DSMVersion.VERSION3_1)){
					msgIntent.putExtra(UploadIntentService.DIRECTORY, "");
				}
				else{
					msgIntent.putExtra(UploadIntentService.DIRECTORY, serverP.getDSMHandlerFactory().getDSHandler().getSharedDirectory());
				}
				msgIntent.putExtra(UploadIntentService.COOKIES, serverP.getCookies());
				msgIntent.putExtra(UploadIntentService.DSM_VERSION, serverP.getDsmVersion().getTitle());
				msgIntent.putExtra(UploadIntentService.PATH, serverP.getUrl());
				msgIntent.putExtra(UploadIntentService.DEBUG, ((Synodroid)a.getApplication()).DEBUG);
				a.startService(msgIntent);
			}
		}
	}
	
	public void checkToast(SynoServer serverP){
		if (use_safe && serverP.getDsmVersion().smallerThen(DSMVersion.VERSION3_1)){
			toast = false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.ds.action.TaskAction#getName()
	 */
	public String getName() {
		return "Adding file " + uri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.ds.action.TaskAction#getTask()
	 */
	public Task getTask() {
		return task;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.ds.action.TaskAction#getToastId()
	 */
	public int getToastId() {
		return R.string.action_adding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.ds.action.TaskAction#isToastable()
	 */
	public boolean isToastable() {
		return toast;
	}

}
