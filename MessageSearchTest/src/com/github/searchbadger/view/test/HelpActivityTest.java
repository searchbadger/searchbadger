package com.github.searchbadger.view.test;

import android.test.ActivityInstrumentationTestCase2;

import com.github.searchbadger.view.HelpActivity;

public class HelpActivityTest extends
		ActivityInstrumentationTestCase2<HelpActivity> {

	private HelpActivity testActivity;

	public HelpActivityTest() {
		super("com.github.searchbadger", HelpActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		testActivity = this.getActivity();
	}

	public void testPreconditions() {
		assertNotNull(testActivity);
	}


}