package edu.wisc.cs.cs638.messagesearch.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.wisc.cs.cs638.messagesearch.*;
import edu.wisc.cs.cs638.messagesearch.core.*;
import edu.wisc.cs.cs638.messagesearch.core.MessageSearchController.SearchSourceSelected;
import edu.wisc.cs.cs638.messagesearch.util.*;

public class SearchActivity extends Activity {

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


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);

		model = MessageSearchModel.getInstance();
		controller = MessageSearchController.getInstance();
		
		pickerDate = new DatePickerDialog(this, controller.new DatePickerSelected(this), 0, 0, 0);

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
		searchButton.setOnClickListener(controller.new SearchButtonListener());
		contactsButton.setOnClickListener(controller.new ContactSourceListener());

		SearchSourceSelected searchSourceSelected = controller.new SearchSourceSelected(this);
		smsButton.setOnClickListener(searchSourceSelected);
		facebookButton.setOnClickListener(searchSourceSelected);
		twitterButton.setOnClickListener(searchSourceSelected);
		starButton.setOnClickListener(searchSourceSelected);
		
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
		sendReceiveRadioGroup.setOnCheckedChangeListener(controller.new SendReceiveTypeSelected());
		radioGroupDate.setOnCheckedChangeListener(controller.new DateRangeSelected(this));
		
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
		updateContactFilter();
		updateSendReceiveFilter();
		updateDates();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		updateTextContacts();
	}
	
	public Date getDateBefore() {
		return controller.srchGen.getDateBefore();
	}
	public Date getDateAfter() {
		return controller.srchGen.getDateAfter();
	}
	public Date getDateFrom() {
		return controller.srchGen.getDateFrom();
	}
	public Date getDateTo() {
		return controller.srchGen.getDateTo();
	}
	public void setDateBefore(Date date) {
		controller.srchGen.setDateBefore(date);
	}
	public void setDateAfter(Date date) {
		controller.srchGen.setDateAfter(date);
	}
	public void setDateFrom(Date date) {
		controller.srchGen.setDateFrom(date);
	}
	public void setDateTo(Date date) {
		controller.srchGen.setDateTo(date);
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

	public void updateContactFilter() {

		// disable/enable the contact filter
		List<MessageSource> searchSources = controller.srchGen.getSearchSources();
		boolean enableFilterContact = false;
		if (searchSources.size() == 1) {
			switch (searchSources.get(0)) {
			case SMS:
			case FACEBOOK:
			case TWITTER:
				enableFilterContact = true;
				break;
			}
		}
		if (enableFilterContact == false) {
			checkBoxFilterContacts.setChecked(false);
			toggleFilterContacts();
		}
		checkBoxFilterContacts.setEnabled(enableFilterContact);

	}

	public void updateSendReceiveFilter() {

		// select the correct type
		SendReceiveType type = controller.srchGen.getType();
		switch (type) {
		case SENT:
			sendReceiveRadioGroup.check(R.id.radioSent);
			break;

		case RECEIVED:
			sendReceiveRadioGroup.check(R.id.radioReceived);
			break;
		}
	}

	public void updateDates() {
		
		// update the dates shown on the buttons
		beforeButton.setText(dateFormat.format(controller.srchGen.getDateBefore()));
		afterButton.setText(dateFormat.format(controller.srchGen.getDateAfter()));
		fromButton.setText(dateFormat.format(controller.srchGen.getDateFrom()));
		toButton.setText(dateFormat.format(controller.srchGen.getDateTo()));
	}
	
	public void updateTextContacts() {
		
		// create and show the list of selected contacts
		List<Contact> contacts =  controller.srchGen.getContacts();
		StringBuilder listNames = new StringBuilder();
		listNames.append("Selected contacts: ");
		if(contacts.size() == 0) {
			listNames.append("None");
		}
		else {
			Iterator<Contact> iterator = contacts.iterator();
			while (iterator.hasNext()) {
				listNames.append(iterator.next().getName());
				if(iterator.hasNext())
					listNames.append(", ");
			} 
		}
		contactsText.setText(listNames);
		
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
			cal.setTime(controller.srchGen.getDateBefore());
			break;
		case R.id.buttonAfter:
			pickerButtonId = R.id.buttonAfter;
			cal.setTime(controller.srchGen.getDateAfter());
			break;
		case R.id.buttonFrom:
			pickerButtonId = R.id.buttonFrom;
			cal.setTime(controller.srchGen.getDateFrom());
			break;
		case R.id.buttonTo:
			pickerButtonId = R.id.buttonTo;
			cal.setTime(controller.srchGen.getDateTo());
			break;
		default:
			return;
		}

		// update and show the date picker
		pickerDate.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE));
		showDialog(DATE_DIALOG_ID);
	}

}