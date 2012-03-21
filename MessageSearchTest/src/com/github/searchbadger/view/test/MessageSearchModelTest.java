package com.github.searchbadger.view.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import com.github.searchbadger.core.MessageSearchApplication;
import com.github.searchbadger.core.MessageSearchModel;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SendReceiveType;

public class MessageSearchModelTest extends TestCase {

	private MessageSearchModel model;
	private ContentResolver contentResolver;
	private Search filter;
	private Date date;
	private List<Map<String,String>> results;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		model = MessageSearchModel.getInstance();
		contentResolver = MessageSearchApplication.getAppContext().getContentResolver();
		date = new Date();
		clearSmsDatabase();
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
		begin = cal.getTime();
		filter = new Search("", begin, null, null, null, null);
		cal.set(2012, 2, 14, 9, 0, 0); // 02/14/2012 9:00:00 am
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter date after 02/14/2012 9:00:00 am: ", 3, results.size());
		
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

		assertTrue("TODO: testSearchBlankFilterContacts", false);
		/*
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Foo 12345 Inbox", 1);
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar Inbox", 1);
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Food Inbox", 1);
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar1 Inbox", 1);
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar10 Inbox", 1);
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar100 Inbox", 1);
		
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello 53703 World Sent", 2);
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent", 2);
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent", 2);
		
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox", 3);
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello Worlld Inbox", 3);
		
		// testing contact filter
		filter = new Search("", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter contacts {1}: ", 6, results.size());
		filter = new Search("", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter contacts {2}: ", 3, results.size());
		filter = new Search("", null, null, null, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter contacts {1,3}: ", 8, results.size());
		*/
	}
	
	private static final String SMS_URI = "content://sms";
	private static final String ADDRESS = "address";
	private static final String PERSON = "person";
	private static final String DATE = "date";
	private static final String TYPE = "type";
	private static final String BODY = "body";

	private static final int MESSAGE_TYPE_INBOX = 1;
	private static final int MESSAGE_TYPE_SENT = 2;


	private void addSmsToDatabase( String address, long date, int type, String body, int contactID  )
	{
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
	
	private void addSmsToDatabase( String address, long date, int type, String body )
	{
		addSmsToDatabase( address, date, type, body, 0);
	}
	
	

	private void clearSmsDatabase()
	{
	    // clear the SMS database
	    contentResolver.delete( Uri.parse( SMS_URI ), null, null );
	}
}
