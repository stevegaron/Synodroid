package com.bigpupdev.synodroid.ui;

import java.io.File;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.WindowManager;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;

public class FileActivity extends BaseActivity{
	private static final String PREFERENCE_FULLSCREEN = "general_cat.fullscreen";
	private static final String PREFERENCE_GENERAL = "general_cat";
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * Activity creation
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);        
		 
        setContentView(R.layout.activity_file);
        attachSlidingMenu(((Synodroid)getApplication()).getServer());
		getActivityHelper().setupActionBar(getString(R.string.sliding_files), false, getSlidingMenu());
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }
	
	@Override
	public void onBackPressed() {
		if (menu != null && menu.isMenuShowing()) {
    		menu.showContent();
    	}
		else{
			FragmentManager fm = getSupportFragmentManager();
			FileFragment fragment_file = (FileFragment) fm.findFragmentById(R.id.fragment_file);
	    	if (fragment_file != null && !fragment_file.getCurrentFolder().equals(Environment.getExternalStorageDirectory().getPath())) {
	    		File backFile = new File(fragment_file.getCurrentFolder());
	    		String back = backFile.getAbsolutePath().substring(0, backFile.getAbsolutePath().length() - backFile.getName().length() -1);
	    		fragment_file.addFilesToAdapter(back);
	    		return;
	    	} 
	    	else{
	    		super.onBackPressed();
	    	}
		}
    }
	
	
	@Override
	public boolean onSearchRequested() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		try{
			if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"BrowserActivity: Resuming File activity.");
		}catch (Exception ex){/*DO NOTHING*/}
		
		// Check for fullscreen
		SharedPreferences preferences = getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
		if (preferences.getBoolean(PREFERENCE_FULLSCREEN, false)) {
			// Set fullscreen or not
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
}
