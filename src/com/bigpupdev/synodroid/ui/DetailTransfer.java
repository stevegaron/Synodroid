/**
 * Copyright 2010 Eric Taix Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.bigpupdev.synodroid.ui;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.ui.SynodroidFragment;
import com.bigpupdev.synodroid.adapter.DetailAdapter;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * This activity displays a task's details
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class DetailTransfer extends SynodroidFragment{
	// The adapter for transfert informations
	DetailAdapter transAdapter;
	private Activity a;
	
	public void finish(){
		getActivity().finish();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		a = this.getActivity();
		try{
			if (((Synodroid)((DetailActivity)a).getApplication()).DEBUG) Log.d(Synodroid.DS_TAG,"DetailTransfer: Creating detail transfer fragment.");
		}catch (Exception ex){/*DO NOTHING*/}
		
		// Build the transfer tab
		View v = inflater.inflate(R.layout.detail_transfer, null);
		
		ListView transListView = (ListView) v.findViewById(android.R.id.list);
		transAdapter = new DetailAdapter(this);
		transListView.setAdapter(transAdapter);
		transListView.setOnItemClickListener(transAdapter);
		LinearLayout empty = (LinearLayout) v.findViewById(android.R.id.empty);
		transListView.setEmptyView(empty);
		setRetainInstance(true);
		return v;
	}
	
	@Override
	public void handleMessage(Message msgP) {
		((DetailActivity)a).handleMessage(msgP);
	}
}
