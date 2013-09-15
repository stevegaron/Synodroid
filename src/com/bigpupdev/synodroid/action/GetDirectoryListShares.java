/**
 * Copyright 2010 Eric Taix Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.bigpupdev.synodroid.action;

import java.util.List;

import com.bigpupdev.synodroid.server.SynoServer;
import com.bigpupdev.synodroid.protocol.ResponseHandler;
import com.bigpupdev.synodroid.R;

import com.bigpupdev.synodroid.data.Folder;
import com.bigpupdev.synodroid.data.SharedFolderSelection;
import com.bigpupdev.synodroid.data.Task;

/**
 * Enum all shared directories
 * 
 * @author Eric Taix (eric.taix at gmail dot com)
 */
public class GetDirectoryListShares implements SynoAction {

	private String id;
	
	public GetDirectoryListShares(String idP) {
		id = idP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#execute(com.bigpupdev.synodroid.ds.TorrentListActivity, com.bigpupdev.synodroid.common.SynoServer)
	 */
	public void execute(ResponseHandler handlerP, SynoServer serverP) throws Exception {
		String name = "/";
		if (id == null)
			id = "remote/"+serverP.getDSMHandlerFactory().getDSHandler().getSharedDirectory(false);
		
		if (id.startsWith("remote/")){
			name = id.substring(7);
		}
		
		List<Folder> folders = serverP.getDSMHandlerFactory().getDSHandler().getDirectoryListing(id);
		
		serverP.fireMessage(handlerP, ResponseHandler.MSG_SHARED_DIRECTORIES_RETRIEVED, new SharedFolderSelection(name, folders));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getName()
	 */
	public String getName() {
		return "List directories";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.SynoAction#getToastId()
	 */
	public int getToastId() {
		return R.string.action_enum_shared;
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

	/**
	 * @return the task
	 */
	public Task getTask() {
		return null;
	}

}
