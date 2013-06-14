package com.bigpupdev.synodroid.ui;

import java.util.ArrayList;
import java.util.List;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.action.AddPwTaskListAction;
import com.bigpupdev.synodroid.action.AddTaskListAction;
import com.bigpupdev.synodroid.action.ClearAllTaskAction;
import com.bigpupdev.synodroid.action.EnumShareAction;
import com.bigpupdev.synodroid.action.GetDirectoryListShares;
import com.bigpupdev.synodroid.action.RemoveErroneousMultipleTaskAction;
import com.bigpupdev.synodroid.action.ResumeAllAction;
import com.bigpupdev.synodroid.action.StopAllAction;
import com.bigpupdev.synodroid.adapter.TaskAdapter;
import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.server.SynoServer;
import com.bigpupdev.synodroid.utils.ActivityHelper;
import com.bigpupdev.synodroid.ui.DownloadPreferenceActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Front-door {@link Activity} that displays high-level features the schedule application offers to
 * users. Depending on whether the device is a phone or an Android 3.0+ tablet, different layouts
 * will be used. For example, on a phone, the primary content is a {@link DashboardFragment},
 * whereas on a tablet, both a {@link DashboardFragment} and a {@link TagStreamFragment} are
 * displayed.
 */
public class HomeActivity extends BaseActivity {
	private static final String PREFERENCE_AUTO = "auto";
	private static final String PREFERENCE_AUTO_CREATENOW = "auto.createnow";
	private static final String PREFERENCE_FULLSCREEN = "general_cat.fullscreen";
	private static final String PREFERENCE_GENERAL = "general_cat";
	private static final String PREFERENCE_SHOW_GET_STARTED = "general_cat.show_get_started";
	
	public static final int NO_SERVER_DIALOG_ID = 2;
	private static final int ADD_DOWNLOAD = 3;
	private static final int OTP_REQUEST_DIALOG_ID = 4;
	
