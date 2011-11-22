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

import java.util.ArrayList;
import java.util.List;

import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.data.TaskFile;
import com.bigpupdev.synodroid.data.TaskStatus;
import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.utils.Utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * An adaptor for task's files. This adaptor aims to create a view for each detail in the listView
 * 
 * @author eric.taix at gmail.com
 */
public class FileDetailAdapter extends BaseAdapter {

	// The task
	private Task task;
	// List of task to update
	private List<TaskFile> modifiedTasks = new ArrayList<TaskFile>();
	// List of file
	private List<TaskFile> files = new ArrayList<TaskFile>();
	// The XML view inflater
	private final LayoutInflater inflater;
	// The main activity
	private Fragment fragment;

	/**
	 * Constructor
	 * 
	 * @param activityP
	 *            The current activity
	 * @param torrentsP
	 *            List of torrent
	 */
	public FileDetailAdapter(Fragment fragmentP, Task taskP) {
		task = taskP;
		fragment = fragmentP;
		Context c = fragment.getActivity().getApplicationContext();
		inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Update the torrents list
	 * 
	 * @param torrentsP
	 */
	public void updateFiles(List<TaskFile> filesP) {
		files = filesP;
		notifyDataSetChanged();
	}

	/**
	 * Return the count of element
	 * 
	 * @return The number of torrent in the list
	 */
	public int getCount() {
		if (files != null) {
			return files.size();
		} else {
			return 0;
		}
	}

	/**
	 * Return the torrent at the defined index
	 * 
	 * @param indexP
	 *            The index to use starting from 0
	 * @return Instance of Torrent
	 */
	public Object getItem(int indexP) {
		if (files != null) {
			if (indexP < files.size()) {
				return files.get(indexP);
			}
		}
		return null;
	}

	/**
	 * Return the item id of the item at index X
	 * 
	 * @param indexP
	 */
	public long getItemId(int indexP) {
		return indexP;
	}

	/**
	 * Return the view used for the item at position indexP. Always try to reuse an old view
	 */
	public View getView(int positionP, View convertViewP, ViewGroup parentP) {
		TaskFile file = files.get(positionP);
		RelativeLayout view = null;
		if (convertViewP != null) {
			view = (RelativeLayout) convertViewP;
		}
		// Create a new instance according to the class of the detail
		else {
			view = (RelativeLayout) inflater.inflate(R.layout.file_template, parentP, false);
		}
		// Binds datas
		bindData(view, file);
		return view;
	}

	/**
	 * Bind commons torrent's data with widget
	 * 
	 * @param viewP
	 * @param torrentP
	 */
	private void bindData(View viewP, final TaskFile fileP) {
		// The filename
		TextView fileText = (TextView) viewP.findViewById(R.id.id_file_name);
		fileText.setText(fileP.name);

		// The file size
		TextView fileSize = (TextView) viewP.findViewById(R.id.id_file_size);
		try{
			fileSize.setText(Utils.bytesToFileSize(Long.parseLong(fileP.filesize), false, fileP.filesize));
		}
		catch (Exception ex){
			fileSize.setText(fileP.filesize);
		}
		
		// Is the file has to be download
		CheckBox downloadFile = (CheckBox) viewP.findViewById(R.id.id_file_to_download);
		downloadFile.setTag(fileP.name);
		downloadFile.setVisibility((task.isTorrent ? View.VISIBLE : View.GONE));
		downloadFile.setChecked(fileP.download);
		downloadFile.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String tag = (String) buttonView.getTag();
				TaskFile file = new TaskFile();
				file.name = tag;
				int index = files.indexOf(file);
				if (index != -1) {
					file = files.get(index);
					if (isChecked != file.download) {
						fileP.download = isChecked;
						try{
							modifiedTasks.add(file);	
						} catch (NullPointerException e){
							modifiedTasks = new ArrayList<TaskFile>();
							modifiedTasks.add(file);
						}
						
					}
				}
			}
		});
		// Changing the checkbox state is only able if the download is not finished
		downloadFile.setEnabled((task.status.equals(TaskStatus.TASK_DOWNLOADING.name()) ? true : false));
	}

	/**
	 * Return the modified tasks list and then reset the modified list
	 * 
	 * @return
	 */
	public List<TaskFile> getModifiedTaskList() {
		List<TaskFile> result = modifiedTasks;
		modifiedTasks = new ArrayList<TaskFile>();
		return result;
	}
}
