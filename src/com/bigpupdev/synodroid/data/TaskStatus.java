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

import android.content.Context;

/**
 * Here are defined all torrent'status
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public enum TaskStatus {
	TASK_UNKNOWN, TASK_WAITING, TASK_DOWNLOADING, TASK_SEEDING, TASK_PAUSED, TASK_FINISHED, TASK_HASH_CHECKING, TASK_ERROR, TASK_ERROR_BROKEN_LINK, TASK_ERROR_DISK_FULL, TASK_PRE_SEEDING, TASK_FINISHING, TASK_ERROR_DEST_NO_EXIST, TASK_ERROR_DEST_DENY, TASK_ERROR_QUOTA_REACHED, TASK_ERROR_TIMEOUT, TASK_ERROR_EXCEED_MAX_FS_SIZE, TASK_ERROR_EXCEED_MAX_TEMP_FS_SIZE, TASK_ERROR_EXCEED_MAX_DEST_FS_SIZE, TASK_ERROR_TORRENT_DUPLICATE, TASK_ERROR_TORRENT_INVALID;

	/**
	 * Return a localized status label
	 * 
	 * @param ctxP
	 * @param statusP
	 * @return
	 */
	public static String getLabel(Context ctxP, String statusP) {
		try{
			TaskStatus status = TaskStatus.valueOf(statusP);
			switch (status) {
			case TASK_WAITING:
				return ctxP.getString(R.string.detail_status_waiting);
			case TASK_DOWNLOADING:
				return ctxP.getString(R.string.detail_status_downloading);
			case TASK_PRE_SEEDING:
			case TASK_SEEDING:
				return ctxP.getString(R.string.detail_status_seeding);
			case TASK_PAUSED:
				return ctxP.getString(R.string.detail_status_paused);
			case TASK_FINISHING:
			case TASK_FINISHED:
				return ctxP.getString(R.string.detail_status_finished);
			case TASK_HASH_CHECKING:
				return ctxP.getString(R.string.detail_status_hash_checking);
			case TASK_UNKNOWN:
				return ctxP.getString(R.string.detail_unknown);
			case TASK_ERROR_DEST_NO_EXIST:
				return ctxP.getString(R.string.detail_status_error_no_exist);
			case TASK_ERROR_DEST_DENY:
				return ctxP.getString(R.string.detail_status_error_denied);
			case TASK_ERROR_QUOTA_REACHED:
				return ctxP.getString(R.string.detail_status_error_quota);
			case TASK_ERROR_TIMEOUT:
				return ctxP.getString(R.string.detail_status_error_timeout);
			case TASK_ERROR_EXCEED_MAX_FS_SIZE:
				return ctxP.getString(R.string.detail_status_error_max_fs_size);
			case TASK_ERROR_BROKEN_LINK:
				return ctxP.getString(R.string.detail_status_error_broken);
			case TASK_ERROR_DISK_FULL:
				return ctxP.getString(R.string.detail_status_error_full);
			case TASK_ERROR_EXCEED_MAX_TEMP_FS_SIZE:
				return ctxP.getString(R.string.detail_status_error_max_temp_file);
			case TASK_ERROR_EXCEED_MAX_DEST_FS_SIZE:
				return ctxP.getString(R.string.detail_status_error_max_dest_file);
			case TASK_ERROR_TORRENT_DUPLICATE:
				return ctxP.getString(R.string.detail_status_error_duplicate);
			case TASK_ERROR_TORRENT_INVALID:
				return ctxP.getString(R.string.detail_status_error_invalid);
			case TASK_ERROR:
			default:
				return ctxP.getString(R.string.detail_status_error);
			}
		} catch (IllegalArgumentException e){
			return ctxP.getString(R.string.detail_unknown);
		}
		
	}
}
