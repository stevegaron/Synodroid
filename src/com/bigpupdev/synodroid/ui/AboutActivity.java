package com.bigpupdev.synodroid.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.utils.ViewPagerIndicator;

public class AboutActivity extends BaseActivity{
	private static final String PREFERENCE_FULLSCREEN = "general_cat.fullscreen";
	private static final String PREFERENCE_GENERAL = "general_cat";
	
	MyAdapter mAdapter;
    ViewPager mPager;
    ViewPagerIndicator mIndicator;
    
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
        setContentView(R.layout.activity_about);
        mAdapter = new MyAdapter(getSupportFragmentManager(), 2, this);
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        // Find the indicator from the layout
        mIndicator = (ViewPagerIndicator)findViewById(R.id.indicator);
        
        // Set the indicator as the pageChangeListener
        mPager.setOnPageChangeListener(mIndicator);
     
        // Initialize the indicator. We need some information here:
        // * What page do we start on.
        // * How many pages are there in total
        // * A callback to get page titles
        mIndicator.init(0, mAdapter.getCount(), mAdapter);
		Resources res = getResources();
		Drawable prev = res.getDrawable(R.drawable.indicator_prev_arrow);
		Drawable next = res.getDrawable(R.drawable.indicator_next_arrow);
		mIndicator.setFocusedTextColor(new int[]{255, 255, 255});
		mIndicator.setUnfocusedTextColor(new int[]{120, 120, 120});
		
		// Set images for previous and next arrows.
		mIndicator.setArrows(prev, next);
		
		mIndicator.setOnClickListener(new OnIndicatorClickListener());

		getActivityHelper().setupActionBar(getString(R.string.menu_about), false);
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
		private AboutActivity mCurActivity;
		
		public MyAdapter(FragmentManager pFm, int pItemNum, AboutActivity pCurActivity) {
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
        	if (position == 0){
        		return new AboutFragment();
        	}
        	else{
        		return new HelpFragment();
        	}
        }

        public String getTitle(int pos){
        	if (pos == 0)
        		return mCurActivity.getString(R.string.tab_about);
        	else
        		return mCurActivity.getString(R.string.tab_help);
		}

    }
	
	class OnIndicatorClickListener implements ViewPagerIndicator.OnClickListener{
		public void onCurrentClicked(View v) {}
		
		public void onNextClicked(View v) {
			mPager.setCurrentItem(Math.min(mAdapter.getCount() - 1, mIndicator.getCurrentPosition() + 1));
		}

		public void onPreviousClicked(View v) {
			mPager.setCurrentItem(Math.max(0, mIndicator.getCurrentPosition() - 1));
		}
    	
    }
}
