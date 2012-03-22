package com.github.searchbadger.core;

import java.text.SimpleDateFormat;
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
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;

import com.github.searchbadger.R;

public class MessageSearchModel {
	private final static MessageSearchModel instance = new MessageSearchModel();
	private Search _currentSearch;
	
	//private Cursor searchResultCursor;
	private List<Map<String,String>> searchResults;
	private List<Message> searchResultMessages;
	private final static String projectionList[] = {"_id", "thread_id", "address", "date", "body", "type"};
	
	
	public static MessageSearchModel getInstance() {
		return instance;
	}
	
	public void search(Search filter) {
		this._currentSearch = filter;
		// SMS content provider uri 
		String url = "content://sms"; 
		Uri uri = Uri.parse(url); 
	
		List<String> selectionArgList = new LinkedList<String>();
		String selection = "";
		String arg = "";
		
		// Go through each possible search type, and build SQL query
		if (filter.getText() != null && filter.getText().length() != 0) {
			selection += "body LIKE ?";
			arg = DatabaseUtils.sqlEscapeString("'%" + filter.getText() + "%'");
			selectionArgList.add(arg);
		}
		if (filter.getContacts() != null){
			List<Contact> contacts = filter.getContacts();
			Iterator<Contact> iter = contacts.iterator();
			
			if (selection.length() > 0)
				selection += " AND ";
				
			selection += "(";
			while (iter.hasNext()){
				Contact c = iter.next();
				selection += "address = ?";
				arg = ((Long)c.getId()).toString();
				selectionArgList.add(arg);
				if (iter.hasNext()) selection += " OR ";
			}
			selection += ")";
			
		}
		
		if (filter.getBegin() != null){
			if (selection.length() > 0)
				selection += " AND ";
			
			selection += "date >= ?";
			arg = ((Long)(filter.getBegin().getTime())).toString();
			
			selectionArgList.add(arg);
		}
		
		if (filter.getEnd() != null){
			if (selection.length() > 0)
				selection += " AND ";
			
			selection += "date <= ?";
			arg = ((Long)(filter.getEnd().getTime())).toString();
			
			selectionArgList.add(arg);
		}
		/*
		if (filter.getType() != null){
			
			//TODO: Figure out what the types are actually supposed to be. probably not "sent" and "received"
			if (filter.getType()==SendReceiveType.SENT) {
				if (selection.length() > 0)
					selection += " AND ";
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
		*/
		// Make query to content provider and store cursor to table returned
		String[] selectionArgsArray = new String[selectionArgList.size()];
		selectionArgList.toArray(selectionArgsArray);
		Cursor searchResultCursor = MessageSearchApplication.getAppContext().getContentResolver().query(uri, projectionList, selection, selectionArgsArray, "date DESC");
		
		searchResults = new ArrayList<Map<String,String>>();
		searchResultMessages = new ArrayList<Message>();
		if (searchResultCursor != null) {
			try {
				int count = searchResultCursor.getCount();
				if (count > 0) {
					searchResultCursor.moveToFirst();
					do {

						String[] columns = searchResultCursor.getColumnNames();
						for (int i=0; i<columns.length; i++) {
						Log.v("SearchBadger","columns " + i + ": " + columns[i] + ": "
								+ searchResultCursor.getString(i));
						}

						long messageId = searchResultCursor.getLong(0);
						long threadId = searchResultCursor.getLong(1);
						String address = searchResultCursor.getString(2);
						long contactId = searchResultCursor.getLong(3);
						String contactId_string = String.valueOf(contactId);
						long timestamp = searchResultCursor.getLong(4);
						String body = searchResultCursor.getString(5);
						
						Contact c = new Contact(contactId, MessageSource.SMS,
								contactId_string, null);
						
						Message msg = new Message(c, messageId, threadId, new Date (timestamp),
								body, false);
						searchResultMessages.add(msg);

						HashMap<String, String> map = new HashMap<String, String>();
						map.put("Message", body);
						String date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date (timestamp));
						map.put("Date", date);
						map.put("FromAddress", address);
						map.put("ID", ((Long) messageId).toString());
						map.put("ThreadID", ((Long) threadId).toString());
						map.put("ContactID", contactId_string);
						searchResults.add(map);
					} while (searchResultCursor.moveToNext() == true);

				}
			} finally {
				searchResultCursor.close();
			}
		}

	}
	
	public List<Map<String,String>> getSearchResultsMap() {
		return searchResults;
	}
	
	public List<Message> getSearchResults() {
		return searchResultMessages;
	}
	
	public Search getCurrentSearch() {
		return _currentSearch;
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
	public List<Message> getThread(Message msgInThread) {
		// SMS content provider uri 
		String url = "content://sms"; 
		Uri uri = Uri.parse(url); 

		String[] selectionArgs = new String[]{((Long)msgInThread.getThreadId()).toString()};
		String selection = "thread_id = ?";
		
		Cursor searchResultCursor = MessageSearchApplication.getAppContext().getContentResolver().query(uri, projectionList, selection, selectionArgs, "date DESC");
		
		List<Map<String,String>> threadMap = new LinkedList<Map<String,String>>();
		List<Message> threadMessages = new LinkedList<Message>();
		if (searchResultCursor != null) {
			try {
				int count = searchResultCursor.getCount();
				if (count > 0) {
					searchResultCursor.moveToFirst();
					do {

						String[] columns = searchResultCursor.getColumnNames();
						for (int i=0; i<columns.length; i++) {
						Log.v("SearchBadger","columns " + i + ": " + columns[i] + ": "
								+ searchResultCursor.getString(i));
						}

						long messageId = searchResultCursor.getLong(0);
						long threadId = searchResultCursor.getLong(1);
						String address = searchResultCursor.getString(2);
						long contactId = searchResultCursor.getLong(3);
						String contactId_string = String.valueOf(contactId);
						long timestamp = searchResultCursor.getLong(4);
						String body = searchResultCursor.getString(5);
						
						Contact c = new Contact(contactId, MessageSource.SMS,
								contactId_string, null);
						
						Message msg = new Message(c, messageId, threadId, new Date (timestamp),
								body, false);
						threadMessages.add(msg);

						HashMap<String, String> map = new HashMap<String, String>();
						map.put("Message", body);
						String date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date (timestamp));
						map.put("Date", date);
						map.put("FromAddress", address);
						map.put("ID", ((Long) messageId).toString());
						map.put("ThreadID", ((Long) threadId).toString());
						map.put("ContactID", contactId_string);
						threadMap.add(map);
					} while (searchResultCursor.moveToNext() == true);

				}
			} finally {
				searchResultCursor.close();
			}
		}

		return threadMessages;
	}
	
	/*
	 * Given a message source this gets the contacts for that source.§
	 */
	public List<Contact> getContacts(MessageSource source) {
		return null;
	}
	
}