	//private TagStreamFragment mTagStreamFragment;
    @Override
	public boolean onSearchRequested() {
    	showSearchActivity(false);
		return true;
	}
   
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}
    
    public void updateActionBarTitle(String title, boolean is_secure){
    	ActivityHelper ah = getActivityHelper();
    	if (ah != null) ah.setActionBarTitle(title, is_secure);
    }	
    
    public void updateActionBarTitleOCL(android.view.View.OnClickListener ocl){
    	ActivityHelper ah = getActivityHelper();
    	if (ah != null) ah.setTitleOnClickListener(ocl);
    }
    /**
	 * Create the connection and error dialogs
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		Dialog dialog = null;
		switch (id) {
		// No server have been yet configured
		case NO_SERVER_DIALOG_ID:
			AlertDialog.Builder builderNoServer = new AlertDialog.Builder(HomeActivity.this);
			builderNoServer.setTitle(R.string.dialog_title_information);
			builderNoServer.setMessage(getString(R.string.no_server_configured));
			builderNoServer.setCancelable(true);
			builderNoServer.setPositiveButton(getString(R.string.button_yesplease), new OnClickListener() {
				// Launch the Preference activity
				public void onClick(DialogInterface dialogP, int whichP) {
					okToCreateAServer();
				}
			});
			builderNoServer.setNegativeButton(getString(R.string.button_nothanks), new OnClickListener() {
				// Launch the Preference activity
				public void onClick(DialogInterface dialogP, int whichP) {
					FragmentManager fm = getSupportFragmentManager();
					DownloadFragment fragment_download = (DownloadFragment) fm.findFragmentById(R.id.fragment_download);
					fragment_download.alreadyCanceled = true;
				}
			});
			dialog = builderNoServer.create();
			break;
		case OTP_REQUEST_DIALOG_ID:
			final Synodroid app = (Synodroid) getApplication();
			FragmentManager fm = getSupportFragmentManager();
			final DownloadFragment fragment_download = (DownloadFragment) fm.findFragmentById(R.id.fragment_download);
			fragment_download.setOTPDialog(true);
			
			AlertDialog.Builder otp_request = new AlertDialog.Builder(HomeActivity.this);
			otp_request.setTitle(R.string.title_otp);
			LayoutInflater otp_inflater = getLayoutInflater();
			View otp_v = otp_inflater.inflate(R.layout.otp_request, null);
			final EditText otp_edt = (EditText) otp_v.findViewById(R.id.otp_pass);
			otp_edt.setText("");
			otp_request.setView(otp_v);
			otp_request.setPositiveButton(getString(android.R.string.ok), new OnClickListener() {
				// Launch the Preference activity
				public void onClick(DialogInterface dialogP, int whichP) {
					try{
			        	app.connectServer(fragment_download, app.getServer(), fragment_download.getPostOTPActions(), false, otp_edt.getText().toString());
			        	fragment_download.setOTPDialog(false);
			        	fragment_download.resetPostOTPActions();
			        	removeDialog(OTP_REQUEST_DIALOG_ID);
			        }
			        catch (Exception e){
						//Cannot clear all when download fragment not accessible.
						try{
							if (((Synodroid)getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "HomeActivity: App tried to call OTP login when download fragment hidden.");
						}catch (Exception ex){/*DO NOTHING*/}
					}
				}
			});
			dialog = otp_request.create();
			break;
		case ADD_DOWNLOAD:
			AlertDialog.Builder add_download = new AlertDialog.Builder(HomeActivity.this);
			add_download.setTitle(R.string.menu_add);
			LayoutInflater inflater = getLayoutInflater();
			View v = inflater.inflate(R.layout.add_download, null);
			final EditText edt = (EditText) v.findViewById(R.id.add_url);
			edt.setText("");
			final EditText user = (EditText) v.findViewById(R.id.username);
			user.setText("");
			final EditText pass = (EditText) v.findViewById(R.id.pass);
			pass.setText("");
			final ImageView exp_col = (ImageView) v.findViewById(R.id.exp_col);
			final LinearLayout creds = (LinearLayout) v.findViewById(R.id.credentials);
			final RelativeLayout adv = (RelativeLayout) v.findViewById(R.id.adv_settings);
			adv.setOnClickListener(new android.view.View.OnClickListener(){

				@Override
				public void onClick(View v) {
					if (creds.getVisibility() == View.GONE){
						exp_col.setImageResource(R.drawable.ic_colapse);
						creds.setVisibility(View.VISIBLE);
					}
					else{
						creds.setVisibility(View.GONE);
						exp_col.setImageResource(R.drawable.ic_expand);
					}
				}
				
			});
			
			try{
				if (((Synodroid)getApplication()).getServer().getDsmVersion().smallerThen(DSMVersion.VERSION3_1)){
					adv.setVisibility(View.GONE);
				}
			}
			catch (NullPointerException e){}
			
			add_download.setView(v);
			add_download.setCancelable(true);
			add_download.setPositiveButton(getString(R.string.menu_add), new OnClickListener() {
				// Launch the Preference activity
				public void onClick(DialogInterface dialogP, int whichP) {
					try{
						if (((Synodroid)getApplication()).DEBUG) Log.i(Synodroid.DS_TAG, "HomeActivity: Adding url:" + edt.getText().toString());
					}catch (Exception ex){/*DO NOTHING*/}
					Synodroid app = (Synodroid) getApplication();
					FragmentManager fm = getSupportFragmentManager();
			        try{
			        	DownloadFragment fragment_download = (DownloadFragment) fm.findFragmentById(R.id.fragment_download);
			        	
			        	List<Uri> outlines = new ArrayList<Uri>();
			        	for (String line: edt.getText().toString().split("\n")){
			        		if (line.equals("")) continue;
			        		
			        		if (!line.startsWith("http://") && !line.startsWith("https://") && !line.startsWith("ftp://") && !line.startsWith("file://") && !line.startsWith("magnet:")){
								line = "http://"+line;
							}
				        	if (line.startsWith("http://magnet/")){
								line = line.replace("http://magnet/", "magnet:");
							}
							else if (line.startsWith("https://magnet/")){
								line = line.replace("https://magnet/", "magnet:");
							}
							
			        		outlines.add(Uri.parse(line));
			        	}
			        	
			        	if (!user.getText().toString().equals("") || !pass.getText().toString().equals("")){
			        		app.executeAsynchronousAction(fragment_download, new AddPwTaskListAction(outlines, user.getText().toString(), pass.getText().toString()), true);
			        	}
			        	else{
			        		app.executeAsynchronousAction(fragment_download, new AddTaskListAction(outlines), true);
			        	}
			        }
					catch (Exception e){
						//Cannot clear all when download fragment not accessible.
						try{
							if (((Synodroid)getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "HomeActivity: App tried to call add download when download fragment hidden.");
						}catch (Exception ex){/*DO NOTHING*/}
					}
					removeDialog(ADD_DOWNLOAD);
				}
			});
			add_download.setNegativeButton(getString(android.R.string.cancel), new OnClickListener() {
				// Launch the Preference activity
				public void onClick(DialogInterface dialogP, int whichP) {
					removeDialog(ADD_DOWNLOAD);
				}
			});
			dialog = add_download.create();
			break;
		}
		return dialog;
	}
	
	/**
	 * The user agree to create a new as no server has been configured or no server is suitable for the current connection
	 */
	private void okToCreateAServer() {
		final SharedPreferences preferences = getSharedPreferences(PREFERENCE_AUTO, Activity.MODE_PRIVATE);
		preferences.edit().putBoolean(PREFERENCE_AUTO_CREATENOW, true).commit();
		showPreferenceActivity();
	}
	
	/**
	 * Show the preference activity
	 */
	private void showPreferenceActivity() {
		Intent next = new Intent();
		next.setClass(this, DownloadPreferenceActivity.class);
		startActivity(next);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		((Synodroid) getApplication()).pauseServer();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences preferences = getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
	 	
		if (preferences.getBoolean(PREFERENCE_SHOW_GET_STARTED, true)) {
	 	   Intent next = new Intent();
	 	   next.setClass(HomeActivity.this, GetStartedActivity.class);
	 	   startActivity(next);
	 	}
		try{
			if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"HomeActivity: Resuming home activity.");
		}catch (Exception ex){/*DO NOTHING*/}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intentP) {
		super.onNewIntent(intentP);
		setIntent(intentP);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        attachSlidingMenu();
        getActivityHelper().setupActionBar(getString(R.string.app_name), true, getSlidingMenu());
        
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupHomeActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.refresh_menu_items, menu);
        getMenuInflater().inflate(R.menu.download_menu_items, menu);
    	super.onCreateOptionsMenu(menu);
    	return true;
    }

    private boolean serverValid(SynoServer s){
    	if (s == null) return false;
    	if (!s.isConnected()) return false;
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	SynoServer server = ((Synodroid)getApplication()).getServer();
        if (item.getItemId() == R.id.menu_refresh) {
        	try{
        		if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"HomeActivity: Menu refresh selected.");
        	}catch (Exception ex){/*DO NOTHING*/}
        	
        	if (serverValid(server)) triggerRefresh();
            return true;
        }
        else if (item.getItemId() == R.id.menu_search){
        	try{
        		if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"HomeActivity: Menu search selected.");
        	}catch (Exception ex){/*DO NOTHING*/}
        	
            showSearchActivity(false);          
        }
        else if (item.getItemId() == R.id.menu_add){
        	try{
        		if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"HomeActivity: Menu add selected.");
        	}catch (Exception ex){/*DO NOTHING*/}
        
        	if (serverValid(server)) showDialog(ADD_DOWNLOAD);
        }
		else if (item.getItemId() == R.id.menu_share){
			try{
				if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"HomeActivity: Menu get share list selected.");
			}catch (Exception ex){/*DO NOTHING*/}
        	
			if (serverValid(server)){
	            Synodroid app = (Synodroid) getApplication();
				FragmentManager fm = getSupportFragmentManager();
		        try{
		        	DownloadFragment fragment_download = (DownloadFragment) fm.findFragmentById(R.id.fragment_download);
		        	if (app.getServer().getDsmVersion().greaterThen(DSMVersion.VERSION3_0)){
		        		app.executeAsynchronousAction(fragment_download, new GetDirectoryListShares(null), false);
		        	}
		        	else{
		        		app.executeAsynchronousAction(fragment_download, new EnumShareAction(), false);
		        	}
		        }
				catch (Exception e){
					try{
						if (((Synodroid)getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "HomeActivity: App tried to call get share when download fragment hidden.");
					}catch (Exception ex){/*DO NOTHING*/}
				}
			}
		}
		else if (item.getItemId() == R.id.menu_clear_all){
			try{
				if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"HomeActivity: Menu clear all completed selected.");
			}catch (Exception ex){/*DO NOTHING*/}
        	
			if (serverValid(server)){
	            Synodroid app = (Synodroid) getApplication();
				FragmentManager fm = getSupportFragmentManager();
		        try{
		        	DownloadFragment fragment_download = (DownloadFragment) fm.findFragmentById(R.id.fragment_download);
		        	app.executeAction(fragment_download, new ClearAllTaskAction(), false);
	            	app.delayedRefresh();
		        }
				catch (Exception e){
					try{
						if (((Synodroid)getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "HomeActivity: App tried to call clear all when download fragment hidden.");
					}catch (Exception ex){/*DO NOTHING*/}
				}
			}
		}
		else if (item.getItemId() == R.id.menu_erroneous){
			try{
				if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"HomeActivity: Menu clear all erroneous tasks selected.");
			}catch (Exception ex){/*DO NOTHING*/}
        	
			if (serverValid(server)){
				Synodroid app = (Synodroid) getApplication();
				FragmentManager fm = getSupportFragmentManager();
		        try{
		        	DownloadFragment fragment_download = (DownloadFragment) fm.findFragmentById(R.id.fragment_download);
		        	List<Task> t_list = new ArrayList<Task>();
					
		        	for (int i = 0; i < fragment_download.taskView.getCount(); i++){
		        		Task t = (Task) fragment_download.taskView.getItemAtPosition(i);	
		        		if (t.status.startsWith("TASK_ERROR_")){
		        			t_list.add(t);
		        		}
		        	}
		        	if (t_list.size() != 0){
		        		app.executeAction(fragment_download, new RemoveErroneousMultipleTaskAction(t_list), false);
		            	app.delayedRefresh();
		        	}
		        }
				catch (Exception e){
					try{
						if (((Synodroid)getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "HomeActivity: App tried to remove erroneous tasks when download fragment hidden.");
					}catch (Exception ex){/*DO NOTHING*/}
				}
			}
		}
		else if (item.getItemId() == R.id.menu_pause_all){
			try{
				if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"HomeActivity: Menu pause all selected.");
			}catch (Exception ex){/*DO NOTHING*/}
        	
			if (serverValid(server)){
	            Synodroid app = (Synodroid) getApplication();
				FragmentManager fm = getSupportFragmentManager();
		        try{
		        	DownloadFragment fragment_download = (DownloadFragment) fm.findFragmentById(R.id.fragment_download);
		        	List<Task> tasks = ((TaskAdapter) fragment_download.taskView.getAdapter()).getTaskList();
		    		app.executeAction(fragment_download, new StopAllAction(tasks), false);
	            	app.delayedRefresh();
		        }
				catch (Exception e){
					try{
						if (((Synodroid)getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "HomeActivity: App tried to call pause all when download fragment hidden.");
					}catch (Exception ex){/*DO NOTHING*/}
				}
			}
		}
		else if (item.getItemId() == R.id.menu_revert){
			try{
				if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"HomeActivity: Menu resume all selected.");
			}catch (Exception ex){/*DO NOTHING*/}
        	
			if (serverValid(server)){
				Synodroid app = (Synodroid) getApplication();
				FragmentManager fm = getSupportFragmentManager();
		        try{
		        	DownloadFragment fragment_download = (DownloadFragment) fm.findFragmentById(R.id.fragment_download);
		        	List<Task> tasks = ((TaskAdapter) fragment_download.taskView.getAdapter()).getTaskList();
		    		app.executeAction(fragment_download, new ResumeAllAction(tasks), false);
	            	app.delayedRefresh();
		        }
				catch (Exception e){
					try{
						if (((Synodroid)getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "HomeActivity: App tried to call resume all when download fragment hidden.");
					}catch (Exception ex){/*DO NOTHING*/}
				}
			}
		}
		return super.onOptionsItemSelected(item);
    }

    private void triggerRefresh() {
    	try{
    		if (((Synodroid)getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"HomeActivity: Forcing task list refresh.");
    	}catch (Exception ex){/*DO NOTHING*/}
    	
    	((Synodroid) getApplication()).forceRefresh();
    }

    public void updateRefreshStatus(boolean refreshing) {
        getActivityHelper().setRefreshActionButtonCompatState(refreshing);
    }
}
