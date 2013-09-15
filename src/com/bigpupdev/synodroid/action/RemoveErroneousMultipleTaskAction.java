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

import com.bigpupdev.synodroid.data.Task;

/**
 * Delete a torrent
 * 
 * @author Eric Taix (eric.taix at gmail dot com)
 */
public class RemoveErroneousMultipleTaskAction implements SynoAction {

	// The task to resume
	private List<Task> tasks;
	private String taskids;

	public RemoveErroneousMultipleTaskAction(List<Task> tasksP) {
		tasks = tasksP;
		taskids = "";
		for (Task task : tasks) {
			if (!taskids.equals("")){
				taskids += ":";
			}
			taskids += ""+task.taskId;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#execute(com.bigpupdev.synodroid.ds.TorrentListActivity, com.bigpupdev.synodroid.common.SynoServer)
	 */
	public void execute(ResponseHandler handlerP, SynoServer serverP) throws Exception {
		serverP.getDSMHandlerFactory().getDSHandler().delete(taskids);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getName()
	 */
	public String getName() {
		return "Removing task " + taskids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getToastId()
	 */
	public int getToastId() {
		return R.string.action_erroneous;
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
		return null;
	}

}
