package com.bigpupdev.synodroid.ui;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.action.GetSearchEngineAction;
import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.utils.EulaHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.Toast;

public class SearchActivity extends BaseActivity{
	private static final String PREFERENCE_FULLSCREEN = "general_cat.fullscreen";
	private static final String PREFERENCE_GENERAL = "general_cat";
	private static final String TORRENT_SEARCH_URL_DL = "http://transdroid.org/latest-search";
	
	@Override
	public boolean onSearchRequested() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return super.onSearchRequested();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.default_menu_items, menu);
		try{
			if (((Synodroid)getApplication()).getServer().getDsmVersion().greaterThen(DSMVersion.VERSION3_0)){
				getMenuInflater().inflate(R.menu.default_menu_items_search, menu);	
			}	
		}
		catch (NullPointerException npe){}
		getMenuInflater().inflate(R.menu.update_search, menu);
		
        super.onCreateOptionsMenu(menu);
        return true;
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
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search){
        	try{
        		if (((Synodroid)getApplication()).DEBUG) Log.d(Synodroid.DS_TAG,"HomeActivity: Menu search selected.");
        	}catch (Exception ex){/*DO NOTHING*/}
        	
            startSearch(null, false, null, false);
        }
        else if (item.getItemId() == R.id.menu_update){
        	downloadSearchEngine();
			Toast toast = Toast.makeText(SearchActivity.this, getString(R.string.update_search_engine_toast), Toast.LENGTH_LONG);
			toast.show();
        }
		else if (item.getItemId() == R.id.menu_search_engine){
			try{
				if (((Synodroid)getApplication()).DEBUG) Log.d(Synodroid.DS_TAG,"HomeActivity: Menu get search engine list selected.");
			}catch (Exception ex){/*DO NOTHING*/}
        	
            Synodroid app = (Synodroid) getApplication();
			FragmentManager fm = getSupportFragmentManager();
	        try{
	        	SearchFragment fragment_download = (SearchFragment) fm.findFragmentById(R.id.fragment_search);
	        	app.executeAsynchronousAction(fragment_download, new GetSearchEngineAction(), false);
	        }
			catch (Exception e){
				try{
					if (((Synodroid)getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "HomeActivity: App tried to call get search engine list when download fragment hidden.");
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
