package com.github.searchbadger.core;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.SearchGenerator;
import com.github.searchbadger.view.ContactsActivity;
import com.github.searchbadger.view.SearchResultActivity;

public class SearchBadgerController {
	private static final SearchBadgerController instance = 
		new SearchBadgerController();
	private final SearchBadgerModel model = SearchBadgerModel.getInstance();

	public static final SearchBadgerController getInstance() {
		return instance;
	}

	public class SearchButtonListener implements View.OnClickListener {
		private SearchGenerator srchGen;
		public SearchButtonListener(SearchGenerator srchGen) {
			this.srchGen = srchGen;
		}
		public void onClick(View v) {
			model.search(srchGen.generateSearch());
			Intent resActIntent = new Intent(v.getContext(), 
					SearchResultActivity.class);
			v.getContext().startActivity(resActIntent);
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
		private SearchGenerator srchGen;
		public ContactButtonListener(SearchGenerator gen) {
			srchGen = gen;
		}
		public void onClick(View v) {
		
			// start the select contact activity
			Context context = v.getContext();
			Intent intent = new Intent();
			intent.setClass(context, ContactsActivity.class);
			intent.putParcelableArrayListExtra("messageSources", (ArrayList<MessageSource>)srchGen.getMessageSources());
			context.startActivity(intent);
		}
	}


	// This is currently implemented in ThreadListFragment and SearchResultListFragment
	public class ResultSelected implements View.OnClickListener {
		public void onClick(View v) {

		}
	}
	
	

		
		

}
