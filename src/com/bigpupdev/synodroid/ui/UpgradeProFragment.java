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

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * This activity displays a help page
 * 
 * @author Steve Garon (synodroid at gmail dot com)
 */
public class UpgradeProFragment extends Fragment {
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
		try{
			if (((Synodroid)getActivity().getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"AddDownloadFragment: Creating Add Download fragment");
		}catch (Exception ex){/*DO NOTHING*/}
		
		View upg = inflater.inflate(R.layout.upgrade_pro, null, false);
		
		final FragmentActivity gsActivity = this.getActivity();
		
		Button btnSkip = (Button) upg.findViewById(R.id.btn_skip);
		btnSkip.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				gsActivity.finish();
			}
		});
		
		if (gsActivity instanceof AboutActivity || gsActivity instanceof HelpActivity){
			btnSkip.setVisibility(View.GONE);
		}
		
		return upg;
	}
}
