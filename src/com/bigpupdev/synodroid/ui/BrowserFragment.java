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
import com.bigpupdev.synodroid.action.AddTaskAction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * This activity displays a help page
 * 
 * @author Steve Garon (synodroid at gmail dot com)
 */
@SuppressLint("SetJavaScriptEnabled")
public class BrowserFragment extends SynodroidFragment {
	private static final String PREFERENCE_GENERAL = "general_cat";
	private static final String PREFERENCE_DEFAULT_URL = "general_cat.default_url";
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Activity creation
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
	 	String default_url  = preferences.getString(PREFERENCE_DEFAULT_URL, "http://www.google.com/");
	 	String curBrowserUrl = ((Synodroid)getActivity().getApplication()).getBrowserUrl();
	 	
		try {
			if (((Synodroid) getActivity().getApplication()).DEBUG)	Log.v(Synodroid.DS_TAG, "BrowserFragment: Creating Browser fragment");
		} catch (Exception ex) {/* DO NOTHING */}

		View browser = inflater.inflate(R.layout.browser, null, false);
		WebView myWebView = (WebView) browser.findViewById(R.id.webview);
		final ProgressBar Pbar = (ProgressBar) browser.findViewById(R.id.browser_progress);
		MyWebViewClient webViewClient = new MyWebViewClient();
		MyDownloadListener downloadListener = new MyDownloadListener();
		MyWebChromeClient webChromeClient = new MyWebChromeClient();
		webChromeClient.setPB(Pbar);
		webViewClient.setWebView(myWebView);
		
		myWebView.setWebViewClient(webViewClient);
		myWebView.setDownloadListener(downloadListener);
		myWebView.setWebChromeClient(webChromeClient);
		
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		
		if (curBrowserUrl != null){
			myWebView.loadUrl(curBrowserUrl);
		}
		else{
			myWebView.loadUrl(default_url);
		}	

		return browser;
	}

	@Override
	public void handleMessage(Message msgP) {
		// TODO Auto-generated method stub
		
	}
	
	public class MyWebChromeClient extends WebChromeClient{
		private ProgressBar pb = null;
		
		public void setPB(ProgressBar mPB){
			pb = mPB;
		}
		
		public void onProgressChanged(WebView view, int progress) 
	        {
	        if(progress < 100 && pb.getVisibility() == ProgressBar.GONE){
	        	pb.setVisibility(ProgressBar.VISIBLE);
	        }
	        pb.setProgress(progress);
	        if(progress == 100) {
	        	pb.setVisibility(ProgressBar.GONE);
	        }
	    }
	}
	
	public class MyDownloadListener implements DownloadListener{

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			try{
				if (((Synodroid)getActivity().getApplication()).DEBUG)Log.d(Synodroid.DS_TAG, "Downloading URL: " + url);
			} catch (Exception e){}
			
			if (url.startsWith("http://magnet/")){
				url = url.replace("http://magnet/", "magnet:");
			}
			else if (url.startsWith("https://magnet/")){
				url.replace("https://magnet/", "magnet:");
			}
			
			AddTaskAction addTask = new AddTaskAction(Uri.parse(url), true, true);
			Synodroid app = (Synodroid) getActivity().getApplication();
			app.executeAsynchronousAction(BrowserFragment.this, addTask, false);
		}
		
	}
	
	public class MyWebViewClient extends WebViewClient {
		
		private WebView webView = null;
		
		public MyWebViewClient() {
			super();
			// start anything you need to
		}
		
		public void setWebView(WebView mWV){
			webView = mWV;
		}
		
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	        if (failingUrl.startsWith("http://magnet/")){
	        	failingUrl = failingUrl.replace("http://magnet/", "magnet:");
			}
			else if (failingUrl.startsWith("https://magnet/")){
				failingUrl.replace("https://magnet/", "magnet:");
			}
	        
	        if (failingUrl.startsWith("magnet:")){
	        	AddTaskAction addTask = new AddTaskAction(Uri.parse(failingUrl), true, false);
				Synodroid app = (Synodroid) getActivity().getApplication();
				app.executeAsynchronousAction(BrowserFragment.this, addTask, false);
				
				webView.goBack();
	        }
	    }
		
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// Do something to the urls, views, etc.
			try{
				if (((Synodroid)getActivity().getApplication()).DEBUG)Log.d(Synodroid.DS_TAG, "Loading URL: " + url);
				((Synodroid)getActivity().getApplication()).setBrowserUrl(url);
			} catch (Exception e){}
			
			SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
		 	String default_url  = preferences.getString(PREFERENCE_DEFAULT_URL, "http://www.google.com/");
		 	
		 	if (((BrowserActivity)getActivity()).homeMenu != null){
		 		if (url.equals(default_url)){
					((BrowserActivity)getActivity()).homeMenu.setIcon(R.drawable.ic_resethome);
				}
				else{
					((BrowserActivity)getActivity()).homeMenu.setIcon(R.drawable.ic_sethome);
				}	
		 	}
		}
	}
}
