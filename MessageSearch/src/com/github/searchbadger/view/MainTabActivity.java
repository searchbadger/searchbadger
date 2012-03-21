package com.github.searchbadger.view;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.github.searchbadger.R;

public class MainTabActivity extends TabActivity {
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.tabs_layout);

	        TabHost tabHost = getTabHost();  // The activity TabHost
	        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	        Intent intent;  // Reusable Intent for each tab

	        // Create an Intent to launch an Activity for the tab (to be reused)
	        intent = new Intent().setClass(this, SearchActivity.class);

	        // Initialize a TabSpec for each tab and add it to the TabHost
	        spec = tabHost.newTabSpec("search").setIndicator("Search")
	                      .setContent(intent);
	        tabHost.addTab(spec);

	        // Do the same for the other tabs
	        intent = new Intent().setClass(this, RecentSearchActivity.class);
	        spec = tabHost.newTabSpec("recent").setIndicator("Recent")
	                      .setContent(intent);
	        tabHost.addTab(spec);

	        intent = new Intent().setClass(this, StarredMessagesActivity.class);
	        spec = tabHost.newTabSpec("starred").setIndicator("Starred")
	                      .setContent(intent);
	        tabHost.addTab(spec);

	        tabHost.setCurrentTab(0);
	    }
}
