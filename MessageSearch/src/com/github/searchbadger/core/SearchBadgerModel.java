package com.github.searchbadger.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;

import twitter4j.DirectMessage;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.ContactSMS;
import com.github.searchbadger.util.FacebookHelper;
import com.github.searchbadger.util.Message;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SearchModel;
import com.github.searchbadger.util.SendReceiveType;
import com.github.searchbadger.util.TwitterHelper;

public class SearchBadgerModel implements SearchModel {
	private Search _currentSearch;
	
	//private Cursor searchResultCursor;
	private List<Message> searchResultMessages;
	private List<Message> threadMessages;
	private int threadMessageIndex;
	private final static String projectionList[] = {"_id", "thread_id", "address", "date", "body", "type", "person"};
	private final static String STARRED_MSGS_COLS[] = {"id", "msg_id", "msg_text", "thread_id", "date", "src_name", "author"};
	private final static String STARRED_MSGS_DEL_WHERE = "msg_id = ? AND thread_id = ? AND src_name = ?";
	protected final static String STARRED_MSGS_TABLE = "StarredMessage";
	
	private final static String RECENT_SEARCH_COLS[] = {"id", "Search_Txt", "Date_Start", "Date_End", "Type"};
	private final static String RECENT_SEARCH_TABLE = "RecentSearch";
	
	private final static String RECENT_SEARCH_CONTACTS_COLS[] = {"id", "RecentSearch_ID", "Contact_Id"};
	private final static String RECENT_SEARCH_CONTACTS_TABLE = "RecentSearchContacts";
	private final static String RECENT_SEARCH_SOURCES_COLS[] = {"id", "RecentSearch_ID", "Src_Name"};
	private final static String RECENT_SEARCH_SOURCES_TABLE = "RecentSearchSources";
	
	private final static String TWITTER_MSGS_COLS[] = {"Msg_Id", "Msg_Text", "Date", "Recipient_Id", "Recipient_Name",
		"Sender_Id", "Sender_Name", "Thread_Id"};
	private final static String TWITTER_MSGS_TABLE = "TwitterMessages";

	protected List<Search> recentSearches = null;
	protected List<Message> starredMsgs = null;
	protected SearchBadgerOpenHandler dbOH = new SearchBadgerOpenHandler(SearchBadgerApplication.getAppContext());
	protected SearchBadgerInMemoryOpenHandler inMemDbOH = new SearchBadgerInMemoryOpenHandler(SearchBadgerApplication.getAppContext());
	
	private TwitterHelper twitterHelper = SearchBadgerApplication.getTwitterHelper();
	
	public void search(Search filter) {
		this._currentSearch = filter;
		searchResultMessages = new ArrayList<Message>();
		
		List<MessageSource> sources = filter.getSources();
		Iterator<MessageSource> iter = sources.iterator();
		while (iter.hasNext()){
			MessageSource source = iter.next();
			switch(source) {
			case SMS:
				searchSMS(filter);
				break;
			case FACEBOOK:
				searchFacebook(filter);
				break;
			case TWITTER:
				searchTwitter(filter);
			case STARRED:
				searchStarred(filter);
			}
		}
		

		// TODO need to sort the results
		Collections.sort(searchResultMessages);
		
		// save the search into the recent search
		addRecentSearch(filter);
	}
	
