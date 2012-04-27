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

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.bigpupdev.synodroid.server.DownloadOriginalIntentService;
import com.bigpupdev.synodroid.server.SynoServer;
import com.bigpupdev.synodroid.protocol.ResponseHandler;
import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;

import com.bigpupdev.synodroid.data.Task;

/**
 * Download the original file
 * 
 * @author Eric Taix (eric.taix at gmail dot com)
 */
public class DownloadOriginalLinkAction implements SynoAction {

	// The original file's link
	private Task task;

	public DownloadOriginalLinkAction(Task taskP) {
		task = taskP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.bigpupdev.synodroid.common.SynoAction#execute(com.bigpupdev.synodroid.ds. TorrentListActivity, com.bigpupdev.synodroid.common.SynoServer)
	 */
	public void execute(ResponseHandler handlerP, SynoServer serverP) throws Exception {
		/*StringBuffer data = serverP.getDSMHandlerFactory().getDSHandler().getOriginalFile(task);
		OriginalFile ori = new OriginalFile();
		String[] temp = task.originalLink.split("/");
		ori.fileName = temp[(temp.length) - 1];
		ori.rawData = data;
		serverP.fireMessage(handlerP, ResponseHandler.MSG_ORIGINAL_FILE_RETRIEVED, ori);
		*/
		Activity a = ((Fragment)handlerP).getActivity();
		Intent msgIntent = new Intent(a, DownloadOriginalIntentService.class);
		msgIntent.putExtra(DownloadOriginalIntentService.TASKID, task.taskId);
		msgIntent.putExtra(DownloadOriginalIntentService.ORIGINAL_LINK, task.originalLink);
		msgIntent.putExtra(DownloadOriginalIntentService.COOKIES, serverP.getCookies());
		msgIntent.putExtra(DownloadOriginalIntentService.DSM_VERSION, serverP.getDsmVersion().getTitle());
		msgIntent.putExtra(DownloadOriginalIntentService.PATH, serverP.getUrl());
		msgIntent.putExtra(DownloadOriginalIntentService.DEBUG, ((Synodroid)a.getApplication()).DEBUG);
		a.startService(msgIntent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getName()
	 */
	public String getName() {
		return "Download original file for task: " + task.taskId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getToastId()
	 */
	public int getToastId() {
		return R.string.action_download_original_file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#isToastable()
	 */
	public boolean isToastable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.ds.action.TaskAction#getTask()
	 */
	public Task getTask() {
		return null;
	}

}
