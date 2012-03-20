package com.github.searchbadger.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.github.searchbadger.core.*;
import com.github.searchbadger.util.*;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.wisc.cs.cs638.messagesearch.*;

public class SearchActivity extends Activity implements SearchGenerator {

	static final int DATE_DIALOG_ID = 0;

	private LinearLayout layoutFilterDate;
	private LinearLayout layoutFilterContacts;
	private LinearLayout layoutFilterSentReceived;
	private CheckBox checkBoxFilterDate;
	private CheckBox checkBoxFilterContacts;
	private CheckBox checkBoxFilterSentReceived;
	private Button searchButton;
	private Button contactsButton;
	private ToggleButton smsButton;
	private ToggleButton facebookButton;
	private ToggleButton twitterButton;
	private ToggleButton starButton;
	private Button symbolPoundButton;
	private Button symbolStarButton;
	private Button symbolUnderscoreButton;
	private EditText searchInputText;
	private RadioGroup sendReceiveRadioGroup;
	private RadioGroup radioGroupDate;
	private Button beforeButton;
	private Button afterButton;
	private Button fromButton;
	private Button toButton;
	private TextView contactsText;

	private MessageSearchController controller;
	private MessageSearchModel model;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private int pickerButtonId;
	private DatePickerDialog pickerDate;
	private Date beforeDate;
	private Date afterDate;
	private Date fromDate;
	private Date toDate;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);

		model = MessageSearchModel.getInstance();
		controller = MessageSearchController.getInstance();

		pickerDate = new DatePickerDialog(this, this.new DatePickerSelected(this), 0, 0, 0);

		layoutFilterDate = (LinearLayout) findViewById(R.id.linearFilterDateOptions);
		layoutFilterContacts = (LinearLayout) findViewById(R.id.linearFilterContactsOptions);
		layoutFilterSentReceived = (LinearLayout) findViewById(R.id.linearFilterSentReceivedOptions);
		checkBoxFilterDate = (CheckBox) findViewById(R.id.checkBoxFilterDate);
		checkBoxFilterContacts = (CheckBox) findViewById(R.id.checkBoxFilterContacts);
		checkBoxFilterSentReceived = (CheckBox) findViewById(R.id.checkBoxFilterSentReceived);
		searchButton = (Button) findViewById(R.id.buttonSearch);
		contactsButton = (Button) findViewById(R.id.buttonSelectContacts);
		smsButton = (ToggleButton) findViewById(R.id.toggleButtonTypeSMS);
		facebookButton = (ToggleButton) findViewById(R.id.toggleButtonTypeFacebook);
		twitterButton = (ToggleButton) findViewById(R.id.toggleButtonTypeTwitter);
		starButton = (ToggleButton) findViewById(R.id.toggleButtonTypeStar);
		symbolPoundButton = (Button) findViewById(R.id.buttonSymbolPound);
		symbolStarButton = (Button) findViewById(R.id.buttonSymbolStar);
		symbolUnderscoreButton = (Button) findViewById(R.id.buttonSymbolUnderscore);
		searchInputText = (EditText) findViewById(R.id.editTextSearch);
		sendReceiveRadioGroup = (RadioGroup) findViewById(R.id.radioGroupSentReceived);
		radioGroupDate = (RadioGroup) findViewById(R.id.radioGroupDate);
		beforeButton = (Button) findViewById(R.id.buttonBefore);
		afterButton = (Button) findViewById(R.id.buttonAfter);
		fromButton = (Button) findViewById(R.id.buttonFrom);
		toButton = (Button) findViewById(R.id.buttonTo);
		contactsText = (TextView) findViewById(R.id.textViewSelectContacts);

		// set the onClick events
		checkBoxFilterDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleFilterDate();
			}
		});
		checkBoxFilterContacts.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleFilterContacts();
			}
		});
		checkBoxFilterSentReceived.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleFilterSentReceived();
			}
		});
		searchButton.setOnClickListener(controller.new 
				SearchButtonListener(this));
		contactsButton.setOnClickListener(controller.new 
				ContactButtonListener(this));

		// XXX SearchSourceSelected searchSourceSelected = controller.new SearchSourceSelected(this);
		//		smsButton.setOnClickListener(searchSourceSelected);
		//		facebookButton.setOnClickListener(searchSourceSelected);
		//		twitterButton.setOnClickListener(searchSourceSelected);
		//		starButton.setOnClickListener(searchSourceSelected);

		symbolPoundButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				insertTextSymbol(v);
			}
		});
		symbolStarButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				insertTextSymbol(v);
			}
		});
		symbolUnderscoreButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				insertTextSymbol(v);
			}
		});

		// XXX radioGroupDate.setOnCheckedChangeListener(controller.new DateRangeSelected(this));

		beforeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDatePicker(v);
			}
		});
		afterButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDatePicker(v);
			}
		});
		fromButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDatePicker(v);
			}
		});
		toButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDatePicker(v);
			}
		});

		// update the filters
		radioGroupDate.check(R.id.radioToday);
		toggleFilterDate();
		toggleFilterContacts();
		toggleFilterSentReceived();
		// XXX updateContactFilter();
		// XXX updateSendReceiveFilter();
		updateDateButtons();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// XXX updateTextContacts();
	}


	public int getDatePickerId() {
		return pickerButtonId;
	}

	public void toggleFilterSentReceived() {
		if (checkBoxFilterSentReceived.isChecked())
			layoutFilterSentReceived.setVisibility(View.VISIBLE);
		else
			layoutFilterSentReceived.setVisibility(View.GONE);
	}

	public void toggleFilterContacts() {
		if (checkBoxFilterContacts.isChecked())
			layoutFilterContacts.setVisibility(View.VISIBLE);
		else
			layoutFilterContacts.setVisibility(View.GONE);
	}

	public void toggleFilterDate() {
		if (checkBoxFilterDate.isChecked())
			layoutFilterDate.setVisibility(View.VISIBLE);
		else
			layoutFilterDate.setVisibility(View.GONE);
	}






	public void insertTextSymbol(View v) {

		// determine the button type
		String symbol = "";
		switch (v.getId()) {
		case R.id.buttonSymbolPound:
			symbol = "#";
			break;
		case R.id.buttonSymbolStar:
			symbol = "*";
			break;
		case R.id.buttonSymbolUnderscore:
			symbol = "_";
			break;
		}

		// insert the symbol selected
		// note: the selection start can be the end index if the
		// text was selected backward
		if (searchInputText.hasSelection()) {
			int startSelection = searchInputText.getSelectionStart();
			int endSelection = searchInputText.getSelectionEnd();
			int start = startSelection;
			int end = endSelection;
			if (endSelection < startSelection) {
				start = endSelection;
				end = startSelection;
			}
			String searchText = searchInputText.getText().toString();
			String leftOfSelection = searchText.substring(0, start);
			String rightOfSelection = searchText.substring(end);
			searchText = leftOfSelection + symbol + rightOfSelection;
			searchInputText.setText(searchText);
			//searchInputText.setSelection(start + 1);
			searchInputText.setSelected(false);
		}
		else {
			searchInputText.setText(searchInputText.getText() + symbol);
			searchInputText.setSelected(false);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return pickerDate;
		}
		return null;
	}

	public void showDatePicker(View v) {

		// determine the date picker type
		Calendar cal = Calendar.getInstance();
		switch (v.getId()) {
		case R.id.buttonBefore:
			pickerButtonId = R.id.buttonBefore;
			cal.setTime(beforeDate);
			break;
		case R.id.buttonAfter:
			pickerButtonId = R.id.buttonAfter;
			cal.setTime(afterDate);
			break;
		case R.id.buttonFrom:
			pickerButtonId = R.id.buttonFrom;
			cal.setTime(fromDate);
			break;
		case R.id.buttonTo:
			pickerButtonId = R.id.buttonTo;
			cal.setTime(toDate);
			break;
		default:
			return;
		}

		// update and show the date picker
		pickerDate.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE));
		showDialog(DATE_DIALOG_ID);
	}

	//	public class ContactSelector implements View.OnClickListener {
	//		public void onClick(View v) {
	//
	//			// get the contact object
	//			if (!(v.getParent() instanceof View))
	//				return;
	//			View parentView = (View) v.getParent();
	//			if (!(parentView.getTag() instanceof Contact))
	//				return;
	//			Contact contact = (Contact) parentView.getTag();
	//
	//			// add/remove contact
	//			if (!(v instanceof CheckBox))
	//				return;
	//			CheckBox checkbox = (CheckBox) v;
	//			// TODO remove this
	//			if (checkbox.isChecked())
	//				model.addContact(contact);
	//			else
	//				model.removeContact(contact);
	//
	//			// TODO remove this
	//			Toast.makeText(v.getContext(), model.getContacts().toString(),
	//					Toast.LENGTH_SHORT).show();
	//		}
	//	}
	//
	//	public class SearchSourceSelected implements View.OnClickListener {
	//
	//		private SearchActivity searchActiviy;
	//
	//		public SearchSourceSelected(SearchActivity activity) {
	//			searchActiviy = activity;
	//		}
	//
	//		public void onClick(View v) {
	//			if (!(v instanceof ToggleButton))
	//				return;
	//			ToggleButton button = (ToggleButton) v;
	//
	//			// determine the search type
	//			MessageSource searchSource = null;
	//			switch (v.getId()) {
	//			case R.id.toggleButtonTypeSMS:
	//				searchSource = MessageSource.SMS;
	//				break;
	//			case R.id.toggleButtonTypeFacebook:
	//				searchSource = MessageSource.FACEBOOK;
	//				break;
	//			case R.id.toggleButtonTypeTwitter:
	//				searchSource = MessageSource.TWITTER;
	//				break;
	//			case R.id.toggleButtonTypeStar:
	//				searchSource = MessageSource.STARRED;
	//				break;
	//			}
	//			// TODO: remove this
	//			// add/remove search type
	//			if (button.isChecked())
	//				model.addSearchSource(searchSource);
	//			else
	//				model.removeSearchSource(searchSource);
	//
	//
	//			// disable/enable the contact filter
	//			searchActiviy.updateContactFilter();
	//
	//			// TODO remove this
	//			List<MessageSource> searchSources = model.getSearchSources();
	//			Toast.makeText(v.getContext(), searchSources.toString(),
	//					Toast.LENGTH_SHORT).show();
	//		}
	//	}
	//
	//	public class SendReceiveTypeSelected implements RadioGroup.OnCheckedChangeListener {
	//		public void onCheckedChanged(RadioGroup group, int selectedId) {
	//
	//			// set the send/receive type
	//			switch (selectedId) {
	//			case R.id.radioSent:
	//				model.setType(SendReceiveType.SENT);
	//				break;
	//
	//			case R.id.radioReceived:
	//				model.setType(SendReceiveType.RECEIVED);
	//				break;
	//			}
	//
	//		}
	//	}
	//
	//	public class DateRangeSelected implements RadioGroup.OnCheckedChangeListener {
	//
	//		private SearchActivity searchActiviy;
	//
	//		public DateRangeSelected(SearchActivity activity){
	//			searchActiviy = activity;
	//		}
	//
	//		public void onCheckedChanged(RadioGroup group, int selectedId) {
	//
	//			// return if this is a clear check
	//			if(selectedId == -1) return;
	//
	//			// set the default begin and end date to be today
	//			Calendar cal = Calendar.getInstance();
	//			Date beginDate = new Date();
	//			Date endDate = new Date();
	//			beginDate.setHours(0);
	//			beginDate.setMinutes(0);
	//			beginDate.setSeconds(0);
	//			endDate.setHours(23);
	//			endDate.setMinutes(59);
	//			endDate.setSeconds(59);
	//
	//			// set the begin and end date
	//			switch (selectedId) {
	//			case R.id.radioToday:
	//				break;
	//
	//			case R.id.radioYesterday:
	//				cal.setTime(beginDate);
	//				cal.add(Calendar.DATE, -1);
	//				beginDate = cal.getTime();
	//				break;
	//
	//			case R.id.radioPaskWeek:
	//				cal.setTime(beginDate);
	//				cal.add(Calendar.DATE, -7);
	//				beginDate = cal.getTime();
	//				break;
	//
	//			case R.id.radioPastMonth:
	//				cal.setTime(beginDate);
	//				cal.add(Calendar.DATE, beginDate.getDate() * -1 + 1);
	//				beginDate = cal.getTime();
	//				break;
	//
	//			case R.id.radioBefore:
	//				beginDate = null;
	//				endDate = searchActiviy.getDateBefore();
	//				endDate.setHours(23);
	//				endDate.setMinutes(59);
	//				endDate.setSeconds(59);
	//				break;
	//
	//			case R.id.radioAfter:
	//				beginDate = searchActiviy.getDateAfter();
	//				endDate = null;
	//				beginDate.setHours(0);
	//				beginDate.setMinutes(0);
	//				beginDate.setSeconds(0);
	//				break;
	//
	//			case R.id.radioFrom:
	//				beginDate = searchActiviy.getDateFrom();
	//				endDate = searchActiviy.getDateTo();
	//				beginDate.setHours(0);
	//				beginDate.setMinutes(0);
	//				beginDate.setSeconds(0);
	//				endDate.setHours(23);
	//				endDate.setMinutes(59);
	//				endDate.setSeconds(59);
	//				break;
	//
	//			default:
	//				return;
	//			}
	//
	//			// update the model
	//			model.setBeginDate(beginDate);
	//			model.setEndDate(endDate);
	//
	//		}
	//	}
	//
	//the callback received when the user sets the date in the dialog

	public void setDateBefore(Date date) {
		beforeDate = date;
	}
	public void setDateAfter(Date date) {
		afterDate = date;
	}
	public void setDateFrom(Date date) {
		fromDate = date;
	}
	public void setDateTo(Date date) {
		toDate = date;
	}
	/*
	 * update the text on the before, after, from and to buttons
	 * to match the date picked
	 */
	public void updateDateButtons() {
		beforeButton.setText(beforeDate.toString());
		afterButton.setText(afterDate.toString());
		fromButton.setText(fromDate.toString());
		toButton.setText(toDate.toString());
	}


	public class DatePickerSelected implements DatePickerDialog.OnDateSetListener {

		private SearchActivity searchActivity;

		public DatePickerSelected(SearchActivity activity){
			searchActivity = activity;
		}

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			// update with new date selected
			RadioGroup radioGroupDate  = (RadioGroup) searchActivity.findViewById(R.id.radioGroupDate);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DATE, dayOfMonth);
			switch (searchActivity.getDatePickerId()) {
			case R.id.buttonBefore:
				searchActivity.setDateBefore(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioBefore);
				break;
			case R.id.buttonAfter:
				searchActivity.setDateAfter(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioAfter);
				break;
			case R.id.buttonFrom:
				searchActivity.setDateFrom(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioFrom);
				break;
			case R.id.buttonTo:
				searchActivity.setDateTo(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioFrom);
				break;
			}

			searchActivity.updateDateButtons();


		}
	}


	public Search generateSearch() {
		// TODO Auto-generated method stub
		String text;
		Date begin;
		Date end;
		List<MessageSource> sources; 
		List<Contact> contacts;
		SendReceiveType type;

		// get text
		text = searchInputText.getText().toString();

		// get begin date
		// get end date
		if (checkBoxFilterDate.isChecked()) {
			switch (radioGroupDate.getCheckedRadioButtonId()) {
			case R.id.radioToday:
				
				break;
			case R.id.radioYesterday:
				
				break;
				
			case R.id.radioPaskWeek:
				
				break;
				
			case R.id.radioPastMonth:
				
				break;
				
			case R.id.radioBefore:
				
				break;
				
			case R.id.radioAfter:
				
				break;
				
			case R.id.radioFrom:
				
				break;
				
				// TODO: is R.id. missing a radioTo ??
			//case R.id.
				
				default:
					
					break;
				
			}
		} else {
			begin = null;
			end = null;
		}

		// get sources
		sources = getMessageSources();

		// get type
		if (checkBoxFilterSentReceived.isChecked()) {
			switch (sendReceiveRadioGroup.getCheckedRadioButtonId()) {
			case R.id.radioSent:
				type = SendReceiveType.SENT;
				break;

			case R.id.radioReceived:
				type = SendReceiveType.RECEIVED;
				break;

			default :
				type = SendReceiveType.BOTH;
			}
		} else {
			type = SendReceiveType.BOTH;
		}


		Search search = new Search(text, begin, end, sources, contacts, type);
		return null;
	}

	public List<MessageSource> getMessageSources() {
		List<MessageSource> sources = new ArrayList<MessageSource>(4);
		// check if sms is picked
		if (smsButton.isChecked()) {
			sources.add(MessageSource.SMS);
		}
		// check if fb is picked
		if (facebookButton.isChecked()) {
			sources.add(MessageSource.FACEBOOK);
		}
		// check if twitter is picked
		if (twitterButton.isChecked()) {
			sources.add(MessageSource.TWITTER);
		}
		// check if starred is picked
		if (starButton.isChecked()) {
			sources.add(MessageSource.STARRED);
		}
		return sources;
	}


}