package com.bigpupdev.synodroid.ui;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.action.GetSearchEngineAction;
import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.utils.ActivityHelper;
import com.bigpupdev.synodroid.utils.EulaHelper;
import com.bigpupdev.synodroid.utils.SearchResultsOpenHelper;
import com.bigpupdev.synodroid.utils.UIUtils;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;

public class SearchActivity extends BaseActivity{
	private static final String PREFERENCE_FULLSCREEN = "general_cat.fullscreen";
	private static final String PREFERENCE_GENERAL = "general_cat";
	private static final String PREFERENCE_SEARCH_SOURCE = "general_cat.search_source";
	private static final String PREFERENCE_SEARCH_ORDER = "general_cat.search_order";
	private static final String TORRENT_SEARCH_URL_DL = "http://transdroid.org/latest-search";
	private static boolean searchAtStart = false;
	
	@Override
	public boolean onSearchRequested() {
		if (!UIUtils.isHoneycomb()){
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			return super.onSearchRequested();
		}
		else{
			if (!getActivityHelper().startSearch()){
				searchAtStart = true;
			}
			return true;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.refresh_menu_items, menu);
		getMenuInflater().inflate(R.menu.search_menu, menu);
		try{
			if (((Synodroid)getApplication()).getServer().getDsmVersion().greaterThen(DSMVersion.VERSION3_0)){
				getMenuInflater().inflate(R.menu.default_menu_items_search, menu);	
			}	
		}
		catch (NullPointerException npe){}
		getMenuInflater().inflate(R.menu.update_search, menu);
		
        super.onCreateOptionsMenu(menu);
        getActivityHelper().setupSearch(this, menu);
        if (searchAtStart){
        	getActivityHelper().startSearch();
        	searchAtStart = false;
        }
        return true;
    }
	
    public void updateActionBarTitle(String title){
    	ActivityHelper ah = getActivityHelper();
    	if (ah != null) ah.setActionBarTitle(title, false);
    }
	
	private void downloadSearchEngine() {
		Intent i = new Intent(Intent.ACTION_VIEW);  
		i.setData(Uri.parse(TORRENT_SEARCH_URL_DL));
		try {
			startActivity(i); 
		} catch (Exception e) {
			AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
			// By default the message is "Error Unknown"
			builder.setMessage(R.string.err_nobrowser);
			builder.setTitle(getString(R.string.connect_error_title)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog errorDialog = builder.create();
			try {
				errorDialog.show();
			} catch (BadTokenException ex) {
				// Unable to show dialog probably because intent has been closed. Ignoring...
			}
		}
		 
	}
	
	private void clearDBCache(String query, String provider, String order){
		SearchResultsOpenHelper db_helper = new SearchResultsOpenHelper(this);
		SQLiteDatabase cache = db_helper.getWritableDatabase();
		cache.delete(SearchResultsOpenHelper.TABLE_CACHE, SearchResultsOpenHelper.CACHE_QUERY+"=? AND "+SearchResultsOpenHelper.CACHE_PROVIDER+"=? AND "+SearchResultsOpenHelper.CACHE_ORDER+"=?", new String[]{query, provider, order});
		cache.close();
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_search){
        	try{
        		if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"SearchActivity: Menu search selected.");
        	}catch (Exception ex){/*DO NOTHING*/}
        	
        	if (!UIUtils.isHoneycomb()){
            	startSearch(null, false, null, false);
            }
        }
		else if (item.getItemId() == R.id.menu_refresh){
			try{
        		if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"SearchActivity: Menu refresh selected.");
        	}catch (Exception ex){/*DO NOTHING*/}
        	
			FragmentManager fm = getSupportFragmentManager();
	        try{
	        	SearchFragment fragment_search = (SearchFragment) fm.findFragmentById(R.id.fragment_search);
	        	SharedPreferences preferences = getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
	    		String pref_src = preferences.getString(PREFERENCE_SEARCH_SOURCE, fragment_search.getSourceString());
	    		String pref_order = preferences.getString(PREFERENCE_SEARCH_ORDER, fragment_search.getSortString());

	        	clearDBCache(fragment_search.getLastSearch(), pref_src, pref_order);
	        	fragment_search.refresh();
	        }
			catch (Exception e){
				try{
					if (((Synodroid)getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "SearchActivity: Tried to refresh search but the fragment is hidden.");
				}catch (Exception ex){/*DO NOTHING*/}
			}
        }
        else if (item.getItemId() == R.id.menu_update){
        	downloadSearchEngine();
			Crouton.makeText(SearchActivity.this, getString(R.string.update_search_engine_toast), Synodroid.CROUTON_INFO).show();
        }
		else if (item.getItemId() == R.id.menu_search_engine){
			try{
				if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"SearchActivity: Menu get search engine list selected.");
			}catch (Exception ex){/*DO NOTHING*/}
        	
            Synodroid app = (Synodroid) getApplication();
			FragmentManager fm = getSupportFragmentManager();
	        try{
	        	SearchFragment fragment_download = (SearchFragment) fm.findFragmentById(R.id.fragment_search);
	        	app.executeAsynchronousAction(fragment_download, new GetSearchEngineAction(), false);
	        }
			catch (Exception e){
				try{
					if (((Synodroid)getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "SearchActivity: App tried to call get search engine list when download fragment hidden.");
				}catch (Exception ex){/*DO NOTHING*/}
			}
		}
        return super.onOptionsItemSelected(item);
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
        attachSlidingMenu(((Synodroid)getApplication()).getServer());
        getActivityHelper().setupActionBar(getString(R.string.search_hint), false, getSlidingMenu());
        
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
	
	public void updateRefreshStatus(boolean refreshing) {
        getActivityHelper().setRefreshActionButtonCompatState(refreshing);
    }
}
