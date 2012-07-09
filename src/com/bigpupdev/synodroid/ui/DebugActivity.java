package com.bigpupdev.synodroid.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.TextView;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;

public class DebugActivity extends BaseActivity{
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
		
        setContentView(R.layout.activity_debug);
        TextView tv_logs = (TextView)findViewById(R.id.tv_logs);
        Button btn_send = (Button)findViewById(R.id.SendButton);
        Button btn_cancel = (Button)findViewById(R.id.cancelButton);
        
        final String logs = generateDebugLogs();
        if (logs == null){
        	tv_logs.setText(getString(R.string.no_logs));
        	btn_send.setEnabled(false);
        }
        else{
        	tv_logs.setText(logs);
        }
        
        btn_send.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				sendDebugLogs(logs);
			}
        	
        });
        
        btn_cancel.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				finish();
			}
        	
        });

		getActivityHelper().setupActionBar(getString(R.string.title_debug_logs), false);
        
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }
	
	@Override
	public boolean onSearchRequested() {
		return false;
	}
	
	private String generateDebugLogs (){
		Process mLogcatProc = null;
		BufferedReader reader = null;
		try {
		        mLogcatProc = Runtime.getRuntime().exec(new String[]
		                {"logcat", "-d", Synodroid.DS_TAG+":V *:S" });

		        reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));

		        String line;
		        final StringBuilder log = new StringBuilder();
		        String separator = System.getProperty("line.separator"); 

		        while ((line = reader.readLine()) != null) {
		                log.append(line);
		                log.append(separator);
		        }
		        
		        return log.toString();
		        
		        
		}
		catch (IOException e){}
		finally{
		        if (reader != null)
		                try{
		                        reader.close();
		                }
		                catch (IOException e){}
		}
		return null;
	}
	
	private void sendDebugLogs(String logs){
	    File out_path = Environment.getExternalStorageDirectory();
		out_path = new File(out_path, "Android/data/com.bigpupdev.synodroid/cache/");
		File file = new File(out_path, "debug_log.txt");
		try {
			// Make sure the Pictures directory exists.
			out_path.mkdirs();
			OutputStream os = new FileOutputStream(file);
			os.write(logs.getBytes());
			os.close();
			try {
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "synodroid@gmail.com" });
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Synodroid Professional - Debug log");
				emailIntent.setType("plain/text");
				emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
				startActivity(emailIntent);
			} catch (Exception e) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.err_noemail);
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
		} catch (Exception e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			try{
				Log.e(Synodroid.DS_TAG, "Error writing " + file + " to SDCard.", e);
			}catch (Exception ex){/*DO NOTHING*/}
		}
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
			if (((Synodroid)getApplication()).DEBUG) Log.d(Synodroid.DS_TAG,"DebugActivity: Resuming debug activity.");
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
