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
public class TaskFilesContainer {

	// The tasks list
	private List<TaskFile> taskFiles;
	// The total number of task (this is not the size of tasks list as the server can't return all tasks in one request)
	private int totalFiles = 0;

	/**
	 * Default constructor
	 */
	public TaskFilesContainer() {
	}

	/**
	 * A constructor which initialize the files list
	 * 
	 * @param tasksP
	 */
	public TaskFilesContainer(List<TaskFile> taskFilesP) {
		taskFiles = taskFilesP;
	}

	/**
	 * @return the Files
	 */
	public List<TaskFile> getTasks() {
		if (taskFiles != null) {
			return taskFiles;
		}
		// Return an empty list instead of a null reference
		return new ArrayList<TaskFile>();
	}

	/**
	 * @param tasks
	 *            the Files to set
	 */
	public void setTasks(ArrayList<TaskFile> taskFilesP) {
		this.taskFiles = taskFilesP;
	}

	/**
	 * @return the totalFiles
	 */
	public int getTotalFiles() {
		return totalFiles;
	}

	/**
	 * @param totalTasks
	 *            the totalFilesP to set
	 */
	public void setTotalFiles(int totalFilesP) {
		this.totalFiles = totalFilesP;
	}

}
