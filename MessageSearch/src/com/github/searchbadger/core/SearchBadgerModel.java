package com.github.searchbadger.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
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

public class SearchBadgerModel implements SearchModel {
	private Search _currentSearch;
	
	//private Cursor searchResultCursor;
	private List<Map<String,String>> searchResults;
	private List<Message> searchResultMessages;
	private final static String projectionList[] = {"_id", "thread_id", "address", "date", "body", "type", "person"}
	;
	private final static String STARRED_MSGS_COLS[] = {"id", "msg_id", "msg_text", "thread_id", "date", "src_name"};
	private final static String STARRED_MSGS_DEL_WHERE = "msg_id = ? AND thread_id = ? AND src_name = ?";
	private final static String STARRED_MSGS_TABLE = "StarredMessage";
	protected List<Message> starredMsgs = null;
	protected SearchBadgerOpenHandler dbOH;
	
	public void search(Search filter) {
		this._currentSearch = filter;
		searchResultMessages = new ArrayList<Message>();
		searchResults = new ArrayList<Map<String,String>>();
		
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
			}
		}
		

		// TODO need to sort the results
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
			
			//TODO: Figure out what the types are actually supposed to be. probably not "sent" and "received"
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
						String messageId_string = String.valueOf(messageId);
						long threadId = searchResultCursor.getLong(1);
						String threadId_string = String.valueOf(messageId);
						String address = searchResultCursor.getString(2);
						long timestamp = searchResultCursor.getLong(3);
						String body = searchResultCursor.getString(4);
						long type = searchResultCursor.getLong(5);
						long contactId = searchResultCursor.getLong(6);
						String contactId_string = String.valueOf(contactId);
						
						Contact c = new Contact(contactId_string, MessageSource.SMS,
								contactId_string, null);
						
						Message msg = new Message(c, messageId_string, threadId_string, new Date (timestamp),
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
        Pattern p = Pattern.compile(filter.getJavaRegexText());
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

	        	// TODO why do we need to pass a contact object to the message?
				Contact c = new Contact(author_id, MessageSource.FACEBOOK, "TODO", null);
				
				long timestamp = Long.parseLong(created_time) * 1000L;
				Message msg = new Message(c, message_id, thread_id, new Date (timestamp),
						body, false);
				searchResultMessages.add(msg);

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Message", body);
				String date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date (timestamp));
				map.put("Date", date);
				map.put("FromAddress", "?");
				map.put("ID", message_id);
				map.put("ThreadID", thread_id);
				map.put("ContactID", author_id);
				searchResults.add(map);

	        }
		} catch (JSONException e) {
        	return;
		}
        
		
		/*	
		// Make query to content provider and store cursor to table returned
		String[] selectionArgsArray = new String[selectionArgList.size()];
		selectionArgList.toArray(selectionArgsArray);

		Cursor searchResultCursor = SearchBadgerApplication.getAppContext().getContentResolver().query(uri, projectionList, selection, selectionArgsArray, "date DESC");

		searchResults = new ArrayList<Map<String,String>>();
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
						String messageId_string = String.valueOf(messageId);
						long threadId = searchResultCursor.getLong(1);
						String threadId_string = String.valueOf(messageId);
						String address = searchResultCursor.getString(2);
						long timestamp = searchResultCursor.getLong(3);
						String body = searchResultCursor.getString(4);
						long type = searchResultCursor.getLong(5);
						long contactId = searchResultCursor.getLong(6);
						String contactId_string = String.valueOf(contactId);
						
						Contact c = new Contact(contactId_string, MessageSource.SMS,
								contactId_string, null);
						
						Message msg = new Message(c, messageId_string, threadId_string, new Date (timestamp),
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
*/
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
		dbOH = new SearchBadgerOpenHandler(SearchBadgerApplication.getAppContext());
		SQLiteDatabase db = null;
		try {
			db = dbOH.getReadableDatabase();
		
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
								//Message msg = new Message();
								if (!starredMsgs.add(new Message(new Contact("1", MessageSource.valueOf(srcName), "", null), 
										msg_id, thread_id, msg_date, msg_text, true))) {
									starredMsgs = null;
									return null;
								}
							} while (starredMsgsCursor.moveToNext());
						} finally {
							starredMsgsCursor.close();
						}
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
			vals.put("src_name", msg.getContact().getSource().toString());
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
		String whereArgs[] = {msg.getId(), msg.getThreadId(), msg.getContact().getSource().toString()};
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
	
	/*
	 * TODO Needs to access database. This probably will go through some other 
	 * class for SMSSearch or other search type
	 */
	public List<Map<String,String>> getThread(int index) {
		// SMS content provider uri 
		Message msgInThread = searchResultMessages.get(index);
		
		String url = "content://sms"; 
		Uri uri = Uri.parse(url); 

		String[] selectionArgs = new String[]{msgInThread.getThreadId().toString()};
		String selection = "thread_id = ?";
		
		Cursor searchResultCursor = SearchBadgerApplication.getAppContext().getContentResolver().query(uri, projectionList, selection, selectionArgs, "date DESC");
		
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
						String messageId_string = String.valueOf(messageId);
						long threadId = searchResultCursor.getLong(1);
						String threadId_string = String.valueOf(messageId);
						String address = searchResultCursor.getString(2);
						long timestamp = searchResultCursor.getLong(3);
						String body = searchResultCursor.getString(4);
						long type = searchResultCursor.getLong(5);
						long contactId = searchResultCursor.getLong(6);
						String contactId_string = String.valueOf(contactId);
						
						Contact c = new Contact(contactId_string, MessageSource.SMS,
								contactId_string, null);
						
						Message msg = new Message(c, messageId_string, threadId_string, new Date (timestamp),
								body, false);
						threadMessages.add(msg);

						HashMap<String, String> map = new HashMap<String, String>();
						map.put("Message", body);
						String date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date (timestamp));
						map.put("Date", date);
						map.put("From", address);
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

		return threadMap;
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
		}
		
		return null;
	}

	private static final String[] CONTACT_PROJECTION = new String[] {
			Contacts._ID, Contacts.DISPLAY_NAME };
	
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
						String contactId = cursor.getString(cursor.getColumnIndex(Contacts._ID));
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
        JSONArray jsonArray;
        try {
			jsonArray = new JSONArray(response);
	        contacts = new ArrayList<Contact>(jsonArray.length());
	        for(int i = 0; i < jsonArray.length(); i++) {
	        	id = jsonArray.getJSONObject(i).getLong("uid");
	        	name = jsonArray.getJSONObject(i).getString("name");
	        	

				// add the new contact to the list
				Contact contact = new Contact(
						String.valueOf(id),
						MessageSource.FACEBOOK,
						name,
						null);
				contacts.add(contact);
	        }
		} catch (JSONException e) {
        	return null;
		}
		
		return contacts;
	}
	
     
	
	protected class SearchBadgerOpenHandler extends SQLiteOpenHelper {
		public static final int DATABASE_VERSION = 1;
		public static final String DATABASE_NAME = "searchbadger";
		public static final String STARRED_MESSAGE_CREATE = "CREATE TABLE " + STARRED_MSGS_TABLE +
				"(Id INTEGER PRIMARY KEY ASC, Contact_Id TEXT, Msg_Id TEXT, Msg_Text TEXT, " +
				"Date BIGINT Date, Thread_Id TEXT, Src_Name TEXT);";
		
		public SearchBadgerOpenHandler(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			ContentValues values = new ContentValues();
			db.execSQL(this.STARRED_MESSAGE_CREATE);	
			db.setVersion(1);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
	}
}
