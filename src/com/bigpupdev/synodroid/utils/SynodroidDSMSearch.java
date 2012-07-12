package com.bigpupdev.synodroid.utils;

import java.util.List;

import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.server.SimpleSynoServer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

public class SynodroidDSMSearch extends ContentProvider {
	public static final String PROVIDER_NAME = "com.bigpupdev.synodroid.utils.SynodroidDSMSearch";
	public static final String CONTENT_URI = "content://" + PROVIDER_NAME + "/search/";
	public static final String[] COLS = new String[] { "_ID", "NAME", "TORRENTURL", "DETAILSURL", "SIZE", "ADDED", "SEEDERS", "LEECHERS" };
	
	private static final int SEARCH_TERM = 1;
	

	
    // Static intialization of the URI matcher
    private static final UriMatcher uriMatcher;
    static {
            uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            uriMatcher.addURI(PROVIDER_NAME, "search/*", SEARCH_TERM);
    }
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
        case SEARCH_TERM:
                return "vnd.android.cursor.dir/vnd.synodroid.dsmsearchitem";
        default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		MatrixCursor cursor = new MatrixCursor(COLS);
		String term = "";
		ServerParam param = new ServerParam(selectionArgs);
		SimpleSynoServer server = new SimpleSynoServer();
		
		server.setParams(param);
		if (server.getDsmVersion().greaterThen(DSMVersion.VERSION3_0)){
    		SortOrder order = SortOrder.BySeeders; // Use BySeeders as default
			if (uriMatcher.match(uri) == SEARCH_TERM) {
	            term = uri.getPathSegments().get(1);
			}
			if (sortOrder != null) {
	            order = SortOrder.fromCode(sortOrder);
	            if (order == null) {
	                    throw new RuntimeException(sortOrder + " is not a valid sort order. " + 
	                            "Only BySeeders and Combined are supported.");
	            }
			}
			
			if (param.getDbg()) Log.i(Synodroid.DS_TAG, "DSMSearch: Query received. Term: '" + term + "' -- Params: " + param.toString() + " -- Order: " + order.toString());
	        
			if (!term.equals("")) {
				// Perform the actual search
	            try {
            		List<SearchResult> results = server.getDSMHandlerFactory().getDSHandler().search(term, order, param.getStart(), param.getLimit());
                    // Return the results as MatrixCursor
                    for (SearchResult result : results) {
                            Object[] values = new Object[8];
                            values[0] = result.getID();
                            values[1] = result.getTitle();
                            values[2] = result.getTorrentUrl();
                            values[3] = result.getDetailsUrl();
                            values[4] = result.getSize();
                            values[5] = result.getAddedDate();
                            values[6] = result.getSeeds();
                            values[7] = result.getLeechers();
                            cursor.addRow(values);
                    }
	            } catch (Exception e) {
                    // Log the error and stack trace, but also throw an explicit run-time exception for clarity 
            		if (param.getDbg())Log.e(Synodroid.DS_TAG, "DSMSearch: Search provider error", e);
	                throw new RuntimeException(e.toString());
	            }
	
			}
		}
		// Register the content URI for changes (although update() isn't supported)
        //cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
}
