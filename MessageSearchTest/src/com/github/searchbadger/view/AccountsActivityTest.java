package com.github.searchbadger.view;

import android.test.ActivityInstrumentationTestCase2;

import com.github.searchbadger.view.AccountsActivity;

public class AccountsActivityTest extends
		ActivityInstrumentationTestCase2<AccountsActivity> {

	private AccountsActivity testActivity;

	public AccountsActivityTest() {
		super("com.github.searchbadger", AccountsActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		testActivity = this.getActivity();
	}

	public void testPreconditions() {
		assertNotNull(testActivity);
	}


}