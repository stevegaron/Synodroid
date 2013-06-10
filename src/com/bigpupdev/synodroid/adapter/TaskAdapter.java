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
import java.util.HashMap;
import java.util.List;

import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.server.SynoServer;
import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.data.TaskDetail;
import com.bigpupdev.synodroid.data.TaskStatus;
import com.bigpupdev.synodroid.ui.DownloadFragment;
import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * An adaptor for torrents list. This adaptor aims to create a view for each item in the listView
 * 
 * @author eric.taix at gmail.com
 */
public class TaskAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

	// List of torrent
	private List<Task> tasks = new ArrayList<Task>();
	// List of previous tasks
	private HashMap<Integer, Integer> uploadRatios = new HashMap<Integer, Integer>();
	private HashMap<Integer, Long> uploadTimestamp = new HashMap<Integer, Long>();
	private HashMap<Integer, Boolean> torrents = new HashMap<Integer, Boolean>();
	// The XML view inflater
	private final LayoutInflater inflater;
	// The main activity
	private DownloadFragment fragment;
	// Bitmap which is used for unknown progress
	private BitmapDrawable unknownDrawable;
	
	private Context c;
	private Activity a;

	static class ViewHolder{
		public ImageView image;
		public TextView torrentName;
		public TextView torrentSize;
		public TextView torrentCurrentSize;
		public ProgressBar downProgress;
		public ProgressBar upProgress;
		public ProgressBar unknownProgress;
		public TextView torrentRates;
		public TextView torrentETA;
	}
	
	/**
	 * Constructor
	 * 
	 * @param activityP
	 *            The current activity
	 * @param torrentsP
	 *            List of torrent
	 */
	public TaskAdapter(DownloadFragment fragmentP) {
		fragment = fragmentP;
		a = fragment.getActivity();
		c = a.getApplicationContext();
		inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Bitmap bitmap = BitmapFactory.decodeResource(fragmentP.getResources(), R.drawable.progress_unknown);
		bitmap = Utils.getRoundedCornerBitmap(bitmap, 4);
		unknownDrawable = new BitmapDrawable(bitmap);
	}

	/**
	 * Update the torrents list
	 * 
	 * @param torrentsP
	 */
	public void clearTasksSelection() {
		// First update upload informations
		for (Task task : tasks) {
			task.selected = false;
		}
		notifyDataSetChanged();
	}
	
	/**
	 * Update the torrents list
	 * 
	 * @param torrentsP
	 */
	public ArrayList<Integer> updateTasks(List<Task> tasksP, List<Integer> checked_tasks_id) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		// First update upload informations
		for (Task task : tasksP) {
			if (checked_tasks_id.contains(task.taskId)){
				task.selected = true;
			}
			int taskId = task.taskId;
			Integer progress = uploadRatios.get(taskId);
			if (progress != null) {
				task.uploadProgress = progress;
			}
			Long timestamp = uploadTimestamp.get(taskId);
			if (timestamp != null) {
				task.uploadTimestamp = timestamp;
			}
			Boolean isTorrent = torrents.get(taskId);
			if (isTorrent != null) {
				task.isTorrent = isTorrent;
			}
			ret.add(taskId);
		}
		tasks = tasksP;
		notifyDataSetChanged();
		return ret;
	}

	/**
	 * Update a task with its details
	 * 
	 * @param taskId
	 * @param prorgessP
	 */
	public void updateFromDetail(TaskDetail detailP, List<Integer> checked_tasks_id) {
		Integer upPerc = Utils.computeUploadPercent(detailP);
		int up = (upPerc != null ? upPerc.intValue() : 0);
		// Affect the new upload's progress
		uploadRatios.put(detailP.taskId, up);
		// The upload ration has been updated: timestamp
		uploadTimestamp.put(detailP.taskId, System.currentTimeMillis());
		// Update the torrent flag
		torrents.put(detailP.taskId, detailP.isTorrent);
		// Self update
		updateTasks(tasks, checked_tasks_id);
	}

	/**
	 * Return the count of element
	 * 
	 * @return The number of torrent in the list
	 */
	public int getCount() {
		if (tasks != null) {
			return tasks.size();
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
		if (tasks != null) {
			if (indexP < tasks.size()) {
				return tasks.get(indexP);
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
		try {
			return tasks.get(indexP).taskId;
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Return the view used for the item at position indexP. Always try to reuse an old view
	 */
	public View getView(int positionP, View convertViewP, ViewGroup parentP) {
		View view = convertViewP;
		
		if (view == null) {
			view = inflater.inflate(R.layout.task_template, parentP, false);
		    ViewHolder vh = new ViewHolder();
		    vh.image = (ImageView) view.findViewById(R.id.id_torrent_icon);
		    vh.torrentName = (TextView) view.findViewById(R.id.id_torrent_name);
		    vh.torrentSize = (TextView) view.findViewById(R.id.id_torrent_total_size);
		    vh.torrentCurrentSize = (TextView) view.findViewById(R.id.id_torrent_username);
		    vh.downProgress = (ProgressBar) view.findViewById(R.id.id_download_progress);
		    vh.upProgress = (ProgressBar) view.findViewById(R.id.id_upload_progress);
		    vh.unknownProgress = (ProgressBar) view.findViewById(R.id.id_unknow_progress);
		    vh.torrentRates = (TextView) view.findViewById(R.id.id_torrent_speed);
			vh.torrentETA = (TextView) view.findViewById(R.id.id_torrent_eta);
			
			view.setTag(vh);
		}
		
		try{
			bindView(view, tasks.get(positionP));
		}catch (IndexOutOfBoundsException e){
			view = inflater.inflate(R.layout.empty_task_template, parentP, false);
		}
		
		return view;
	}

	/**
	 * Bind torrent's data with widget
	 * 
	 * @param viewP
	 * @param taskP
	 */
	private void bindView(View viewP, final Task taskP) {
		ViewHolder vh = (ViewHolder) viewP.getTag();
		
		// Torrent's status icon
		IconFacade.bindTorrentStatus(c, vh.image, taskP);

		// The name of the torrent
		vh.torrentName.setText(taskP.fileName);

		// The torrent size
		vh.torrentSize.setText(taskP.totalSize);

		// The torrent's owner
		vh.torrentCurrentSize.setText(taskP.creator);

		// Get progress bar
		vh.unknownProgress.setBackgroundDrawable(unknownDrawable);
		// If state is DOWNLOADING or SEEDING or PAUSED
		if (taskP.status.equals(TaskStatus.TASK_DOWNLOADING.toString()) || taskP.status.equals(TaskStatus.TASK_SEEDING.toString()) || taskP.status.equals(TaskStatus.TASK_PAUSED.toString())) {
			vh.downProgress.setVisibility(View.VISIBLE);
			// If a known value
			if (taskP.downloadProgress != -1) {
				vh.downProgress.setProgress(taskP.downloadProgress);
			}
			else{
				vh.downProgress.setProgress(0);
			}
			// According to the user's preferences AND if it is a Torrent
			SynoServer server = ((Synodroid) a.getApplication()).getServer();
			if (server.getConnection().showUpload) {
				// If a known value
				if (taskP.uploadProgress != -1) {
					vh.upProgress.setVisibility(taskP.isTorrent ? View.VISIBLE : View.INVISIBLE);
					vh.unknownProgress.setVisibility(View.GONE);
					vh.upProgress.setProgress(taskP.uploadProgress);
				}
				// If no value then hide it !
				else {
					vh.upProgress.setVisibility(View.GONE);
					// Show only for a torrent
					vh.unknownProgress.setVisibility(taskP.isTorrent ? View.VISIBLE : View.INVISIBLE);
				}
			} else {
				vh.upProgress.setVisibility(View.INVISIBLE);
				vh.unknownProgress.setVisibility(View.GONE);
			}
		}
		// Hide progress bars
		else {
			vh.downProgress.setVisibility(View.INVISIBLE);
			vh.upProgress.setVisibility(View.INVISIBLE);
			vh.unknownProgress.setVisibility(View.GONE);
		}
		// The current rates
		ColorFacade.bindTorrentStatus(c, vh.torrentRates, taskP);
		String rates = "";
		if (taskP.downloadRate.length() > 0) {
			rates += "<font color=\"#009900\">D:" + taskP.downloadRate + "</font>&nbsp;&nbsp;&nbsp;&nbsp;";
		}
		if (taskP.uploadRate.length() > 0) {
			rates += "<font color=\"#AA6500\">U:" + taskP.uploadRate + "</font>";
		}
		if (rates != ""){
			vh.torrentRates.setText(Html.fromHtml(rates));
		}
		else if (!(taskP.status.equals(TaskStatus.TASK_DOWNLOADING.toString()) || 
				taskP.status.equals(TaskStatus.TASK_PRE_SEEDING.toString()) || 
				taskP.status.equals(TaskStatus.TASK_SEEDING.toString()) ) ){
			vh.torrentRates.setText(TaskStatus.getLabel(c, taskP.status));
		}
		else{
			vh.torrentRates.setText("");
		}
		
		// The estimated time left
		vh.torrentETA.setText(taskP.eta);
		
		if (taskP.selected){
			viewP.setBackgroundResource(R.drawable.list_item_selector_highlighted);
		}
		else{
			viewP.setBackgroundResource(R.drawable.list_item_selector_default);
		}
		final View listItem = viewP;
		final Task curTask = taskP;
		vh.image.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				fragment.checkView(curTask, listItem, !curTask.selected);
			}});
	}

	/**
	 * Return the tasks list
	 * 
	 * @return
	 */
	public List<Task> getTaskList() {
		return tasks;
	}

	/**
	 * Click on a item
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (fragment.mCurrentActionMode.isActionModeEnabled()){
			this.onItemLongClick(parent, view, position, id);
		}
		else{
			Task task = tasks.get(position);
			if (task != null) {
				fragment.onTaskClicked(task);
			}
		}
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Task task = tasks.get(position);
		if (task != null) {
			fragment.checkView(task, view, !task.selected);
			return true;
		}
		return false;
	}
}
