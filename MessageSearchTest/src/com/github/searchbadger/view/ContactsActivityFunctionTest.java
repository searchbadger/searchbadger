package com.github.searchbadger.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.testutil.SearchTestModel;
import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.MessageSource;

public class ContactsActivityFunctionTest extends
		ActivityInstrumentationTestCase2<ContactsActivity> {

	private ContactsActivity testActivity;
	private ListView list;
	private List<Contact> contacts;

	public ContactsActivityFunctionTest() {
		super("com.github.searchbadger", ContactsActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		// switch to the model to use the test model
		SearchTestModel testModel = new SearchTestModel();
		SearchBadgerApplication.setSearchModel(testModel);
		
		// test the ContactsActivity functionality with SMS contacts
		List<MessageSource> source = new ArrayList<MessageSource>();
		source.add(MessageSource.SMS);
		
		// add the first contact of the test contacts as being selected
		contacts = testModel.getContacts(MessageSource.SMS);
		List<Contact> selectContacts = new ArrayList<Contact>();
		selectContacts.add(contacts.get(0));
		
		// create the intent data
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(SearchActivity.INTENT_DATA_SOURCES, 
				(ArrayList<MessageSource>)source);
		intent.putParcelableArrayListExtra(SearchActivity.INTENT_DATA_CONTACTS, 
				(ArrayList<Contact>)selectContacts);
		setActivityIntent(intent);

		
		testActivity = this.getActivity();
		list = (ListView) testActivity.findViewById(R.id.listView_contact);
	}

	public void testPreconditions() {
		assertNotNull(testActivity);
		assertNotNull(list);
		assertEquals("The number of item in the list", 5, list.getChildCount());
		
		View v = list.getChildAt(0);
		CheckBox checkbox = (CheckBox) v.findViewById(R.id.contact_checkBox);
		assertEquals("Make sure the first item is already checked", true, checkbox.isChecked());
	}
	
	@UiThreadTest
	public void testSelectContact() {


		List<Contact> selectContacts = testActivity.getSelectedContacts();
		Contact c = contacts.get(1);
		View v = list.getChildAt(1);
		CheckBox checkbox = (CheckBox) v.findViewById(R.id.contact_checkBox);
		assertEquals("Make sure the second item is not checked", false, checkbox.isChecked());
		assertEquals("Make sure the second contact is not in the selected list", false, selectContacts.contains(c));
		
		checkbox.performClick();
		assertEquals("Make sure the second item is now checked", true, checkbox.isChecked());
		assertEquals("Make sure the second contact is now in the selected list", true, selectContacts.contains(c));
		
		checkbox.performClick();
		assertEquals("Make sure the second item is now unchecked", false, checkbox.isChecked());
		assertEquals("Make sure the second contact is now not in the selected list", false, selectContacts.contains(c));
	}
	
	@UiThreadTest
	public void testDone() {

        Button buttonDone = (Button) testActivity.findViewById(R.id.buttonDone);
        buttonDone.performClick();
		assertEquals("Make sure activity has finished", true, testActivity.isFinishing());
		
		// TODO see if we can check the results
	}
	

}