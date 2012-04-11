package com.github.searchbadger.view;

import android.test.ActivityInstrumentationTestCase2;

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
		testActivity = this.getActivity();
		assertNotNull(testActivity);
	}

	public void testWithSearches() {

		// switch to the model to use the test model
		SearchTestModel testModel = new SearchTestModel();
		SearchBadgerApplication.setSearchModel(testModel);

		testActivity = this.getActivity();
		assertNotNull(testActivity);
	}

}