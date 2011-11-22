/**
 * Copyright 2010 Eric Taix
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 */
package com.bigpupdev.synodroid.adapter;

import java.util.List;

import com.bigpupdev.synodroid.data.Task;
import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.action.TaskActionMenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * An adapter which is able to build an action list according to the task's state
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class ActionAdapter extends BaseAdapter {

	// Available actions
	private List<TaskActionMenu> actions;
	// The XML view inflater
	private final LayoutInflater inflater;

	/**
	 * Constructor
	 * 
	 * @param ctxP
	 *            The context
	 */
	public ActionAdapter(Context ctxP, Task taskP) {
		actions = TaskActionMenu.createActions(ctxP, taskP);
		inflater = (LayoutInflater) ctxP.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		return actions.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(int positionP) {
		return actions.get(positionP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int positionP) {
		return positionP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int positionP, View convertViewP, ViewGroup parentP) {
		LinearLayout view = null;
		if (convertViewP != null) {
			view = (LinearLayout) convertViewP;
		} else {
			view = (LinearLayout) inflater.inflate(R.layout.action_template, parentP, false);
		}
		TextView textView = (TextView) view.findViewById(R.id.id_action);
		textView.setEnabled(actions.get(positionP).isEnabled());
		textView.setText(actions.get(positionP).getTitle());
		return view;
	}

}
