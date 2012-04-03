package com.github.searchbadger.model;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.CheckBox;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerController;
import com.github.searchbadger.view.ContactsActivity;
import com.github.searchbadger.view.SearchActivity;
import com.github.searchbadger.view.SearchResultActivity;

public class SearchBadgerControllerTest extends InstrumentationTestCase {

	private SearchBadgerController controller;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		controller = SearchBadgerController.getInstance();
	}
	

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testPreconditions() {
		assertNotNull(controller);
	}
	
	
	public void testSearchButtonListener() {

	      Instrumentation instrumentation = getInstrumentation();

	      // monitor search activity
	      Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(SearchActivity.class.getName(), null, false);

	      // start search activity
	      Intent intent = new Intent(Intent.ACTION_MAIN);
	      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	      intent.setClassName(instrumentation.getTargetContext(), SearchActivity.class.getName());
	      instrumentation.startActivitySync(intent);

	      // wait for activity to start
	      Activity searchActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
	      assertNotNull(searchActivity);

	      // monitor search result activity
	      instrumentation.removeMonitor(monitor);
	      monitor = instrumentation.addMonitor(SearchResultActivity.class.getName(), null, false);
	      
	      // click on the search button
	      Button searchButton = (Button) searchActivity.findViewById(R.id.buttonSearch); 
	      TouchUtils.clickView(this, searchButton);
	      
	      // wait for the search result activity
	      Activity searchResultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
	      assertNotNull(searchResultActivity);
	      
	      // cleanup
	      instrumentation.removeMonitor(monitor);
	      searchResultActivity.finish();
	      searchActivity.finish();
	      

	}
	

	public void testContactButtonListener() {

	      Instrumentation instrumentation = getInstrumentation();

	      // monitor search activity
	      Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(SearchActivity.class.getName(), null, false);

	      // start search activity
	      Intent intent = new Intent(Intent.ACTION_MAIN);
	      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	      intent.setClassName(instrumentation.getTargetContext(), SearchActivity.class.getName());
	      instrumentation.startActivitySync(intent);

	      // wait for activity to start
	      Activity searchActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
	      assertNotNull(searchActivity);

	      
	      // enable the contact filter
	      CheckBox checkBoxFilterContacts = (CheckBox) searchActivity.findViewById(R.id.checkBoxFilterContacts);
	      TouchUtils.clickView(this, checkBoxFilterContacts);

	      // monitor contacts activity
	      instrumentation.removeMonitor(monitor);
	      monitor = instrumentation.addMonitor(ContactsActivity.class.getName(), null, false);
	      
	      // click on the contacts button
	      Button contactsButton = (Button) searchActivity.findViewById(R.id.buttonSelectContacts);
	      TouchUtils.clickView(this, contactsButton);
	      
	      // wait for the search result activity
	      Activity contactActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
	      assertNotNull(contactActivity);
	      
	      // cleanup
	      instrumentation.removeMonitor(monitor);
	      contactActivity.finish();
	      searchActivity.finish();
	      
	}
	
}
