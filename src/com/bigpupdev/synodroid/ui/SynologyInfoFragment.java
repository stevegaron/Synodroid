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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This activity displays a help page
 * 
 * @author Steve Garon (synodroid at gmail dot com)
 */
public class SynologyInfoFragment extends Fragment {
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
			if (((Synodroid)getActivity().getApplication()).DEBUG) Log.d(Synodroid.DS_TAG,"SynologyInfoFragment: Creating Synology Info fragment");
		}catch (Exception ex){/*DO NOTHING*/}
		
		View help = inflater.inflate(R.layout.syno_info, null, false);
		
		TextView main_web = (TextView) help.findViewById(R.id.syno_main_web);
		main_web.setText(Html.fromHtml("<a href=\"http://www.synology.com\">www.synology.com</a>"));
		main_web.setMovementMethod(LinkMovementMethod.getInstance());

		TextView buy = (TextView) help.findViewById(R.id.syno_buy_web);
		buy.setText(Html.fromHtml("<a href=\"http://www.synology.com/support/wheretobuy.php\">www.synology.com/support/wheretobuy.php</a>"));
		buy.setMovementMethod(LinkMovementMethod.getInstance());

		return help;
	}
}
