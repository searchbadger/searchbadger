package com.github.searchbadger.testutil;

import android.database.sqlite.SQLiteDatabase;

import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.core.SearchBadgerModel;

public class SearchBadgerTestModel extends SearchBadgerModel {
	public void clearStarredMessagesList() {
		this.starredMsgs = null;
	}
	
	public void clearStarredMessageDb() {
		SQLiteDatabase db = null;
		try {
			db = dbOH.getWritableDatabase();
			db.delete(STARRED_MSGS_TABLE, null, null);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	public void removeStarredDb() {
		SearchBadgerApplication.getAppContext().deleteDatabase(SearchBadgerOpenHandler.DATABASE_NAME);
	}
	//public 
}
