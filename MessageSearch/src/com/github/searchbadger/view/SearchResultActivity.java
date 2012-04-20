package com.github.searchbadger.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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
	    inflater.inflate(R.menu.menu_thread, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    
	        case R.id.menu_settings:
	        	ShowSettings();
	            return true;
	        case R.id.menu_word_cloud:
	        	ShowWordCloud();
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
	
	public void ShowWordCloud() {

		// Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View threadFrame = findViewById(R.id.thread_view);
        boolean dualPane = threadFrame != null && threadFrame.getVisibility() == View.VISIBLE;
        if(dualPane == false) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("You must select a conversation thread in order to generate a Word Cloud.")
        	       .setNegativeButton("OK", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
        	return;
        }
		
		Intent intent = new Intent(this, WordCloudActivity.class);
		startActivity(intent);
	}
}
