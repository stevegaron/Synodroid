/**
 * Copyright 2010 Eric Taix Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.bigpupdev.synodroid.action;

import com.bigpupdev.synodroid.server.SynoServer;
import com.bigpupdev.synodroid.protocol.ResponseHandler;
import com.bigpupdev.synodroid.ui.DownloadFragment;
import com.bigpupdev.synodroid.R;

import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.data.TaskDetail;

/**
 * This action requests the server for information details about a task
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class DetailTaskAction implements SynoAction {

	// The task to resume
	private Task task;

	public DetailTaskAction(Task taskP) {
		task = taskP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#execute(com.bigpupdev.synodroid. ds.DownloadActivity, com.bigpupdev.synodroid.common.SynoServer)
	 */
	public void execute(ResponseHandler handlerP, SynoServer serverP) throws Exception {
		TaskDetail details = serverP.getDSMHandlerFactory().getDSHandler().getDetails(task);
		serverP.fireMessage(handlerP, DownloadFragment.MSG_DETAILS_RETRIEVED, details);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getName()
	 */
	public String getName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getToastId()
	 */
	public int getToastId() {
		return R.string.action_detailing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#isToastable()
	 */
	public boolean isToastable() {
		return false;
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
	 * @see com.bigpupdev.synodroid.ds.action.TaskAction#getTask()
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * @param task
	 *            the task to set
	 */
	public void setTask(Task taskP) {
		task = taskP;
	}

}
