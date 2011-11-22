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
package com.bigpupdev.synodroid.data;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a container of tasks list and a summary of tasks informations
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class TaskContainer {

	// The tasks list
	private List<Task> tasks;
	// The total download rate
	private String totalUp = "";
	// The total upload rate
	private String totalDown = "";
	// The total number of task (this is not the size of tasks list as the server can't return all tasks in one request)
	private int totalTasks = 0;

	/**
	 * Default constructor
	 */
	public TaskContainer() {
	}

	/**
	 * A constructor which initialize the tasks list
	 * 
	 * @param tasksP
	 */
	public TaskContainer(List<Task> tasksP) {
		tasks = tasksP;
	}

	/**
	 * @return the tasks
	 */
	public List<Task> getTasks() {
		if (tasks != null) {
			return tasks;
		}
		// Return an empty list instead of a null reference
		return new ArrayList<Task>();
	}

	/**
	 * @param tasks
	 *            the tasks to set
	 */
	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}

	/**
	 * @return the totalUp
	 */
	public String getTotalUp() {
		return totalUp;
	}

	/**
	 * @param totalUp
	 *            the totalUp to set
	 */
	public void setTotalUp(String totalUp) {
		this.totalUp = totalUp;
	}

	/**
	 * @return the totalDown
	 */
	public String getTotalDown() {
		return totalDown;
	}

	/**
	 * @param totalDown
	 *            the totalDown to set
	 */
	public void setTotalDown(String totalDown) {
		this.totalDown = totalDown;
	}

	/**
	 * @return the totalTasks
	 */
	public int getTotalTasks() {
		return totalTasks;
	}

	/**
	 * @param totalTasks
	 *            the totalTasks to set
	 */
	public void setTotalTasks(int totalTasks) {
		this.totalTasks = totalTasks;
	}

}
