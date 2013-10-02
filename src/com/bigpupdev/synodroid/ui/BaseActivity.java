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

import java.util.ArrayList;
import java.util.List;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.action.SynoAction;
import com.bigpupdev.synodroid.adapter.SlidingMenuAdapter;
import com.bigpupdev.synodroid.preference.PreferenceFacade;
import com.bigpupdev.synodroid.server.SynoServer;
import com.bigpupdev.synodroid.utils.ActionModeHelper;
import com.bigpupdev.synodroid.utils.ActivityHelper;
import com.bigpupdev.synodroid.utils.EulaHelper;
import com.bigpupdev.synodroid.utils.SlidingMenuItem;
import com.slidingmenu.lib.SlidingMenu;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.BadTokenException;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A base activity that defers common functionality across app activities to an
 * {@link ActivityHelper}. This class shouldn't be used directly; instead, activities should
 * inherit from {@link BaseSinglePaneActivity} or {@link BaseMultiPaneActivity}.
 */
public abstract class BaseActivity extends FragmentActivity {
	private static final String PREFERENCE_AUTO = "auto";
	private static final String PREFERENCE_AUTO_CREATENOW = "auto.createnow";
	private static final String PREFERENCE_GENERAL = "general_cat";
	private static final String PREFERENCE_AUTO_DSM = "general_cat.auto_detect_DSM";
	private static final String PREFERENCE_DEF_SRV = "servers_cat.default_srv";
	private static final String PREFERENCE_SERVER = "servers_cat";
	
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
	
	public static final int NO_SERVER_DIALOG_ID = 2;
	public static final int OTP_REQUEST_DIALOG_ID = 4;
	
	// Flag to tell app that the connect dialog is opened
	private boolean connectDialogOpened = false;
	private boolean alreadyCanceled = false;
		
	protected SlidingMenu menu = null;
	private SlidingMenuItem menuListSelectedItem = null;
	
	public SlidingMenu getSlidingMenu(){
		return menu;
	};
	
	public boolean getAlreadyCanceled(){
		return alreadyCanceled;
	}
	
	public void setAlreadyCanceled(boolean value){
		alreadyCanceled = value;
	}
	
	public void updateActionBarTitleOCL(android.view.View.OnClickListener ocl){
    	ActivityHelper ah = getActivityHelper();
    	if (ah != null) ah.setTitleOnClickListener(ocl);
    }
    
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
	
