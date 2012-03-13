package edu.wisc.cs.cs638.messagesearch.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import edu.wisc.cs.cs638.messagesearch.R;
import edu.wisc.cs.cs638.messagesearch.view.SearchActivity;

public class SearchActivityTest extends ActivityInstrumentationTestCase2<SearchActivity>{

	private SearchActivity testActivity;
	private LinearLayout dateLayout, contactsLayout, sentLayout;
	private CheckBox dateCheck, contactsCheck, sentCheck;
	
	public SearchActivityTest() {
		super("edu.wisc.cs.cs638.messagesearch.view", SearchActivity.class);
	}

	protected void setUp() throws Exception{
		super.setUp();
		testActivity = this.getActivity();
		dateLayout = (LinearLayout) testActivity.findViewById(edu.wisc.cs.cs638.messagesearch.R.id.linearFilterDateOptions);
        contactsLayout = (LinearLayout) testActivity.findViewById(edu.wisc.cs.cs638.messagesearch.R.id.linearFilterContactsOptions);
        sentLayout = (LinearLayout) testActivity.findViewById(edu.wisc.cs.cs638.messagesearch.R.id.linearFilterSentReceivedOptions);
        dateCheck = (CheckBox) testActivity.findViewById(edu.wisc.cs.cs638.messagesearch.R.id.checkBoxFilterDate);
        contactsCheck = (CheckBox) testActivity.findViewById(edu.wisc.cs.cs638.messagesearch.R.id.checkBoxFilterContacts);
        sentCheck = (CheckBox) testActivity.findViewById(edu.wisc.cs.cs638.messagesearch.R.id.checkBoxFilterSentReceived);
	}
	
	protected void testPreconditions() {
		assertNotNull(testActivity);
		assertNotNull(dateLayout);
		assertNotNull(contactsLayout);
		assertNotNull(sentLayout);
		assertNotNull(dateCheck);
		assertNotNull(contactsCheck);
		assertNotNull(sentCheck);
	}
	
	protected void testToggleDate() {
		
	}
	
	protected void testToggleContacts() {
		
	}
	
	protected void testToggleSentReceived() {
		
	}
	
	protected void tearDown() {
		
	}

}
