package com.github.searchbadger.view;

import java.util.Date;

import com.github.searchbadger.view.SearchActivity;


import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SearchActivityTest extends
		ActivityInstrumentationTestCase2<SearchActivity> {

	private SearchActivity testActivity;
	private LinearLayout dateLayout, contactsLayout, sentLayout;
	private CheckBox dateCheck, contactsCheck, sentCheck;
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
	
	private final Date before = new Date();
	private final Date after = new Date();
	private final Date from = new Date();
	private final Date to = new Date();

	public SearchActivityTest() {
		super("com.github.searchbadger", SearchActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		testActivity = this.getActivity();
		dateLayout = (LinearLayout) testActivity
				.findViewById(com.github.searchbadger.R.id.linearFilterDateOptions);
		contactsLayout = (LinearLayout) testActivity
				.findViewById(com.github.searchbadger.R.id.linearFilterContactsOptions);
		sentLayout = (LinearLayout) testActivity
				.findViewById(com.github.searchbadger.R.id.linearFilterSentReceivedOptions);
		dateCheck = (CheckBox) testActivity
				.findViewById(com.github.searchbadger.R.id.checkBoxFilterDate);
		contactsCheck = (CheckBox) testActivity
				.findViewById(com.github.searchbadger.R.id.checkBoxFilterContacts);
		sentCheck = (CheckBox) testActivity
				.findViewById(com.github.searchbadger.R.id.checkBoxFilterSentReceived);
		searchButton = (Button) testActivity.findViewById(com.github.searchbadger.R.id.buttonSearch);
		contactsButton = (Button) testActivity.findViewById(com.github.searchbadger.R.id.buttonSelectContacts);
		smsButton = (ToggleButton) testActivity.findViewById(com.github.searchbadger.R.id.toggleButtonTypeSMS);
		facebookButton = (ToggleButton) testActivity.findViewById(com.github.searchbadger.R.id.toggleButtonTypeFacebook);
		twitterButton = (ToggleButton) testActivity.findViewById(com.github.searchbadger.R.id.toggleButtonTypeTwitter);
		starButton = (ToggleButton) testActivity.findViewById(com.github.searchbadger.R.id.toggleButtonTypeStar);
		symbolPoundButton = (Button) testActivity.findViewById(com.github.searchbadger.R.id.buttonSymbolPound);
		symbolStarButton = (Button) testActivity.findViewById(com.github.searchbadger.R.id.buttonSymbolStar);
		symbolUnderscoreButton = (Button) testActivity.findViewById(com.github.searchbadger.R.id.buttonSymbolUnderscore);
		searchInputText = (EditText) testActivity.findViewById(com.github.searchbadger.R.id.editTextSearch);
		sendReceiveRadioGroup = (RadioGroup) testActivity.findViewById(com.github.searchbadger.R.id.radioGroupSentReceived);
		radioGroupDate = (RadioGroup) testActivity.findViewById(com.github.searchbadger.R.id.radioGroupDate);
		beforeButton = (Button) testActivity.findViewById(com.github.searchbadger.R.id.buttonBefore);
		afterButton = (Button) testActivity.findViewById(com.github.searchbadger.R.id.buttonAfter);
		fromButton = (Button) testActivity.findViewById(com.github.searchbadger.R.id.buttonFrom);
		toButton = (Button) testActivity.findViewById(com.github.searchbadger.R.id.buttonTo);
		contactsText = (TextView) testActivity.findViewById(com.github.searchbadger.R.id.textViewSelectContacts);
	
	}

	public void testPreconditions() {
		assertNotNull(testActivity);
		assertNotNull(dateLayout);
		assertNotNull(contactsLayout);
		assertNotNull(sentLayout);
		assertNotNull(contactsCheck);
		assertNotNull(sentCheck);
	}

	@UiThreadTest
	public void testToggleDate() {
		dateCheck.performClick();
		assertTrue(dateCheck.isChecked());
		assertEquals("Date layout visibility",
				dateLayout.getVisibility(), View.VISIBLE);
		dateCheck.performClick();
		assertTrue(!dateCheck.isChecked());
		assertEquals("Date layout visibility",
				dateLayout.getVisibility(), View.GONE);
	}

	@UiThreadTest
	public void testToggleContacts() {
		contactsCheck.performClick();
		assertTrue(contactsCheck.isChecked());
		assertEquals("Contacts layout visibility",
				contactsLayout.getVisibility(), View.VISIBLE);
		contactsCheck.performClick();
		assertTrue(!contactsCheck.isChecked());
		assertEquals("Contacts layout visibility",
				contactsLayout.getVisibility(), View.GONE);
	}

	@UiThreadTest
	public void testToggleSentReceived() {
		sentCheck.performClick();
		assertTrue(sentCheck.isChecked());
		assertEquals("Sent/received layout visibility",
				sentLayout.getVisibility(), View.VISIBLE);
		sentCheck.performClick();
		assertTrue(!sentCheck.isChecked());
		assertEquals("Sent/received layout visibility",
				sentLayout.getVisibility(), View.GONE);
	}
	
	@UiThreadTest
	public void testInsertTextSymbol() {
		searchInputText.setText("something to search");
		searchInputText.setSelection(9, 13);
		symbolPoundButton.performClick();
		assertEquals("Search box text",  "something#search", searchInputText.getText().toString());
		symbolStarButton.performClick();
		assertEquals("Search box text",  "something#*search", searchInputText.getText().toString());
		searchInputText.setSelection(0, 3);
		symbolUnderscoreButton.performClick();
		assertEquals("Search box text",  "_ething#*search", searchInputText.getText().toString());
		
	}
	
	@UiThreadTest
	public void testShowDatePicker() {
		beforeButton.performClick();
		
	}
	

}