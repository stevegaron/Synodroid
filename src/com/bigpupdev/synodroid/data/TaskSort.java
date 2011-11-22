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

import com.bigpupdev.synodroid.R;

/**
 * This enumeration enumerates the different kind of sort<br/>
 * 
 * !!!!!!!!!!!!!! DO NOT MODIFIY enum constant values, because they are used as it in the request parameters
 * 
 * @author Eric Taix (eric.taix at gmail dot com)
 */
public enum TaskSort {
	TASK_ID(R.string.label_sort_taskid), FILENAME(R.string.label_sort_filename), TOTAL_SIZE(R.string.label_sort_totalsize), CURRENT_SIZE(R.string.label_sort_currentsize), PROGRESS(R.string.label_sort_progress), UPLOAD_RATE(R.string.label_sort_uploadrate), CURRENT_RATE(R.string.label_sort_currentrate), TIMELEFT(R.string.label_sort_timeleft), STATUS(R.string.label_sort_status), USERNAME(
			R.string.label_sort_username);

	// The resource id to display to the user
	private final int resId;

	/**
	 * Constructor which set the resource id
	 * 
	 * @param titleP
	 */
	private TaskSort(int resIdP) {
		resId = resIdP;
	}

	/**
	 * Return the resource id
	 * 
	 * @return
	 */
	public int getResId() {
		return resId;
	}

}
