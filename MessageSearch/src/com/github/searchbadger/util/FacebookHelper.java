package com.github.searchbadger.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.core.SearchBadgerPreferences;

public class FacebookHelper {

    public interface UpdateActivityListener{
        void Update();
        void DisplayError(String title, String message);
    }
    
    private static final String FACEBOOK_APP_ID = "336829723032066"; // this is our facebook app id
    public Facebook facebook = new Facebook(FACEBOOK_APP_ID);
    private String[] permissions = { "read_mailbox" };
    private static Hashtable<String, String> currentPermissions = new Hashtable<String, String>();
    private boolean isReady = false;
    private Context context;
    private SearchBadgerPreferences prefs = SearchBadgerPreferences.getInstance();
    public AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(facebook);
    private UpdateActivityListener updateActivityListener;
    
    public FacebookHelper() {
    	context = SearchBadgerApplication.getAppContext();

		// if our session is still valid, go check if we have the required permissions
		if(isSessionValid()) {
			requestPermissions();
		}
	}
    
    
	public void login(Activity activity, int activityCode ) {

		// if we don't have permissions or still need to login
        if (!isReady || !isSessionValid()) {
        	facebook.authorize(activity, permissions, activityCode, new LoginDialogListener());
        }
    }

	public void logout() {
		// reset variables and logout
		isReady = false;
        if (isSessionValid()) {
        	// clear the token and expire time
            prefs.saveFacebookToken(null);
            prefs.saveFacebookExpires(0);
            
            AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(facebook);
            asyncRunner.logout(context, new LogoutRequestListener());
        }
    }

	public boolean isReady() {
		return isReady && facebook.isSessionValid();
	}
	
	public boolean isSessionValid() {
		facebook.setAccessToken(prefs.getFacebookToken());
		facebook.setAccessExpires(prefs.getFacebookExpires());
		return facebook.isSessionValid();
	}
	
	public void authorizeCallback(int requestCode, int resultCode, Intent data) {
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
	
	public void setUpdateActivityListener(UpdateActivityListener updateActivityListener) {
		this.updateActivityListener = updateActivityListener;
	}
	
	private void requestPermissions() {
				
        Bundle params = new Bundle();
        params.putString("access_token", facebook.getAccessToken());
        asyncRunner.request("me/permissions", params, new PermissionsRequestListener());
	}

	public static abstract class BaseDialogListener implements DialogListener {
        public void onComplete(Bundle values) {}
        public void onFacebookError(FacebookError error) {}
        public void onError(DialogError error) {}
        public void onCancel() {}
	}

	public static abstract class BaseRequestListener implements RequestListener {
	    public void onFacebookError(FacebookError e, final Object state) {	    }
	    public void onFileNotFoundException(FileNotFoundException e, final Object state) {	    }
	    public void onIOException(IOException e, final Object state) {	    }
	    public void onMalformedURLException(MalformedURLException e, final Object state) {	    }
	}

    private final class LoginDialogListener extends BaseDialogListener {
        @Override
		public void onComplete(Bundle values) {
        	// save the token and expire time
            prefs.saveFacebookToken(facebook.getAccessToken());
            prefs.saveFacebookExpires(facebook.getAccessExpires());

            // request the permission to make sure we have all the required permissions
			requestPermissions();

            // update the activity if necessary
            if(updateActivityListener != null) {
            	updateActivityListener.Update();
            }
        }

        @Override
		public void onFacebookError(FacebookError error) {
            // update the activity if necessary
            if(updateActivityListener != null) {
            	updateActivityListener.Update();
            	updateActivityListener.DisplayError("Facebook Error", error.getMessage());
            }
        }
        
        @Override
		public void onError(DialogError error) {
            // update the activity if necessary
            if(updateActivityListener != null) {
            	updateActivityListener.Update();
            	updateActivityListener.DisplayError("Facebook Error", error.getMessage());
            }
        }
    }

	private class LogoutRequestListener extends BaseRequestListener {
		public void onComplete(String response, Object state) {
			
            // update the activity if necessary
            if(updateActivityListener != null) {
            	updateActivityListener.Update();
            }
		}

        @Override
		public void onFacebookError(FacebookError error, final Object state) {
            // update the activity if necessary
            if(updateActivityListener != null) {
            	updateActivityListener.Update();
            	updateActivityListener.DisplayError("Facebook Error", error.getMessage());
            }
        }
	}

	private class PermissionsRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            // parse the permission list
            currentPermissions.clear();
            try {
                JSONObject jsonObject = new JSONObject(response).getJSONArray("data").getJSONObject(0);
                Iterator<?> iterator = jsonObject.keys();
                String permission;
                while (iterator.hasNext()) {
                    permission = (String) iterator.next();
                    currentPermissions.put(permission,
                            String.valueOf(jsonObject.getInt(permission)));
                    
                    Log.d("SearchBadger", permission + " = " + String.valueOf(jsonObject.getInt(permission)));
                }
            } catch (JSONException e) {
                Log.d("SearchBadger", "Permissions could not be fetched.");
            }
            
            // check if we have all the permissions
            isReady = containsPermissions();
            Log.d("SearchBadger", "isReady = " + String.valueOf(isReady));

            // update the activity if necessary
            if(updateActivityListener != null) {
            	updateActivityListener.Update();
            }
        }
	}
	
	private boolean containsPermissions()
	{
		// check if we have all the permissions
		boolean allFound = true;
		for(int i = 0; i < permissions.length; i++) {
			if (currentPermissions.containsKey(permissions[i])
                    && currentPermissions.get(permissions[i]).equals("1")) {
				// found permission and it's set to enabled
			} else {
				allFound = false;
			}
		}
		return allFound;
	}
	
}
