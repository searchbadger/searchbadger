package com.github.searchbadger.view;

import android.test.ActivityInstrumentationTestCase2;

import com.github.searchbadger.view.RecentSearchActivity;

public class RecentSearchActivityTest extends
		ActivityInstrumentationTestCase2<RecentSearchActivity> {

	private RecentSearchActivity testActivity;

	public RecentSearchActivityTest() {
		super("com.github.searchbadger", RecentSearchActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		testActivity = this.getActivity();
	}

	public void testPreconditions() {
		assertNotNull(testActivity);
	}


}