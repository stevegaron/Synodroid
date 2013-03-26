/**
 * Copyright 2010 Eric Taix Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.bigpupdev.synodroid.ui;

import java.io.File;
import java.util.Arrays;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.action.AddTaskAction;
import com.bigpupdev.synodroid.adapter.FileAdapter;
import com.bigpupdev.synodroid.utils.FileItem;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This activity displays a help page
 * 
 * @author Steve Garon (synodroid at gmail dot com)
 */
public class FileFragment extends SynodroidFragment {
	private ListView lvFiles;
	private TextView tvFiles;
	private FileAdapter faFiles;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Activity creation
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final Synodroid app = (Synodroid) getActivity().getApplication();
		try {
			if (app.DEBUG)
				Log.v(Synodroid.DS_TAG,	"FileFragment: Creating File fragment");
		} catch (Exception ex) {/* DO NOTHING */}

		View file_layout = inflater.inflate(R.layout.file, null, false);
		lvFiles = (ListView) file_layout.findViewById(R.id.file_list);
		tvFiles = (TextView) file_layout.findViewById(R.id.file_text);
		View empty = file_layout.findViewById(android.R.id.empty);
		faFiles = new FileAdapter(getActivity());
        addFilesToAdapter(Environment.getExternalStorageDirectory().getPath());
        lvFiles.setAdapter(faFiles);
        lvFiles.setEmptyView(empty);
		lvFiles.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FileItem selectedItem = (FileItem) lvFiles.getItemAtPosition(position);
				File selectedFile = new File(tvFiles.getText() + "/" + selectedItem.tag);
				File backFile = new File(tvFiles.getText().toString());
				
				if (!selectedFile.exists()) return;
				
				if (selectedFile.isDirectory()){
					if (app.DEBUG) Log.d(Synodroid.DS_TAG, "FileFragment: Directory " + selectedFile.getAbsolutePath() + " was click. Loading the content...");
					if (selectedItem.id == -1){
						//Go Back
						String back = backFile.getAbsolutePath().substring(0, backFile.getAbsolutePath().length() - backFile.getName().length() -1);
						addFilesToAdapter(back);
					}
					else{
						addFilesToAdapter(selectedFile.getAbsolutePath());
					}
				}
				else{
					if (app.DEBUG) Log.d(Synodroid.DS_TAG, "FileFragment: File " + selectedFile.getAbsolutePath() + " was click.");
					if (selectedFile.getName().endsWith(".torrent") || selectedFile.getName().endsWith(".nzb") ){
						if (app.DEBUG) Log.d(Synodroid.DS_TAG, "FileFragment: The file is a torrent or nzb file, it will be added to the download list.");
						app.executeAsynchronousAction(FileFragment.this, new AddTaskAction(Uri.parse(selectedFile.getAbsolutePath()), false, true), true);
					}
					else{
						if (app.DEBUG) Log.d(Synodroid.DS_TAG, "FileFragment: Unsupported file type. Do Nothing...");
					}
				}
			}
        });

		return file_layout;
	}
	
	public String getCurrentFolder(){
		return tvFiles.getText().toString();
	}
	
	public void addFilesToAdapter(String path){
		faFiles.clear();	
		tvFiles.setText(path);
		
		File dir = new File(path);
		if (dir.exists()){
			File[] f_list = dir.listFiles();
			if (f_list == null) {
				return;
			}
			
			Arrays.sort(f_list);
			
			if (!path.equals(Environment.getExternalStorageDirectory().getPath())){
				faFiles.add(new FileItem("..",R.drawable.ic_file, -1));
			}
			
			for (File cur_file : f_list){
				
				if (cur_file.getName().startsWith(".")) continue;
				
				if (cur_file.isDirectory()){
					faFiles.add(new FileItem(cur_file.getName(),R.drawable.ic_file, 0));
				}
				else{
					if (cur_file.getName().endsWith(".torrent") || cur_file.getName().endsWith(".nzb") ){
						faFiles.add(new FileItem(cur_file.getName(),R.drawable.ic_torrent_file, 1));
					}
					else{
						faFiles.add(new FileItem(cur_file.getName(),R.drawable.ic_unknown_file, 2));
					}
				}
			}
	        
		}
	}

	@Override
	public void handleMessage(Message msgP) {
		// TODO Auto-generated method stub
		
	}
}
