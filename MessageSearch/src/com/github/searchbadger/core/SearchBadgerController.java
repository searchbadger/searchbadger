package com.github.searchbadger.core;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.github.searchbadger.util.*;
import com.github.searchbadger.view.*;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.github.searchbadger.R;

public class MessageSearchController {
	private static final MessageSearchController instance = 
		new MessageSearchController();
	private final MessageSearchModel model = MessageSearchModel.getInstance();

	public static final MessageSearchController getInstance() {
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
			// get the Message sources to pass to ContactsActivity
			List<MessageSource> sources = srchGen.getMessageSources();
			int[] srcs = new int[sources.size()];
			for (int i = 0; i < sources.size(); i++) {
				srcs[i] = sources.get(i).ordinal();
			}
			// start the select contact activity
			Context context = v.getContext();
			Intent intent = new Intent();
			intent.setClass(context, ContactsActivity.class);
			// TODO: make sure this way of passing message sources works
			intent.putExtra("messageSources", srcs);
			context.startActivity(intent);
		}
	}



	public class ResultSelected implements View.OnClickListener {
		public void onClick(View v) {

		}
	}
	
	

		
		

}
