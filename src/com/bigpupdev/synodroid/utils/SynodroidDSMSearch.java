package com.bigpupdev.synodroid.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.protocol.DSMHandlerFactory;
import com.bigpupdev.synodroid.protocol.QueryBuilder;
import com.bigpupdev.synodroid.protocol.https.AcceptAllHostNameVerifier;
import com.bigpupdev.synodroid.protocol.https.AcceptAllTrustManager;

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
	private static final int MAX_LOOP = 5;
	
	static {
		SSLContext sc;
		try {
			sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new AcceptAllTrustManager() }, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new AcceptAllHostNameVerifier());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public JSONObject sendJSONRequest(String url, String requestP, String methodP, ServerParam param) throws Exception {
		HttpURLConnection con = null;
		OutputStreamWriter wr = null;
		BufferedReader br = null;
		StringBuffer sb = null;
		try {

			// For some reason in Gingerbread I often get a response code of -1.
			// Here I retry for a maximum of MAX_RETRY to send the request and it usually succeed at the second try...
			int retry = 0;
			int MAX_RETRY = 2;
			while (retry <= MAX_RETRY) {
				try{
					// Create the connection
					con = ServiceHelper.createConnection(url, requestP, methodP, param.getDbg(), param.getCookie(), param.getUrl());
					// Add the parameters
					wr = new OutputStreamWriter(con.getOutputStream());
					wr.write(requestP);
					// Send the request
					wr.flush();
					wr.close();
	
					// Now read the reponse and build a string with it
					br = new BufferedReader(new InputStreamReader(con.getInputStream()));
					sb = new StringBuffer();
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					br.close();
					// Verify is response if not -1, otherwise take reason from the header
					if (con.getResponseCode() == -1) {
						retry++;
						if (param.getDbg()) Log.d(Synodroid.DS_TAG, "Response code is -1 (retry: " + retry + ")");
					} else {
						if (param.getDbg()) Log.d(Synodroid.DS_TAG, "Response is: " + sb.toString());
						JSONObject respJSO = new JSONObject(sb.toString());
						return respJSO;
					}
				}catch (Exception e){
					if (param.getDbg()) Log.e(Synodroid.DS_TAG, "Caught exception while contacting the server, retying...", e);
					retry ++;
				}
				finally{
					con.disconnect();
				}
				
			}
			throw new Exception("Failed to read response from server. Please reconnect!");
		}
		// Special for SSL Handshake failure
		catch (IOException ioex) {
			if (param.getDbg()) Log.e(Synodroid.DS_TAG, "Unexpected error", ioex);
			String msg = ioex.getMessage();
			if (msg != null && msg.indexOf("SSL handshake failure") != -1) {
				// Don't need to translate: the opposite message (HTTP on a SSL port) is in english and come from the server
				throw new Exception("SSL handshake failure.\n\nVerify if you don't speak HTTPS to a standard server port.\n");
			} else {
				throw ioex;
			}
		}
		// Unexpected exception
		catch (Exception ex) {
			if (param.getDbg()) Log.e(Synodroid.DS_TAG, "Unexpected error", ex);
			throw ex;
		}
		// Finally close everything
		finally {
			if (con != null) {
				con.disconnect();
			}
			wr = null;
			br = null;
			sb = null;
			con = null;
		}
	}
	
	private List<SearchResult> search(String query, SortOrder order, ServerParam param) throws Exception{
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		DSMVersion vers = DSMVersion.titleOf(param.getDSMVersion());
		if (vers == null) {
			vers = DSMVersion.VERSION2_2;
		}
		if (vers.greaterThen(DSMVersion.VERSION3_1)){
		
			DSMHandlerFactory dsm = DSMHandlerFactory.getFactory(vers, null, param.getDbg());
			
			String url = dsm.getDSHandler().getSearchUrl();
			QueryBuilder builder = new QueryBuilder().add("action", "search").add("query", query);
			JSONObject json = null;
			json = sendJSONRequest(url, builder.toString(), "GET", param);
			if (json != null){
				if (json.getBoolean("success") && json.getBoolean("running")){
					String taskid = json.getString("taskid");
					boolean stop = false;
					QueryBuilder rBuilder = new QueryBuilder().add("start", String.valueOf(param.getStart())).add("limit", String.valueOf(param.getLimit())).add("action", "query").add("dir", "DESC").add("sort", order.equals(SortOrder.BySeeders)?"seeds":"date").add("taskid", taskid).add("categories", "1").add("category", "_allcat_");
					int loop = 0;
					while(!stop){
						json = sendJSONRequest(url, rBuilder.toString(), "GET", param);
						if (json != null ){
							if ( json.getBoolean("success") && ( !json.getBoolean("running") || loop == MAX_LOOP )){
								JSONArray items = json.getJSONArray("items");
								for (int i = 0; i < items.length(); i++){
									JSONObject item = items.getJSONObject(i);
									SearchResult sr = new SearchResult(item.getInt("id"), item.getString("title"), item.getString("dlurl"), item.getString("page"), item.getString("size"), item.getString("date"), item.getInt("seeds"), item.getInt("leechs"));
									results.add(sr);
								}
								stop = true;
							}
							else{
								loop ++;
								Thread.sleep(2000);
							}
						}
						else{
							stop = true;
						}
					}
				}
			}
		}
		
		return results;
	}
	
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
		
		Log.d(Synodroid.DS_TAG, 
                "Term: '" + term + "' Param: " + param.toString() + " Order: " + order.toString());
        
		if (!term.equals("")) {

            // Perform the actual search
            try {
                    List<SearchResult> results = search(term, order, param);
                    // Return the results as MatrixCursor
                    for (SearchResult result : results) {
                            Object[] values = new Object[8];
                            values[0] = result.getID();
                            values[1] = result.getTitle();
                            values[2] = result.getTorrentUrl();
                            values[3] = result.getDetailsUrl();
                            values[4] = result.getSize();
                            values[5] = result.getAddedDate() != null? result.getAddedDate().getTime(): -1;
                            values[6] = result.getSeeds();
                            values[7] = result.getLeechers();
                            cursor.addRow(values);
                    }
            } catch (Exception e) {
                    // Log the error and stack trace, but also throw an explicit run-time exception for clarity 
                    Log.e(Synodroid.DS_TAG, "Search provider error", e);
                    throw new RuntimeException(e.toString());
            }

		}
		// Register the content URI for changes (although update() isn't supported)
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
}
