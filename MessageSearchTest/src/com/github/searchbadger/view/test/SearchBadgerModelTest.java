package com.github.searchbadger.view.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.core.SearchBadgerModel;
import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SendReceiveType;

public class SearchBadgerModelTest extends TestCase {

	private SearchBadgerModel model;
	private ContentResolver contentResolver;
	private Search filter;
	private Date date;
	private List<Map<String,String>> results;
	
	private long[] contactIDs = new long[5];

	private static final String SMS_URI = "content://sms";
	private static final String ADDRESS = "address";
	private static final String PERSON = "person";
	private static final String DATE = "date";
	private static final String TYPE = "type";
	private static final String BODY = "body";

	private static final int MESSAGE_TYPE_INBOX = 1;
	private static final int MESSAGE_TYPE_SENT = 2;

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		// DISABLE this if you want to run it on a real device
		boolean inEmulator = "generic".equals(Build.BRAND.toLowerCase());
		if(inEmulator == false)
		{
			throw new Exception("This script is disabled on a real device since it deletes the SMS database");
		}
		 
		
		model = SearchBadgerModel.getInstance();
		contentResolver = SearchBadgerApplication.getAppContext().getContentResolver();
		date = new Date();
		clearContactDatabase();
		clearSmsDatabase();
		addDefaultContactDatabase();
	}
	

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		clearSmsDatabase();
		addDefaultSmsDatabase();
	}
	
	public void addDefaultSmsDatabase() {

		// This is the test sms database that will be left after the tests have been run
		// so that we have some data we can manually search for.
		// Feel free to add any messages here
		// We could make a function to populate message from an input file if 
		// everyone wants their own custom test SMS database
		
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Foo Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Food Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		
	}

	public void addDefaultContactDatabase() {
		contactIDs[0] = addContactToDatabase("Homer", "Simpson", "1-111-111-1111");  
		contactIDs[1] = addContactToDatabase("Marge", "Simpson", "2-222-222-2222");  
		contactIDs[2] = addContactToDatabase("Lisa", "Simpson", "3-333-333-3333");   
		contactIDs[3] = addContactToDatabase("Bart", "Simpson", "4-444-444-4444");   
		contactIDs[4] = addContactToDatabase("Maggie", "Simpson", "5-555-555-5555"); 
	}

	public void testPreconditions() {
		assertNotNull(model);
		assertNotNull(contentResolver);
	}
	
	public void testSearchBlankNoFilters() {
		
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Foo Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Food Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		
		filter = new Search("", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with no filters: ", 10, results.size());
	}
	
	public void testSearchTextNoFilters() {

		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Foo Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Food Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		
		// testing text search
		filter = new Search("Hello", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing 'Hello' search with no filters: ", 5, results.size());
		
		// testing another text search
		filter = new Search("foo", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing 'foo' search with no filters: ", 2, results.size());
	}
	

	public void testSearchBlankFilterDate() {

		Date begin, end;
		Calendar cal = Calendar.getInstance();
		
		cal.set(2012, 2, 14, 8, 0, 0); // 02/14/2012 8:00:00 am
		date = cal.getTime();
		
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Foo Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Food Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		
		cal.set(2012, 2, 14, 10, 0, 0); // 02/14/2012 10:00:00 am
		date = cal.getTime();
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		
		cal.set(2012, 2, 15, 8, 0, 0); // 02/15/2012 8:00:00 am
		date = cal.getTime();
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		
		// testing before filter
		cal.set(2012, 2, 14, 9, 0, 0); // 02/14/2012 9:00:00 am
		end = cal.getTime();
		filter = new Search("", null, end, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter date before 02/14/2012 9:00:00 am: ", 4, results.size());

		// testing after filter
		cal.set(2012, 2, 14, 11, 0, 0); // 02/14/2012 11:00:00 am
		begin = cal.getTime();
		filter = new Search("", begin, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter date after 02/14/2012 11:00:00 am: ", 3, results.size());
		
		// testing from to filter
		cal.set(2012, 2, 14, 9, 0, 0); // 02/14/2012 9:00:00 am
		begin = cal.getTime();
		cal.set(2012, 2, 14, 11, 0, 0); // 02/14/2012 11:00:00 am
		end = cal.getTime();
		filter = new Search("", begin, end, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter date from 02/14/2012 9:00:00 am to from 02/14/2012 11:00:00 am: ", 1, results.size());
	}
	
	public void testSearchBlankFilterSendReceive() {

		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Foo Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Food Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		
		// testing received filter
		filter = new Search("", null, null, null, null, SendReceiveType.RECEIVED);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter SendReceiveType.RECEIVED: ", 7, results.size());
		
		// testing send filter
		filter = new Search("", null, null, null, null, SendReceiveType.SENT);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter SendReceiveType.SENT: ", 3, results.size());
	}
	

	public void testSearchSymbolNoFilter() {

		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Foo 12345 Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Food Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar100 Inbox");
		
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello 53703 World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello Worlld Inbox");
		
		// testing # symbol
		filter = new Search("#####", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing ##### search with no filter: ", 2, results.size());
		filter = new Search("bar#", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing bar# search with no filter: ", 3, results.size());
		
		// testing * symbol
		filter = new Search("W*d", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing W*d search with no filter: ", 5, results.size());
		filter = new Search("*inbox", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing *inbox search with no filter: ", 7, results.size());
		filter = new Search("hello*", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing hello* search with no filter: ", 5, results.size());
		
		// testing _ symbol
		filter = new Search("W___d", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing W___d search with no filter: ", 4, results.size());
	}
	


	public void testSearchBlankFilterContacts() {

		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Foo 12345 Inbox", contactIDs[0]);
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar Inbox", contactIDs[0]);
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Food Inbox", contactIDs[0]);
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar1 Inbox", contactIDs[0]);
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar10 Inbox", contactIDs[0]);
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar100 Inbox", contactIDs[0]);
		
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello 53703 World Sent", contactIDs[1]);
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent", contactIDs[1]);
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent", contactIDs[1]);
		
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox", contactIDs[2]);
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello Worlld Inbox", contactIDs[2]);
		
		List<Contact> selectedContacts = new LinkedList<Contact>();
		
		// testing contact filter
		selectedContacts.clear();
		selectedContacts.add(new Contact(contactIDs[0], MessageSource.SMS, null, null));
		filter = new Search("", null, null, null, selectedContacts, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter contacts {" + contactIDs[0] + "}: ", 6, results.size());

		selectedContacts.clear();
		selectedContacts.add(new Contact(contactIDs[1], MessageSource.SMS, null, null));
		filter = new Search("", null, null, null, selectedContacts, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter contacts {" + contactIDs[1] + "}: ", 3, results.size());

		selectedContacts.clear();
		selectedContacts.add(new Contact(contactIDs[0], MessageSource.SMS, null, null));
		selectedContacts.add(new Contact(contactIDs[2], MessageSource.SMS, null, null));
		filter = new Search("", null, null, null, selectedContacts, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter contacts {" + contactIDs[0] + ","
				+ contactIDs[2]+ "}: ", 8, results.size());
		
	}
	

	private void addSmsToDatabase( String address, long date, int type, String body, long contactID  ){
	    ContentValues values = new ContentValues();
	    values.put( ADDRESS, address );
	    values.put( DATE, date );
	    values.put( TYPE, type );
	    values.put( BODY, body );
	    if(contactID != 0)
	    	values.put( PERSON, contactID );

	    // insert row into the SMS database
	    contentResolver.insert( Uri.parse( SMS_URI ), values );
	}
	
	private void addSmsToDatabase( String address, long date, int type, String body ) {
		addSmsToDatabase( address, date, type, body, 0);
	}
	
	

	private void clearSmsDatabase() {
	    // clear the SMS database
	    contentResolver.delete( Uri.parse( SMS_URI ), null, null );
	}
	
	private long addContactToDatabase(String firstname, String lastname, String number) {

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();
        
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .build());
        //INSERT NAME
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                //.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, szFullname) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastname) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstname) // Name of the person
                .build());
        //INSERT PHONE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build()); // 
        
    	// SAVE CONTACT IN BCR Structure
		Uri newContactUri = null;
		//PUSH EVERYTHING TO CONTACTS
        try
        {
            ContentProviderResult[] res = contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            if (res!=null && res[0]!=null) {
            	newContactUri = res[0].uri;	
            	// contact:content://com.android.contacts/raw_contacts/612
            	
            	Cursor c = contentResolver.query(newContactUri, new String[]{Contacts._ID}, null, null, null);
            	try {
            	    c.moveToFirst();
            	    long id = c.getLong(0);

                	Log.v("SearchBadger","" + id);
            	    return id;
            	} finally {
            	    c.close();
            	}
            }
        }
        catch (RemoteException e)
        { 
            // error
        	newContactUri = null;
        }
        catch (OperationApplicationException e) 
        {
            // error
        	newContactUri = null;
        }  
        
        return 0;
		/*
	    ContentValues contentValues = new ContentValues();
	    contentValues.put(People.NAME, name);
	    contentValues.put(People.STARRED, 1);
	    Uri newContactUri = contentResolver.insert(People.CONTENT_URI, contentValues);
	     
	    Uri phoneUri = Uri.withAppendedPath(newContactUri, People.Phones.CONTENT_DIRECTORY);
	    contentValues.clear();
	    contentValues.put(People.Phones.TYPE, People.TYPE_MOBILE);
	    contentValues.put(People.NUMBER, number);
	     
	    contentResolver.insert(phoneUri, contentValues);
	    */
	}
	
	private void clearContactDatabase() {
	    // clear the contact database
	    //contentResolver.delete( People.CONTENT_URI, null, null );
	    //contentResolver.delete( People.Phones.CONTENT_DIRECTORY, null, null );
		Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
	            null, null, null, null);
	    while (cur.moveToNext()) {
	        try{
	            String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
	            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
	            System.out.println("The uri is " + uri.toString());
	            contentResolver.delete(uri, null, null);
	        }
	        catch(Exception e)
	        {
	            System.out.println(e.getStackTrace());
	        }
	    }
	}
	
}
