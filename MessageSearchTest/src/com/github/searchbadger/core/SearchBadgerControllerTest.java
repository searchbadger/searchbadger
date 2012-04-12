package com.github.searchbadger.core;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ToggleButton;

import com.github.searchbadger.R;
import com.github.searchbadger.testutil.SearchTestModel;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.view.ContactsActivity;
import com.github.searchbadger.view.MainTabActivity;
import com.github.searchbadger.view.SearchActivity;
import com.github.searchbadger.view.SearchResultActivity;

public class SearchBadgerControllerTest extends InstrumentationTestCase {

	private SearchBadgerController controller;
	private ContactsActivity contactActivity;
	private SearchActivity searchActivity;
	private SearchResultActivity searchResultActivity;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		controller = SearchBadgerController.getInstance();
		SearchTestModel testModel = new SearchTestModel();
		SearchBadgerApplication.setSearchModel(testModel);
		
		contactActivity = null;
		searchActivity = null;
		searchResultActivity = null;
	}
	

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if(contactActivity != null) contactActivity.finish();
		if(searchActivity != null ) searchActivity.finish();
		if(searchResultActivity != null) searchResultActivity.finish();
	}
	
	public void testPreconditions() {
		assertNotNull(controller);
	}
	
	public void toggleSource(MessageSource source) {

		searchActivity.runOnUiThread(new Runnable() {
			public void run() {
				ToggleButton sourceButton;
				sourceButton = (ToggleButton) searchActivity.findViewById(R.id.toggleButtonTypeSMS);
				sourceButton.setChecked(false);
				sourceButton = (ToggleButton) searchActivity.findViewById(R.id.toggleButtonTypeFacebook);
				sourceButton.setChecked(false);
				sourceButton = (ToggleButton) searchActivity.findViewById(R.id.toggleButtonTypeTwitter);
				sourceButton.setChecked(false);
				sourceButton = (ToggleButton) searchActivity.findViewById(R.id.toggleButtonTypeStar);
				sourceButton.setChecked(false);
			}
		});
		
		if(source != null) {
			switch(source) {
			case SMS:
				searchActivity.runOnUiThread(new Runnable() {
					public void run() {
						ToggleButton sourceButton = (ToggleButton) searchActivity
								.findViewById(R.id.toggleButtonTypeSMS);
						sourceButton.setChecked(true);
					}
				});
				break;
			case FACEBOOK:
				searchActivity.runOnUiThread(new Runnable() {
					public void run() {
						ToggleButton sourceButton = (ToggleButton) searchActivity
								.findViewById(R.id.toggleButtonTypeFacebook);
						sourceButton.setChecked(true);
					}
				});
				break;
			case TWITTER:
				searchActivity.runOnUiThread(new Runnable() {
					public void run() {
						ToggleButton sourceButton = (ToggleButton) searchActivity
								.findViewById(R.id.toggleButtonTypeTwitter);
						sourceButton.setChecked(true);
					}
				});
				break;
			case STARRED:
				searchActivity.runOnUiThread(new Runnable() {
					public void run() {
						ToggleButton sourceButton = (ToggleButton) searchActivity
								.findViewById(R.id.toggleButtonTypeStar);
						sourceButton.setChecked(true);
					}
				});
				break;
			}
		}
		searchActivity.updateSourceSelectionContact();
		
	}

	public void runTestSearchButtonListener(MessageSource source) {

		Instrumentation instrumentation = getInstrumentation();

		// monitor search activity
		Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(
				SearchActivity.class.getName(), null, false);

		// start search activity
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClassName(instrumentation.getTargetContext(),
				MainTabActivity.class.getName());
		instrumentation.startActivitySync(intent);

		// wait for activity to start
		searchActivity = (SearchActivity) getInstrumentation().waitForMonitor(monitor);
		assertNotNull(searchActivity);

		toggleSource(source);
		
		
		// monitor search result activity
		instrumentation.removeMonitor(monitor);

		monitor = instrumentation.addMonitor(
				SearchResultActivity.class.getName(), null, false);

		// click on the search button
		Button searchButton = (Button) searchActivity
				.findViewById(R.id.buttonSearch);
		TouchUtils.clickView(this, searchButton);

		// wait for the search result activity
		searchResultActivity = (SearchResultActivity) getInstrumentation().waitForMonitorWithTimeout(monitor,5);
		//assertNotNull(searchResultActivity);

		// cleanup
		instrumentation.removeMonitor(monitor);
	      
	}

	public void testSearchButtonListenerSMS() {
		runTestSearchButtonListener(MessageSource.SMS);
	}
	
	public void testSearchButtonListenerFacebook() {
		runTestSearchButtonListener(MessageSource.FACEBOOK);
	}
	
	public void testSearchButtonListenerTwitter() {
		runTestSearchButtonListener(MessageSource.TWITTER);
	}
	
	public void testSearchButtonListenerStar() {
		runTestSearchButtonListener(MessageSource.STARRED);
	}
	
	public void testSearchButtonListenerNone() {
		runTestSearchButtonListener(null);
	}

	public void runTestContactButtonListener(MessageSource source) {

		Instrumentation instrumentation = getInstrumentation();

		// monitor search activity
		Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(
				SearchActivity.class.getName(), null, false);

		// start search activity
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClassName(instrumentation.getTargetContext(),
				MainTabActivity.class.getName());
		instrumentation.startActivitySync(intent);

		// wait for activity to start
		searchActivity = (SearchActivity)getInstrumentation().waitForMonitor(monitor);
		assertNotNull(searchActivity);


		toggleSource(source);
		
		// enable the contact filter
		CheckBox checkBoxFilterContacts = (CheckBox) searchActivity
				.findViewById(R.id.checkBoxFilterContacts);
		TouchUtils.clickView(this, checkBoxFilterContacts);

		// monitor contacts activity
		instrumentation.removeMonitor(monitor);
		monitor = instrumentation.addMonitor(ContactsActivity.class.getName(),
				null, false);

		// click on the contacts button
		Button contactsButton = (Button) searchActivity.findViewById(R.id.buttonSelectContacts);
		TouchUtils.clickView(this, contactsButton);

		// wait for the contact activity
		contactActivity = (ContactsActivity)getInstrumentation().waitForMonitorWithTimeout(monitor,5);
		//assertNotNull(contactActivity);

		// cleanup
		instrumentation.removeMonitor(monitor);
	}

	public void testContactButtonListenerSMS() {
		runTestContactButtonListener(MessageSource.SMS);
	}
	
	public void testContactButtonListenerFacebook() {
		runTestContactButtonListener(MessageSource.FACEBOOK);
	}
	
	public void testContactButtonListenerTwitter() {
		runTestContactButtonListener(MessageSource.TWITTER);
	}
	
	public void testContactButtonListenerStar() {
		runTestContactButtonListener(MessageSource.STARRED);
	}

	public void testContactButtonListenerNone() {
		runTestContactButtonListener(null);
	}
}
