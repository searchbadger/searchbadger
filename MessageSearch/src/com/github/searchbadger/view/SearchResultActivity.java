package com.github.searchbadger.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.github.searchbadger.R;

public class SearchResultActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_layout);
        
	}

}
