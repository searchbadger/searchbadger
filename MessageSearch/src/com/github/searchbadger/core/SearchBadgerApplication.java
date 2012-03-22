package com.github.searchbadger.core;

import android.app.Application;
import android.content.Context;

public class SearchBadgerApplication extends Application{

    private static Context context;

    public void onCreate(){
        super.onCreate();
        SearchBadgerApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return SearchBadgerApplication.context;
    }
}
