package com.github.searchbadger.view;


import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.searchbadger.R;

public class MainTabActivity extends TabActivity {
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.tabs_layout);

	        Resources res = getResources(); // Resource object to get Drawables
	        TabHost tabHost = getTabHost();  // The activity TabHost
	        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	        Intent intent;  // Reusable Intent for each tab

	        // Create an Intent to launch an Activity for the tab (to be reused)
	        intent = new Intent().setClass(this, SearchActivity.class);

	        // Initialize a TabSpec for each tab and add it to the TabHost
	        spec = tabHost.newTabSpec("search").setIndicator("Search",
                    	  res.getDrawable(R.drawable.tabicon_search))
	                      .setContent(intent);
	        tabHost.addTab(spec);

	        // Do the same for the other tabs
	        intent = new Intent().setClass(this, RecentSearchActivity.class);
	        spec = tabHost.newTabSpec("recent").setIndicator("Recent",
              	  res.getDrawable(R.drawable.tabicon_recent))
	                      .setContent(intent);
	        tabHost.addTab(spec);

	        intent = new Intent().setClass(this, StarredMessagesActivity.class);
	        spec = tabHost.newTabSpec("starred").setIndicator("Starred",
              	          res.getDrawable(R.drawable.tabicon_star))
	                      .setContent(intent);
	        tabHost.addTab(spec);

	        tabHost.setCurrentTab(0);
	        
	        // TODO Disable tabs
	        //tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(false);
	        //tabHost.getTabWidget().getChildTabViewAt(2).setEnabled(false);
	        
	        // holo seems to hide the icons on the tab so force them to show
	        if (Build.VERSION.SDK_INT >= 11 /*HONEYCOMB*/ ) {
	        	View tab;
	        	ImageView image;
	        	TextView text;

	        	tab = tabHost.getTabWidget().getChildTabViewAt(0);
	        	image = (ImageView) tab.findViewById(android.R.id.icon);
		        if(image != null && image.getVisibility() == View.GONE) {
		        	image.setImageDrawable(res.getDrawable(R.drawable.tabicon_search));
		        	image.setVisibility(View.VISIBLE);
		        }
		        text = (TextView) tab.findViewById(android.R.id.title);
		        text.setSingleLine();

		        tab = tabHost.getTabWidget().getChildTabViewAt(1);
		        image = (ImageView) tab.findViewById(android.R.id.icon);
		        if(image != null && image.getVisibility() == View.GONE) {
		        	image.setImageDrawable(res.getDrawable(R.drawable.tabicon_recent));
		        	image.setVisibility(View.VISIBLE);
		        }
		        text = (TextView) tab.findViewById(android.R.id.title);
		        text.setSingleLine();

		        tab = tabHost.getTabWidget().getChildTabViewAt(2);
		        image = (ImageView) tab.findViewById(android.R.id.icon);
		        if(image != null && image.getVisibility() == View.GONE) {
		        	image.setImageDrawable(res.getDrawable(R.drawable.tabicon_star));
		        	image.setVisibility(View.VISIBLE);
		        }
		        text = (TextView) tab.findViewById(android.R.id.title);
		        text.setSingleLine();
	        }
	    }
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.default_menu, menu);
		    return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle item selection
		    switch (item.getItemId()) {
		    
		        case R.id.menu_settings:
		        	ShowSettings();
		            return true;
		        case R.id.menu_help:
		        	ShowHelp();
		            return true;
		        case R.id.menu_about:
		        	ShowAbout();
		            return true;
		            
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		}
		
		public void ShowSettings() {
			Intent intent = new Intent(this, AccountsActivity.class);
			startActivity(intent);
		}
		
		public void ShowHelp() {
			Intent intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
		}
		
		public void ShowAbout() {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		}
}
