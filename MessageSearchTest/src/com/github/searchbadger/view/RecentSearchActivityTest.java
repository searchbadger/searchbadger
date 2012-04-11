package com.github.searchbadger.view;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.testutil.SearchTestModel;
import com.github.searchbadger.view.RecentSearchActivity;

public class RecentSearchActivityTest extends
		ActivityInstrumentationTestCase2<RecentSearchActivity> {

	private RecentSearchActivity testActivity;

	public RecentSearchActivityTest() {
		super("com.github.searchbadger", RecentSearchActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testPreconditions() {
		// switch to the model to use the test model
		SearchTestModel testModel = new SearchTestModel();
		SearchBadgerApplication.setSearchModel(testModel);
		testModel.searches = null;
		
		testActivity = this.getActivity();
		assertNotNull(testActivity);
	}

	public void testWithSearches() {

		// switch to the model to use the test model
		SearchTestModel testModel = new SearchTestModel();
		SearchBadgerApplication.setSearchModel(testModel);

		testActivity = this.getActivity();
		assertNotNull(testActivity);
		
		ListView listView = testActivity.getListView();
		assertEquals("Check the listview count", 3, listView.getCount());
		View v = listView.getChildAt(0);
		TouchUtils.clickView(this, v);
		testActivity.finish();
	}

	public void testRecreateActivity() {
		// switch to the model to use the test model
		SearchTestModel testModel = new SearchTestModel();
		SearchBadgerApplication.setSearchModel(testModel);

		// start activity
		testModel.sleepDelay = 10000;
		testActivity = this.getActivity();
		testActivity.finish();
	}
}