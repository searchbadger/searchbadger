package com.github.searchbadger.view.test;

import android.test.ActivityInstrumentationTestCase2;

import com.github.searchbadger.view.StarredMessagesActivity;

public class StarredMessagesActivityTest extends
		ActivityInstrumentationTestCase2<StarredMessagesActivity> {

	private StarredMessagesActivity testActivity;

	public StarredMessagesActivityTest() {
		super("com.github.searchbadger", StarredMessagesActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		testActivity = this.getActivity();
	}

	public void testPreconditions() {
		assertNotNull(testActivity);
	}


}