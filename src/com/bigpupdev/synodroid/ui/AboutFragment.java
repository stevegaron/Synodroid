package com.bigpupdev.synodroid.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.TextView;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.utils.EulaHelper;

public class AboutFragment extends Fragment{
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * Activity creation
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		try{
			if (((Synodroid)getActivity().getApplication()).DEBUG) Log.d(Synodroid.DS_TAG,"AboutActivity: Creating about fragment.");
		}catch (Exception ex){/*DO NOTHING*/}
		
		final FragmentActivity aboutActivity = this.getActivity();
		View about = inflater.inflate(R.layout.about, null, false);
		Button eulaBtn = (Button) about.findViewById(R.id.id_eula_view);
		eulaBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Diplay the EULA
				try {
					EulaHelper.showEula(true, aboutActivity);
				} catch (BadTokenException e) {
					// Unable to show dialog probably because intent has been closed. Ignoring...
				}
			}
		});

		String vn = "" + getString(R.string.app_name);
		try {
			PackageInfo pi = aboutActivity.getPackageManager().getPackageInfo(aboutActivity.getPackageName(), 0);
			if (pi != null) {
				vn += " " + pi.versionName;
			}
		} catch (Exception e) {
			try{
				if (((Synodroid)getActivity().getApplication()).DEBUG) Log.e(Synodroid.DS_TAG, "AboutFragment: Error while retrieving package information", e);
			}catch (Exception ex){/*DO NOTHING*/}
		}
		TextView vname = (TextView) about.findViewById(R.id.app_vers_name_text);
		vname.setText(vn);

		TextView helpBtn = (TextView) about.findViewById(R.id.t_gmail);
		helpBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					emailIntent.setType("plain/text");
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "synodroid@gmail.com" });
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Synodroid Professional - help");
					startActivity(emailIntent);
				} catch (Exception e) {
					AlertDialog.Builder builder = new AlertDialog.Builder(aboutActivity);
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
			}
		});
		
		TextView gplusBtn = (TextView) about.findViewById(R.id.t_gplus);
		gplusBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String url = "https://plus.google.com/111893484035545745539";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		
		return about;
	}
}
