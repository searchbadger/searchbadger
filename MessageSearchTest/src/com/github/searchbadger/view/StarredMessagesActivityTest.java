package com.github.searchbadger.view;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.testutil.SearchTestModel;

public class StarredMessagesActivityTest extends
		ActivityInstrumentationTestCase2<StarredMessagesActivity> {

	private StarredMessagesActivity testActivity;

	public StarredMessagesActivityTest() {
		super("com.github.searchbadger", StarredMessagesActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testPreconditions() {
		// switch to the model to use the test model
		SearchTestModel testModel = new SearchTestModel();
		SearchBadgerApplication.setSearchModel(testModel);
		testModel.searchResultMessages = null;
		
		testActivity = this.getActivity();
		assertNotNull(testActivity);
	}
	
	public void doMessageTest() {

		StarredMessagesListFragment listFragment = (StarredMessagesListFragment)
				testActivity.getSupportFragmentManager().findFragmentById(R.id.message_list);
		ListView listView = listFragment.getListView();
		assertEquals("Check the listview count", 5, listView.getCount());
		View v = listView.getChildAt(0);
		CheckBox c = (CheckBox)v.findViewById(R.id.starred_checkbox);
		// click on star button 2x then click on the list item row
		assertEquals("Make sure the item is not checked", false, c.isChecked());
		TouchUtils.clickView(this, c);
		assertEquals("Make sure the item is checked", true, c.isChecked());
		TouchUtils.clickView(this, c);
		assertEquals("Make sure the item is not checked", false, c.isChecked());
		//TouchUtils.clickView(this, v);
		

		// monitor ThreadActivity
		Instrumentation instrumentation = getInstrumentation();
		Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(
				ThreadActivity.class.getName(), null, false);
		TouchUtils.clickView(this, v);
		Activity activity = instrumentation.waitForMonitor(monitor);
		instrumentation.removeMonitor(monitor);
		activity.finish();

	}

	public void testWithMessages() {

		// switch to the model to use the test model
		SearchTestModel testModel = new SearchTestModel();
		SearchBadgerApplication.setSearchModel(testModel);

		testActivity = this.getActivity();
		assertNotNull(testActivity);
		
		// do some test
		StarredMessagesListFragment listFragment = (StarredMessagesListFragment)
				testActivity.getSupportFragmentManager().findFragmentById(R.id.message_list);
		ListView listView = listFragment.getListView();
		assertEquals("Check the listview count", 5, listView.getCount());
		View v = listView.getChildAt(0);
		CheckBox c = (CheckBox)v.findViewById(R.id.starred_checkbox);
		// click on star button 2x then click on the list item row
		assertEquals("Make sure the item is not checked", false, c.isChecked());
		TouchUtils.clickView(this, c);
		assertEquals("Make sure the item is checked", true, c.isChecked());
		TouchUtils.clickView(this, c);
		assertEquals("Make sure the item is not checked", false, c.isChecked());
		//TouchUtils.clickView(this, v);
		

		// monitor ThreadActivity
		Instrumentation instrumentation = getInstrumentation();
		Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(
				ThreadActivity.class.getName(), null, false);
		TouchUtils.clickView(this, v);
		Activity activity = instrumentation.waitForMonitor(monitor);
		instrumentation.removeMonitor(monitor);
		activity.finish();

	}
	
	public void testWithMessagesLandscape() {

		// switch to the model to use the test model
		SearchTestModel testModel = new SearchTestModel();
		SearchBadgerApplication.setSearchModel(testModel);

		testActivity = this.getActivity();
		
		Instrumentation instrumentation = getInstrumentation();

		// monitor search activity
		Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(
				StarredMessagesActivity.class.getName(), null, false);

		// change to landscape
		testActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getInstrumentation().waitForIdleSync();
		testActivity = (StarredMessagesActivity) instrumentation.waitForMonitor(monitor);
		instrumentation.removeMonitor(monitor);
		assertNotNull(testActivity);

		// do some test
		StarredMessagesListFragment listFragment = (StarredMessagesListFragment) testActivity
				.getSupportFragmentManager()
				.findFragmentById(R.id.message_list);
		ListView listView = listFragment.getListView();
		assertEquals("Check the listview count", 5, listView.getCount());
		View v = listView.getChildAt(0);
		CheckBox c = (CheckBox)v.findViewById(R.id.starred_checkbox);
		// click on star button 2x then click on the list item row
		assertEquals("Make sure the item is not checked", false, c.isChecked());
		TouchUtils.clickView(this, c);
		assertEquals("Make sure the item is checked", true, c.isChecked());
		TouchUtils.clickView(this, c);
		assertEquals("Make sure the item is not checked", false, c.isChecked());
		TouchUtils.clickView(this, v);
		testActivity.finish();
	}

	
}