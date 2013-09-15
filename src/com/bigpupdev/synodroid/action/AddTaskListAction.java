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

import java.util.List;

import com.bigpupdev.synodroid.server.SynoServer;
import com.bigpupdev.synodroid.protocol.ResponseHandler;
import com.bigpupdev.synodroid.R;

import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.data.Task;

import android.net.Uri;

/**
 * This action upload a file
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class AddTaskListAction implements SynoAction {

	private List<Uri> uriList;
	private Task task;
	private boolean toast = true;
	
	/**
	 * Constructor to upload a file defined by an Uri
	 * 
	 * @param outlines
	 * @param outside_url
	 */
	public AddTaskListAction(List<Uri> outlines) {
		uriList = outlines;
		task = new Task();
		for (Uri uri: uriList){
			String temp = null;
			if (uri.getLastPathSegment() != null ){
				temp = uri.getLastPathSegment();
			}
			else{
				temp = uri.toString();
			}
			
			if (task.fileName == null){
				task.fileName = temp;
			}
			else{
				task.fileName += ", "+ temp;
			}
		}
		task.outside_url = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.ds.action.TaskAction#execute(com.bigpupdev.synodroid.ds .DownloadActivity, com.bigpupdev.synodroid.common.SynoServer)
	 */
	public void execute(ResponseHandler handlerP, SynoServer serverP) throws Exception {
		if(serverP.getDsmVersion().smallerThen(DSMVersion.VERSION3_1)){
			for (Uri uri: uriList){
				serverP.getDSMHandlerFactory().getDSHandler().uploadUrl(uri, null, null);
			}
		}
		else{
			serverP.getDSMHandlerFactory().getDSHandler().uploadUrlList(uriList, null, null);
		}
	}
	
	public void checkToast(SynoServer serverP){
		if (serverP.getDsmVersion().smallerThen(DSMVersion.VERSION3_1)){
			toast = false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.ds.action.TaskAction#getName()
	 */
	public String getName() {
		return "Adding files";
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
	 * @see com.bigpupdev.synodroid.ds.action.TaskAction#requireConfirm()
	 */
	public boolean requireConfirm() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.ds.action.TaskAction#isToastable()
	 */
	public boolean isToastable() {
		return toast;
	}

	public String getUriString(){
		return task.fileName;
	}
}
