package com.bigpupdev.synodroid.utils;

import android.net.Uri;

public class ServerParam {
	private String dsm_version = "";
	private String cookie = "";
	private String url = "";
	private boolean dbg = false;
	private int start = 0;
	private int limit = 0;
	
	public String getDSMVersion() { return dsm_version; }
    public String getCookie() { return cookie; }
    public String getUrl() { return url; }
    public boolean getDbg() { return dbg; }
    public int getStart() { return start; }
    public int getLimit() { return limit; }
    
	public ServerParam(String[] selectionArgs){
		if (selectionArgs.length == 6){
			this.dsm_version = selectionArgs[0];
			this.cookie = selectionArgs[1];
			this.url = selectionArgs[2];
			this.dbg = selectionArgs[3]=="true";
			this.start = Integer.parseInt(selectionArgs[4]);
			this.limit = Integer.parseInt(selectionArgs[5]);
		}
		else{
			throw new RuntimeException("Wrong number of server configuration arguments.");
		}
	}
	
	public Uri getUri(){
		return Uri.parse(getUrl());	
	}
	
	public String toString(){
		return "{ dsm: "+dsm_version+", cookie: " +cookie+", uri: "+url+", dbg: "+dbg+", start: "+start+", limit: "+limit+" }";
	}
}
