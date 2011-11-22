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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.data.TaskStatus;

/**
 * This implementation retrieve the next task to update according to the lastest timestamp.<br/>
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class LastUpdateStrategy implements NextTaskStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.action.NextTaskStrategy#getNextTask(java.util .List)
	 */
	public Task getNextTask(List<Task> currentTasksP) {
		if (currentTasksP != null && currentTasksP.size() > 0) {
			// Copy the list
			List<Task> tasks = new ArrayList<Task>();
			for (Task task : currentTasksP) {
				tasks.add(task);
			}
			// Sort the list:
			// - DOWNLOADING and SEEDING on top
			// - PAUSING on top if no upload information is available
			// - others on bottom
			Collections.sort(tasks, new Comparator<Task>() {
				public int compare(Task t1, Task t2) {
					boolean a1 = isActive(t1);
					boolean a2 = isActive(t2);
					// t1 and t2 are active
					if (a1 && a2) {
						return (t1.uploadTimestamp < t2.uploadTimestamp ? -1 : 1);
					}
					// No active task
					else if (!a1 && !a2) {
						return 0;
					}
					// One active and one inactive
					else {
						return (a1 ? -1 : 1);
					}
				}
			});
			// Get the first task
			Task first = tasks.get(0);
			// Verify if it is active
			if (isActive(first)) {
				return first;
			}
		}
		return null;
	}

	/**
	 * Determine if t1 and t2 are active (downloading, seeding or pause without an upload value)
	 * 
	 * @param taskP
	 * @return
	 */
	private boolean isActive(Task taskP) {
		if (TaskStatus.TASK_DOWNLOADING.toString().equals(taskP.status) || TaskStatus.TASK_SEEDING.toString().equals(taskP.status) || (TaskStatus.TASK_PAUSED.toString().equals(taskP.status) && taskP.uploadProgress == -1)) {
			return true;
		} else {
			return false;
		}
	}
}
