package com.bigpupdev.synodroid.wizard;

import java.util.HashMap;
import java.util.List;

import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.ui.DownloadPreferenceActivity;
import com.bigpupdev.synodroid.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * The wizard which try to find server on a local network
 * 
 * @author Eric Taix
 */
public class AddServerWizard {

	public static final String META_NAME = "NAME";
	public static final String META_PORT = "PORT";
	public static final String META_HOST = "HOST";
	public static final String META_SCHEME = "SCHEME";
	public static final String META_PASSWORD = "PASSWORD";
	public static final String META_USERNAME = "USERNAME";
	public static final String META_DSM = "DSM";
	public static final String META_DDNS_NAME = "DDNS-NAME";
	public static final String META_DDNS = "DDNS";
	public static final String META_HTTPS = "HTTPS";
	public static final String META_WIFI = "WIFI";

	private static final int MSG_USER_EDITED = 1;
	private static final int MSG_DSM_SELECTED = 2;
	private static final int MSG_OPTIONS_HTTPS_EDITED = 3;
	private static final int MSG_OPTIONS_INTERNET_EDITED = 4;
	private static final int MSG_OPTIONS_NAME_EDITED = 5;
	private static final int MSG_OPTIONS_WIFI_EDITED = 6;
	
	private HashMap<String, Object> metaData = new HashMap<String, Object>();
	private boolean canceled = false;
	private boolean DEBUG;
	
	// ====================================================================
	// The message handler
	private AddHandler handler = new AddHandler() {
		@Override
		public void handleMessage(Message msg) {
			if (!canceled) {
				switch (msg.what) {
				// Name was given to the server
				case MSG_OPTIONS_NAME_EDITED:
					try {
						optionsSRVName.dismiss();
						optionsSRVName = null;
						if (metaData.get(META_NAME).equals("")){
							createNewServer();
						}
						else{
							editUser();
						}
					} catch (Exception e) {
						if (DEBUG) Log.e(Synodroid.DS_TAG, "Exception thrown: ", e);
					}
					break;
				// User informations has been edited
				case MSG_USER_EDITED:
					try {
						userDialog.dismiss();
						userDialog = null;
						if (metaData.get(META_PASSWORD).equals("") || metaData.get(META_USERNAME).equals("")){
							editUser();
						}
						else{	
							selectDSM();	
						}
					} catch (Exception e) {
						if (DEBUG) Log.e(Synodroid.DS_TAG, "Exception thrown: ", e);
					}
					break;
				// User informations has been edited
				case MSG_DSM_SELECTED:
					try {
						dsmDialog.dismiss();
						dsmDialog = null;
						editOptionsSecure();
					} catch (Exception e) {
						if (DEBUG) Log.e(Synodroid.DS_TAG, "Exception thrown: ", e);
					}
					break;
				// Options have been edited
				case MSG_OPTIONS_HTTPS_EDITED:
					try {
						optionsHTTPSDialog.dismiss();
						optionsHTTPSDialog = null;
						switch(wizardDirType){
							case TYPE_WIFI_ONLY:
							case TYPE_WIFI_3G:
								show_wifi();
								break;
							case TYPE_3G_ONLY:
								metaData.put(META_WIFI, "");
								metaData.put(META_HOST, "");
								show_internet();
								break;
						}
					} catch (Exception e) {
						if (DEBUG) Log.e(Synodroid.DS_TAG, "Exception thrown: ", e);
					}
					break;
					// Options have been edited
				case MSG_OPTIONS_WIFI_EDITED:
					try {
						optionsWifi.dismiss();
						optionsWifi = null;
						if (metaData.get(META_WIFI).equals("") || metaData.get(META_HOST).equals("")){
							show_wifi();
						}
						else{
							switch(wizardDirType){
							case TYPE_WIFI_3G:
								show_internet();
								break;
							default:
								metaData.put(META_DDNS_NAME, "");
								metaData.put(META_DDNS, false);
								createServers();
								break;
							}
						}
					} catch (Exception e) {
						if (DEBUG) Log.e(Synodroid.DS_TAG, "Exception thrown: ", e);
					}
					break;
				case MSG_OPTIONS_INTERNET_EDITED:
					try {
						optionsInternetDialog.dismiss();
						optionsInternetDialog = null;
						if (metaData.get(META_DDNS_NAME).equals("")){
							show_internet();
						}
						else{
							createServers();
						}
					} catch (Exception e) {
						if (DEBUG) Log.e(Synodroid.DS_TAG, "Exception thrown: ", e);
					}
					break;
				default:
					break;
				}
			}
		}
	};

