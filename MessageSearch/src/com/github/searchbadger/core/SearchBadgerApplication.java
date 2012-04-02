package com.github.searchbadger.core;

import android.app.Application;
import android.content.Context;

import com.github.searchbadger.util.SearchModel;

public class SearchBadgerApplication extends Application{

    private static Context context;
    private static SearchModel searchModel;

    public void onCreate(){
        super.onCreate();
        SearchBadgerApplication.context = getApplicationContext();
        searchModel = new SearchBadgerModel();
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
}
