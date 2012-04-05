package com.github.searchbadger.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.facebook.android.Util;
import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.core.SearchBadgerPreferences;
import com.github.searchbadger.util.FacebookHelper;

public class AccountsActivity extends PreferenceActivity {
	
	
	private FacebookHelper facebookHelper = SearchBadgerApplication.getFacebookHelper();
    private SearchBadgerPreferences prefs = SearchBadgerPreferences.getInstance();
    
    final static int AUTHORIZE_FACEBOOK_DIALOG = 0;
    
    private Preference facebookConnect;
    private Handler handler;
    private EditTextPreference maxResult;
    private EditTextPreference numMessageThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.accounts_layout);

		handler = new Handler();
		
		// setup the facebook preference
		facebookConnect = (Preference) findPreference("facebookConnect");
		FacebookUpdateListner facebookUpdateListner = new FacebookUpdateListner();
		facebookHelper.setUpdateActivityListener(facebookUpdateListner);
		FacebookClickListener facebookClickListener = new FacebookClickListener(this);
		facebookConnect.setOnPreferenceClickListener(facebookClickListener);
		updateFacebookButton();
		
		// setup the search max result preference
		maxResult = (EditTextPreference) findPreference("prefMaxResults");
		maxResult.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				prefs.saveSearchResultMax(Integer.parseInt((String)newValue));
				updateMaxResult();
				return true;
			}
		});
		updateMaxResult();
		
		// setup the number of messages per thread preference
		numMessageThread = (EditTextPreference) findPreference("prefNumMessagesThread");
		numMessageThread.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				prefs.saveNumMessagePerThread(Integer.parseInt((String)newValue));
				updateNumMessageThread();
				return true;
			}
		});
		updateNumMessageThread();
		
	}
	

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
  
            case AUTHORIZE_FACEBOOK_DIALOG: {
            	facebookHelper.authorizeCallback(requestCode, resultCode, data);
                break;
            }
        }
    }
    
    public void updateFacebookButton() {
    	if(facebookHelper.isSessionValid()) {
    		facebookConnect.setTitle("Log Out");
    		facebookConnect.setSummary("Disconnect from Facebook");
    	} else {
    		facebookConnect.setTitle("Log In");
    		facebookConnect.setSummary("Connect to Facebook");
    	}
    	
    }
    
    public void updateMaxResult() {
		maxResult.setTitle("Max Results: " + prefs.getSearchResultMax());
    }
    
    public void updateNumMessageThread() {
    	numMessageThread.setTitle("Thread Messages: " + prefs.getNumMessagePerThread());
    }
    
    private class FacebookClickListener implements OnPreferenceClickListener {

    	PreferenceActivity activity;
    	
		public FacebookClickListener(PreferenceActivity activity) {
			this.activity = activity;
		}

		public boolean onPreferenceClick(Preference preference) {

	    	if(facebookHelper.isSessionValid()) { 
	    		facebookHelper.logout();
	    	} else {
	    		facebookHelper.login(activity, AUTHORIZE_FACEBOOK_DIALOG);
	    	}
			return true;
		}
    	
    }
    
    private class FacebookUpdateListner implements FacebookHelper.UpdateActivityListener {

		public void Update() {
			// call the update in the original thread
			handler.post(new Runnable() {
                public void run() {
                	updateFacebookButton();
                }
            });
		}

		public void DisplayError(final String title, final String message) {
			// call the update in the original thread
			handler.post(new Runnable() {
                public void run() {
                	Util.showAlert(getActvityContext(), title, message);
                }
            });
		}
    	
    }
 
    private Context getActvityContext() {
    	return this;
    }
}