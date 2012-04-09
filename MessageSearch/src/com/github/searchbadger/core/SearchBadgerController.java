package com.github.searchbadger.core;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SearchModel;
import com.github.searchbadger.view.ContactsActivity;
import com.github.searchbadger.view.SearchActivity;
import com.github.searchbadger.view.SearchResultActivity;

public class SearchBadgerController {
	private static final SearchBadgerController instance = 
		new SearchBadgerController();
	private final SearchModel model = SearchBadgerApplication.getSearchModel();

	public static final SearchBadgerController getInstance() {
		return instance;
	}

	public class SearchButtonListener implements View.OnClickListener {
		private SearchActivity srchGen;
		public SearchButtonListener(SearchActivity srchGen) {
			this.srchGen = srchGen;
		}
		public void onClick(View v) {
			// check if we can perform the search
			if(srchGen.isFacebookSelectedAndNotReady() == true) return;
			
			// make sure a source was selected
			final Search filter = srchGen.generateSearch();
			if(filter.getSources().size() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setMessage("Please select a source and try again.")
				       .setTitle("No source was selected")
				       .setIcon(android.R.drawable.ic_dialog_alert)
				       .setCancelable(false)
				       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
				return;
			}
			
			// show progress bar
			final ProgressDialog dialog = ProgressDialog.show(srchGen, "", 
                    "Searching. Please wait...", true);
			 
			Thread thread = new Thread(new Runnable() {
				public void run() {

					try {
						// perform search
						model.search(filter);
						
						srchGen.runOnUiThread(new Runnable() {
							public void run() {
								// hide the progress bar
								dialog.dismiss();
								
								// show search results activity
								Intent resActIntent = new Intent(srchGen, 
										SearchResultActivity.class);
								srchGen.startActivity(resActIntent);
							}
						});
					
				    } catch (Exception e) {
				    }
				}

			});
			thread.start();

			
		}
	}

	public final class StarredMessageListener implements View.OnClickListener {
		public void onClick(View v) {
		}
	}

	public final class RecentSearchListener implements View.OnClickListener {
		public void onClick(View v) {
		}
	}

	public final class ContactButtonListener implements View.OnClickListener {
		private SearchActivity srchGen;
		public ContactButtonListener(SearchActivity gen) {
			srchGen = gen;
		}
		public void onClick(View v) {
			// check if we can display the contact
			if(srchGen.isFacebookSelectedAndNotReady() == true) return;
		
			// start the select contact activity
			Context context = v.getContext();
			Intent intent = new Intent();
			intent.setClass(context, ContactsActivity.class);
			intent.putParcelableArrayListExtra(SearchActivity.INTENT_DATA_SOURCES, 
					(ArrayList<MessageSource>)srchGen.getMessageSources());
			intent.putParcelableArrayListExtra(SearchActivity.INTENT_DATA_CONTACTS, 
					(ArrayList<Contact>)srchGen.getSelectedContacts());
			srchGen.startActivityForResult(intent, SearchActivity.PICK_CONTACT_REQUEST);
		}
	}


	// This is currently implemented in ThreadListFragment and SearchResultListFragment
	public class ResultSelected implements View.OnClickListener {
		public void onClick(View v) {

		}
	}
	
	

		
		

}
