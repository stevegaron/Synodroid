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
package com.bigpupdev.synodroid.adapter;

import com.bigpupdev.synodroid.data.Task;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

/**
 * A simple facade which bind a icon according to a torent status
 * 
 * @author eric.taix @ gmail.com
 * 
 */
public class ColorFacade {

	/**
	 * Set the image according to the torrent status
	 * 
	 * @param viewP
	 * @param siteP
	 */
	public static void bindTorrentStatus(Context ctxP, TextView viewP, Task torrentP) {
		// Trap invalid task status and replace by unknown
		switch (torrentP.getStatus()) {
		case TASK_DOWNLOADING:
		case TASK_PRE_SEEDING:
		case TASK_SEEDING:
		case TASK_WAITING:
		case TASK_HASH_CHECKING:
			viewP.setTextColor(Color.parseColor("#999999"));
			break;
		case TASK_FINISHING:
		case TASK_FINISHED:
			viewP.setTextColor(Color.parseColor("#009900"));
			break;
		case TASK_PAUSED:
			viewP.setTextColor(Color.parseColor("#5555DD"));
			break;
		case TASK_UNKNOWN:
		case TASK_ERROR:
		case TASK_ERROR_DEST_NO_EXIST:
		case TASK_ERROR_DEST_DENY:
		case TASK_ERROR_QUOTA_REACHED:
		case TASK_ERROR_TIMEOUT:
		case TASK_ERROR_EXCEED_MAX_FS_SIZE:
		case TASK_ERROR_BROKEN_LINK:
		case TASK_ERROR_DISK_FULL:
		case TASK_ERROR_EXCEED_MAX_TEMP_FS_SIZE:
		case TASK_ERROR_EXCEED_MAX_DEST_FS_SIZE:
		case TASK_ERROR_TORRENT_DUPLICATE:
		default:
			viewP.setTextColor(Color.parseColor("#AA0000"));
			break;
		}
	}
}
