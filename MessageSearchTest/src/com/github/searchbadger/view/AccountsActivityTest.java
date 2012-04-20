package com.github.searchbadger.view;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.github.searchbadger.core.SearchBadgerPreferences;

public class AccountsActivityTest extends
		ActivityInstrumentationTestCase2<AccountsActivity> {

	private AccountsActivity testActivity;

	public AccountsActivityTest() {
		super("com.github.searchbadger", AccountsActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		SearchBadgerPreferences prefs = SearchBadgerPreferences.getInstance();
		prefs.saveNumMessagePerThread(10);
		prefs.saveSearchResultMax(100);
		prefs.saveFacebookExpires(0);
		prefs.saveFacebookToken(null);
		testActivity = this.getActivity();
	}

	@UiThreadTest
	public void testPreconditions() {
		SearchBadgerPreferences prefs = SearchBadgerPreferences.getInstance();
		assertNotNull(testActivity);
		MockFacebook mockFacebook = new MockFacebook("foo");
		MockAsyncFacebookRunner mockAsyncFacebookRunner = new MockAsyncFacebookRunner(mockFacebook);
		testActivity.facebookHelper.facebook = mockFacebook;
		testActivity.facebookHelper.asyncRunner = mockAsyncFacebookRunner;
		
		/*
		testActivity.clearSearchButton.getOnPreferenceClickListener().onPreferenceClick(null);
		testActivity.maxResult.getOnPreferenceChangeListener().onPreferenceChange(null, new String("23"));
		assertEquals(23, prefs.getSearchResultMax());
		testActivity.numMessageThread.getOnPreferenceChangeListener().onPreferenceChange(null, new String("57"));
		assertEquals(57, prefs.getNumMessagePerThread());
		testActivity.facebookConnect.getOnPreferenceClickListener().onPreferenceClick(null);
		testActivity.facebookConnect.getOnPreferenceClickListener().onPreferenceClick(null);
		*/
		
		testActivity.onActivityResult(AccountsActivity.AUTHORIZE_FACEBOOK_DIALOG, 0, null);
		testActivity.onActivityResult(AccountsActivity.AUTHORIZE_FACEBOOK_DIALOG, 1, null);
		
//		testActivity.runOnUiThread(new Runnable() {
//			
//			public void run() {
//				testActivity.maxResult.getOnPreferenceChangeListener().onPreferenceChange(testActivity.maxResult, new String("23"));
//				testActivity.numMessageThread.getOnPreferenceChangeListener().onPreferenceChange(testActivity.numMessageThread, new String("57"));
//				testActivity.clearSearchButton.getOnPreferenceClickListener().onPreferenceClick(null);
//			}
//		});
	}


	private class MockFacebook extends Facebook {

		public boolean isSessionValid = false;
		
		public MockFacebook(String arg0) {
			super(arg0);
		}

		@Override
		public void authorizeCallback(int arg0, int arg1, Intent arg2) {}

		@Override
		public boolean isSessionValid() {
			return isSessionValid;
		}

		@Override
		public void authorize(Activity arg0, String[] arg1, int arg2, DialogListener arg3) {
			arg3.onComplete(null);
			arg3.onError(new DialogError("Hello", 0, "World"));
			isSessionValid = !isSessionValid;
			SearchBadgerPreferences prefs = SearchBadgerPreferences.getInstance();
			isSessionValid = true;
			prefs.saveFacebookExpires(new Date().getTime() * 2);
			prefs.saveFacebookToken("Hello World");
			
		}

		@Override
		public long getAccessExpires() {
			return 0;
		}

		@Override
		public String getAccessToken() {
			return null;
		}

		@Override
		public void setAccessExpires(long arg0) {}

		@Override
		public void setAccessToken(String arg0) {}

	}

	private class MockAsyncFacebookRunner extends AsyncFacebookRunner {
	
		public MockAsyncFacebookRunner(Facebook arg0) {
			super(arg0);
		}
	
		@Override
		public void logout(Context arg0, RequestListener arg1) {}
	
		@Override
		public void request(String arg0, RequestListener arg1, Object arg2) {}
	
		@Override
		public void logout(Context arg0, RequestListener arg1, Object arg2) {}
	
		@Override
		public void request(Bundle arg0, RequestListener arg1, Object arg2) {}
	
		@Override
		public void request(Bundle arg0, RequestListener arg1) {}
	
		@Override
		public void request(String arg0, Bundle arg1, RequestListener arg2,	Object arg3) {}
	
		@Override
		public void request(String arg0, Bundle arg1, RequestListener arg2) {}
	
		@Override
		public void request(String arg0, Bundle arg1, String arg2, RequestListener arg3, Object arg4) {}
	
		@Override
		public void request(String arg0, RequestListener arg1) {}
	
	}

}