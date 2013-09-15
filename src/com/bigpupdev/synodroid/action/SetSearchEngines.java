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

import com.bigpupdev.synodroid.data.SearchEngine;
import com.bigpupdev.synodroid.data.Task;

/**
 * Set the shared directory
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class SetSearchEngines implements SynoAction {

	private List<SearchEngine> seList;

	/**
	 * Constructor
	 * 
	 * @param taskP
	 * @param filesP
	 * @param seedingRatioP
	 * @param seedingIntervalP
	 */
	public SetSearchEngines(List<SearchEngine> seListP) {
		seList = seListP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.action.SynoAction#execute(com.bigpupdev.synodroid.common.protocol.ResponseHandler, com.bigpupdev.synodroid.common.SynoServer)
	 */
	public void execute(ResponseHandler handlerP, SynoServer serverP) throws Exception {
		serverP.getDSMHandlerFactory().getDSHandler().setSearchEngines(seList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.action.SynoAction#getName()
	 */
	public String getName() {
		return "Setting search engine";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.action.SynoAction#getTask()
	 */
	public Task getTask() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.action.SynoAction#getToastId()
	 */
	public int getToastId() {
		return R.string.action_set_searchengine;
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
	 * @see com.bigpupdev.synodroid.common.action.SynoAction#isToastable()
	 */
	public boolean isToastable() {
		return true;
	}

}
