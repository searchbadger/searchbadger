package com.github.searchbadger.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.MessageSource;

public class ContactsActivityTest extends
		ActivityInstrumentationTestCase2<ContactsActivity> {

	private ContactsActivity testActivity;

	public ContactsActivityTest() {
		super("com.github.searchbadger", ContactsActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testPreconditions() {
		testActivity = this.getActivity();
		assertNotNull(testActivity);
	}
	

	public void testSourceSMS() {
		List<MessageSource> source = new ArrayList<MessageSource>();
		source.add(MessageSource.SMS);
		
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(SearchActivity.INTENT_DATA_SOURCES, 
				(ArrayList<MessageSource>)source);
		intent.putParcelableArrayListExtra(SearchActivity.INTENT_DATA_CONTACTS, 
				(ArrayList<Contact>)null);
		setActivityIntent(intent);

		testActivity = this.getActivity();
		assertNotNull(testActivity);
	}
	
	public void testSourceStarred() {
		List<MessageSource> source = new ArrayList<MessageSource>();
		source.add(MessageSource.STARRED);
		
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(SearchActivity.INTENT_DATA_SOURCES, 
				(ArrayList<MessageSource>)source);
		setActivityIntent(intent);

		testActivity = this.getActivity();
		assertNotNull(testActivity);
	}
	
	

}