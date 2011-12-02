/**
 * Copyright 2010 Eric Taix Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.bigpupdev.synodroid.ui;

import java.util.ArrayList;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.data.TaskFile;
import com.bigpupdev.synodroid.data.TaskStatus;
import com.bigpupdev.synodroid.ui.SynodroidFragment;
import com.bigpupdev.synodroid.action.GetFilesAction;
import com.bigpupdev.synodroid.adapter.FileDetailAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This activity displays a task's details
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class DetailFiles extends SynodroidFragment {
	FileDetailAdapter fileAdapter;
	private ListView filesListView;
		
	private Activity a;


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void handleMessage(Message msgP) {
		((DetailActivity)a).handleMessage(msgP);
	}
	
	public void resetList(){
		if (((Synodroid)((DetailActivity)a).getApplication()).DEBUG) Log.d(Synodroid.DS_TAG,"DetailFiles: Reseting file list.");
		
		if (fileAdapter != null)
			fileAdapter.updateFiles(new ArrayList<TaskFile>());
	}
	
	public void updateEmptyValues(String text, boolean showPB){
		if (filesListView != null){
			LinearLayout empty = (LinearLayout) filesListView.getEmptyView();
			if (empty != null){
				ProgressBar pb = (ProgressBar) empty.findViewById(R.id.empty_pb);
				TextView tv = (TextView) empty.findViewById(R.id.empty_text);
				if (showPB){
					pb.setVisibility(View.VISIBLE);
				}
				else{
					pb.setVisibility(View.GONE);
				}
				
				tv.setText(text);
			}
		}
	}
	
	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		a = this.getActivity();
		if (((Synodroid)((DetailActivity)a).getApplication()).DEBUG) Log.d(Synodroid.DS_TAG,"DetailFiles: Creating file list fragment.");
		
		// Get the details intent
		Intent intent = a.getIntent();
		Task task = (Task) intent.getSerializableExtra("com.bigpupdev.synodroid.ds.Details");
	
		View v = inflater.inflate(R.layout.detail_files, null);

		filesListView = (ListView) v.findViewById(android.R.id.list);
		fileAdapter = new FileDetailAdapter(this, task);
		filesListView.setAdapter(fileAdapter);
		LinearLayout empty = (LinearLayout) v.findViewById(android.R.id.empty);
		
		filesListView.setEmptyView(empty);
		
		if (!task.isTorrent && !task.isNZB){
			updateEmptyValues(getString(R.string.empty_file_list_wrong_type), false);
		}
		else if (!task.status.equals(TaskStatus.TASK_DOWNLOADING.name())){
			updateEmptyValues(a.getString(R.string.empty_file_list), false);
		}
		else{
			Synodroid app = (Synodroid) a.getApplication();
			app.executeAsynchronousAction(this, new GetFilesAction(task), false);
		}
		setRetainInstance(true);
		return v;
	}

	public void finish() {
		getActivity().finish();
	}

}
