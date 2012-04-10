package com.github.searchbadger.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.InstrumentationTestCase;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.github.searchbadger.core.SearchBadgerPreferences;

public class FacebookHelperTest extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
	}
	
	@Override
	protected void tearDown() throws Exception {
	}
	

	public void testMessageSource() {
		
		SearchBadgerPreferences prefs = SearchBadgerPreferences.getInstance();
		FacebookHelper facebookHelper;

		// test valid session
		prefs.saveFacebookExpires(new Date().getTime() * 2);
		prefs.saveFacebookToken("Hello World");
		facebookHelper = new FacebookHelper();
		assertEquals(true, facebookHelper.isSessionValid());

		// test invalid session
		prefs.saveFacebookExpires(0);
		prefs.saveFacebookToken(null);
		facebookHelper = new FacebookHelper();
		assertEquals(false, facebookHelper.isSessionValid());
		
		// insert the mock so we can test the other functions
		MockFacebook mockFacebook = new MockFacebook(FacebookHelper.FACEBOOK_APP_ID);
		MockAsyncFacebookRunner mockAsyncFacebookRunner = new MockAsyncFacebookRunner(mockFacebook);
		facebookHelper.facebook = mockFacebook;
		facebookHelper.asyncRunner = mockAsyncFacebookRunner;
		
		// test login
		mockFacebook.isSessionValid = false;
		facebookHelper.isReady = false;
		facebookHelper.login(null, 0);
		mockFacebook.isSessionValid = true;
		facebookHelper.isReady = true;
		facebookHelper.login(null, 0);
		
		// test logout
		mockFacebook.isSessionValid = false;
		facebookHelper.isReady = true;
		facebookHelper.logout();
		mockFacebook.isSessionValid = true;
		facebookHelper.logout();
		prefs.saveFacebookExpires(100);
		prefs.saveFacebookToken("Hello World");
		facebookHelper.logout();
		assertEquals(false, facebookHelper.isReady);
		assertEquals(null, prefs.getFacebookToken());
		assertEquals(0, prefs.getFacebookExpires());
		

		// test isReady
		mockFacebook.isSessionValid = false;
		facebookHelper.isReady = false;
		assertEquals(false, facebookHelper.isReady());
		facebookHelper.isReady = true;
		assertEquals(false, facebookHelper.isReady());
		mockFacebook.isSessionValid = true;
		assertEquals(true, facebookHelper.isReady());
		

		// test authorizeCallback
		facebookHelper.authorizeCallback(0, 0, null);
		
		// test setUpdateActivityListener
		MockUpdateListener updateListener = new MockUpdateListener();
		facebookHelper.setUpdateActivityListener(updateListener);
		
		// test BaseDialogListener
		MockDialogListener mockDialogListener = new MockDialogListener();
		mockDialogListener.onCancel();
		mockDialogListener.onComplete(null);
		mockDialogListener.onFacebookError(new FacebookError("mock error"));
		mockDialogListener.onError(new DialogError("mock", 0, "error"));
		
		// test MockRequestListener
		MockRequestListener mockRequestListener = new MockRequestListener();
		mockRequestListener.onComplete(null, null);
		mockRequestListener.onFacebookError(null, null);
		mockRequestListener.onFileNotFoundException(null, null);
		mockRequestListener.onIOException(null, null);
		mockRequestListener.onMalformedURLException(null, null);
		
		// test LoginDialogListener
		FacebookHelper.LoginDialogListener loginDialogListener = facebookHelper.new LoginDialogListener();
		loginDialogListener.onComplete(null);
		loginDialogListener.onFacebookError(new FacebookError("mock error"));
		loginDialogListener.onError(new DialogError("mock", 0, "error"));
		
		// test LogoutRequestListener
		FacebookHelper.LogoutRequestListener logoutRequestListener = facebookHelper.new LogoutRequestListener();
		logoutRequestListener.onComplete(null, null);
		logoutRequestListener.onFacebookError(new FacebookError("mock error"), null);
		
		// test PermissionsRequestListener
		FacebookHelper.PermissionsRequestListener permissionsRequestListener = facebookHelper.new PermissionsRequestListener();
		permissionsRequestListener.onComplete("{\"data\":[{\"installed\":1,\"email\":1,\"read_mailbox\":1}]}", null);
		permissionsRequestListener.onFacebookError(new FacebookError("mock error"), null);
		
		// test containsPermissions
		facebookHelper.isReady = false;
		permissionsRequestListener.onComplete("{\"data\":[{\"installed\":1,\"email\":1,\"read_mailbox\":1}]}", null);
		facebookHelper.containsPermissions();
		assertEquals(3, facebookHelper.currentPermissions.size());
		assertEquals(true, facebookHelper.isReady);
		permissionsRequestListener.onComplete("{\"data\":[{\"installed\":1,\"email\":1,\"read_mailbox\":0}]}", null);
		assertEquals(false, facebookHelper.isReady);
		
		
	}

    private class MockUpdateListener implements FacebookHelper.UpdateActivityListener {

		public void Update() {
		}

		public void DisplayError(final String title, final String message) {
		}
    	
    }
    
    private class MockDialogListener extends FacebookHelper.BaseDialogListener {

		@Override
		public void onComplete(Bundle values) {
			super.onComplete(values);
		}

		@Override
		public void onFacebookError(FacebookError error) {
			super.onFacebookError(error);
		}

		@Override
		public void onError(DialogError error) {
			super.onError(error);
		}

		@Override
		public void onCancel() {
			super.onCancel();
		}
    }
    
    private class MockRequestListener extends FacebookHelper.BaseRequestListener {

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			super.onFacebookError(e, state);
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e, Object state) {
			super.onFileNotFoundException(e, state);
		}

		@Override
		public void onIOException(IOException e, Object state) {
			super.onIOException(e, state);
		}

		@Override
		public void onMalformedURLException(MalformedURLException e, Object state) {
			super.onMalformedURLException(e, state);
		}

		public void onComplete(String arg0, Object arg1) {
			
		}
    	
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
		public void authorize(Activity arg0, String[] arg1, int arg2, DialogListener arg3) {}

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