	// The cancel listener available for every dialogs
	private DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			// If we want to add a message when the user cancel the wizard: it's here
			// !
			canceled = true;
		}
	};

	// The current context in which this wizard is executed
	private DownloadPreferenceActivity context;
	// The view inflater
	private LayoutInflater inflater;
	// Often used label
	private CharSequence cancelSeq;
	// The user dialog
	private AlertDialog userDialog;
	// The DSM dialog
	private AlertDialog dsmDialog;
	// The HTTPS option dialog
	private AlertDialog optionsHTTPSDialog;
	// The HTTPS option dialog
	private AlertDialog optionsSRVName;
	// The Internet option dialog
	private AlertDialog optionsInternetDialog;
	// The Wifi option dialog
	private AlertDialog optionsWifi;
	
	private int wizardDirType = 0;
	final static int TYPE_WIFI_ONLY = 0;
	final static int TYPE_3G_ONLY = 1;
	final static int TYPE_WIFI_3G = 2;
	
	/**
	 * Constructor
	 * 
	 * @param ctxP
	 */
	public AddServerWizard(DownloadPreferenceActivity ctxP, boolean debug) {
		context = ctxP;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		cancelSeq = context.getText(R.string.button_cancel);
		DEBUG = debug;
	}

	/**
	 * Start the wizard
	 * 
	 * @param contextP
	 * @return
	 */
	public void start() {
		createNewServer();
	}

	private void createNewServer(){
		LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.add_wizard_create_server, null);
		final EditText name = (EditText) ll.findViewById(R.id.add_wzi_srv_name);
		final Spinner type = (Spinner) ll.findViewById(R.id.add_wiz_srv_type);
		optionsSRVName = new AddWizardBuilder(context).setTitle(context.getText(R.string.add_wizard_srv_sel_title)).setView(ll).setPositiveButton(context.getText(R.string.button_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				metaData.put(META_NAME, name.getText().toString());
				metaData.put(META_SCHEME, "HTTP");
				metaData.put(META_PORT, 5000);
				wizardDirType = (int) type.getSelectedItemId();
				Message msg = new Message();
				msg.what = MSG_OPTIONS_NAME_EDITED;
				handler.sendMessage(msg);
			}
		}).create();
		optionsSRVName.show();
	}
	
	private void show_wifi(){
		LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.add_wizard_wifi_selector, null);
		final EditText host = (EditText) ll.findViewById(R.id.add_wiz_srv_host);
		final Spinner wifi = (Spinner) ll.findViewById(R.id.add_wiz_wifi_list);
		final EditText name = (EditText) ll.findViewById(R.id.add_wiz_wifi_name);
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiMgr.isWifiEnabled()){
			List<WifiConfiguration> wifis = wifiMgr.getConfiguredNetworks();
			if (wifis.size()==0){
				wifi.setVisibility(View.GONE);
			}
			else{
				name.setVisibility(View.GONE);
				String[] wifiSSIDs = new String[wifis.size()];
				for (int iLoop = 0; iLoop < wifis.size(); iLoop++) {
					String ssid = wifis.get(iLoop).SSID;
					if (ssid != null) {
						if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
							ssid = ssid.substring(1, ssid.length() - 1);
						}
						wifiSSIDs[iLoop] = ssid;
					}
				}
				ArrayAdapter<String> sa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, wifiSSIDs);
				sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				wifi.setAdapter(sa);
			}
		}
		else{
			wifi.setVisibility(View.GONE);
		}
		optionsWifi = new AddWizardBuilder(context).setTitle(context.getText(R.string.add_wizard_wifi_title)).setView(ll).setPositiveButton(context.getText(R.string.button_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				metaData.put(META_HOST, host.getText().toString());
				if (wifi.getVisibility() == View.VISIBLE){
					metaData.put(META_WIFI, wifi.getSelectedItem().toString());
				}
				else{
					metaData.put(META_WIFI, name.getText().toString());
				}
				Message msg = new Message();
				msg.what = MSG_OPTIONS_WIFI_EDITED;
				handler.sendMessage(msg);
			}
		}).create();
		optionsWifi.show();
	}
	
	
	/**
	 * Create server(s)
	 */
	private void createServers() {
		context.onWizardFinished(metaData);
	}

	/**
	 * Edit HTTPS options
	 */
	private void editOptionsSecure() {
		LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.wizard_options_secured, null);
		final CheckBox httpsCB = (CheckBox) ll.findViewById(R.id.https_option);
		optionsHTTPSDialog = new AddWizardBuilder(context).setTitle(context.getText(R.string.add_wizard_sec_title)).setView(ll).setPositiveButton(context.getText(R.string.button_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				metaData.put(META_HTTPS, httpsCB.isChecked());
				metaData.put(META_HTTPS, httpsCB.isChecked());
				Message msg = new Message();
				msg.what = MSG_OPTIONS_HTTPS_EDITED;
				handler.sendMessage(msg);
			}
		}).create();
		optionsHTTPSDialog.show();
	}

	/**
	 * Edit Internet options
	 */
	private void show_internet() {
		LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.wizard_options_internet, null);
		final CheckBox ddnsCB = (CheckBox) ll.findViewById(R.id.ddns_option);
		final EditText ddnsET = (EditText) ll.findViewById(R.id.ddns_hostname);
		ddnsCB.setVisibility(View.GONE);
		ddnsET.setEnabled(true);
		optionsInternetDialog = new AddWizardBuilder(context).setTitle(context.getText(R.string.add_wizard_cell_title)).setView(ll).setPositiveButton(context.getText(R.string.button_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				metaData.put(META_DDNS, true);
				metaData.put(META_DDNS_NAME, ddnsET.getText().toString());
				Message msg = new Message();
				msg.what = MSG_OPTIONS_INTERNET_EDITED;
				handler.sendMessage(msg);
			}
		}).create();
		optionsInternetDialog.show();
	}

	/**
	 * Select the DSM version
	 */
	private void selectDSM() {
		final DSMVersion[] versions = DSMVersion.values();
		dsmDialog = new AddWizardBuilder(context).setTitle(context.getText(R.string.wizard_selectdsm_title)).setAdapter(new ArrayAdapter<DSMVersion>(context, R.layout.wizard_dsmrow, R.id.label, versions), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				DSMVersion dsm = versions[which];
				metaData.put(META_DSM, dsm.getTitle());
				Message msg = new Message();
				msg.what = MSG_DSM_SELECTED;
				handler.sendMessage(msg);
			}
		}).create();
		dsmDialog.show();
	}

	/**
	 * Show and edit user informations (username & password)
	 */
	private void editUser() {
		LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.wizard_user_pass_form, null);
		final TextView uView = (TextView) ll.findViewById(R.id.user);
		final TextView pView = (TextView) ll.findViewById(R.id.password);
		userDialog = new AddWizardBuilder(context).setTitle(context.getText(R.string.wizard_user_title)).setView(ll).setPositiveButton(context.getText(R.string.button_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				metaData.put(META_USERNAME, uView.getText().toString());
				metaData.put(META_PASSWORD, pView.getText().toString());
				Message msg = new Message();
				msg.what = MSG_USER_EDITED;
				handler.sendMessage(msg);
			}
		}).create();
		userDialog.show();
	}

	/* ======================================================================== */
	/**
	 * An AlertDailog builder which add some default behaviour
	 */
	private class AddWizardBuilder extends AlertDialog.Builder {
		/**
		 * Default constructor
		 * 
		 * @param ctxP
		 */
		public AddWizardBuilder(Context ctxP) {
			super(ctxP);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.app.AlertDialog.Builder#create()
		 */
		@Override
		public AlertDialog create() {
			// First add default values
			setCancelable(false);
			setNegativeButton(cancelSeq, cancelListener);
			// Then create the dialog
			return super.create();
		}
	}
}
