package com.github.searchbadger.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SearchBadgerPreferences {
	private static final String APP_SHARED_PREFS = "search_badger_prefs"; 
    private static final String FACEBOOK_TOKEN = "facebook_access_token";
    private static final String FACEBOOK_EXPIRES = "facebook_expires_in";
    private static final String SEARCH_RESULT_MAX = "search_result_max";
    private static final String NUM_MESSAGE_PER_THREAD = "num_message_per_thread";
    
	
    private SharedPreferences sharedPrefs;
    private Editor editor;
    
    private static final SearchBadgerPreferences instance = new SearchBadgerPreferences();
	public static final SearchBadgerPreferences getInstance() {
		return instance;
	}

    private SearchBadgerPreferences()
    {
    	Context context = SearchBadgerApplication.getAppContext();
        this.sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.editor = sharedPrefs.edit();
    }

    public void saveFacebookToken(String token) {
        editor.putString(FACEBOOK_TOKEN, token);
        editor.commit();
    }
    
    public void saveFacebookExpires(long expires) {
        editor.putLong(FACEBOOK_EXPIRES, expires);
        editor.commit();
    }
    
    public String getFacebookToken() {
    	return sharedPrefs.getString(FACEBOOK_TOKEN, null);
    }
    
    public long getFacebookExpires() {
    	return sharedPrefs.getLong(FACEBOOK_EXPIRES, 0);
    }

    public void saveSearchResultMax(long max) {
        editor.putLong(SEARCH_RESULT_MAX, max);
        editor.commit();
    }
    
    public void saveNumMessagePerThread(long number) {
        editor.putLong(NUM_MESSAGE_PER_THREAD, number);
        editor.commit();
    }

    public long getSearchResultMax() {
    	return sharedPrefs.getLong(SEARCH_RESULT_MAX, 100);
    }
    
    public long getNumMessagePerThread() {
    	return sharedPrefs.getLong(NUM_MESSAGE_PER_THREAD, 10);
    }
}
