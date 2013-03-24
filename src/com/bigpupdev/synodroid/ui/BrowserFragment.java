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

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * This activity displays a help page
 * 
 * @author Steve Garon (synodroid at gmail dot com)
 */
public class BrowserFragment extends Fragment {
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
		try {
			if (((Synodroid) getActivity().getApplication()).DEBUG)
				Log.v(Synodroid.DS_TAG,
						"BrowserFragment: Creating Browser fragment");
		} catch (Exception ex) {/* DO NOTHING */
		}

		View browser = inflater.inflate(R.layout.browser, null, false);
		WebView myWebView = (WebView) browser.findViewById(R.id.webview);
		myWebView.setWebViewClient(new MyWebViewClient());
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.loadUrl("http://www.google.com");

		return browser;
	}

	public class MyWebViewClient extends WebViewClient {

		public MyWebViewClient() {
			super();
			// start anything you need to
		}

		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// Do something to the urls, views, etc.
		}
	}
}
