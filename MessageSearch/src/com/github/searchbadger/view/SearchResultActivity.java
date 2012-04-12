package com.github.searchbadger.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.searchbadger.R;

public class SearchResultActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_layout);
        
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
