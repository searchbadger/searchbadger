package com.github.searchbadger.core;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.Message;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SendReceiveType;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;



public class MessageSearchModel {
	private final static MessageSearchModel instance = new MessageSearchModel();
	private Search _currentSearch;
	
	private Cursor searchResultCursor;
	private final static String projectionList[] = {"_id", "person", "date", "body"};
	
	
	public static MessageSearchModel getInstance() {
		return instance;
	}
	
	public void search(Search filter) {

		// SMS content provider uri 
		String url = "content://sms/inbox"; 
		Uri uri = Uri.parse(url); 
	
		ArrayList<String> selectionArgList = new ArrayList<String>();
		
		String selection = "";
		String arg = "";
		
		// Go through each possible search type, and build SQL query
		if (filter.getText() != null){
			selection += "body LIKE ?";
			arg = "%" + filter.getText() + "&";
			
			selectionArgList.add(arg);
		}
		if (filter.getContacts() != null){
			List<Contact> contacts = filter.getContacts();
			Iterator<Contact> iter = contacts.iterator();
			
			if (selection.length() > 0)
				selection += "and";
				
			selection += "(";
			while (iter.hasNext()){
				Contact c = iter.next();
				selection += "person = ?";
				arg = c.getName();
				
				selectionArgList.add(arg);
			}
			selection += ")";
			
		}
		
		if (filter.getBegin() != null){
			if (selection.length() > 0)
				selection += "and";
			
			selection += "date >= ?";
			arg = filter.getBegin().toString();
			
			selectionArgList.add(arg);
		}
		
		if (filter.getEnd() != null){
			if (selection.length() > 0)
				selection += "and";
			
			selection += "date <= ?";
			arg = filter.getBegin().toString();
			
			selectionArgList.add(arg);
		}
		
		String[] selectionArgArray = selectionArgList.toArray(new String[selectionArgList.size()]);
		
		// Make query to content provider and store cursor to table returned
		searchResultCursor = Activity.managedQuery(uri, projectionList, selection, selectionArgArray, "");

	}
	
	public Cursor getResultCursor() {
		return searchResultCursor;
	}
	
	public Search getCurrentSearch() {
		return _currentSearch;
	}
	
	public void setCurrentSearch(Search srch) {
		_currentSearch = srch;
		// TODO: this should probably also add the search to the recent searches database...
	}
	
	/*
	 * A list of recent searches is returned. 
	 * Note: not messages, these are actual searches.
	 * The search can be performed again by the user, who sees the filters set
	 * the searches are stored in a database so that we can access them again 
	 * even if the user shuts off their phone
	 */
	public List<Search> getRecentSearches() {
		
		return null;
	}
	
	/*
	 * TODO: interface with database for persistent storage of starred msgs
	 */
	public List<Message> getStarredMessages() {
		return null;
	}
	
	/*
	 * TODO: interface with database for persistent storage of starred msgs
	 */
	public boolean addStarredMessage(Message msg) {
		return true;
	}
	
	/*
	 * TODO: interface with database for persistent storage of starred msgs
	 */
	public boolean removeStarredMessage(Message msg) {
		return true;
	}	
	
	/*
	 * TODO Needs to access database. This probably will go through some other 
	 * class for SMSSearch or other search type
	 */
	public List<Message> getThread(Message msg) {
		return null;
	}
	
}
