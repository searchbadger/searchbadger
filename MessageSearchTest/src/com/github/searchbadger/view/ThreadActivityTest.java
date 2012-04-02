package com.github.searchbadger.view;

import android.test.ActivityInstrumentationTestCase2;

import com.github.searchbadger.view.ThreadActivity;

public class ThreadActivityTest extends
		ActivityInstrumentationTestCase2<ThreadActivity> {

	private ThreadActivity testActivity;

	public ThreadActivityTest() {
		super("com.github.searchbadger", ThreadActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		testActivity = this.getActivity();
	}

	public void testPreconditions() {
		assertNotNull(testActivity);
	}


}