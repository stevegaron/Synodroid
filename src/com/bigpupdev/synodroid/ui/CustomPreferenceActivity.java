/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bigpupdev.synodroid.ui;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.adapter.SlidingMenuAdapter;
import com.bigpupdev.synodroid.server.SynoServer;
import com.bigpupdev.synodroid.utils.ActionModeHelper;
import com.bigpupdev.synodroid.utils.ActivityHelper;
import com.bigpupdev.synodroid.utils.SlidingMenuItem;
import com.slidingmenu.lib.SlidingMenu;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A base activity that defers common functionality across app activities to an
 * {@link ActivityHelper}. This class shouldn't be used directly; instead, activities should
 * inherit from {@link BaseSinglePaneActivity} or {@link BaseMultiPaneActivity}.
 */
public abstract class CustomPreferenceActivity extends BasePreferenceActivity {
    final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);
    final ActionModeHelper mActionModeHelper = ActionModeHelper.createInstance();
	
    private static final int SMNU_DL = 0;
	private static final int SMNU_BR = 1;
	private static final int SMNU_FI = 2;
	private static final int SMNU_RS = 3;
	private static final int SMNU_SE = 4;
	private static final int SMNU_HP = 5;
	private static final int SMNU_AB = 6;
	private static final int SMNU_SET = 7;
	
	private SlidingMenu menu = null;
	private SlidingMenuItem menuListSelectedItem = null;
	
	public SlidingMenu getSlidingMenu(){
		return menu;
	};
	
	/**
	 * Show the preference activity
	 */
	public void showSearchActivity(boolean clear) {
		Intent next = new Intent();
		if (clear) next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		next.setClass(this, SearchActivity.class);
		next.putExtra("start_search", true);
		startActivity(next);
	}
	
	public void setServerChangeListener(OnClickListener ocl){
		if (menu == null) return;
		
		RelativeLayout changeSrv = (RelativeLayout) menu.findViewById(R.id.lServer);
		changeSrv.setOnClickListener(ocl);
		
		ImageView changeSrvImg = (ImageView) menu.findViewById(R.id.ivChangeSrv);
		changeSrvImg.setVisibility(View.VISIBLE);
	}
	
	public void updateSMServer(SynoServer server){
		if (menu == null) return;
		
		TextView svName = (TextView) menu.findViewById(R.id.tvSrvName);
		TextView svURL = (TextView) menu.findViewById(R.id.tvSrvUrl);
		
		if (server != null && server.isConnected()){
			svName.setText(server.getNickname());
			svURL.setText(server.getUser());
		}
		else{
			svName.setText(R.string.empty_not_connected);
			svURL.setText("");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Make sure the menu is hidden for when the back button is pressed
		if (menu != null) menu.showContent(false);
		
	}
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mActivityHelper.onKeyDown(keyCode, event) ||
                super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	return mActivityHelper.onCreateOptionsMenu(menu) || super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mActivityHelper.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    	if (menu != null && menu.isMenuShowing()) {
    		menu.showContent();
    	} else {
    		super.onBackPressed();
    	}
    }
    
    public void attachSecondarySlidingMenu(){
    	if (menu != null){
    		menu.setMode(SlidingMenu.LEFT_RIGHT);
    		menu.setSecondaryMenu(R.layout.sliding_bookmarks);
    		menu.setSecondaryShadowDrawable(R.drawable.shadow_right);
    	}
    }
    
    public void attachSlidingMenu(){
    	attachSlidingMenu(null);
    }
    
    public void attachSlidingMenu(SynoServer srv){
    	// configure the SlidingMenu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.90f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.home_menu_frame);
        
        SlidingMenuAdapter adapter = new SlidingMenuAdapter(this);
        adapter.add(new SlidingMenuItem(getString(R.string.sliding_downloads), R.drawable.ic_download, SMNU_DL));
        adapter.add(new SlidingMenuItem(getString(R.string.sliding_browser), R.drawable.ic_browser, SMNU_BR));
        adapter.add(new SlidingMenuItem(getString(R.string.sliding_files), R.drawable.ic_file, SMNU_FI));
        //adapter.add(new SlidingMenuItem(getString(R.string.sliding_rss), R.drawable.ic_rss, SMNU_RS));
        adapter.add(new SlidingMenuItem(getString(R.string.sliding_search), R.drawable.ic_title_search, SMNU_SE));
        adapter.add(new SlidingMenuItem(getString(R.string.menu_parameter), R.drawable.ic_settings, SMNU_SET));
        adapter.add(new SlidingMenuItem(getString(R.string.help), R.drawable.ic_help, SMNU_HP));
        adapter.add(new SlidingMenuItem(getString(R.string.sliding_about), R.drawable.ic_about, SMNU_AB));
        
        final ListView menuList = (ListView) menu.findViewById(R.id.lvMenu);
        final Activity act = this;
        menuList.setAdapter(adapter);
        
        menuList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                try {
                	menuListSelectedItem = (SlidingMenuItem) menuList.getItemAtPosition(i);
                	switch (menuListSelectedItem.id){
	                	default:
	                	case SMNU_DL:
	                		try{
	                    		if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"SlidingMenu: Menu download selected.");
	                    	}catch (Exception ex){/*DO NOTHING*/}
	                    	
	                		if (!(act instanceof HomeActivity)){
	                			final Intent intent = new Intent(act, HomeActivity.class);
	                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                            act.startActivity(intent);
	                		}
	                		else{
	                			menu.showContent(true);
	                		}
	                		break;
	                	case SMNU_BR:
	                		try{
	                    		if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"SlidingMenu: Menu browser selected.");
	                    	}catch (Exception ex){/*DO NOTHING*/}
	                    	
	                		if (!(act instanceof BrowserActivity)){
	                			final Intent intent = new Intent(act, BrowserActivity.class);
	                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                            act.startActivity(intent);
	                		}
	                		else{
	                			menu.showContent(true);
	                		}
	                		break;
	                	case SMNU_FI:
	                		try{
	                    		if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"SlidingMenu: Menu file selected.");
	                    	}catch (Exception ex){/*DO NOTHING*/}
	                    	
	                		if (!(act instanceof FileActivity)){
	                			final Intent intent = new Intent(act, FileActivity.class);
	                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                            act.startActivity(intent);
	                		}
	                		else{
	                			menu.showContent(true);
	                		}
	                		break;
	                	case SMNU_RS:
	                		menu.showContent(true);
	                		Crouton.makeText(CustomPreferenceActivity.this, R.string.not_yet_implemented, Synodroid.CROUTON_ALERT).show();
	                		break;
	                	case SMNU_SE:
	                		try{
	                    		if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"SlidingMenu: Menu search selected.");
	                    	}catch (Exception ex){/*DO NOTHING*/}
	                    	
	                		if (!(act instanceof SearchActivity)){
	                			showSearchActivity(true);     
		                	}
	                		else {
	                			menu.showContent(true);
	                		}
	                		break;
	                	case SMNU_HP:
	                		try{
	            				if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"SlidingMenu: Menu help selected.");
	            			}catch (Exception ex){/*DO NOTHING*/}
	                    	
	                		if (!(act instanceof HelpActivity)){
	                			// Starting new intent
		            			Intent next = new Intent();
		            			next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		                        next.setClass(act, HelpActivity.class);
		            			startActivity(next);
	                		}
	                		else{
	                			menu.showContent(true);
	                		}
	                		break;
	                	case SMNU_AB:
	                		try{
	            				if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"SlidingMenu: Menu about selected.");
	            			}catch (Exception ex){/*DO NOTHING*/}
	                    	
	                		if (!(act instanceof AboutActivity)){
	                			// Starting new intent
		            			Intent next = new Intent();
		            			next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		                        next.setClass(act, AboutActivity.class);
		            			startActivity(next);
	                		}
	                		else{
	                			menu.showContent(true);
	                		}
	                		break;
	                	case SMNU_SET:
	                		try{
	            				if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"SlidingMenu: Menu settings selected.");
	            			}catch (Exception ex){/*DO NOTHING*/}
	                    	
	                		Intent next = new Intent();
	                		next.setClass(act, DownloadPreferenceActivity.class);
	                		startActivity(next);
	                		break;
                	}
                	
                }
                catch(Exception e) {
                    Log.e(Synodroid.DS_TAG, "Cannot get item at position "+i+".");
                }
            }
		});

        if (srv != null){
        	updateSMServer(srv);
        }
    }
    
    /**
     * Returns the {@link ActivityHelper} object associated with this activity.
     */
    public ActivityHelper getActivityHelper() {
        return mActivityHelper;
    }
    
    /**
     * Returns the {@link ActivityHelper} object associated with this activity.
     */
    public ActionModeHelper getActionModeHelper() {
        return mActionModeHelper;
    }
    
}