	public void searchSMS(Search filter) {
		// SMS content provider uri 
		String url = "content://sms/"; 

		Uri uri = Uri.parse(url); 
	
		List<String> selectionArgList = new LinkedList<String>();
		String selection = "";
		String arg = "";
		
		
				
		// Go through each possible search type, and build SQL query
		if (filter.getText() != null && filter.getText().length() != 0) {
			selection += "body";
			
			// use the glob for any text that contains regex symbols for GLOB and LIKE
			if (filter.getText().contains("#") || filter.getText().contains("*") || filter.getText().contains("_") || filter.getText().contains("%")){
				selection += " GLOB ?";
				arg = filter.getGlobText();				
			}
			else{
				selection += " LIKE ?";
				arg = "%" + filter.getText() + "%";
			}

			selectionArgList.add(arg);
		}
		if (filter.getContacts() != null){
			List<Contact> contacts = filter.getContacts();
			Iterator<Contact> iter = contacts.iterator();
			if(contacts.size() != 0) {
				if (selection.length() > 0)
					selection += " AND ";
					
				selection += "(";
				boolean firstAddress = true;
				while (iter.hasNext()){
					Contact c = iter.next();
					if (c instanceof ContactSMS) {
						ContactSMS cSMS = (ContactSMS)c;
						List<String> addresses = cSMS.getAddresses();
						Iterator<String> iterAddresses = addresses.iterator();
						while (iterAddresses.hasNext()){
							if(firstAddress == true)
								firstAddress = false;
							else
								selection += " OR ";
							String address = iterAddresses.next();
							selection += "address = ?";
							selectionArgList.add(address);
						}
					}
				}
				selection += ")";				
			}			
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
		
		if (filter.getType() != null){
			if (selection.length() > 0)
				selection += " AND ";
			
			selection += "type = ?";
			if (filter.getType()==SendReceiveType.SENT) {
				arg = "2";
				selectionArgList.add(arg);
			}
			else if (filter.getType()==SendReceiveType.RECEIVED) {
				arg = "1";
				selectionArgList.add(arg);
			}
		}
				
		// Make query to content provider and store cursor to table returned
		String[] selectionArgsArray = new String[selectionArgList.size()];
		selectionArgList.toArray(selectionArgsArray);

		Cursor searchResultCursor = SearchBadgerApplication.getAppContext().getContentResolver().query(uri, projectionList, selection, selectionArgsArray, "date DESC");

		List<Contact> contactsSMS = getSMSContacts();
		if (searchResultCursor != null) {
			try {
				int count = searchResultCursor.getCount();
				if (count > 0) {
					searchResultCursor.moveToFirst();
					do {

						/*
						String[] columns = searchResultCursor.getColumnNames();
						for (int i=0; i<columns.length; i++) {
						Log.v("SearchBadger","columns " + i + ": " + columns[i] + ": "
								+ searchResultCursor.getString(i));
						}
						*/

						long messageId = searchResultCursor.getLong(0);
						String messageId_string = String.valueOf(messageId);
						long threadId = searchResultCursor.getLong(1);
						String threadId_string = String.valueOf(threadId);
						String address = searchResultCursor.getString(2);
						long timestamp = searchResultCursor.getLong(3);
						String body = searchResultCursor.getString(4);
						long type = searchResultCursor.getLong(5);
						long contactId = searchResultCursor.getLong(6);
						String contactId_string = String.valueOf(contactId);
						
						// convert address to author
						String author = address;
						Contact c = findContact(contactsSMS, address);
						if(c != null) author = c.getName();
						if(type == 2) author = "Me";
						Message msg = new Message(messageId_string, threadId_string, author, MessageSource.SMS, new Date (timestamp),
								body, false);
						searchResultMessages.add(msg);

					} while (searchResultCursor.moveToNext() == true);

				}
			} finally {
				searchResultCursor.close();
			}
		}

	}
	

	public void searchFacebook(Search filter) {

		FacebookHelper facebookHelper = SearchBadgerApplication.getFacebookHelper();

		if(facebookHelper.isReady() == false) return;
		StringBuilder query = new StringBuilder();
		query.append("SELECT thread_id, message_id, author_id, created_time, body FROM message WHERE thread_id IN (SELECT thread_id FROM thread WHERE folder_id = 0) ");
		
		// thread_id IN (SELECT thread_id FROM thread WHERE folder_id = 0) AND author_id != me() ORDER BY created_time ASC
		
		// Go through each possible search type, and build SQL query
		if (filter.getText() != null && filter.getText().length() != 0) {
			
			// use the glob for any text that contains regex symbols for GLOB and LIKE
			if (filter.containsRegEx()){
				// get all the messages then do some post process to filter them since FQL doesn't support regex			
			}
			else{
				query.append(" AND ");
				
				String textFilter = filter.getFQLText();
				query.append(" strpos(lower(body) , '" + textFilter  + "') >= 0 ");
			}

		}
		if (filter.getContacts() != null){
			List<Contact> contacts = filter.getContacts();
			Iterator<Contact> iter = contacts.iterator();
			if(contacts.size() != 0) {
				query.append(" AND ");
					
				query.append("(");
				boolean firstAddress = true;
				while (iter.hasNext()){
					Contact c = iter.next();
					String id = c.getId();
					if(firstAddress == true)
						firstAddress = false;
					else
						query.append(" OR ");
					query.append(" author_id = " + id + " ");
					
				}
				query.append(")");				
			}			
		}
		
		if (filter.getBegin() != null){
			query.append(" AND ");
			
			// NOTE: time is in unix time in seconds
			query.append(" created_time >= ");
			query.append(((Long)(filter.getBegin().getTime() / 1000L )).toString());
			
		}
		
		if (filter.getEnd() != null){
			query.append(" AND ");
			
			// NOTE: time is in unix time in seconds
			query.append(" created_time <= ");
			query.append(((Long)(filter.getEnd().getTime() / 1000L )).toString());
			
		}
		
		if (filter.getType() != null){
			query.append(" AND ");
			
			if (filter.getType()==SendReceiveType.SENT) {
				query.append(" author_id = me() ");
			}
			else if (filter.getType()==SendReceiveType.RECEIVED) {
				query.append(" author_id != me() ");
			}
		}

		// add the date sort
		query.append("  ORDER BY created_time DESC ");
		
		Log.d("SearchBadger", query.toString());
		
		// send the search request for the message search
		Bundle params = new Bundle();
        params.putString("method", "fql.query");
        params.putString("query", query.toString());
        String response;
        try {
            response = facebookHelper.facebook.request(null, params, "GET");
    		Log.d("SearchBadger", response);
        } catch (FileNotFoundException e) {
        	return;
        } catch (MalformedURLException e) {
        	return;
        } catch (IOException e) {
        	return;
        }
        
        
        // parse the json message
        String thread_id, message_id, author_id, created_time, body;
        JSONArray jsonArray;
        boolean requiresRegexFilter = filter.containsRegEx();
        Pattern p = Pattern.compile(filter.getJavaRegexText(), Pattern.CASE_INSENSITIVE);
		List<Contact> facebookContacts = getFacebookContacts();
        try {
			jsonArray = new JSONArray(response);
	        for(int i = 0; i < jsonArray.length(); i++) {
	        	thread_id = jsonArray.getJSONObject(i).getString("thread_id");
	        	message_id = jsonArray.getJSONObject(i).getString("message_id");
	        	author_id = jsonArray.getJSONObject(i).getString("author_id");
	        	created_time = jsonArray.getJSONObject(i).getString("created_time");
	        	body = jsonArray.getJSONObject(i).getString("body");
	        	
	        	// post process the regex filter
	        	if(requiresRegexFilter) {
	        		Matcher matcher = p.matcher(body);
	        		if(!matcher.find()) continue;
	        	}


				// convert address to author
				String author = "Me";
				Contact c = findContact(facebookContacts, author_id);
				if(c != null) author = c.getName();
				// TODO we should set the default author to unknown, find the id of facebook login, then
				// set author to Me if id equals facebook user id
	        	
				long timestamp = Long.parseLong(created_time) * 1000L;
				Message msg = new Message(message_id, thread_id, author, MessageSource.FACEBOOK, new Date (timestamp),
						body, false);
				searchResultMessages.add(msg);


	        }
		} catch (JSONException e) {
        	return;
		}
        
	}
	
	protected Twitter authenticate(){
		TwitterHelper helper = SearchBadgerApplication.getTwitterHelper();
		Twitter tt = helper.twitter;

		// Twitter authentication

		// Check if we have access token - if we do, no need to authenticate in that case
		AccessToken authenticated = null;
		try {
			authenticated = tt.getOAuthAccessToken();
		} catch (Exception e2) {
			Log.d("SearchBadger", "Error in returning access token");
		}

		if (authenticated == null){
			tt.setOAuthConsumer(helper.CONSUMER_KEY, helper.CONSUMER_SECRET);
			AccessToken at = new AccessToken(helper.getAccessToken(), helper.getAccessSecret());
			tt.setOAuthAccessToken(at);
		}
		
		return tt;
	}
	
	protected void authenticateAndUpdateTwitterMessages(){
		// Authenticate
		TwitterHelper helper =  SearchBadgerApplication.getTwitterHelper();
		Twitter tt = authenticate();
		
		boolean retRes = false;
		SQLiteDatabase db = null;
		
		// set app user's Twitter id
		try {
			if (helper.getUserTwitterId().length() == 0)
				helper.setUserTwitterId(String.valueOf(tt.getId()));
			
		} catch (IllegalStateException e1) {
			Log.d("SearchBadger", "State exception.");
		} catch (TwitterException e1) {
			Log.d("SearchBadger","Twitter id fetch exception.");
		}
		
		// We initially get 200 messages from Twitter.  If the latest msgId of these retrieved messages
		// do not match the latest msgId in our in-mem DB, we add all new messages we can get from
		// Twitter to our in-mem DB
		
		Paging page = new Paging(1,200);
		ResponseList<DirectMessage> allMessages = null;
		ResponseList<DirectMessage> tempMessages = null;
		try {
			tempMessages = tt.getDirectMessages(page);
		} catch (TwitterException e) {
			Log.d("SearchBadger", "Error in querying Twitter API");
		}
		
		allMessages = tempMessages;
		
		String newestMsgId = String.valueOf(allMessages.get(allMessages.size() - 1).getId());
		
		if (!newestMsgId.equals(helper.getMostRecentMsgId())){
			int pageNo = 2;
			while (tempMessages.size() == 200){
				page.setPage(pageNo);		
				try {
					tempMessages = tt.getDirectMessages(page);
				} catch (TwitterException e) {
					Log.d("SearchBadger", "Error in querying Twitter API");
				}
				
				allMessages.addAll(tempMessages);	
				
				pageNo++;
			}
			
			// add all direct messages to in-memory DB
			ContentValues vals = new ContentValues();
			
			try{
				db = inMemDbOH.getWritableDatabase();
				for (int i = 0; i < allMessages.size(); i++){
					DirectMessage msg = allMessages.get(i);
					vals.clear();
					
					vals.put("Msg_Id", msg.getId());
					vals.put("Msg_Text", msg.getText());
					vals.put("Date", msg.getCreatedAt().getTime());
					vals.put("Recipient_Id", msg.getRecipientId());
					vals.put("Recipient_Name", msg.getRecipient().getName());
					vals.put("Sender_Id", msg.getSenderId());
					vals.put("Sender_Name", msg.getSender().getName());
					vals.put("Thread_Id", helper.getUserTwitterId()+msg.getSenderId());
					retRes = ((db.insert(TWITTER_MSGS_TABLE, "Msg_Id", vals)) != -1);
					
					// set id of most recent message retrieved
					if (i == allMessages.size() - 1)
						helper.setMostRecentMsgId(String.valueOf(msg.getId()));
				}
				
			} finally {
				if (db != null) {
					// We do not close the DB bc the data in in-mem DB is gone if it is closed
					//db.close();
				}
			}
		}	

	}

	public void searchTwitter(Search filter) {
		TwitterHelper helper = SearchBadgerApplication.getTwitterHelper();
		boolean retRes = false;
		SQLiteDatabase db = null;
		
		// Authenticate and Update in-mem DB if necessary
		authenticateAndUpdateTwitterMessages();
		
		// Build query for search
		
		List<String> selectionArgList = new LinkedList<String>();
		String selection = "";
		String arg = "";
				
		// Go through each possible search type, and build SQL query
		if (filter.getText() != null && filter.getText().length() != 0) {
			selection += "Msg_Text";
			
			// use the glob for any text that contains regex symbols for GLOB and LIKE
			if (filter.getText().contains("#") || filter.getText().contains("*") || filter.getText().contains("_") || filter.getText().contains("%")){
				selection += " GLOB ?";
				arg = filter.getGlobText();				
			}
			else{
				selection += " LIKE ?";
				arg = "%" + filter.getText() + "%";
			}

			selectionArgList.add(arg);
		}
		if (filter.getContacts() != null){
			List<Contact> contacts = filter.getContacts();
			Iterator<Contact> iter = contacts.iterator();
			if(contacts.size() != 0) {
				if (selection.length() > 0)
					selection += " AND ";
					
				selection += "(";
				while (iter.hasNext()){
					Contact c = iter.next();
					selection += "Sender_Name LIKE ?";
					selectionArgList.add(c.getName());
					
					if (iter.hasNext())
						selection += " OR ";
					
				}
				selection += ")";				
			}			
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
		
		if (filter.getType() != null){
			if (selection.length() > 0)
				selection += " AND ";
			
			arg = helper.getUserTwitterId();
			selectionArgList.add(arg);
			if (filter.getType()==SendReceiveType.SENT) {
				selection += "Sender_Id = ?";
			}
			else if (filter.getType()==SendReceiveType.RECEIVED) {
				selection += "Recipient_Id = ?";
			}
		}
		
		// Make query to in-memory DB and store cursor to table returned
		String[] selectionArgsArray = new String[selectionArgList.size()];
		selectionArgList.toArray(selectionArgsArray);
				
		try{
			db = inMemDbOH.getWritableDatabase();
			Cursor searchResultCursor = db.query(TWITTER_MSGS_TABLE, TWITTER_MSGS_COLS, selection, 
					selectionArgsArray, null, null, "date DESC");
			
			if (searchResultCursor != null){
				try{
					int count = searchResultCursor.getCount();
					if (count > 0){
						searchResultCursor.moveToFirst();
						do{
							/*
							String columns[] = searchResultCursor.getColumnNames();
							for (int i = 0; i < columns.length; i++){
								Log.v("SearchBadger","columns " + i + ": " + columns[i] + ": "
										+ searchResultCursor.getString(i));
							}
							*/
							
							//"Msg_Id", "Msg_Text", "Date", "Thread_Id", "Sender_Name"
							String messageIdString = searchResultCursor.getString(0);
							String messageTextString = searchResultCursor.getString(1);
							long timestamp = searchResultCursor.getLong(2);
							String author = searchResultCursor.getString(6);
							String threadIdString = searchResultCursor.getString(7);
							
							Message msg = new Message(messageIdString, threadIdString, author, MessageSource.TWITTER, 
									new Date (timestamp),messageTextString, false);
							searchResultMessages.add(msg);
						} while (searchResultCursor.moveToNext() == true);
					}
				}finally {
					searchResultCursor.close();
				}
			}
			
		}finally {
			if (db != null) {
				// We do not close the DB bc the data in in-mem DB is gone if it is closed
				//db.close();
			}
		}
	}

	public void searchStarred(Search filter) {
		
		List<String> selectionArgList = new LinkedList<String>();
		String selection = "";
		String arg = "";
		
		// Go through each possible search type, and build SQL query
		if (filter.getText() != null && filter.getText().length() != 0) {
			selection += "msg_text";
			
			// use the glob for any text that contains regex symbols for GLOB and LIKE
			if (filter.getText().contains("#") || filter.getText().contains("*") || filter.getText().contains("_") || filter.getText().contains("%")){
				selection += " GLOB ?";
				arg = filter.getGlobText();				
			}
			else{
				selection += " LIKE ?";
				arg = "%" + filter.getText() + "%";
			}

			selectionArgList.add(arg);
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
		
		if (filter.getType() != null){
			if (selection.length() > 0)
				selection += " AND ";
			
			if (filter.getType()==SendReceiveType.SENT) {
				selection += "author = ?";
				arg = "Me";
				selectionArgList.add(arg);
			}
			else if (filter.getType()==SendReceiveType.RECEIVED) {
				selection += "author != ?"; // TODO
				arg = "Me";
				selectionArgList.add(arg);
			}
		}
				
		// Make query to content provider and store cursor to table returned
		String[] selectionArgsArray = new String[selectionArgList.size()];
		selectionArgList.toArray(selectionArgsArray);

		

		SQLiteDatabase db = null;
		try {
			db = dbOH.getWritableDatabase();
		
			Cursor searchResultCursor = db.query(STARRED_MSGS_TABLE, STARRED_MSGS_COLS, 
					selection, selectionArgsArray, null, null, null);

			if (searchResultCursor != null) {
				try {
					int count = searchResultCursor.getCount();
					if (count > 0) {
						searchResultCursor.moveToFirst();
						do {

							/*
							String[] columns = searchResultCursor.getColumnNames();
							for (int i=0; i<columns.length; i++) {
							Log.v("SearchBadger","columns " + i + ": " + columns[i] + ": "
									+ searchResultCursor.getString(i));
							}
							*/

							//"id", "msg_id", "msg_text", "thread_id", "date", "src_name", "author"	
							String messageId_string = searchResultCursor.getString(1);
							String body = searchResultCursor.getString(2);
							String threadId_string = searchResultCursor.getString(3);
							long timestamp = searchResultCursor.getLong(4);
							String srcName = searchResultCursor.getString(5);
							MessageSource source = MessageSource.valueOf(srcName);
							String author = searchResultCursor.getString(6);
														
							Message msg = new Message(messageId_string, threadId_string, author, source, new Date (timestamp),
									body, false);
							searchResultMessages.add(msg);

						} while (searchResultCursor.moveToNext() == true);

					}
				} finally {
					searchResultCursor.close();
				}
			}
			

		} finally {
			if (db != null) {
				db.close();
			}
		}
		

	}
	
	
	public List<Message> getSearchResults() {
		return searchResultMessages;
	}
	
	public Search getCurrentSearch() {
		return _currentSearch;
	}
	
	
	/*
	 * TODO: interface with database for persistent storage of starred msgs
	 */
	public List<Message> getStarredMessages() {
		SQLiteDatabase db = null;
		try {
			db = dbOH.getWritableDatabase();
		
			if (this.starredMsgs == null) {
				Cursor starredMsgsCursor = db.query(STARRED_MSGS_TABLE, STARRED_MSGS_COLS, 
						null, null, null, null, null);//"date");
				if (starredMsgsCursor != null) {
					starredMsgs = new ArrayList<Message>();
					if (starredMsgsCursor.getCount() > 0) {
						try {
							starredMsgsCursor.moveToFirst();
							starredMsgs = new ArrayList<Message>();
							do {
								//{"id", "msg_id", "msg_text", "thread_id", "src_name"};
								//date, msg_id, thread_id, msg_text, name
								String msg_id = starredMsgsCursor.getString(1);
								String msg_text = starredMsgsCursor.getString(2);
								String thread_id = starredMsgsCursor.getString(3);
								Long long_date = starredMsgsCursor.getLong(4);
								Date msg_date = new Date(long_date);
								String srcName = starredMsgsCursor.getString(5);
								String author = starredMsgsCursor.getString(6);
								//Message msg = new Message();
								if (!starredMsgs.add(new Message( 
										msg_id, thread_id, author, MessageSource.valueOf(srcName), msg_date, msg_text, true))) {
									starredMsgs = null;
									return null;
								}
							} while (starredMsgsCursor.moveToNext());
						} finally {
							starredMsgsCursor.close();
						}
					}
					else{
						starredMsgsCursor.close();
					}
				}
			}
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return starredMsgs;
	}
	
	/*
	 * TODO: interface with database for persistent storage of starred msgs
	 */
	public boolean addStarredMessage(Message msg) {
		ContentValues vals = new ContentValues();
		boolean retRes = false;
		/*public static final String STARRED_MESSAGE_CREATE = "CREATE TABLE StarredMessage" +
				"(Id INTEGER PRIMARY KEY ASC, TEXT Msg_Id, TEXT Msg_Text, BIGINT Date, TEXT Thread_Id, INTEGER Src_Id);";*/
		if (starredMsgs == null) {
			this.getStarredMessages();
		}
		msg.toogleStarred();
		if (!starredMsgs.add(msg)) {
			return false;
		}
		
		SQLiteDatabase db = null;
		try {
			db = dbOH.getWritableDatabase();
		
			vals.put("Msg_Id", msg.getId());
			vals.put("Msg_Text", msg.getText());
			vals.put("Date", msg.getDate().getTime());
			vals.put("Thread_Id", msg.getThreadId());
			vals.put("src_name", msg.getSource().toString());
			vals.put("author", msg.getAuthor());
			retRes = ((db.insert(STARRED_MSGS_TABLE, "Msg_Id", vals)) != -1);
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return retRes;
	}
	
	/*
	 * TODO: interface with database for persistent storage of starred msgs
	 */
	public boolean removeStarredMessage(Message msg) {
		ContentValues vals = new ContentValues();
		boolean retRes = false;
		/*public static final String STARRED_MESSAGE_CREATE = "CREATE TABLE StarredMessage" +
				"(Id INTEGER PRIMARY KEY ASC, TEXT Msg_Id, TEXT Msg_Text, BIGINT Date, TEXT Thread_Id, INTEGER Src_Id);";*/
		if (starredMsgs == null) {
			this.getStarredMessages();
		}
		msg.toogleStarred();
		if (!starredMsgs.remove(msg)) {
			return false;
		}
		
		SQLiteDatabase db = null;
		String whereArgs[] = {msg.getId(), msg.getThreadId(), msg.getSource().toString()};
		try {
			db = dbOH.getWritableDatabase();
			retRes = ((db.delete(STARRED_MSGS_TABLE, STARRED_MSGS_DEL_WHERE, whereArgs)) == 1);
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return retRes;
	}	
	
	public boolean containsStarredMessage(Message message) {
		List<Message> starredMessages = getStarredMessages();
		if(starredMessages == null) return false;
		
		Iterator<Message> iter = starredMessages.iterator();
		while(iter.hasNext()) {
			Message m = iter.next();
			if(m.equals(message)) return true;
		}
		
		return false;
	} 

	
	public boolean hasRecentSearchesBeenLoaded(){
		if (this.recentSearches == null) return false;
		return true;
	}
	
	/*
	 * A list of recent searches is returned. 
	 * Note: not messages, these are actual searches.
	 * The search can be performed again by the user, who sees the filters set
	 * the searches are stored in a database so that we can access them again 
	 * even if the user shuts off their phone
	 */
	public List<Search> getRecentSearches() {
		SQLiteDatabase db = null; 
		try {
			db = dbOH.getWritableDatabase();
		
			if (this.recentSearches == null) {
				Cursor recentSearchesCursor = db.query(RECENT_SEARCH_TABLE, RECENT_SEARCH_COLS, 
						null, null, null, null, "id DESC LIMIT 20"); // TODO hard coded limit here. probably want to move this
				if (recentSearchesCursor != null) {
					recentSearches = new ArrayList<Search>();
					if (recentSearchesCursor.getCount() > 0) {
						try {
							recentSearchesCursor.moveToFirst();
							recentSearches = new ArrayList<Search>();
							do {

								Long id = recentSearchesCursor.getLong(0);
								String Search_Txt = recentSearchesCursor.getString(1);
								
								Date dateStart = null;
								Long Date_Start = recentSearchesCursor.getLong(2);
								if(Date_Start != null && Date_Start != 0) dateStart = new Date(Date_Start);

								Date dateEnd = null;
								Long Date_End = recentSearchesCursor.getLong(3);
								if(Date_End != null && Date_End != 0) dateEnd = new Date(Date_End);
								
								SendReceiveType type = null;
								String Type = recentSearchesCursor.getString(4);
								if(Type != null) type = SendReceiveType.valueOf(Type);

								List<MessageSource> sources = null;
								Cursor sourcesCursor = db.query(RECENT_SEARCH_SOURCES_TABLE, RECENT_SEARCH_SOURCES_COLS, 
										"RecentSearch_ID = ?", new String[] { id.toString() }, null, null, null);
								if(sourcesCursor != null) {
									try { 
										if(sourcesCursor.getCount() > 0) {
											
											sources = new LinkedList<MessageSource>();	
											while(sourcesCursor.moveToNext()){
												String srcName = sourcesCursor.getString(2);
												sources.add(MessageSource.valueOf(srcName));
											}
										}
									} finally {
										sourcesCursor.close();
									}
								}
								
								List<Contact> selectContacts = null;
								Cursor contactsCursor = db.query(RECENT_SEARCH_CONTACTS_TABLE, RECENT_SEARCH_CONTACTS_COLS, 
										"RecentSearch_ID = ?", new String[] { id.toString() }, null, null, null);
								if(contactsCursor != null) {
									try {
										if(contactsCursor.getCount() > 0 && sources.size() == 1) {

											// TODO this might be slow if there are a bunch recent searches
											// with selected contacts (do we want to cash getContacts?)
											List<Contact> contacts = getContacts(sources.get(0));
											
											selectContacts = new ArrayList<Contact>();	
											while(contactsCursor.moveToNext()){
												
												String Contact_Id = contactsCursor.getString(2);
												Contact c = findContact(contacts, Contact_Id);
												if(c != null)
													selectContacts.add(c);
											}
										}
									} finally {
										contactsCursor.close();
									}
								}
								
								if (!recentSearches.add(new Search( 
										Search_Txt, dateStart, dateEnd, 
										sources, selectContacts, type))) {
									recentSearches = null;
									return null;
								}
							} while (recentSearchesCursor.moveToNext());
						} finally {
							recentSearchesCursor.close();
						}
					}
					else{
						recentSearchesCursor.close();
					}
				}
			}
		} catch (Exception e) {
			
		}
		finally {
			if (db != null) {
				db.close();
			}
		}
		return recentSearches;
	}
	

	public boolean addRecentSearch(Search search) {
		ContentValues vals = new ContentValues();
		boolean retRes = false;
		if (recentSearches == null) {
			this.getRecentSearches();
		}
		recentSearches.add(0, search);
		
		// TODO limiting searches to last 20
		while(recentSearches.size() > 20) {
			recentSearches.remove(20);
		}
		
		SQLiteDatabase db = null;
		try {
			db = dbOH.getWritableDatabase();
		
			vals.put("Search_Txt", search.getText());
			if(search.getBegin() != null) vals.put("Date_Start", search.getBegin().getTime());
			if(search.getEnd() != null) vals.put("Date_End", search.getEnd().getTime());
			if(search.getType() != null) vals.put("Type", search.getType().toString());
			long row_id = db.insert(RECENT_SEARCH_TABLE, null, vals);
			retRes = (row_id == -1);
			
			List<MessageSource> sources = search.getSources();
			if(sources != null) {
				Iterator<MessageSource> iter = sources.iterator();
				while(iter.hasNext()) {
					MessageSource source = iter.next();
					ContentValues valsSource = new ContentValues();
					valsSource.put("Src_Name", source.toString());
					valsSource.put("RecentSearch_ID", row_id);
					retRes |= ((db.insert(RECENT_SEARCH_SOURCES_TABLE, null, valsSource)) != -1);
				}
			}
			
			List<Contact> selectedContacts = search.getContacts();
			if(selectedContacts != null) {
				Iterator<Contact> iter = selectedContacts.iterator();
				while(iter.hasNext()) {
					Contact contact = iter.next();
					ContentValues valsContacts = new ContentValues();
					valsContacts.put("Contact_Id", contact.getId());
					valsContacts.put("RecentSearch_ID", row_id);
					retRes |= ((db.insert(RECENT_SEARCH_CONTACTS_TABLE, null, valsContacts)) != -1);
				}
			}
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return retRes;
	}
	
	
	public void clearRecentSearches() {
		if(recentSearches != null) recentSearches.clear();
		
		SQLiteDatabase db = null;
		try {
			db = dbOH.getWritableDatabase();
			db.delete(RECENT_SEARCH_TABLE, null, null);
			db.delete(RECENT_SEARCH_SOURCES_TABLE, null, null);
			db.delete(RECENT_SEARCH_CONTACTS_TABLE, null, null);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}	
	
	/*
	 * TODO Needs to access database. This probably will go through some other 
	 * class for SMSSearch or other search type
	 */
	public List<Message> getThread(Message message) {
		Message msgInThread = message;
		switch(msgInThread.getSource()) {
		case SMS:
			return getThreadSMS(message);
		case FACEBOOK:
			return getThreadFacebook(message);
		case TWITTER:
			return getThreadTwitter(message);
		}
		
		return null;
	}
	
	public void resetLastThread() {
		threadMessages = null;
	}
	
	public List<Message> getLastThread() {
		return threadMessages;
	}
	
	public int getLastThreadIndex() {
		return threadMessageIndex;
	}
	
	public List<Message> getThreadSMS(Message message) {
		// SMS content provider uri 
		Message msgInThread = message;
		
		String url = "content://sms"; 
		Uri uri = Uri.parse(url); 

		String[] selectionArgs = new String[]{msgInThread.getThreadId().toString()};
		String selection = "thread_id = ?";
		Log.d("SearchBadger", msgInThread.getThreadId().toString());
		
		Cursor searchResultCursor = SearchBadgerApplication.getAppContext().getContentResolver().query(uri, projectionList, selection, selectionArgs, "date ASC");
		
		List<Contact> contactsSMS = getSMSContacts();
		threadMessages = new LinkedList<Message>();
		if (searchResultCursor != null) {
			try {
				int count = searchResultCursor.getCount();
				if (count > 0) {
					searchResultCursor.moveToFirst();
					int index = 0;
					do {

						/*
						String[] columns = searchResultCursor.getColumnNames();
						for (int i=0; i<columns.length; i++) {
						Log.v("SearchBadger","columns " + i + ": " + columns[i] + ": "
								+ searchResultCursor.getString(i));
						}
						*/
						
						long messageId = searchResultCursor.getLong(0);
						String messageId_string = String.valueOf(messageId);
						long threadId = searchResultCursor.getLong(1);
						String threadId_string = String.valueOf(threadId);
						String address = searchResultCursor.getString(2);
						long timestamp = searchResultCursor.getLong(3);
						String body = searchResultCursor.getString(4);
						long type = searchResultCursor.getLong(5);
						long contactId = searchResultCursor.getLong(6);
						//String contactId_string = String.valueOf(contactId);
						

						// convert address to author
						String author = address;
						Contact c = findContact(contactsSMS, address);
						if(c != null) author = c.getName();
						if(type == 2) author = "Me";
						
						Message msg = new Message(messageId_string, threadId_string, author, MessageSource.SMS, new Date (timestamp),
								body, false);
						threadMessages.add(msg);
						
						if(msg.equals(message)) threadMessageIndex = index;
						index++;

					} while (searchResultCursor.moveToNext() == true);

				}
			} finally {
				searchResultCursor.close();
			}
		}

		return threadMessages;
	}
	
	public List<Message> getThreadFacebook(Message message) {
		
		Message msgInThread = message;

		FacebookHelper facebookHelper = SearchBadgerApplication.getFacebookHelper();
		if(facebookHelper.isReady() == false) return null;
		StringBuilder query = new StringBuilder();
		query.append("SELECT thread_id, message_id, author_id, created_time, body FROM message WHERE thread_id = ");
		query.append(msgInThread.getThreadId());
		query.append(" ORDER BY created_time ASC ");

		// send the search request for the message search
		Bundle params = new Bundle();
        params.putString("method", "fql.query");
        params.putString("query", query.toString());
        String response;
        try {
            response = facebookHelper.facebook.request(null, params, "GET");
    		Log.d("SearchBadger", response);
        } catch (FileNotFoundException e) {
        	return null;
        } catch (MalformedURLException e) {
        	return null;
        } catch (IOException e) {
        	return null;
        }
        
        
        // parse the json message
        String thread_id, message_id, author_id, created_time, body;
        JSONArray jsonArray;
		List<Contact> facebookContacts = getFacebookContacts();
		threadMessages = new LinkedList<Message>();
        try {
			jsonArray = new JSONArray(response);
	        for(int i = 0; i < jsonArray.length(); i++) {
	        	thread_id = jsonArray.getJSONObject(i).getString("thread_id");
	        	message_id = jsonArray.getJSONObject(i).getString("message_id");
	        	author_id = jsonArray.getJSONObject(i).getString("author_id");
	        	created_time = jsonArray.getJSONObject(i).getString("created_time");
	        	body = jsonArray.getJSONObject(i).getString("body");
	        	

				// convert address to author
				String author = "Me";
				Contact c = findContact(facebookContacts, author_id);
				if(c != null) author = c.getName();
				// TODO we should set the default author to unknown, find the id of facebook login, then
				// set author to Me if id equals facebook user id
	        	
				long timestamp = Long.parseLong(created_time) * 1000L;
				Message msg = new Message(message_id, thread_id, author, MessageSource.FACEBOOK, new Date (timestamp),
						body, false);
				threadMessages.add(msg);
				
				if(msg.equals(message)) threadMessageIndex = i;

	        }
		} catch (JSONException e) {
        	return null;
		}
        
		return threadMessages;
	}
	
	public List<Message> getThreadTwitter(Message message){
		Message msgInThread = message;
		List<Message> threadMessages = new LinkedList<Message>();
		
		// Authenticate and update in-mem DB if necessary
		authenticateAndUpdateTwitterMessages();
		
		// Carry out query to find all messages with the desired thread_id
		String[] selectionArgs = new String[]{msgInThread.getThreadId().toString()};
		String selection = "thread_id = ?";
		Log.d("SearchBadger", msgInThread.getThreadId().toString());
		
		SQLiteDatabase db = null;
		
		try{
			db = inMemDbOH.getWritableDatabase();
			Cursor threadResultCursor = db.query(TWITTER_MSGS_TABLE, TWITTER_MSGS_COLS, selection, 
					selectionArgs, null, null, "date DESC");
			
			if (threadResultCursor != null){
				try{
					int count = threadResultCursor.getCount();
					if (count > 0){
						threadResultCursor.moveToFirst();
						do{
							/*
							String columns[] = threadResultCursor.getColumnNames();
							for (int i = 0; i < columns.length; i++){
								Log.v("SearchBadger","columns " + i + ": " + columns[i] + ": "
										+ threadResultCursor.getString(i));
							}
							*/
							
							//"Msg_Id", "Msg_Text", "Date", "Thread_Id", "Sender_Name"
							String messageIdString = threadResultCursor.getString(0);
							String messageTextString = threadResultCursor.getString(1);
							long timestamp = threadResultCursor.getLong(2);
							String author = threadResultCursor.getString(6);
							String threadIdString = threadResultCursor.getString(7);
							
							Message msg = new Message(messageIdString, threadIdString, author, MessageSource.TWITTER, 
									new Date (timestamp),messageTextString, false);
							threadMessages.add(msg);
						} while (threadResultCursor.moveToNext() == true);
					}
				}finally {
					threadResultCursor.close();
				}
			}
			
		}finally {
			if (db != null) {
				// We do not close the DB bc the data in in-mem DB is gone if it is closed
				//db.close();
			}
		}
		
		return threadMessages;
		
	}
	
	
	/*
	 * Given a message source this gets the contacts for that source.�
	 */
	public List<Contact> getContacts(MessageSource source) {
		switch(source) {
		case SMS:
			return getSMSContacts();
		case FACEBOOK:
			return getFacebookContacts();
		case TWITTER:
			return getTwitterContacts();
		}
		
		return null;
	}

	private static final String[] CONTACT_PROJECTION = new String[] {
			Contacts._ID, Contacts.DISPLAY_NAME };
	
	protected Contact findContact(List<Contact> contacts, String id) {
		if(contacts == null) return null;
		Iterator<Contact> iter = contacts.iterator();
		while(iter.hasNext()) {
			Contact c = iter.next();
			if(c.contains(id)) return c;
		}
		return null;
	}
	
	protected List<Contact> getSMSContacts() {
		List<Contact> contacts = null; 
		
		// get the list of contacts
		Cursor cursor = SearchBadgerApplication.getAppContext().getContentResolver().query(Contacts.CONTENT_URI,
				CONTACT_PROJECTION, null, null, null);
		
		// go through all the contacts
		if (cursor != null) {
			int count = cursor.getCount();
			contacts = new ArrayList<Contact>(count);
			try {
				if (count > 0) {
					cursor.moveToFirst();
					do {
//Contacts.openContactPhotoInputStream(SearchBadgerApplication.getAppContext().getContentResolver(), contactUri)
						// get the id and name
						String contactId = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
						String contactName = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
						
						// get all the number for the contact
						List<String> addresses = null;
						List<String> selectionArgList = new LinkedList<String>();
						selectionArgList.add(contactId);
						String[] selectionArgsArray = new String[selectionArgList.size()];
						selectionArgList.toArray(selectionArgsArray);
						
						Cursor c = SearchBadgerApplication.getAppContext().getContentResolver().query(
								Phone.CONTENT_URI, 
								new String[]{Phone.NUMBER, Phone.TYPE}, 
								Phone.CONTACT_ID + " = ? ", /*" DISPLAY_NAME = '"+contactName+"'",*/
								selectionArgsArray, 
			                    null); // TODO
						try {
							addresses = new ArrayList<String>(c.getCount());
				            while(c.moveToNext()){
				                switch(c.getInt(c.getColumnIndex(Phone.TYPE))){
				                case Phone.TYPE_MOBILE :
				                case Phone.TYPE_HOME :
				                case Phone.TYPE_WORK :
				                case Phone.TYPE_OTHER :
				                	addresses.add(c.getString(c.getColumnIndex(Phone.NUMBER)));
				                	break;
				                }
				            }
						}finally {
							c.close();
						}

						// add the new contact to the list
						ContactSMS contact = new ContactSMS(
								contactId,
								MessageSource.SMS,
								contactName,
								null,
								addresses);
						contacts.add(contact);
						
					} while (cursor.moveToNext() == true);

				}
			} finally {
				cursor.close();
			}
		}
		
		return contacts;
	}
	

	protected List<Contact> getFacebookContacts() {
		List<Contact> contacts = null; 
		FacebookHelper facebookHelper = SearchBadgerApplication.getFacebookHelper();
		
		// send the GET request for the facebook contact list
		if(facebookHelper.isReady() == false) return null;
		String query = "select name, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) order by name";
        Bundle params = new Bundle();
        params.putString("method", "fql.query");
        params.putString("query", query);
        String response;
        try {
            response = facebookHelper.facebook.request(null, params, "GET");
        } catch (FileNotFoundException e) {
        	return null;
        } catch (MalformedURLException e) {
        	return null;
        } catch (IOException e) {
        	return null;
        }
        
        long id;
        String name;
        String pic;
        JSONArray jsonArray;
        try {
			jsonArray = new JSONArray(response);
	        contacts = new ArrayList<Contact>(jsonArray.length());
	        for(int i = 0; i < jsonArray.length(); i++) {
	        	id = jsonArray.getJSONObject(i).getLong("uid");
	        	name = jsonArray.getJSONObject(i).getString("name");
	        	pic = jsonArray.getJSONObject(i).getString("pic_square");
	        	

				// add the new contact to the list
				Contact contact = new Contact(
						String.valueOf(id),
						MessageSource.FACEBOOK,
						name,
						null,
						pic);
				contacts.add(contact);
	        }
		} catch (JSONException e) {
        	return null;
		}
		
		return contacts;
	}
	
	protected List<Contact> getTwitterContacts(){
		List<Contact> contacts = new LinkedList<Contact>();
		
		TwitterHelper helper = SearchBadgerApplication.getTwitterHelper();
		Twitter tt = authenticate();
		
		// Get ids of friends and followers and use that to create Contacts to display in search
		IDs followerIds = null;
		IDs friendIds = null;
		try {
			followerIds = tt.getFollowersIDs(-1);
			friendIds = tt.getFriendsIDs(-1);
			
			long[] ids;
			long[] targets = new long[100];
			
			// run twice to get all info for followers and people you are following 
			for (int k = 0; k < 2; k++){
				if (k == 0)
					ids = followerIds.getIDs();
				else
					ids = friendIds.getIDs();
			
				// Twitter api only allows fetching of 100 contact ids at a time
				int i = 0, len = 0;
				for (len = ids.length; i < len; i += 100){
					int endingIndex = Math.min(100, len - i);
					System.arraycopy(ids, i, targets, 0, endingIndex);
					List<User> followerList = tt.lookupUsers(targets);
					for (int j = 0; j < followerList.size(); j++){
						Contact c = new Contact(String.valueOf(followerList.get(j).getId()),
								MessageSource.TWITTER,
								followerList.get(j).getName(),
								null,
								followerList.get(j).getProfileImageURL().toString());
						if(!contacts.contains(c))
							contacts.add(c);
					}
				}
			}
			
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			Log.d("SearchBadger", "Error in retrieving Twitter contacts.");
		}
		
		return contacts;
		
	}
    protected class SearchBadgerInMemoryOpenHandler extends SQLiteOpenHelper{
    	public static final int DATABASE_VERSION = 3;
    	
    	public static final String TWITTER_MESSAGES_CREATE = "CREATE TABLE " + TWITTER_MSGS_TABLE +
    			"(Id INTEGER PRIMARY KEY ASC, Msg_Id TEXT, Msg_Text TEXT, "+
    			"Date BIGINT Date, Recipient_Id TEXT, Recipient_Name TEXT, " +
    			"Sender_Id TEXT, Sender_Name TEXT, Thread_Id TEXT);";
    	
    	public SearchBadgerInMemoryOpenHandler(Context context){
    		// passing in null (2nd param) as name of DB is what keeps it as in-memory DB
    		super(context, null, null, DATABASE_VERSION);
    	}
    	
    	@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TWITTER_MESSAGES_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TWITTER_MSGS_TABLE);
			onCreate(db);
		}
    }
    
    protected class SearchBadgerOpenHandler extends SQLiteOpenHelper {
		public static final int DATABASE_VERSION = 3;
		public static final String DATABASE_NAME = "searchbadger";
		
		public static final String STARRED_MESSAGE_CREATE = "CREATE TABLE " + STARRED_MSGS_TABLE +
				"(Id INTEGER PRIMARY KEY ASC, Contact_Id TEXT, Msg_Id TEXT, Msg_Text TEXT, " +
				"Date BIGINT Date, Thread_Id TEXT, Src_Name TEXT, author TEXT);";

		public static final String RECENT_SEARCH_CREATE = "CREATE TABLE " + RECENT_SEARCH_TABLE +
				"(Id INTEGER PRIMARY KEY ASC, Search_Txt TEXT, " +
				"Date_Start BIGINT Date, Date_End BIGINT Date, Type TEXT);";

		public static final String RECENT_SEARCH_CONTACTS_CREATE = "CREATE TABLE " + RECENT_SEARCH_CONTACTS_TABLE +
				"(Id INTEGER PRIMARY KEY ASC, RecentSearch_ID INTEGER, Contact_Id TEXT);";
		
		public static final String RECENT_SEARCH_SOURCES_CREATE = "CREATE TABLE " + RECENT_SEARCH_SOURCES_TABLE +
				"(Id INTEGER PRIMARY KEY ASC, RecentSearch_ID INTEGER, Src_Name TEXT);";
		
		public SearchBadgerOpenHandler(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(STARRED_MESSAGE_CREATE);	
			db.execSQL(RECENT_SEARCH_CREATE);	
			db.execSQL(RECENT_SEARCH_CONTACTS_CREATE);
			db.execSQL(RECENT_SEARCH_SOURCES_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + STARRED_MSGS_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + RECENT_SEARCH_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + RECENT_SEARCH_CONTACTS_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + RECENT_SEARCH_SOURCES_TABLE);
			onCreate(db);
		}
	}
}
