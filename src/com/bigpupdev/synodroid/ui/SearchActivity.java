package com.bigpupdev.synodroid.ui;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.utils.EulaHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;

public class SearchActivity extends BaseActivity{
	private static final String PREFERENCE_FULLSCREEN = "general_cat.fullscreen";
	private static final String PREFERENCE_GENERAL = "general_cat";
	
	@Override
	public boolean onSearchRequested() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return super.onSearchRequested();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.default_menu_items, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			SharedPreferences preferences = getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
			if (preferences.getBoolean(PREFERENCE_FULLSCREEN, false)) {
				// Set fullscreen or not
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			} else {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}

		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (!EulaHelper.hasAcceptedEula(this)) {
            EulaHelper.showEula(false, this);
        }
        setContentView(R.layout.activity_search);
        getActivityHelper().setupActionBar(getString(R.string.search_hint), false);
    }
	
	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	}

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }
}
