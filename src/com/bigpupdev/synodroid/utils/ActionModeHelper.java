package com.bigpupdev.synodroid.utils;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;

import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.action.DeleteMultipleTaskAction;
import com.bigpupdev.synodroid.action.GetFilesAction;
import com.bigpupdev.synodroid.action.PauseMultipleTaskAction;
import com.bigpupdev.synodroid.action.ResumeMultipleTaskAction;
import com.bigpupdev.synodroid.action.UpdateFilesAction;
import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.data.TaskFile;
import com.bigpupdev.synodroid.ui.BaseActivity;
import com.bigpupdev.synodroid.ui.DetailFiles;
import com.bigpupdev.synodroid.ui.DownloadFragment;
import com.bigpupdev.synodroid.ui.SynodroidFragment;

public class ActionModeHelper{
	public boolean terminating = false;
	public boolean started = false;
	SynodroidFragment mCurrentFragment = null;
	ActivityHelper mCurrentActivityHelper = null;
	
	public static ActionModeHelper createInstance() {
        return UIUtils.isHoneycomb() ?
                new ActionModeHelperHoneycomb() :
                new ActionModeHelper();
    }
	
	public void stopActionMode(){
		if (mCurrentActivityHelper != null){
			terminating = true;
			
			if (mCurrentFragment instanceof DownloadFragment )
				((DownloadFragment)mCurrentFragment).resetChecked();
			else if (mCurrentFragment instanceof DetailFiles)
				((DetailFiles)mCurrentFragment).resetChecked();
			
    		mCurrentActivityHelper.stopActionMode();
			mCurrentFragment = null;
			mCurrentActivityHelper = null;
            terminating = false;
		}
		started = false;
	}
	
	private List<Task> countSelected(){
		List<Task> t_list= new ArrayList<Task>();
    	for ( int i = ((DownloadFragment)mCurrentFragment).checked_tasks.size() -1 ; i >= 0 ; i--){
    		t_list.add(((DownloadFragment)mCurrentFragment).checked_tasks.get(i));
    	}
		return t_list;
	}
	
	private List<TaskFile> countSelectedFile(){
		List<TaskFile> t_list= new ArrayList<TaskFile>();
    	for ( int i = ((DetailFiles)mCurrentFragment).checked_tasks.size() -1 ; i >= 0 ; i--){
    		t_list.add(((DetailFiles)mCurrentFragment).checked_tasks.get(i));
    	}
		return t_list;
	}
	
	public void startActionMode(DownloadFragment fragment){
		if (mCurrentActivityHelper != null){
			return;
		}
		terminating = false;
		mCurrentFragment = fragment;
		mCurrentActivityHelper = ((BaseActivity) fragment.getActivity()).getActivityHelper();
		
		final Synodroid app = (Synodroid) mCurrentFragment.getActivity().getApplication();
    	
		View.OnClickListener cancelClickListener = new View.OnClickListener() {
            public void onClick(View view) {
            	stopActionMode();
            }
        };
        View.OnClickListener clearClickListener = new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode clear clicked.");
            	}catch (Exception ex){/*DO NOTHING*/}
            	app.executeAction(((DownloadFragment)mCurrentFragment), new DeleteMultipleTaskAction(countSelected()), false);
            	stopActionMode();
            	app.forceRefresh();
            }
        };
        View.OnClickListener resumeClickListener = new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode resume clicked.");
            	}catch (Exception ex){/*DO NOTHING*/}
            	app.executeAction(((DownloadFragment)mCurrentFragment), new ResumeMultipleTaskAction(countSelected()), false);
            	stopActionMode();
            	app.forceRefresh();
            }
        };
        View.OnClickListener pauseClickListener = new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode pause clicked.");
            	}catch (Exception ex){/*DO NOTHING*/}
            	app.executeAction(((DownloadFragment)mCurrentFragment), new PauseMultipleTaskAction(countSelected()), false);
            	stopActionMode();
            	app.forceRefresh();
            }
        };
		
		mCurrentActivityHelper.startActionMode(cancelClickListener, clearClickListener,
				resumeClickListener, pauseClickListener);
		started = true;
	}
	
	public void setTitle(String title){
		if (mCurrentActivityHelper != null){
			mCurrentActivityHelper.setActionModeTitle(title);
		}
	}

	public boolean isActionModeEnabled(){
		return started;
	}
	
	public void startActionMode(DetailFiles fragment, final Task taskP) {
		if (mCurrentActivityHelper != null){
			return;
		}
		terminating = false;
		mCurrentFragment = fragment;
		mCurrentActivityHelper = ((BaseActivity) fragment.getActivity()).getActivityHelper();
		
		final Synodroid app = (Synodroid) mCurrentFragment.getActivity().getApplication();
    	
		View.OnClickListener cancelClickListener = new View.OnClickListener() {
            public void onClick(View view) {
            	stopActionMode();
            }
        };
        View.OnClickListener highClickListener = new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode High clicked.");
            	}catch (Exception ex){/*DO NOTHING*/}
            	app.executeAction(((DetailFiles)mCurrentFragment), new UpdateFilesAction(taskP, countSelectedFile(), "high"), false);
            	app.executeAsynchronousAction(((DetailFiles)mCurrentFragment), new GetFilesAction(taskP), false);
				stopActionMode();
            }
        };
        View.OnClickListener normalClickListener = new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode Normal clicked.");
            	}catch (Exception ex){/*DO NOTHING*/}
            	app.executeAction(((DetailFiles)mCurrentFragment), new UpdateFilesAction(taskP, countSelectedFile(), "normal"), false);
            	app.executeAsynchronousAction(((DetailFiles)mCurrentFragment), new GetFilesAction(taskP), false);
				stopActionMode();
            }
        };
        View.OnClickListener lowClickListener = new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode Low clicked.");
            	}catch (Exception ex){/*DO NOTHING*/}
            	app.executeAction(((DetailFiles)mCurrentFragment), new UpdateFilesAction(taskP, countSelectedFile(), "low"), false);
            	app.executeAsynchronousAction(((DetailFiles)mCurrentFragment), new GetFilesAction(taskP), false);
				stopActionMode();
            }
        };
        View.OnClickListener skipClickListener = new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		if (app.DEBUG) Log.v(Synodroid.DS_TAG, "ActionModeHelper: Action Mode Skip clicked.");
            	}catch (Exception ex){/*DO NOTHING*/}
            	app.executeAction(((DetailFiles)mCurrentFragment), new UpdateFilesAction(taskP, countSelectedFile(), "skip"), false);
            	app.executeAsynchronousAction(((DetailFiles)mCurrentFragment), new GetFilesAction(taskP), false);
				stopActionMode();
            }
        };
		
		mCurrentActivityHelper.startActionMode(cancelClickListener, highClickListener,
				normalClickListener, lowClickListener, skipClickListener);
		
	}

}
