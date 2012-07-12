package com.bigpupdev.synodroid.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.action.DeleteMultipleTaskAction;
import com.bigpupdev.synodroid.action.PauseMultipleTaskAction;
import com.bigpupdev.synodroid.action.ResumeMultipleTaskAction;
import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.ui.DownloadFragment;

public class ActionModeHelperHoneycomb extends ActionModeHelper{
	public boolean terminating = false;
	ActionMode mCurrentActionMode = null;
	DownloadFragment mCurrentFragment = null;
	Activity mActivity = null;
	
	@Override
	public void stopActionMode(){
		if (mCurrentActionMode != null) mCurrentActionMode.finish();
	}
	
	@Override
	public void startActionMode(DownloadFragment fragment){
		if (mCurrentActionMode != null){
			return;
		}
		terminating = false;
		mCurrentFragment = fragment;
		mCurrentActionMode = fragment.getActivity().startActionMode(mContentSelectionActionModeCallback);
	}
	
	@Override
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
        	List<Task> t_list= new ArrayList<Task>();
        	for ( int i = mCurrentFragment.checked_tasks.size() -1 ; i >= 0 ; i--){
        		t_list.add(mCurrentFragment.checked_tasks.get(i));
        	}
        	
        	switch (menuItem.getItemId()) {
				case R.id.menu_pause:
                	try{
                		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode pause clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	app.executeAction(mCurrentFragment, new PauseMultipleTaskAction(t_list), false);
                	actionMode.finish();
                	app.forceRefresh();
                    return true;
                case R.id.menu_clear:
                	try{
                		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode clear clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	app.executeAction(mCurrentFragment, new DeleteMultipleTaskAction(t_list), false);
                	actionMode.finish();
                	app.forceRefresh();
                    return true;
                case R.id.menu_resume:
                	try{
                		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode resume clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	app.executeAction(mCurrentFragment, new ResumeMultipleTaskAction(t_list), false);
                	actionMode.finish();
                	app.forceRefresh();
                    return true;
            }
            return false;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
        	terminating = true;
    		mCurrentFragment.resetChecked();
        	mCurrentActionMode = null;
            mCurrentFragment = null;
            terminating = false;
        }
    };
}
