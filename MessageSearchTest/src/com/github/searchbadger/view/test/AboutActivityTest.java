package com.github.searchbadger.view.test;

import android.test.ActivityInstrumentationTestCase2;

import com.github.searchbadger.view.AboutActivity;

public class AboutActivityTest extends
		ActivityInstrumentationTestCase2<AboutActivity> {

	private AboutActivity testActivity;

	public AboutActivityTest() {
		super("com.github.searchbadger", AboutActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		testActivity = this.getActivity();
	}

	public void testPreconditions() {
		assertNotNull(testActivity);
	}


}