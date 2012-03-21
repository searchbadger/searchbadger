package com.github.searchbadger.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.Message;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SendReceiveType;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.github.searchbadger.R;

public class MessageSearchModel {
	private final static MessageSearchModel instance = new MessageSearchModel();
	private Search _currentSearch;
	
	//private Cursor searchResultCursor;
	private List<Map<String,String>> searchResults;
	private final static String projectionList[] = {"_id", "person", "date", "body"};
	
	
	public static MessageSearchModel getInstance() {
		return instance;
	}
	
	public void search(Search filter) {

		// SMS content provider uri 
		String url = "content://sms/inbox"; 
		Uri uri = Uri.parse(url); 
	
		String [] selectionArgList = new String[] { "_id", "thread_id", "address", "person", "date", "body", "type" };
		String selection = "";
		String arg = "";
		
		// Go through each possible search type, and build SQL query
		if (filter.getText() != null){
			selection += "body LIKE '%" + filter.getText() + "%'";
			//arg = "%" + filter.getText() + "&";
			
			//selectionArgList.add(arg);
		}
		if (filter.getContacts() != null){
			List<Contact> contacts = filter.getContacts();
			Iterator<Contact> iter = contacts.iterator();
			
			if (selection.length() > 0)
				selection += "AND";
				
			selection += "(";
			while (iter.hasNext()){
				Contact c = iter.next();
				selection += "address = " + ((Long)c.getId()).toString();
				selection += "person = " + c.getName();
				if (iter.hasNext()) selection += " OR ";
				//arg = ((Integer)c.getId()).toString();
				
				//selectionArgList.add(arg);
			}
			selection += ")";
			
		}
		
		if (filter.getBegin() != null){
			if (selection.length() > 0)
				selection += "AND";
			
			selection += "date >= " + filter.getBegin().toString();
			//arg = filter.getBegin().toString();
			
			//selectionArgList.add(arg);
		}
		
		if (filter.getEnd() != null){
			if (selection.length() > 0)
				selection += "AND";
			
			selection += "date <= " + filter.getBegin().toString();
			//arg = filter.getBegin().toString();
			
			//selectionArgList.add(arg);
		}
		
		if (filter.getType() != null){
			
			//TODO: Figure out what the types are actually supposed to be. probably not "sent" and "received"
			if (filter.getType()==SendReceiveType.SENT) {
				if (selection.length() > 0)
					selection += "AND";
				selection += "type = " + "sent";
			}
			else if (filter.getType()==SendReceiveType.RECEIVED) {
				if (selection.length() > 0)
					selection += "AND";
				selection += "type = " + "received";
			}
			//arg = filter.getBegin().toString();
			
			//selectionArgList.add(arg);
		}
		
		// Make query to content provider and store cursor to table returned
		//searchResultCursor = Activity.managedQuery(uri, projectionList, selection, selectionArgArray, "");
		Cursor searchResultCursor = MessageSearchApplication.getAppContext().getContentResolver().query(uri, projectionList, selection, selectionArgList, "date DESC");
		
		searchResults = new ArrayList<Map<String,String>>();
		
		if (searchResultCursor != null) {
            try {
                    int count = searchResultCursor.getCount();
                    if (count > 0) {
                    	searchResultCursor.moveToFirst();

//                          String[] columns = cursor.getColumnNames();
//                          for (int i=0; i<columns.length; i++) {
//                                  Log.v("columns " + i + ": " + columns[i] + ": "
//                                                  + cursor.getString(i));
//                          }

                            long messageId = searchResultCursor.getLong(0);
                            long threadId = searchResultCursor.getLong(1);
                            String address = searchResultCursor.getString(2);
                            long contactId = searchResultCursor.getLong(3);
                            String contactId_string = String.valueOf(contactId);
                            long timestamp = searchResultCursor.getLong(4);
                            String body = searchResultCursor.getString(5);
                            
                            Contact c = new Contact(contactId, MessageSource.SMS, contactId_string, null);

                            Message msg = new Message(c, messageId, threadId, body, false);
                            
                            HashMap<String, String> map = new HashMap<String,String>();
                            map.put("Message", body);
                            map.put("Time", ((Long)timestamp).toString());
                            map.put("FromAddress", address);
                            map.put("ID", ((Long)messageId).toString());
                            map.put("ThreadID", ((Long)threadId).toString());
                            map.put("ContactID", contactId_string);
                            searchResults.add(map);

                    }
            } finally {
                    searchResultCursor.close();
            }
    }          

	}
	
	public List<Map<String,String>> getSearchResults() {
		return searchResults;
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
