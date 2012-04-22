package com.github.searchbadger.view;

import java.util.List;

import com.github.searchbadger.core.*;
import com.github.searchbadger.util.*;
import com.github.searchbadger.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class RecentSearchActivity extends FragmentActivity {

    private SearchModel model = SearchBadgerApplication.getSearchModel();
    private List<Search> recentSearches;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.recent_search_layout);
		//R.layout.

	}
	
    
}
