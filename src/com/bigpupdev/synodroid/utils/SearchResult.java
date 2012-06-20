package com.bigpupdev.synodroid.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.net.Uri;

public class SearchResult {

	final private int id;
	final private String title;
    final private String torrentUrl;
    final private String detailsUrl;
    final private String size;
    final private String added;
    final private int seeds;
    final private int leechers;

    public int getID() { return id; }
    public String getTitle() { return title; }
    public String getTorrentUrl() { return torrentUrl; }
    public String getDetailsUrl() { return detailsUrl; }
    public String getSize() { return size; }
    public String getAddedDate() { return added; }
    public int getSeeds() { return seeds; }
    public int getLeechers() { return leechers; }
    
    public SearchResult(int id, String title, String torrentUrl, String detailsUrl, String size, String added, int seeds, int leechers) throws ParseException {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		this.id = id;
		this.title = title;
    	this.torrentUrl = torrentUrl;
        this.detailsUrl = detailsUrl;
        this.size = size;
        String temp = null;
        try{
        	temp = format.parse(added).toLocaleString();
        }
        catch (ParseException e){
        	temp = added;
        }
        this.added = temp;
        this.seeds = seeds;
        this.leechers = leechers;
    }

    public Uri getDetailsUri() {
    	return Uri.parse(getDetailsUrl());
    }

    public Uri getTorrentUri() {
        return Uri.parse(getTorrentUrl());
    }
    
    @Override
    public String toString() {
        return title;
    }
    
}