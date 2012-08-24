package com.bigpupdev.synodroid.utils;

import com.bigpupdev.synodroid.Synodroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SearchResultsOpenHelper extends SQLiteOpenHelper{
	
	private static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "search_helper";
	public static final String TABLE_CACHE = "cache";
	
	public static final String CACHE_QUERY = "query";
	public static final String CACHE_PROVIDER = "provider";
	public static final String CACHE_ORDER = "search_order";
	public static final String CACHE_ID = "id";
	public static final String CACHE_TITLE = "title";
	public static final String CACHE_TURL = "torrent_url";
	public static final String CACHE_DURL = "details_url";
	public static final String CACHE_SIZE = "size";
	public static final String CACHE_ADDED = "added_date";
	public static final String CACHE_SEED = "seeds";
	public static final String CACHE_LEECH = "leechs";
	
	private static final String DROP_CACHE_TABLE = "DROP TABLE IF EXISTS " + TABLE_CACHE;
	private static final String CREATE_CACHE_TABLE = 
			"CREATE TABLE " + TABLE_CACHE + " (" +
			CACHE_QUERY + " TEXT, " +
			CACHE_PROVIDER + " TEXT, " +
			CACHE_ORDER + " TEXT, " +
			CACHE_ID + " INTEGER, " +
			CACHE_TITLE + " TEXT, " +
			CACHE_TURL + " TEXT, " +
			CACHE_DURL + " TEXT, " +
			CACHE_SIZE + " TEXT, " +
			CACHE_ADDED + " TEXT, " +
			CACHE_SEED + " INTEGER, " +
			CACHE_LEECH + " INTEGER);";

	public SearchResultsOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CACHE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(Synodroid.DS_TAG, "Upgrading database from "+oldVersion +" to " +newVersion+" which will destroy all old data...");
		db.execSQL(DROP_CACHE_TABLE);
		onCreate(db);
	}

}
