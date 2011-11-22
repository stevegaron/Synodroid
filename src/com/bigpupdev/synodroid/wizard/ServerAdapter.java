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
package com.bigpupdev.synodroid.wizard;

import javax.jmdns.ServiceInfo;

import com.bigpupdev.synodroid.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * An list adapter which creates views for list row items
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class ServerAdapter extends BaseAdapter {

	// An array of ServiceInfo (see onjour protocol and jmDNS project)
	private ServiceInfo[] infos;
	// The layout inflater service
	private LayoutInflater inflater = null;

	/**
	 * The constructor which initialiaze the service info list
	 * 
	 * @param infosP
	 */
	public ServerAdapter(Context ctxP, ServiceInfo[] infosP) {
		infos = infosP;
		inflater = (LayoutInflater) ctxP.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		return infos.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(int position) {
		return infos[position];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		return position;
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
			view = (LinearLayout) inflater.inflate(R.layout.wizard_serverrow, parentP, false);
		}
		bindView(view, infos[positionP]);
		return view;
	}

	/**
	 * Bind view values
	 * 
	 * @param viewP
	 * @param infoP
	 */
	private void bindView(LinearLayout viewP, ServiceInfo infoP) {
		TextView nv = (TextView) viewP.findViewById(R.id.label);
		nv.setText(infoP.getName());
		TextView dv = (TextView) viewP.findViewById(R.id.description);
		dv.setText(infoP.getURL());
	}

}
