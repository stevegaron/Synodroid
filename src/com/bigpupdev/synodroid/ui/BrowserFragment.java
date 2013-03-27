/**
 * Copyright 2010 Eric Taix Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.bigpupdev.synodroid.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.action.AddTaskAction;
import com.bigpupdev.synodroid.utils.UIUtils;

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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
	
	private ImageButton go_btn = null;
	private ImageButton stop_btn = null;
	private EditText url_text = null;
	private ImageView url_favicon = null;
	private WebView myWebView = null;
	
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
		myWebView = (WebView) browser.findViewById(R.id.webview);
		url_favicon = (ImageView) browser.findViewById(R.id.favicon);
		stop_btn = (ImageButton) browser.findViewById(R.id.stop);
		go_btn = (ImageButton) browser.findViewById(R.id.go);
		url_text = (EditText) browser.findViewById(R.id.url);
		url_text.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View currentView, boolean hasFocus) {
				if (!hasFocus){
					go_btn.requestFocus();
				}
				
			}});	
		go_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View clickedView) {
				go_btn.requestFocus();
				String url = url_text.getText().toString();
				if (!url.equals("")){
					if (!url.contains(".")){
						try {
							url = "http://www.google.com/m?q=" + URLEncoder.encode(url, "utf-8");
						} catch (UnsupportedEncodingException e) {}
					}
					if (!url.contains("://")){
						url = "http://" + url;
					}
					
					myWebView.loadUrl(url);	
				}
			}
			
		});
		stop_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View clickedView) {
				myWebView.stopLoading();				
			}
			
		});
		
		final ProgressBar Pbar = (ProgressBar) browser.findViewById(R.id.browser_progress);
		MyWebViewClient webViewClient = new MyWebViewClient();
		MyDownloadListener downloadListener = new MyDownloadListener();
		MyWebChromeClient webChromeClient = new MyWebChromeClient();
		webChromeClient.setPB(Pbar);
		webViewClient.setWebView(myWebView);
		
		myWebView.setWebViewClient(webViewClient);
		myWebView.setDownloadListener(downloadListener);
		myWebView.setWebChromeClient(webChromeClient);
		myWebView.setOnTouchListener(new View.OnTouchListener() { 
			@Override
			public boolean onTouch(View v, MotionEvent event) {
			           switch (event.getAction()) { 
			               case MotionEvent.ACTION_DOWN: 
			               case MotionEvent.ACTION_UP: 
			                   if (!v.hasFocus()) { 
			                       v.requestFocus(); 
			                   } 
			                   break; 
			           } 
			           return false; 
			        }
			});
		
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
		private boolean shouldUpdate = true;
		
		public void setPB(ProgressBar mPB){
			pb = mPB;
		}
		
		public void onProgressChanged(WebView view, int progress){
			if(progress < 100 && pb.getVisibility() == ProgressBar.GONE){
	        	pb.setVisibility(ProgressBar.VISIBLE);
	        }
	        
	        pb.setProgress(progress);
	        
	        if(progress == 100) {
	        	pb.setVisibility(ProgressBar.GONE);
	        	((BrowserActivity)getActivity()).updateRefreshStatus(false);
				shouldUpdate = true;
				stop_btn.setVisibility(View.GONE);
	        }
	        else if (shouldUpdate){
				((BrowserActivity)getActivity()).updateRefreshStatus(true);
				shouldUpdate = false;
				stop_btn.setVisibility(View.VISIBLE);
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
			
			if (favicon != null){
				url_favicon.setVisibility(View.VISIBLE);
				url_favicon.setImageBitmap(favicon);
			}
			else{
				url_favicon.setVisibility(View.GONE);
			}
			
			url_text.setText(url);
			
			
			SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
		 	String default_url  = preferences.getString(PREFERENCE_DEFAULT_URL, "http://www.google.com/");
		 	
		 	MenuItem mnuHome = ((BrowserActivity)getActivity()).homeMenu;
		 	
		 	if (mnuHome != null){
		 		if (url.equals(default_url)){
		 			mnuHome.setIcon(R.drawable.ic_resethome);
				}
				else{
					mnuHome.setIcon(R.drawable.ic_sethome);
				}	
		 	}
		 	if(!UIUtils.isHoneycomb()){
				ViewGroup actionbar = ((BrowserActivity)getActivity()).getActivityHelper().getActionBarCompat();
				ImageButton menuItem = (ImageButton) actionbar.findViewById(mnuHome.getItemId());
				menuItem.setImageDrawable(mnuHome.getIcon());
			}
		 	
		}
	}
}
