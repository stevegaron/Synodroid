/**
 * Copyright 2010 Eric Taix Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.bigpupdev.synodroid.ui;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.TextView;

/**
 * This activity displays a help page
 * 
 * @author Steve Garon (synodroid at gmail dot com)
 */
public class HelpFragment extends Fragment {
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
			if (((Synodroid)getActivity().getApplication()).DEBUG) Log.v(Synodroid.DS_TAG,"HelpFragment: Creating help fragment");
		}catch (Exception ex){/*DO NOTHING*/}
		
		final FragmentActivity helpActivity = this.getActivity();
		View help = inflater.inflate(R.layout.help, null, false);
		Button helpBtn = (Button) help.findViewById(R.id.id_email);
		helpBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					emailIntent.setType("plain/text");
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "synodroid@gmail.com" });
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Synodroid Professional - help");
					startActivity(emailIntent);
				} catch (Exception e) {
					AlertDialog.Builder builder = new AlertDialog.Builder(helpActivity);
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
		Button gplusBtn = (Button) help.findViewById(R.id.id_gplus);
		gplusBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String url = "https://plus.google.com/111893484035545745539";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		
		TextView main_web = (TextView) help.findViewById(R.id.syno_main_web);
		main_web.setText(Html.fromHtml("<a href=\"http://www.synology.com\">www.synology.com</a>"));
		main_web.setMovementMethod(LinkMovementMethod.getInstance());

		TextView buy = (TextView) help.findViewById(R.id.syno_buy_web);
		buy.setText(Html.fromHtml("<a href=\"http://www.synology.com/support/wheretobuy.php\">www.synology.com/support/wheretobuy.php</a>"));
		buy.setMovementMethod(LinkMovementMethod.getInstance());

		return help;
	}
}
