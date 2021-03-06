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
import com.bigpupdev.synodroid.action.GetFilesAction;
import com.bigpupdev.synodroid.action.PauseMultipleTaskAction;
import com.bigpupdev.synodroid.action.ResumeMultipleTaskAction;
import com.bigpupdev.synodroid.action.UpdateFilesAction;
import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.data.TaskFile;
import com.bigpupdev.synodroid.ui.DetailFiles;
import com.bigpupdev.synodroid.ui.DownloadFragment;
import com.bigpupdev.synodroid.ui.SynodroidFragment;

public class ActionModeHelperHoneycomb extends ActionModeHelper{
	public boolean terminating = false;
	ActionMode mCurrentActionMode = null;
	SynodroidFragment mCurrentFragment = null;
	Activity mActivity = null;
	Task mCurTask = null;
	
	@Override
	public void stopActionMode(){
		if (mCurrentActionMode != null) mCurrentActionMode.finish();
		started = false;
	}
	
	@Override
	public void startActionMode(DownloadFragment fragment){
		if (mCurrentActionMode != null){
			return;
		}
		terminating = false;
		mCurrentFragment = fragment;
		mCurrentActionMode = fragment.getActivity().startActionMode(mContentSelectionActionModeCallback);
		started = true;
	}
	
	@Override
	public void startActionMode(DetailFiles fragment, Task taskP){
		if (mCurrentActionMode != null){
			return;
		}
		terminating = false;
		mCurTask = taskP;
		mCurrentFragment = fragment;
		mCurrentActionMode = fragment.getActivity().startActionMode(mFileSelectionActionModeCallback);
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
        	for ( int i = ((DownloadFragment)mCurrentFragment).checked_tasks.size() -1 ; i >= 0 ; i--){
        		t_list.add(((DownloadFragment)mCurrentFragment).checked_tasks.get(i));
        	}
        	
        	switch (menuItem.getItemId()) {
				case R.id.menu_pause:
                	try{
                		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode pause clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	app.executeAction(((DownloadFragment)mCurrentFragment), new PauseMultipleTaskAction(t_list), false);
                	actionMode.finish();
                	app.delayedRefresh();
                    return true;
                case R.id.menu_clear:
                	try{
                		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode clear clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	app.executeAction(((DownloadFragment)mCurrentFragment), new DeleteMultipleTaskAction(t_list), false);
                	actionMode.finish();
                	app.delayedRefresh();
                    return true;
                case R.id.menu_resume:
                	try{
                		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode resume clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	app.executeAction(((DownloadFragment)mCurrentFragment), new ResumeMultipleTaskAction(t_list), false);
                	actionMode.finish();
                	app.delayedRefresh();
                    return true;
            }
            return false;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
        	terminating = true;
        	((DownloadFragment)mCurrentFragment).resetChecked();
        	mCurrentActionMode = null;
            mCurrentFragment = null;
            mCurTask = null;
            terminating = false;
        }
    };
    
    private ActionMode.Callback mFileSelectionActionModeCallback = new ActionMode.Callback() {
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = mCurrentFragment.getActivity().getMenuInflater();
            inflater.inflate(R.menu.action_mode_file_menu, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        	Synodroid app = (Synodroid) mCurrentFragment.getActivity().getApplication();
        	List<TaskFile> t_list= new ArrayList<TaskFile>();
        	for ( int i = ((DetailFiles)mCurrentFragment).checked_tasks.size() -1 ; i >= 0 ; i--){
        		t_list.add(((DetailFiles)mCurrentFragment).checked_tasks.get(i));
        	}
        	
        	switch (menuItem.getItemId()) {
				case R.id.menu_high:
                	try{
                		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode High clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	app.executeAction(((DetailFiles)mCurrentFragment), new UpdateFilesAction(mCurTask, t_list, "high"), false);
                	app.executeAsynchronousAction(((DetailFiles)mCurrentFragment), new GetFilesAction(mCurTask), false);
                	actionMode.finish();
                    return true;
                case R.id.menu_normal:
                	try{
                		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode Normal clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	app.executeAction(((DetailFiles)mCurrentFragment), new UpdateFilesAction(mCurTask, t_list, "normal"), false);
                	app.executeAsynchronousAction(((DetailFiles)mCurrentFragment), new GetFilesAction(mCurTask), false);
                	actionMode.finish();
                    return true;
                case R.id.menu_low:
                	try{
                		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode Low clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	app.executeAction(((DetailFiles)mCurrentFragment), new UpdateFilesAction(mCurTask, t_list, "low"), false);
                	app.executeAsynchronousAction(((DetailFiles)mCurrentFragment), new GetFilesAction(mCurTask), false);
                	actionMode.finish();
                    return true;
                case R.id.menu_skip:
                	try{
                		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode Skip clicked.");
                	}catch (Exception ex){/*DO NOTHING*/}
                	app.executeAction(((DetailFiles)mCurrentFragment), new UpdateFilesAction(mCurTask, t_list, "skip"), false);
                	app.executeAsynchronousAction(((DetailFiles)mCurrentFragment), new GetFilesAction(mCurTask), false);
                	actionMode.finish();
                    return true;
            }
            return false;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
        	terminating = true;
        	((DetailFiles)mCurrentFragment).resetChecked();
        	mCurrentActionMode = null;
        	mCurrentFragment = null;
        	mCurTask = null;
            terminating = false;
        }
    };
}
