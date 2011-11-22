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

import com.bigpupdev.synodroid.server.SynoServer;
import com.bigpupdev.synodroid.protocol.ResponseHandler;
import com.bigpupdev.synodroid.R;

import com.bigpupdev.synodroid.data.Task;

/**
 * Update a task (files and parameters)
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class UpdateTaskPropertiesAction implements SynoAction {

	// The task to update
	private Task task;
	private int ul_rate;
	private int dl_rate;
	private int priority;
	private int max_peers;
	private String destination;
	private int seeding_ratio;
	private int seeding_interval;

	/**
	 * Constructor
	 * 
	 * @param taskP
	 * @param filesP
	 * @param seedingRatioP
	 * @param seedingIntervalP
	 */
	public UpdateTaskPropertiesAction(Task taskP, int ul_rateP, int dl_rateP, int priorityP, int max_peersP, String destinationP, int seeding_ratioP, int seeding_intervalP) {
		task = taskP;
		ul_rate = ul_rateP;
		dl_rate = dl_rateP;
		priority = priorityP;
		max_peers = max_peersP;
		destination = destinationP;
		seeding_ratio = seeding_ratioP;
		seeding_interval = seeding_intervalP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.action.SynoAction#execute(com.bigpupdev.synodroid.common.protocol.ResponseHandler, com.bigpupdev.synodroid.common.SynoServer)
	 */
	public void execute(ResponseHandler handlerP, SynoServer serverP) throws Exception {
		serverP.getDSMHandlerFactory().getDSHandler().setTaskProperty(task, ul_rate, dl_rate, priority, max_peers, destination, seeding_ratio, seeding_interval);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.action.SynoAction#getName()
	 */
	public String getName() {
		return "Updating task " + task.taskId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.action.SynoAction#getTask()
	 */
	public Task getTask() {
		return task;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.action.SynoAction#getToastId()
	 */
	public int getToastId() {
		return R.string.action_updating;
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
