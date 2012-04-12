package com.github.searchbadger.util;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.core.SearchBadgerPreferences;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


public class TwitterHelper {
	public static final String CONSUMER_KEY = "gv750PmPEZHs9ZHUUyIgCQ";
	public static final String CONSUMER_SECRET = "JM0AAaJMWUd6MGdCpRpIDkRHVUiZoajJ0I2LrWTxME";
	
	public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
	
	public static final String OAUTH_CALLBACK_URL = "app://searchbadger";
	
	public CommonsHttpOAuthConsumer httpOauthConsumer;
    public OAuthProvider httpOauthprovider;
    
	private Context context;
	
	private SearchBadgerPreferences prefs = SearchBadgerPreferences.getInstance();
	
	public Twitter twitter = new TwitterFactory().getInstance();
	
	public TwitterHelper(){
		context = SearchBadgerApplication.getAppContext();
	}
	
	public boolean isSessionValid(){
		if (prefs.getTwitterToken() == null)
			return false;
		else
			return true;
	}
	
	// just delete the access token and secret
	public void logout(Activity activity){
		prefs.removeTwitterToken();
		prefs.removeTwitterSecret();
	}
	
	public void login(final Activity activity){
		try {
			httpOauthConsumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			httpOauthprovider = new DefaultOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);
		    String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, OAUTH_CALLBACK_URL);
		    
		    // Open the browser
		    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
		} catch (Exception e) {
		    Log.d("TWITTER_OAUTH", "Error in acquiring request token.");
		}
	}
}
