package com.bigpupdev.synodroid.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.slidingmenu.lib.SlidingMenu;

public class BrowserActivity extends BaseActivity{
	private static final String PREFERENCE_FULLSCREEN = "general_cat.fullscreen";
	private static final String PREFERENCE_GENERAL = "general_cat";
	private static final String PREFERENCE_DEFAULT_URL = "general_cat.default_url";
	public MenuItem homeMenu = null;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onBackPressed() {
		if (menu != null && menu.isMenuShowing()) {
    		menu.showContent();
    	}
		else{
			FragmentManager fm = getSupportFragmentManager();
			BrowserFragment fragment_browser = (BrowserFragment) fm.findFragmentById(R.id.fragment_browser);
	    	if (fragment_browser != null ) {
	    		WebView wv = (WebView) fragment_browser.getView().findViewById(R.id.webview);
	    		if (wv.canGoBack()){
	    			wv.goBack();
	    		}
	    		else{
	    			super.onBackPressed();
	    		}
	    	} 
	    	else{
	    		super.onBackPressed();
	    	}
		}
    }
	
	/**
	 * Activity creation
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);        
		setContentView(R.layout.activity_browser);
        attachSlidingMenu(((Synodroid)getApplication()).getServer());
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		getActivityHelper().setupActionBar(getString(R.string.sliding_browser), false, getSlidingMenu());
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.refresh_menu_items, menu);
		getMenuInflater().inflate(R.menu.browser_menu_items, menu);
		homeMenu = menu.findItem(R.id.menu_sethome);
        super.onCreateOptionsMenu(menu);
        return true;
    }
	
	public void updateRefreshStatus(boolean refreshing) {
        getActivityHelper().setRefreshActionButtonCompatState(refreshing);
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Synodroid app = (Synodroid) getApplication();
		FragmentManager fm = getSupportFragmentManager();
		BrowserFragment fragment_browser = (BrowserFragment) fm.findFragmentById(R.id.fragment_browser);
		
		if (item.getItemId() == R.id.menu_refresh) {
			try{
				if (app.DEBUG) Log.v(Synodroid.DS_TAG,"BrowserActivity: Menu refresh selected.");
			}catch (Exception ex){/*DO NOTHING*/}
			
			WebView webView = (WebView) fragment_browser.getView().findViewById(R.id.webview);
			webView.reload();
			return true;
        }
		else if (item.getItemId() == R.id.menu_sethome){
			try{
				if (app.DEBUG) Log.v(Synodroid.DS_TAG,"BrowserActivity: Menu Set Home selected.");
			}catch (Exception ex){/*DO NOTHING*/}
			SharedPreferences preferences = getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
		 	String default_url  = preferences.getString(PREFERENCE_DEFAULT_URL, "http://www.google.com/");
		 	
			WebView webView = (WebView) fragment_browser.getView().findViewById(R.id.webview);
			String cur_url = webView.getUrl();
			
			if (!default_url.equals(cur_url)){
				item.setIcon(R.drawable.ic_resethome);
			}
			else{
				item.setIcon(R.drawable.ic_sethome);
				cur_url = "http://www.google.com/";
			}
			
			SharedPreferences.Editor editor = preferences.edit();
		    editor.putString(PREFERENCE_DEFAULT_URL, cur_url);
		    editor.commit();
			
			Toast.makeText(BrowserActivity.this, getText(R.string.home_set) + cur_url, Toast.LENGTH_SHORT).show();

			return true;
        }
		else if (item.getItemId() == R.id.menu_gohome){
			try{
				if (app.DEBUG) Log.v(Synodroid.DS_TAG,"BrowserActivity: Menu Go Home selected.");
			}catch (Exception ex){/*DO NOTHING*/}
			
			SharedPreferences preferences = getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
		 	String default_url  = preferences.getString(PREFERENCE_DEFAULT_URL, "http://www.google.com/");
		 	WebView webView = (WebView) fragment_browser.getView().findViewById(R.id.webview);
			webView.loadUrl(default_url);
			return true;
        }
		
		return super.onOptionsItemSelected(item);
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
			if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"BrowserActivity: Resuming Browser activity.");
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
