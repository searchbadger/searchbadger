package com.github.searchbadger.view;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.github.searchbadger.R;

public class HelpActivity extends Activity {

	private WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);
        
        webview = (WebView) findViewById(R.id.webview);
        //webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("file:///android_asset/html/help.html"); 
	}

}
