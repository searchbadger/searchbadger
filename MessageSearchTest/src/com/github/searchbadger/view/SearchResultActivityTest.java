package com.github.searchbadger.view;

import android.test.ActivityInstrumentationTestCase2;

import com.github.searchbadger.view.SearchResultActivity;

public class SearchResultActivityTest extends
		ActivityInstrumentationTestCase2<SearchResultActivity> {

	private SearchResultActivity testActivity;

	public SearchResultActivityTest() {
		super("com.github.searchbadger", SearchResultActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		testActivity = this.getActivity();
	}

	public void testPreconditions() {
		assertNotNull(testActivity);
	}


}