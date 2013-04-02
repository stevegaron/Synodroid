package com.bigpupdev.synodroid.ui;

import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;

public class AboutFragment extends Fragment{
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
			if (((Synodroid)getActivity().getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"AboutActivity: Creating about fragment.");
		}catch (Exception ex){/*DO NOTHING*/}
		
		final FragmentActivity aboutActivity = this.getActivity();
		View about = inflater.inflate(R.layout.about, null, false);
		String vn = "" + getString(R.string.app_name);
		try {
			PackageInfo pi = aboutActivity.getPackageManager().getPackageInfo(aboutActivity.getPackageName(), 0);
			if (pi != null) {
				vn += " " + pi.versionName;
			}
		} catch (Exception e) {
			try{
				if (((Synodroid)getActivity().getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "AboutFragment: Error while retrieving package information", e);
			}catch (Exception ex){/*DO NOTHING*/}
		}
		TextView vname = (TextView) about.findViewById(R.id.app_vers_name_text);
		vname.setText(vn);
		TextView jmdns = (TextView) about.findViewById(R.id.tvJmdnsUrl);
		Linkify.addLinks(jmdns, Linkify.ALL);
		TextView slidingmenu = (TextView) about.findViewById(R.id.tvSlidingMenuUrl);
		Linkify.addLinks(slidingmenu, Linkify.ALL);
		TextView crouton = (TextView) about.findViewById(R.id.tvCroutonUrl);
		Linkify.addLinks(crouton, Linkify.ALL);
		
		return about;
	}
}