	private SynodroidFragment getDisplayFragment(){
		FragmentManager fm = getSupportFragmentManager();
		SynodroidFragment sf = null;
		try{
			sf = (SynodroidFragment) fm.findFragmentById(R.id.fragment_download);
		} catch (Exception ed){
			sf = null;
		}
		
		if (sf == null){
			try{
				sf = (SynodroidFragment) fm.findFragmentById(R.id.fragment_browser);
			} catch (Exception eb){
				sf = null;
			}
		}
		
		if (sf == null){
			try{
				sf = (SynodroidFragment) fm.findFragmentById(R.id.fragment_file);
			} catch (Exception ef){
				sf = null;
			}
		}
		
		if (sf == null){
			try{
				sf = (SynodroidFragment) fm.findFragmentById(R.id.fragment_search);
			} catch (Exception es){
				sf = null;
			}
		}
		
		return sf;
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
			AlertDialog.Builder builderNoServer = new AlertDialog.Builder(BaseActivity.this);
			builderNoServer.setTitle(R.string.dialog_title_information);
			builderNoServer.setMessage(getString(R.string.no_server_configured));
			builderNoServer.setCancelable(true);
			builderNoServer.setPositiveButton(getString(R.string.button_yesplease), new android.content.DialogInterface.OnClickListener() {
				// Launch the Preference activity
				public void onClick(DialogInterface dialogP, int whichP) {
					okToCreateAServer();
				}
			});
			builderNoServer.setNegativeButton(getString(R.string.button_nothanks), new android.content.DialogInterface.OnClickListener() {
				// Launch the Preference activity
				public void onClick(DialogInterface dialogP, int whichP) {
					alreadyCanceled = true;
				}
			});
			dialog = builderNoServer.create();
			break;
		case OTP_REQUEST_DIALOG_ID:
			final Synodroid app = (Synodroid) getApplication();
			final SynodroidFragment current_fragment = getDisplayFragment();
			current_fragment.setOTPDialog(true);
			
			AlertDialog.Builder otp_request = new AlertDialog.Builder(BaseActivity.this);
			otp_request.setTitle(R.string.title_otp);
			LayoutInflater otp_inflater = getLayoutInflater();
			View otp_v = otp_inflater.inflate(R.layout.otp_request, null);
			final EditText otp_edt = (EditText) otp_v.findViewById(R.id.otp_pass);
			otp_edt.setText("");
			otp_request.setView(otp_v);
			otp_request.setPositiveButton(getString(android.R.string.ok), new android.content.DialogInterface.OnClickListener() {
				// Launch the Preference activity
				public void onClick(DialogInterface dialogP, int whichP) {
					try{
			        	app.connectServer(current_fragment, app.getServer(), current_fragment.getPostOTPActions(), false, otp_edt.getText().toString());
			        	current_fragment.setOTPDialog(false);
			        	current_fragment.resetPostOTPActions();
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
	
	/**
	 * Show the dialog to connect to a server
	 */
	public void showDialogToConnect(boolean autoConnectIfOnlyOneServerP, final List<SynoAction> actionQueueP, final boolean automated) {
		SharedPreferences generalPref = getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
		SharedPreferences serverPref = getSharedPreferences(PREFERENCE_SERVER, Activity.MODE_PRIVATE);
		boolean autoDetect = generalPref.getBoolean(PREFERENCE_AUTO_DSM, true);
		String defaultSrv = serverPref.getString(PREFERENCE_DEF_SRV, "0");
		
		final Activity a = this;
		if (!connectDialogOpened && a != null) {
			final Synodroid app = (Synodroid) a.getApplication();
			if (app != null){
				if (!app.isNetworkAvailable())
					return;
				
				final ArrayList<SynoServer> servers = PreferenceFacade.loadServers(a, PreferenceManager.getDefaultSharedPreferences(a), app.DEBUG, autoDetect);
				// If at least one server
				if (servers.size() != 0) {
					// If more than 1 server OR if we don't want to autoconnect then
					// show the dialog
					if (servers.size() > 1 || !autoConnectIfOnlyOneServerP) {
						boolean skip = false;
						String[] serversTitle = new String[servers.size()];
						for (int iLoop = 0; iLoop < servers.size(); iLoop++) {
							SynoServer s = servers.get(iLoop);
							serversTitle[iLoop] = s.getNickname();
							
							//Check if default server and connect to it skipping the dialog...
							if (defaultSrv.equals(s.getID()) && autoConnectIfOnlyOneServerP){
								app.connectServer(getDisplayFragment(), s, actionQueueP, automated);
								skip = true;
							}
						}
						if (!skip){
							connectDialogOpened = true;
							AlertDialog.Builder builder = new AlertDialog.Builder(a);
							builder.setTitle(getString(R.string.menu_connect));
							// When the user select a server
							builder.setItems(serversTitle, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int item) {
									SynoServer server = servers.get(item);
									// Change the server
									app.connectServer(getDisplayFragment(), server, actionQueueP, automated);
									dialog.dismiss();
								}
							});
							AlertDialog connectDialog = builder.create();
							try {
								connectDialog.show();
							} catch (BadTokenException e) {
								// Unable to show dialog probably because intent has been closed. Ignoring...
							}
							connectDialog.setOnDismissListener(new OnDismissListener() {
								public void onDismiss(DialogInterface dialog) {
									connectDialogOpened = false;
								}
							});
						}
					} else {
						// Auto connect to the first server
						if (servers.size() > 0) {
							SynoServer server = servers.get(0);
							// Change the server
							app.connectServer(getDisplayFragment(), server, actionQueueP, automated);
						}
					}
				}
				// No server then show the dialog to configure a server
				else {
					// Only if the EULA has been accepted. If the EULA has not been
					// accepted, it means that the EULA is currenlty being displayed so
					// don't show the "Wizard" dialog
					if (EulaHelper.hasAcceptedEula(a) && !alreadyCanceled) {
						try {
							a.showDialog(NO_SERVER_DIALOG_ID);
						} catch (Exception e) {
							// Unable to show dialog probably because intent has been closed or the dialog is already displayed. Ignoring...
						}
					}
				}
			}
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
        mActivityHelper.onPostCreate(savedInstanceState);
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
	                		Crouton.makeText(BaseActivity.this, R.string.not_yet_implemented, Synodroid.CROUTON_ALERT).show();
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
                    Log.e(Synodroid.DS_TAG, "Cannot get item at position "+i+".", e);
                }
            }
		});

        if (srv != null){
        	updateSMServer(srv);
        }
        if (this instanceof HomeActivity || this instanceof SearchActivity || this instanceof BrowserActivity || this instanceof FileActivity ){
        	setServerChangeListener(new android.view.View.OnClickListener(){
    			public void onClick(View v) {
    				menu.showContent();
    				showDialogToConnect(false, null, false);
    			}
    		});
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
    /**
     * Takes a given intent and either starts a new activity to handle it (the default behavior),
     * or creates/updates a fragment (in the case of a multi-pane activity) that can handle the
     * intent.
     *
     * Must be called from the main (UI) thread.
     */
    public void openActivityOrFragment(Intent intent) {
        // Default implementation simply calls startActivity
        startActivity(intent);
    }

    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
     */
    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }
}
