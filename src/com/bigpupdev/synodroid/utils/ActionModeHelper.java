package com.bigpupdev.synodroid.utils;

import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.action.DeleteTaskAction;
import com.bigpupdev.synodroid.action.PauseTaskAction;
import com.bigpupdev.synodroid.action.ResumeTaskAction;
import com.bigpupdev.synodroid.ui.DownloadFragment;

public class ActionModeHelper {
	public boolean terminating = false;
	ActionMode mCurrentActionMode = null;
	DownloadFragment mCurrentFragment = null;
	
	public void stopActionMode(){
		mCurrentActionMode.finish();
	}
	
	public void startActionMode(DownloadFragment fragment){
		if (mCurrentActionMode != null){
			return;
		}
		terminating = false;
		mCurrentFragment = fragment;
		mCurrentActionMode = fragment.getActivity().startActionMode(mContentSelectionActionModeCallback);
	}
	
	public void setTitle(String title){
		if (mCurrentActionMode != null){
			mCurrentActionMode.setTitle(title);
		}
	}
	
	private ActionMode.Callback mContentSelectionActionModeCallback = new ActionMode.Callback() {
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = mCurrentFragment.getActivity().getMenuInflater();
            inflater.inflate(R.menu.action_mode_menu, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        	Synodroid app = (Synodroid) mCurrentFragment.getActivity().getApplication();
			switch (menuItem.getItemId()) {
                case R.id.menu_pause:
                	try{
                		if (app.DEBUG) Log.d(Synodroid.DS_TAG, "ActionModeHelper: Action Mode pause clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	for ( int i = mCurrentFragment.checked_tasks.size() -1 ; i >= 0 ; i--){
                		app.executeAction(mCurrentFragment, new PauseTaskAction(mCurrentFragment.checked_tasks.get(i)), false);
                	}
                	actionMode.finish();
                    return true;
                case R.id.menu_clear:
                	try{
                		if (app.DEBUG) Log.d(Synodroid.DS_TAG, "ActionModeHelper: Action Mode clear clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	for ( int i = mCurrentFragment.checked_tasks.size() -1 ; i >= 0 ; i--){
                		app.executeAction(mCurrentFragment, new DeleteTaskAction(mCurrentFragment.checked_tasks.get(i)), false);
                	}
                	actionMode.finish();
                    return true;
                case R.id.menu_resume:
                	try{
                		if (app.DEBUG) Log.d(Synodroid.DS_TAG, "ActionModeHelper: Action Mode resume clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	for ( int i = mCurrentFragment.checked_tasks.size() -1 ; i >= 0 ; i--){
                		app.executeAction(mCurrentFragment, new ResumeTaskAction(mCurrentFragment.checked_tasks.get(i)), false);
                	}
                	actionMode.finish();
                    return true;
            }
            return false;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
        	terminating = true;
    		for ( int i = mCurrentFragment.checked_items.size() -1 ; i >= 0 ; i--){
        		mCurrentFragment.checked_items.get(i).setChecked(false);
        	}
        	mCurrentActionMode = null;
            mCurrentFragment = null;
            terminating = false;
        }
    };
}
