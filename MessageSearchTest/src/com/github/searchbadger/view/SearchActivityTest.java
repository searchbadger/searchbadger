package com.github.searchbadger.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.github.searchbadger.R;
import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SendReceiveType;

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
		
		testActivity.findViewById(R.id.radioAfter).performClick();
		testActivity.findViewById(R.id.radioBefore).performClick();
		testActivity.findViewById(R.id.radioFrom).performClick();
		testActivity.findViewById(R.id.radioPastMonth).performClick();
		testActivity.findViewById(R.id.radioPaskWeek).performClick();
		testActivity.findViewById(R.id.radioSinceYesterday).performClick();
		testActivity.findViewById(R.id.radioToday).performClick();
		
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
		

		testActivity.findViewById(R.id.radioReceived).performClick();
		testActivity.findViewById(R.id.radioSent).performClick();
		
		sentCheck.performClick();
		assertTrue(!sentCheck.isChecked());
		assertEquals("Sent/received layout visibility",
				sentLayout.getVisibility(), View.GONE);
	}
	
	@UiThreadTest
	public void testInsertTextSymbol() {
		testActivity.searchInputText.setText("something to search");
		testActivity.searchInputText.setSelection(9, 13);
		testActivity.symbolPoundButton.performClick();
		assertEquals("Search box text",  "something#search", testActivity.searchInputText.getText().toString());
		symbolStarButton.performClick();
		assertEquals("Search box text",  "something#*search", testActivity.searchInputText.getText().toString());
		searchInputText.setSelection(0, 3);
		testActivity.symbolUnderscoreButton.performClick();
		assertEquals("Search box text",  "_ething#*search", testActivity.searchInputText.getText().toString());
		searchInputText.setSelection(3, 0);
		testActivity.symbolUnderscoreButton.performClick();
		assertEquals("Search box text",  "_hing#*search", testActivity.searchInputText.getText().toString());
		
	}
	
	@UiThreadTest
	public void testClickButtons() {
		testActivity.beforeButton.performClick();
		testActivity.afterButton.performClick();
		testActivity.fromButton.performClick();
		testActivity.toButton.performClick();
		testActivity.clearSearchButton.performClick();
		testActivity.starButton.performClick();
		testActivity.facebookButton.performClick();
		testActivity.twitterButton.performClick();
	}
	
	@UiThreadTest
	public void testOtherFunctions() {
		testActivity.getDatePickerId();
		testActivity.onCreateDialog(-1);
		
		Date now = new Date();
		testActivity.setDateBefore(now);
		assertEquals(testActivity.beforeDate.getTime(), now.getTime());
		testActivity.setDateAfter(now);
		assertEquals(testActivity.afterDate.getTime(), now.getTime());
		testActivity.setDateFrom(now);
		testActivity.setDateTo(now);
		
	}

	@UiThreadTest
	public void testGenerateSearch() {
		testActivity.checkBoxFilterDate.setChecked(false);
		testActivity.checkBoxFilterContacts.setChecked(false);
		testActivity.checkBoxFilterSentReceived.setChecked(false);
		testActivity.generateSearch();
		

		testActivity.checkBoxFilterDate.setChecked(false);
		testActivity.checkBoxFilterContacts.setChecked(false);
		testActivity.checkBoxFilterSentReceived.setChecked(true);
		testActivity.findViewById(R.id.radioReceived).performClick();
		testActivity.generateSearch();
		testActivity.findViewById(R.id.radioSent).performClick();
		testActivity.generateSearch();
		

		testActivity.checkBoxFilterDate.setChecked(false);
		testActivity.checkBoxFilterContacts.setChecked(true);
		testActivity.checkBoxFilterSentReceived.setChecked(false);
		testActivity.selectedContacts.add(new Contact("1", MessageSource.SMS, null, null));
		testActivity.generateSearch();

		testActivity.checkBoxFilterDate.setChecked(true);
		testActivity.checkBoxFilterContacts.setChecked(false);
		testActivity.checkBoxFilterSentReceived.setChecked(false);
		testActivity.findViewById(R.id.radioAfter).performClick();
		testActivity.generateSearch();
		testActivity.findViewById(R.id.radioBefore).performClick();
		testActivity.generateSearch();
		testActivity.findViewById(R.id.radioFrom).performClick();
		testActivity.generateSearch();
		testActivity.findViewById(R.id.radioPastMonth).performClick();
		testActivity.generateSearch();
		testActivity.findViewById(R.id.radioPaskWeek).performClick();
		testActivity.generateSearch();
		testActivity.findViewById(R.id.radioSinceYesterday).performClick();
		testActivity.generateSearch();
		testActivity.findViewById(R.id.radioToday).performClick();
		testActivity.generateSearch();
	}

	@UiThreadTest
	public void testLoadSearch() {

		String text = "foo";
		Date begin = new Date();
		Date end = new Date(begin.getTime() + 1000);
		List<MessageSource> sources = new ArrayList<MessageSource>();
		sources.add(MessageSource.SMS);
		sources.add(MessageSource.FACEBOOK);
		sources.add(MessageSource.TWITTER);
		sources.add(MessageSource.STARRED);
		List<Contact> contacts = new ArrayList<Contact>();
		contacts.add(new Contact("1", MessageSource.SMS, null, null));
		contacts.add(new Contact("2", MessageSource.SMS, null, null));
		SendReceiveType type = SendReceiveType.SENT;
		testActivity.loadRecentSearch(new Search(text, begin, end, sources, contacts, type));
		testActivity.loadRecentSearch(new Search(text, null, null, null, null, null));
		testActivity.loadRecentSearch(new Search(text, begin, end, sources, contacts, type));
		testActivity.loadRecentSearch(new Search(text, begin, null, sources, contacts, type));
		testActivity.loadRecentSearch(new Search(text, null, end, sources, contacts, type));
		testActivity.loadRecentSearch(new Search(text, begin, end, sources, null, type));
	}
	

}