package com.bigpupdev.synodroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.BadTokenException;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.bigpupdev.synodroid.R;
import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.server.TorrentDownloadAndAdd;
import com.bigpupdev.synodroid.utils.SearchViewBinder;
import com.bigpupdev.synodroid.utils.SynodroidSearchSuggestion;

public class SearchFragment extends SynodroidFragment {
	private static final String PREFERENCE_GENERAL = "general_cat";
	private static final String PREFERENCE_SEARCH_SOURCE = "general_cat.search_source";
	private static final String PREFERENCE_SEARCH_ORDER = "general_cat.search_order";
	private static final String TORRENT_SEARCH_URL_DL = "http://code.google.com/p/transdroid-search/downloads/list";
	private static final String TORRENT_SEARCH_URL_DL_MARKET = "market://details?id=org.transdroid.search";
	
	private final String[] from = new String[] { "NAME", "SIZE", "ADDED", "LEECHERS", "SEEDERS", "TORRENTURL" };
	private final int[] to = new int[] { R.id.result_title, R.id.result_size, R.id.result_date, R.id.result_leechers, R.id.result_seeds, R.id.result_url };
	private TextView emptyText;
	private Button btnInstall;
	private Button btnAlternate;

	private Spinner SpinnerSource, SpinnerSort;
	private ArrayAdapter<CharSequence> AdapterSource, AdapterSort;

