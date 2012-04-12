package com.github.searchbadger.view;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;

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
	
	public void testLandscape() {
		testActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		assertNotNull(testActivity);
	}


}