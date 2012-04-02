package com.github.searchbadger.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.testutil.SearchTestModel;
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

	public void testSource2Plus() {
		List<MessageSource> source = new ArrayList<MessageSource>();
		source.add(MessageSource.SMS);
		source.add(MessageSource.FACEBOOK);
		
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
	

	public void testButtons() {

		// switch to the model to use the test model
		SearchTestModel testModel = new SearchTestModel();
		SearchBadgerApplication.setSearchModel(testModel);
		
		// test the ContactsActivity functionality with SMS contacts
		List<MessageSource> source = new ArrayList<MessageSource>();
		source.add(MessageSource.SMS);
		
		// add the first contact of the test contacts as being selected
		List<Contact> contacts = testModel.getContacts(MessageSource.SMS);
		List<Contact> selectContacts = new ArrayList<Contact>();
		selectContacts.add(contacts.get(0));
		
		// create the intent data
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(SearchActivity.INTENT_DATA_SOURCES, 
				(ArrayList<MessageSource>)source);
		intent.putParcelableArrayListExtra(SearchActivity.INTENT_DATA_CONTACTS, 
				(ArrayList<Contact>)selectContacts);
		setActivityIntent(intent);

		// start activity
		testActivity = this.getActivity();
		ListView list = (ListView) testActivity.findViewById(R.id.listView_contact);
		
		// make sure the current view looks correct
		assertNotNull(testActivity);
		assertNotNull(list);
		assertEquals("The number of item in the list", 5, list.getChildCount());
		
		View v = list.getChildAt(0);
		CheckBox checkbox = (CheckBox) v.findViewById(R.id.contact_checkBox);
		assertEquals("Make sure the first item is already checked", true, checkbox.isChecked());
		
		// check that the checkboxes work
		List<Contact> selectContacts2 = testActivity.getSelectedContacts();
		Contact c = contacts.get(1);
		v = list.getChildAt(1);
		checkbox = (CheckBox) v.findViewById(R.id.contact_checkBox);
		assertEquals("Make sure the second item is not checked", false, checkbox.isChecked());
		assertEquals("Make sure the second contact is not in the selected list", false, selectContacts2.contains(c));
		
	    TouchUtils.clickView(this, checkbox);
		assertEquals("Make sure the second item is now checked", true, checkbox.isChecked());
		assertEquals("Make sure the second contact is now in the selected list", true, selectContacts2.contains(c));
		
	    TouchUtils.clickView(this, checkbox);
		assertEquals("Make sure the second item is now unchecked", false, checkbox.isChecked());
		assertEquals("Make sure the second contact is now not in the selected list", false, selectContacts2.contains(c));
		
		// check that the done button works
        Button buttonDone = (Button) testActivity.findViewById(R.id.buttonDone);
	    TouchUtils.clickView(this, buttonDone);
		assertEquals("Make sure activity has finished", true, testActivity.isFinishing());
		
	}
	

}