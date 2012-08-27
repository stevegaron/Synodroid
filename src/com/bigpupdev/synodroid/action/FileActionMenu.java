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
import java.util.List;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.action.SynoAction;
import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.data.TaskFile;

import android.app.Activity;
import android.content.Context;

/**
 * An utility class which declare all available actions for a task
 * 
 * @author eric.taix at gmail.com
 */
public class FileActionMenu {

	// The task associated to this action
	private TaskFile file;
	// The text to display
	private String title;
	// The action to execute if this TaskAction is selected
	private SynoAction action;
	// Flag to know if this action is eanbled
	private boolean enabled;

	/**
	 * Generate a list of actions according to a task's state
	 * 
	 * @param taskP
	 * @return
	 */
	public static List<FileActionMenu> createActions(Context ctxP, TaskFile fileP, Task taskP, List<TaskFile> files) {
		Synodroid app = (Synodroid) ((Activity)ctxP).getApplication();
		
		ArrayList<FileActionMenu> result = new ArrayList<FileActionMenu>();
		if (app.getServer().getDsmVersion().smallerThen(DSMVersion.VERSION3_1)){
			List<TaskFile> t_list = new ArrayList<TaskFile>();
			t_list.add(fileP);
			
			result.add(new FileActionMenu(fileP, ctxP.getString(R.string.priority_skip), new UpdateFilesAction(taskP, t_list, "skip"), true));
			result.add(new FileActionMenu(fileP, ctxP.getString(R.string.priority_high), new UpdateFilesAction(taskP, t_list, "high"), true));
			result.add(new FileActionMenu(fileP, ctxP.getString(R.string.priority_normal), new UpdateFilesAction(taskP, t_list, "normal"), true));
			result.add(new FileActionMenu(fileP, ctxP.getString(R.string.priority_low), new UpdateFilesAction(taskP, t_list, "low"), true));
		}
		
		return result;
	}

	/**
	 * Private constructor to avoid instanciation
	 * 
	 * @param taskP
	 */
	private FileActionMenu(TaskFile fileP, String titleP, SynoAction actionP, boolean enabledP) {
		file = fileP;
		title = titleP;
		action = actionP;
		enabled = enabledP;
	}

	/**
	 * @return the task
	 */
	public TaskFile getFile() {
		return file;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the action
	 */
	public SynoAction getAction() {
		return action;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

}
