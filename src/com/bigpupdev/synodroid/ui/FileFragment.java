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

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.adapter.FileAdapter;
import com.bigpupdev.synodroid.utils.FileItem;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * This activity displays a help page
 * 
 * @author Steve Garon (synodroid at gmail dot com)
 */
public class FileFragment extends Fragment {
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
		try {
			if (((Synodroid) getActivity().getApplication()).DEBUG)
				Log.v(Synodroid.DS_TAG,
						"BrowserFragment: Creating File fragment");
		} catch (Exception ex) {/* DO NOTHING */
		}

		View file_layout = inflater.inflate(R.layout.file, null, false);
		ListView lvFiles = (ListView) file_layout.findViewById(R.id.file_list);
		FileAdapter adapter = new FileAdapter(getActivity());
        addFilesToAdapter(Environment.getExternalStorageDirectory().getPath(), adapter);
        lvFiles.setAdapter(adapter);

		return file_layout;
	}
	
	private void addFilesToAdapter(String path, FileAdapter faFiles){
		File dir = new File(path);
		if (dir.exists()){
			File[] f_list = dir.listFiles();
			if (f_list == null) {
				faFiles.clear();
				return;
			}
			for (int i = 0; i < f_list.length; i++){
				File cur_file = f_list[i];
				if (cur_file.isDirectory()){
					faFiles.add(new FileItem(cur_file.getName(),R.drawable.ic_file, i));
				}
				else{
					faFiles.add(new FileItem(cur_file.getName(),-1, i));
				}
			}
		}
		else{
			faFiles.clear();
		}
	}
}
