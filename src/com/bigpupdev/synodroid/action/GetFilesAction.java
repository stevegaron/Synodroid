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
import com.bigpupdev.synodroid.ui.DownloadFragment;
import com.bigpupdev.synodroid.protocol.ResponseHandler;
import com.bigpupdev.synodroid.R;

import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.data.TaskFile;
import com.bigpupdev.synodroid.data.TaskFilesContainer;

/**
 * Retrieve task's files
 * 
 * @author Eric Taix (eric.taix at gmail dot com)
 */
public class GetFilesAction implements SynoAction {

	// The torrent to resume
	private Task task;
	private int LIMIT_PAR_REQUEST = 25;

	public GetFilesAction(Task taskP) {
		task = taskP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#execute(com.bigpupdev.synodroid.ds.TorrentListActivity, com.bigpupdev.synodroid.common.SynoServer)
	 */
	public void execute(ResponseHandler handlerP, SynoServer serverP) throws Exception {
		int start = 0;
		TaskFilesContainer container = serverP.getDSMHandlerFactory().getDSHandler().getFiles(task, start, LIMIT_PAR_REQUEST);
		int total = container.getTotalFiles();
		if (total > LIMIT_PAR_REQUEST) {
			int nbLoop = (total - 1) / 25;
			for (int iLoop = 0; iLoop < nbLoop; iLoop++) {
				start += LIMIT_PAR_REQUEST;
				// Retrieve other taks part
				TaskFilesContainer secondaryContainer = serverP.getDSMHandlerFactory().getDSHandler().getFiles(task, start, LIMIT_PAR_REQUEST);
				List<TaskFile> tasks = secondaryContainer.getTasks();
				// Add them to the main container
				container.getTasks().addAll(tasks);
			}
		}
		serverP.fireMessage(handlerP, DownloadFragment.MSG_DETAILS_FILES_RETRIEVED, container.getTasks());
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getName()
	 */
	public String getName() {
		return "Get task's files " + task.taskId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getToastId()
	 */
	public int getToastId() {
		return R.string.action_files;
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

}
