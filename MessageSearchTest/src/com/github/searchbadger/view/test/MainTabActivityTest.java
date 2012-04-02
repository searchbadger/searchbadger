package com.github.searchbadger.view.test;

import android.test.ActivityInstrumentationTestCase2;

import com.github.searchbadger.view.MainTabActivity;

public class MainTabActivityTest extends
		ActivityInstrumentationTestCase2<MainTabActivity> {

	private MainTabActivity testActivity;

	public MainTabActivityTest() {
		super("com.github.searchbadger", MainTabActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		testActivity = this.getActivity();
	}

	public void testPreconditions() {
		assertNotNull(testActivity);
	}


}