	private String[] SortOrder = { "Combined", "BySeeders" };
	private String lastSearch = "";
	private ListView resList;
	
	
	/**
	 * Activity creation
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		final Activity a = getActivity();
		if (savedInstanceState != null)
			lastSearch = savedInstanceState.getString("lastSearch");
		else
			lastSearch = "";
		
		RelativeLayout searchContent = (RelativeLayout) inflater.inflate(R.layout.torrent_search, null, false);
		resList = (ListView) searchContent.findViewById(R.id.resList);

		emptyText = (TextView) searchContent.findViewById(R.id.empty);

		btnInstall = (Button) searchContent.findViewById(R.id.btnTorSearchInst);
		btnAlternate = (Button) searchContent.findViewById(R.id.btnTorSearchInstAlternate);
		
		SpinnerSource = (Spinner) searchContent.findViewById(R.id.srcSpinner);
		SpinnerSort = (Spinner) searchContent.findViewById(R.id.sortSpinner);

		AdapterSource = new ArrayAdapter<CharSequence>(a, android.R.layout.simple_spinner_item);
		AdapterSource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SpinnerSource.setAdapter(AdapterSource);

		AdapterSort = new ArrayAdapter<CharSequence>(a, android.R.layout.simple_spinner_item);
		AdapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SpinnerSort.setAdapter(AdapterSort);

		SharedPreferences preferences = a.getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
		String pref_src = preferences.getString(PREFERENCE_SEARCH_SOURCE, "");
		String pref_order = preferences.getString(PREFERENCE_SEARCH_ORDER, "");

		int lastOrder = 0;
		int lastSource = 0;

		for (int i = 0; i < SortOrder.length; i++) {
			if (pref_order.equals(SortOrder[i])) {
				lastOrder = i;
			}
			AdapterSort.add(SortOrder[i]);
		}

		// Gather the supported torrent sites
		StringBuilder s = new StringBuilder();
		Cursor sites = getSupportedSites();
		if (sites != null) {
			if (sites.moveToFirst()) {
				int i = 0;
				do {
					s.append(sites.getString(1));
					s.append("\n");
					if (pref_src.equals(sites.getString(1))) {
						lastSource = i;
					}
					AdapterSource.add(sites.getString(1));
					i++;
				} while (sites.moveToNext());
			}
			emptyText.setText(getString(R.string.sites) + "\n" + s.toString());
			btnInstall.setVisibility(Button.GONE);
			btnAlternate.setVisibility(Button.GONE);
			resList.setVisibility(ListView.GONE);

		} else {
			SpinnerSort.setVisibility(Spinner.GONE);
			SpinnerSource.setVisibility(Spinner.GONE);
			resList.setVisibility(ListView.GONE);
			emptyText.setText(R.string.provider_missing);
			btnInstall.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent goToMarket = null;
					goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(TORRENT_SEARCH_URL_DL_MARKET));
					try {
						startActivity(goToMarket);
					} catch (Exception e) {
						AlertDialog.Builder builder = new AlertDialog.Builder(a);
						// By default the message is "Error Unknown"
						builder.setMessage(R.string.err_nomarket);
						builder.setTitle(getString(R.string.connect_error_title)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
						AlertDialog errorDialog = builder.create();
						try {
							errorDialog.show();
						} catch (BadTokenException ex) {
							// Unable to show dialog probably because intent has been closed. Ignoring...
						}
					}

				}
			});
			btnAlternate.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent goToMarket = null;
					goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(TORRENT_SEARCH_URL_DL));
					try {
						startActivity(goToMarket);
					} catch (Exception e) {
						AlertDialog.Builder builder = new AlertDialog.Builder(a);
						// By default the message is "Error Unknown"
						builder.setMessage(R.string.err_nobrowser);
						builder.setTitle(getString(R.string.connect_error_title)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
						AlertDialog errorDialog = builder.create();
						try {
							errorDialog.show();
						} catch (BadTokenException ex) {
							// Unable to show dialog probably because intent has been closed. Ignoring...
						}
					}

				}
			});
		}
	
		SpinnerSource.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String source = ((TextView) arg1).getText().toString();
				SharedPreferences preferences = a.getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
				preferences.edit().putString(PREFERENCE_SEARCH_SOURCE, source).commit();
				if (!lastSearch.equals("")) {
					new TorrentSearchTask().execute(lastSearch);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		SpinnerSort.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String order = ((TextView) arg1).getText().toString();
				SharedPreferences preferences = a.getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
				preferences.edit().putString(PREFERENCE_SEARCH_ORDER, order).commit();
				if (!lastSearch.equals("")) {
					new TorrentSearchTask().execute(lastSearch);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		SpinnerSource.setSelection(lastSource);
		SpinnerSort.setSelection(lastOrder);
		
		resList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				final RelativeLayout rl = (RelativeLayout) arg1;
				Dialog d = new AlertDialog.Builder(a).setTitle(R.string.dialog_title_confirm).setMessage(R.string.dialog_message_confirm_add).setNegativeButton(android.R.string.no, null).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						TextView tv = (TextView) rl.findViewById(R.id.result_url);
						String url = tv.getText().toString();
						new TorrentDownloadAndAdd(SearchFragment.this).execute(url);
					}
				}).create();
				try {
					d.show();
				} catch (BadTokenException e) {
					Log.e(Synodroid.DS_TAG, e.getMessage());
					// Unable to show dialog probably because intent has been closed. Ignoring...
				}
			}
		});
		return searchContent;
	}
	
	private Cursor getSupportedSites() {
		// Create the URI of the TorrentSitesProvider
		String uriString = "content://org.transdroid.search.torrentsitesprovider/sites";
		Uri uri = Uri.parse(uriString);
		// Then query all torrent sites (no selection nor projection nor sort):
		return getActivity().managedQuery(uri, null, null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		Intent intent = this.getActivity().getIntent();
		Activity a = this.getActivity();
		String action = intent.getAction();
		
		if (Intent.ACTION_SEARCH.equals(action)) {
			if (getSupportedSites() != null) {
				if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
				}
				String searchKeywords = intent.getStringExtra(SearchManager.QUERY);
				lastSearch = searchKeywords;
				if (!searchKeywords.equals("")) {
					new TorrentSearchTask().execute(searchKeywords);
					SearchRecentSuggestions suggestions = new SearchRecentSuggestions(a, SynodroidSearchSuggestion.AUTHORITY, SynodroidSearchSuggestion.MODE);
					suggestions.saveRecentQuery(searchKeywords, null);
				} else {
					emptyText.setText(R.string.no_keyword);
					emptyText.setVisibility(TextView.VISIBLE);
					resList.setVisibility(TextView.GONE);
				}
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(a);
				builder.setMessage(R.string.err_provider_missing);
				builder.setTitle(getString(R.string.connect_error_title)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog errorDialog = builder.create();
				try {
					errorDialog.show();
				} catch (BadTokenException e) {
					// Unable to show dialog probably because intent has been closed. Ignoring...
				}
			}

		}
		else if (intent.getBooleanExtra("start_search", false)){
			getActivity().onSearchRequested();
		}
	}
	
	private class TorrentSearchTask extends AsyncTask<String, Void, Cursor> {

		@Override
		protected void onPreExecute() {
			emptyText.setVisibility(TextView.VISIBLE);
			emptyText.setText(getString(R.string.searching) + " " + lastSearch);
			resList.setVisibility(ListView.GONE);
			resList.setAdapter(null);
		}

		@Override
		protected Cursor doInBackground(String... params) {
			try {
				// Create the URI of the TorrentProvider
				String uriString = "content://org.transdroid.search.torrentsearchprovider/search/" + params[0];
				Uri uri = Uri.parse(uriString);
				// Then query for this specific record (no selection nor projection nor sort):
				SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCE_GENERAL, Activity.MODE_PRIVATE);
				String pref_src = preferences.getString(PREFERENCE_SEARCH_SOURCE, "");
				String pref_order = preferences.getString(PREFERENCE_SEARCH_ORDER, "");

				return getActivity().managedQuery(uri, null, "SITE = ?", new String[] { pref_src }, pref_order);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Cursor cur) {
			try{
				if (cur == null) {
					emptyText.setVisibility(TextView.VISIBLE);
					resList.setVisibility(ListView.GONE);
					emptyText.setText(getString(R.string.no_results) + " " + lastSearch);
				} else {// Show results in the list
					if (cur.getCount() == 0) {
						emptyText.setVisibility(TextView.VISIBLE);
						resList.setVisibility(ListView.GONE);
						emptyText.setText(getString(R.string.no_results) + " " + lastSearch);
					} else {
						emptyText.setVisibility(TextView.GONE);
						resList.setVisibility(ListView.VISIBLE);
						SimpleCursorAdapter cursor = new SimpleCursorAdapter(getActivity(), R.layout.search_row, cur, from, to);
						cursor.setViewBinder(new SearchViewBinder());
						resList.setAdapter(cursor);
					}
				}
			}
			catch (Exception e){
				Log.d(Synodroid.DS_TAG, "Activity was killed before the searchresult came back...");
			}
		}

	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putString("lastSearch", lastSearch);
		
		// etc.
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void handleMessage(Message msgP) {}
}
