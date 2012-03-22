package com.github.searchbadger.core;

import android.app.Application;
import android.content.Context;

public class MessageSearchApplication extends Application{

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MessageSearchApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MessageSearchApplication.context;
    }
}
