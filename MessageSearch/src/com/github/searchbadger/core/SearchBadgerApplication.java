package com.github.searchbadger.core;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.github.searchbadger.util.FacebookHelper;
import com.github.searchbadger.util.SearchModel;

public class SearchBadgerApplication extends Application{

    private static Context context;
    private static SearchModel searchModel;
    private static FacebookHelper facebookHelper;

    public void onCreate(){
        super.onCreate();
        SearchBadgerApplication.context = getApplicationContext();
        if (this.getApplicationContext() == null) {
        	SearchBadgerApplication.context = getBaseContext();
        	Log.d("SearchBadger", "Using base context.");
        } else {
        	Log.d("SearchBadger", "Using application context.");
        }
        searchModel = new SearchBadgerModel();
        facebookHelper = new FacebookHelper();
    }
    
    protected void attachBaseContext(Context base) {
    	super.attachBaseContext(base);
    	SearchBadgerApplication.context = this.getApplicationContext();
    	if (this.getApplicationContext() == null) {
        	Log.d("SearchBadger", "Attaching new base context!");
        	SearchBadgerApplication.context = getBaseContext();
    	} else {
    		Log.d("SearchBadger", "Attaching new application base context!");
    	}
    }

    public static Context getAppContext() {
        return SearchBadgerApplication.context;
    }
    
    public static void setSearchModel(SearchModel model) {
    	searchModel = model;
    }
    
    public static SearchModel getSearchModel() {
    	return searchModel;
    }
    
    public static FacebookHelper getFacebookHelper() {
    	return facebookHelper;
    }
}
