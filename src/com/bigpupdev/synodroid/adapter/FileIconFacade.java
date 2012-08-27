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

import com.bigpupdev.synodroid.data.TaskFile;
import com.bigpupdev.synodroid.R;

import android.widget.ImageView;

/**
 * A simple facade which bind a icon according to a torent status
 * 
 * @author eric.taix @ gmail.com
 * 
 */
public class FileIconFacade {

	/**
	 * Set the image according to the torrent status
	 * 
	 * @param viewP
	 * @param siteP
	 */
	public static void bindPriorityStatus(ImageView viewP, TaskFile fileP) {
		// Trap invalid task status and replace by unknown
		int id = R.drawable.file_normal;
		if (fileP.download){
			if (fileP.priority.equals("0")){
				id = R.drawable.file_normal;
			}
			else if (fileP.priority.equals("1")){
				id = R.drawable.file_high;
			}
			else if (fileP.priority.equals("-1")){
				id = R.drawable.file_low;
			}
		}
		else{
			id = (R.drawable.file_skipped);
		}
		
		viewP.setImageResource(id);
	}
}
