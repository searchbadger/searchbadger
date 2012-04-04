package com.github.searchbadger.testutil;

import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.core.SearchBadgerModel;

public class SearchBadgerTestModel extends SearchBadgerModel {
	public void clearStarredMessagesList() {
		this.starredMsgs = null;
	}
	public void removeStarredDb() {
		SearchBadgerApplication.getAppContext().deleteDatabase(SearchBadgerOpenHandler.DATABASE_NAME);
	}
	//public 
}
