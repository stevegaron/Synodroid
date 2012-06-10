package com.bigpupdev.synodroid.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.utils.CirclePageIndicator;
import com.bigpupdev.synodroid.utils.ViewPagerIndicator;

public class GetStartedActivity extends BaseActivity{
	private static final String PREFERENCE_FULLSCREEN = "general_cat.fullscreen";
	private static final String PREFERENCE_GENERAL = "general_cat";
	
	MyAdapter mAdapter;
    ViewPager mPager;
    CirclePageIndicator mIndicator;
    
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
		
        setContentView(R.layout.activity_get_started);
        mAdapter = new MyAdapter(getSupportFragmentManager(), 6, this);
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        // Find the indicator from the layout
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        
        // Set the indicator as the pageChangeListener
        mPager.setOnPageChangeListener(mIndicator);
     
        // Initialize the indicator. We need some information here:
        // * What page do we start on.
        // * How many pages are there in total
        // * A callback to get page titles
        mIndicator.setViewPager(mPager);
		getActivityHelper().setupActionBar(getString(R.string.welcome), false);
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		try{
			if (((Synodroid)getApplication()).DEBUG) Log.d(Synodroid.DS_TAG,"AboutActivity: Resuming about activity.");
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
	
	public static class MyAdapter extends FragmentPagerAdapter implements ViewPagerIndicator.PageInfoProvider{
		int mItemsNum;
		public GetStartedActivity mCurActivity;
		
		public MyAdapter(FragmentManager pFm, int pItemNum, GetStartedActivity pCurActivity) {
			super(pFm);
			mItemsNum = pItemNum;
			mCurActivity = pCurActivity;
        }

		@Override
        public int getCount() {
            return mItemsNum;
        }

        @Override
        public Fragment getItem(int position) {
        	switch (position){
        		case 0:
        			return new GetStartedFragment();
        		case 1:
        			return new SynologyInfoFragment();
        		case 2:
        			return new AddServerFragment();
        		case 3:
        			return new AddDownloadFragment();
        		case 4:
        			return new SearchEngineFragment();
        		default:
        			return new UpgradeProFragment();
        	}
        }

		@Override
		public String getTitle(int pos) {
			return "";
		}
    }
}
