package com.bigpupdev.synodroid.utils;

import android.content.SearchRecentSuggestionsProvider;

public class SynodroidSearchSuggestion extends SearchRecentSuggestionsProvider {
	public final static String AUTHORITY = "com.bigpupdev.synodroid.utils.SynodroidSearchSuggestion";
	public final static int MODE = DATABASE_MODE_QUERIES;

	public SynodroidSearchSuggestion() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
