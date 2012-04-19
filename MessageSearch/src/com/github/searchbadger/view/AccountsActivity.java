package com.github.searchbadger.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.facebook.android.Util;
import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.core.SearchBadgerPreferences;
import com.github.searchbadger.util.FacebookHelper;

import com.github.searchbadger.util.TwitterHelper;
import com.github.searchbadger.util.SearchModel;

public class AccountsActivity extends PreferenceActivity {
	
	
	protected FacebookHelper facebookHelper = SearchBadgerApplication.getFacebookHelper();
	protected TwitterHelper twitterHelper = SearchBadgerApplication.getTwitterHelper();
	protected SearchBadgerPreferences prefs = SearchBadgerPreferences.getInstance();
    
    final static int AUTHORIZE_FACEBOOK_DIALOG = 0;
    
    protected Preference facebookConnect;
    protected Preference twitterConnect;
    protected Handler handler;
	/* TODO Disabling these unless we decide to implement the features
    protected EditTextPreference maxResult;
    protected EditTextPreference numMessageThread;
    */
    protected Preference clearSearchButton;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.accounts_layout);

		handler = new Handler();
		
		// setup the facebook preference
		facebookConnect = findPreference("facebookConnect");
		FacebookUpdateListner facebookUpdateListner = new FacebookUpdateListner();
		facebookHelper.setUpdateActivityListener(facebookUpdateListner);
		FacebookClickListener facebookClickListener = new FacebookClickListener(this);
		facebookConnect.setOnPreferenceClickListener(facebookClickListener);
		updateFacebookButton();
		
		// setup the twitter preference
		twitterConnect = findPreference("twitterConnect");
		//TwitterUpdateListener twitterUpdateListener = new TwitterUpdateListener();
		//twitterHelper.setUpdateActivityListener(facebookUpdateListner);
		TwitterClickListener twitterClickListener = new TwitterClickListener(this);
		twitterConnect.setOnPreferenceClickListener(twitterClickListener);
		updateTwitterButton();
		
		/* TODO Disabling these unless we decide to implement the features
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
		*/
		
		// setup the clear searches preference
		clearSearchButton = (Preference) findPreference("prefClearSearchHistory");
		clearSearchButton.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				SearchModel model = SearchBadgerApplication.getSearchModel();
				model.clearRecentSearches();
				return true;
			}
		});
	}
	

    @Override
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
    
    public void updateTwitterButton(){
    	if (twitterHelper.isSessionValid()){
    		twitterConnect.setTitle("Log Out");
    		twitterConnect.setSummary("Disconnect from Twitter");
    	}
    	else{
    		twitterConnect.setTitle("Log In");
    		twitterConnect.setSummary("Connect to Twitter");
    	}
    }
    

	/* TODO Disabling these unless we decide to implement the features
    public void updateMaxResult() {
		maxResult.setTitle("Max Results: " + prefs.getSearchResultMax());
    }
    
    public void updateNumMessageThread() {
    	numMessageThread.setTitle("Thread Messages: " + prefs.getNumMessagePerThread());
    }
    */
    
    protected class FacebookClickListener implements OnPreferenceClickListener {

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
    
    private class TwitterClickListener implements OnPreferenceClickListener{
    	
    	Activity activity;
    	
    	public TwitterClickListener(Activity activity){
    		this.activity = activity;
    	}

		public boolean onPreferenceClick(Preference preference) {
			if (twitterHelper.isSessionValid()){
				twitterHelper.logout(activity);
			}
			else{
				twitterHelper.login(activity);
			}
			
			updateTwitterButton();
			
			return false;
		}
    	
    }
    

    protected class FacebookUpdateListner implements FacebookHelper.UpdateActivityListener {

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
 
    protected Context getActvityContext() {
    	return this;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        
        //Check if you got NewIntent event due to Twitter callback only

        if (uri != null && uri.toString().startsWith(TwitterHelper.OAUTH_CALLBACK_URL)) {
            String verifer = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

            try {
                // this will populate token and token_secret in consumer

                twitterHelper.httpOauthprovider.retrieveAccessToken(twitterHelper.httpOauthConsumer, verifer);
                String userKey = twitterHelper.httpOauthConsumer.getToken();
                String userSecret = twitterHelper.httpOauthConsumer.getTokenSecret();

                // Save user_key and user_secret in user preferences
                
                prefs.saveTwitterToken(userKey);
                prefs.saveTwitterSecret(userSecret);
                
        		updateTwitterButton();

            } catch(Exception e){
            	Log.d("TWITTER_OAUTH", "Error in processing callback.");
            }
        } else {
            // Do nothing if the callback comes from elsewhere
        }

    }
}