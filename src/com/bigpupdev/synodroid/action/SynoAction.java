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

import com.bigpupdev.synodroid.data.Task;

/**
 * General interface to execute an action on the Synology's server
 * 
 * @author Eric Taix (eric.taix at gmail dot com)
 */
public interface SynoAction {

	/**
	 * Execute the action on the specific server
	 * 
	 * @param activityP
	 * @param serverP
	 */
	public void execute(ResponseHandler handlerP, SynoServer serverP) throws Exception;

	/**
	 * Return the name of the action
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Return true if a toast must be shown when this action is executed
	 * 
	 * @return
	 */
	public boolean isToastable();
	
	/**
	 * Return true if a toast must be shown when this action is executed
	 * 
	 * @return
	 */
	public boolean requireConfirm();

	/**
	 * Return the resource id to use when a toast must be shown
	 * 
	 * @return
	 */
	public int getToastId();

	/**
	 * Return the task associated with this task. May return null if no specific task is associated.
	 * 
	 * @return
	 */
	public Task getTask();
}
