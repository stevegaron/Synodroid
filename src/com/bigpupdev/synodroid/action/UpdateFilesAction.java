/**
 * Copyright 2010 Steve Garon
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

import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.data.TaskFile;

/**
 * Retrieve task's files
 * 
 * @author Steve Garon (steve.garon at gmail dot com)
 */
public class UpdateFilesAction implements SynoAction {

	// The torrent to resume
	private Task task;
	private List<TaskFile> files;
	private String priority;

	public UpdateFilesAction(Task taskP, List<TaskFile> filesP, String priorityP) {
		task = taskP;
		files = filesP;
		priority = priorityP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#execute(com.bigpupdev.synodroid.ds.TorrentListActivity, com.bigpupdev.synodroid.common.SynoServer)
	 */
	public void execute(ResponseHandler handlerP, SynoServer serverP) throws Exception {
		serverP.getDSMHandlerFactory().getDSHandler().setFilePriority(task, files, priority);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getName()
	 */
	public String getName() {
		return "Updating task " + task.taskId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getToastId()
	 */
	public int getToastId() {
		return R.string.action_updating;
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

}
