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

import java.util.ArrayList;
import java.util.List;

import com.bigpupdev.synodroid.R;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * An adaptor for task's details. This adaptor aims to create a view for each detail in the listView
 * 
 * @author eric.taix at gmail.com
 */
public class DetailAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

	// List of detail
	private List<Detail> details = new ArrayList<Detail>();
	// The XML view inflater
	private final LayoutInflater inflater;
	// The main activity
	private Fragment fragment;

	/**
	 * Constructor
	 * 
	 * @param activityP
	 *            The current activity
	 * @param torrentsP
	 *            List of torrent
	 */
	public DetailAdapter(Fragment fragmentP) {
		fragment = fragmentP;
		Context c = fragment.getActivity().getApplicationContext();
		
		inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Update the torrents list
	 * 
	 * @param torrentsP
	 */
	public void updateDetails(List<Detail> detailsP) {
		details = detailsP;
		notifyDataSetChanged();
	}

	/**
	 * Return the count of element
	 * 
	 * @return The number of torrent in the list
	 */
	public int getCount() {
		if (details != null) {
			return details.size();
		} else {
			return 0;
		}
	}

	/**
	 * Return the torrent at the defined index
	 * 
	 * @param indexP
	 *            The index to use starting from 0
	 * @return Instance of Torrent
	 */
	public Object getItem(int indexP) {
		if (details != null) {
			if (indexP < details.size()) {
				return details.get(indexP);
			}
		}
		return null;
	}

	/**
	 * Return the item id of the item at index X
	 * 
	 * @param indexP
	 */
	public long getItemId(int indexP) {
		return indexP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
		Detail detail = details.get(position);
		if (detail instanceof DetailText) {
			return 0;
		} else if (detail instanceof Detail2Progress) {
			return 1;
		} else if (detail instanceof DetailProgress) {
			return 2;
		} else if (detail instanceof Detail2Text) {
			return 3;
		}
		// By default this is a DetailText
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		return 4;
	}

	/**
	 * Return the view used for the item at position indexP. Always try to reuse an old view
	 */
	public View getView(int positionP, View convertViewP, ViewGroup parentP) {
		Detail detail = details.get(positionP);
		LinearLayout view = null;
		if (convertViewP != null) {
			view = (LinearLayout) convertViewP;
		}
		// Create a new instance according to the class of the detail
		else {
			if (detail instanceof Detail2Progress) {
				view = (LinearLayout) inflater.inflate(R.layout.details_2progress_template, parentP, false);
			} else if (detail instanceof DetailProgress) {
				int res = ((DetailProgress) detail).getRes();
				view = (LinearLayout) inflater.inflate(res, parentP, false);
			} else if (detail instanceof Detail2Text) {
				view = (LinearLayout) inflater.inflate(R.layout.details_2text_template, parentP, false);
			} else {
				view = (LinearLayout) inflater.inflate(R.layout.details_text_template, parentP, false);
			}
		}
		// Binds commons datas
		bindCommonsData(view, detail);
		// If this is a text detail
		if (detail instanceof DetailText) {
			bindDetailText(view, (DetailText) detail);
		} else if (detail instanceof Detail2Progress) {
			bindDetail2Progress(view, (Detail2Progress) detail);
		} else if (detail instanceof DetailProgress) {
			bindDetailProgress(view, (DetailProgress) detail);
		} else if (detail instanceof Detail2Text) {
			bindDetail2Text(view, (Detail2Text) detail);
		}
		return view;
	}

	/**
	 * Bind commons torrent's data with widget
	 * 
	 * @param viewP
	 * @param torrentP
	 */
	private void bindCommonsData(LinearLayout viewP, final Detail detailP) {
		// The name of the detail
		TextView name = (TextView) viewP.findViewById(R.id.id_detail_name);
		name.setText(detailP.getName());
		// The action's image
		ImageView img = (ImageView) viewP.findViewById(R.id.id_detail_action);
		if (detailP.getAction() != null) {
			img.setVisibility(View.VISIBLE);
		} else {
			img.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Bind torrent's data with widget for a DetailText instance
	 * 
	 * @param viewP
	 * @param torrentP
	 */
	private void bindDetailText(LinearLayout viewP, final DetailText detailP) {
		// The value of the detail
		TextView value = (TextView) viewP.findViewById(R.id.id_detail_value);
		value.setText(detailP.getValue());
	}

	/**
	 * Bind torrent's data with widget for a Detail2Text instance
	 * 
	 * @param viewP
	 * @param torrentP
	 */
	private void bindDetail2Text(LinearLayout viewP, final Detail2Text detailP) {
		// The value 1 of the detail
		TextView value = (TextView) viewP.findViewById(R.id.id_detail_value1);
		value.setText(detailP.getValue1());
		// The value 2 of the detail
		value = (TextView) viewP.findViewById(R.id.id_detail_value2);
		value.setText(detailP.getValue2());
	}

	/**
	 * Bind torrent's data with widget for a Detail2Progress instance
	 * 
	 * @param viewP
	 * @param torrentP
	 */
	private void bindDetail2Progress(LinearLayout viewP, final Detail2Progress detailP) {
		// The label for the first progress
		TextView label = (TextView) viewP.findViewById(R.id.id_detail_value1);
		label.setText(detailP.getLabel1());
		// The value for the first progress
		ProgressBar value = (ProgressBar) viewP.findViewById(R.id.id_detail_progress1);
		value.setProgress(detailP.getValue1());

		// The label for the second progress
		label = (TextView) viewP.findViewById(R.id.id_detail_value2);
		label.setText(detailP.getLabel2());
		// The value for the first progress
		value = (ProgressBar) viewP.findViewById(R.id.id_detail_progress2);
		value.setProgress(detailP.getValue2());
	}

	/**
	 * Bind torrent's data with widget for a DetailProgress instance
	 * 
	 * @param viewP
	 * @param torrentP
	 */
	private void bindDetailProgress(LinearLayout viewP, final DetailProgress detailP) {
		// The label for the first progress
		TextView label = (TextView) viewP.findViewById(R.id.id_detail_value);
		label.setText(detailP.getLabel());
		// The value for the first progress
		ProgressBar value = (ProgressBar) viewP.findViewById(R.id.id_detail_progress);
		value.setProgress(detailP.getValue());
	}

	/**
	 * Click on a item
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Detail detail = details.get(position);
		if (detail != null) {
			if (detail.getAction() != null) {
				detail.executeAction();
			}
		}
	}
}
