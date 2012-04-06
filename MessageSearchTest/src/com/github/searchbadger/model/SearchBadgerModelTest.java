package com.github.searchbadger.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import android.content.ContentResolver;
import android.os.Build;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.testutil.SearchBadgerTestModel;
import com.github.searchbadger.testutil.SearchBadgerTestUtil;
import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.ContactSMS;
import com.github.searchbadger.util.Message;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SearchModel;
import com.github.searchbadger.util.SendReceiveType;

public class SearchBadgerModelTest extends ApplicationTestCase<SearchBadgerApplication> {
	private SearchModel model;
	private ContentResolver contentResolver;
	private Search filter;
	private Date date;
	private List<Map<String,String>> results;
	
	public SearchBadgerModelTest() {
		super(SearchBadgerApplication.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		/*// DISABLE this if you want to run it on a real device
		boolean inEmulator = "generic".equals(Build.BRAND.toLowerCase());
		if(inEmulator == false)
		{
			throw new Exception("This script is disabled on a real device since it deletes the SMS database");
		}*/

		createApplication();
		model = SearchBadgerApplication.getSearchModel();
		contentResolver = SearchBadgerApplication.getAppContext().getContentResolver();
		date = new Date();
		SearchBadgerTestUtil.clearContactDatabase();
		SearchBadgerTestUtil.clearSmsDatabase();
		SearchBadgerTestUtil.addDefaultContactDatabase();
	}
	

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		SearchBadgerTestUtil.clearSmsDatabase();
		SearchBadgerTestUtil.addDefaultSmsDatabase();
	}

	public void testPreconditions() {
		assertNotNull(model);
		assertNotNull(contentResolver);
	}
	
	public void testSearchBlankNoFilters() {
		
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Foo Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Food Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello World Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello World Inbox");
		
		List<MessageSource> sources = new LinkedList<MessageSource>();
		sources.add(MessageSource.SMS);
		filter = new Search("", null, null, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with no filters: ", 10, results.size());
	}
	
	public void testSearchTextNoFilters() {

		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Foo Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Food Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello World Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello World Inbox");

		List<MessageSource> sources = new LinkedList<MessageSource>();
		sources.add(MessageSource.SMS);
		
		// testing text search
		filter = new Search("Hello", null, null, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing 'Hello' search with no filters: ", 5, results.size());
		
		// testing another text search
		filter = new Search("foo", null, null, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing 'foo' search with no filters: ", 2, results.size());
	}
	

	public void testSearchBlankFilterDate() {

		Date begin, end;
		Calendar cal = Calendar.getInstance();
		
		cal.set(2012, 2, 14, 8, 0, 0); // 02/14/2012 8:00:00 am
		date = cal.getTime();
		
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Foo Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Food Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		
		cal.set(2012, 2, 14, 10, 0, 0); // 02/14/2012 10:00:00 am
		date = cal.getTime();
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		
		cal.set(2012, 2, 15, 8, 0, 0); // 02/15/2012 8:00:00 am
		date = cal.getTime();
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello World Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello World Inbox");

		List<MessageSource> sources = new LinkedList<MessageSource>();
		sources.add(MessageSource.SMS);
		
		// testing before filter
		cal.set(2012, 2, 14, 9, 0, 0); // 02/14/2012 9:00:00 am
		end = cal.getTime();
		filter = new Search("", null, end, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter date before 02/14/2012 9:00:00 am: ", 4, results.size());

		// testing after filter
		cal.set(2012, 2, 14, 11, 0, 0); // 02/14/2012 11:00:00 am
		begin = cal.getTime();
		filter = new Search("", begin, null, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter date after 02/14/2012 11:00:00 am: ", 3, results.size());
		
		// testing from to filter
		cal.set(2012, 2, 14, 9, 0, 0); // 02/14/2012 9:00:00 am
		begin = cal.getTime();
		cal.set(2012, 2, 14, 11, 0, 0); // 02/14/2012 11:00:00 am
		end = cal.getTime();
		filter = new Search("", begin, end, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter date from 02/14/2012 9:00:00 am to from 02/14/2012 11:00:00 am: ", 1, results.size());
	}
	
	public void testSearchBlankFilterSendReceive() {

		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Foo Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Food Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello World Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello World Inbox");

		List<MessageSource> sources = new LinkedList<MessageSource>();
		sources.add(MessageSource.SMS);
		
		// testing received filter
		filter = new Search("", null, null, sources, null, SendReceiveType.RECEIVED);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter SendReceiveType.RECEIVED: ", 7, results.size());
		
		// testing send filter
		filter = new Search("", null, null, sources, null, SendReceiveType.SENT);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter SendReceiveType.SENT: ", 3, results.size());
	}
	

	public void testSearchSymbolNoFilter() {

		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Foo 12345 Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Food Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar100 Inbox");
		
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello 53703 World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello World Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello Worlld Inbox");

		List<MessageSource> sources = new LinkedList<MessageSource>();
		sources.add(MessageSource.SMS);
		
		// testing # symbol
		filter = new Search("*#####*", null, null, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing *#####* search with no filter: ", 2, results.size());
		filter = new Search("ar#*", null, null, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing bar#* search with no filter: ", 3, results.size());
		
		// testing * symbol
		filter = new Search("*w*d*", null, null, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing *w*d* search with no filter: ", 5, results.size());
		filter = new Search("*inbox", null, null, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing *inbox search with no filter: ", 8, results.size());
		filter = new Search("hello*", null, null, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing hello* search with no filter: ", 5, results.size());
		
		// testing _ symbol
		filter = new Search("*w___d*", null, null, sources, null, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing *w___d* search with no filter: ", 4, results.size());
	}
	


	public void testSearchBlankFilterContacts() {

		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Foo 12345 Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Food Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("1-111-111-1111", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Bar100 Inbox");
		
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello 53703 World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		SearchBadgerTestUtil.addSmsToDatabase("2-222-222-2222", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_SENT, "Hello World Sent");
		
		SearchBadgerTestUtil.addSmsToDatabase("3-333-333-3333", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello World Inbox");
		SearchBadgerTestUtil.addSmsToDatabase("3-333-333-3333", date.getTime(), SearchBadgerTestUtil.MESSAGE_TYPE_INBOX, "Hello Worlld Inbox");
		
		List<Contact> selectedContacts = new LinkedList<Contact>();
		List<String> addresses = new LinkedList<String>();

		List<MessageSource> sources = new LinkedList<MessageSource>();
		sources.add(MessageSource.SMS);
		
		// testing contact filter
		selectedContacts.clear();
		addresses.clear();
		addresses.add("1-111-111-1111");
		selectedContacts.add(new ContactSMS("1", MessageSource.SMS, null, null, addresses));
		filter = new Search("", null, null, sources, selectedContacts, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter contacts address {1-111-111-1111}: ", 6, results.size());

		selectedContacts.clear();
		addresses.clear();
		addresses.add("2-222-222-2222");
		selectedContacts.add(new ContactSMS("2", MessageSource.SMS, null, null, addresses));
		filter = new Search("", null, null, sources, selectedContacts, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter contacts address {2-222-222-2222}:", 3, results.size());

		selectedContacts.clear();
		addresses.clear();
		addresses.add("1-111-111-1111");
		addresses.add("3-333-333-3333");
		selectedContacts.add(new ContactSMS("3", MessageSource.SMS, null, null, addresses));
		filter = new Search("", null, null, sources, selectedContacts, null);
		model.search(filter);
		results = model.getSearchResultsMap();
		assertEquals("Testing blank search with filter contacts address {1-111-111-1111, 3-333-333-3333}: ", 8, results.size());
		
	}
	
	private String randomId() {
		return ((Long)((long)(Math.random() * 9999999999L))).toString();
	}
	
	private MessageSource randomMessageSource() {
		return MessageSource.values()[(int) Math.random() * (MessageSource.values().length - 1)];
	}
	
	public void testStarredMessages() {
		SearchBadgerTestModel testModel = new SearchBadgerTestModel();
		List<Message> starredMsgs = new ArrayList<Message>();
		int numStarredMsgs = (int) (Math.random() * 100);
		for (int starredMsgNum = 0; starredMsgNum < numStarredMsgs; starredMsgNum++) {
			starredMsgs.add(new Message(
					randomId(), randomId(), randomId(), randomMessageSource(), new Date((long)(Math.random() * 9999999999L) ), "This is some text!" + randomId(), false));
				
		}
		SearchBadgerApplication.setSearchModel(testModel);
		model = SearchBadgerApplication.getSearchModel();
		testModel.removeStarredDb();
		for (Message msg : starredMsgs) {
			assertTrue("Adding a starred message failed!", model.addStarredMessage(msg));
		}
		List<Message> retrievedStarredMsgs = testModel.getStarredMessages();
		assertTrue(retrievedStarredMsgs.equals(starredMsgs));
		testModel.clearStarredMessagesList();
		retrievedStarredMsgs = testModel.getStarredMessages();
		assertNotNull("retrievedStarredMsgs is null!", retrievedStarredMsgs);
		assertEquals(starredMsgs.size(), retrievedStarredMsgs.size());
		for (int i = 0; i < retrievedStarredMsgs.size(); i++) {
			assertTrue("i is " + i + " id " + retrievedStarredMsgs.get(i).getId() +  " vs " +
					starredMsgs.get(i).getId(),
					retrievedStarredMsgs.get(i).equals(starredMsgs.get(i)));
		}
		assertTrue(retrievedStarredMsgs.equals(starredMsgs));
		assertTrue(model.removeStarredMessage(starredMsgs.get(((int)(starredMsgs.size()/2)))));
		starredMsgs.remove(((int)(starredMsgs.size()/2)));
		assertTrue(model.removeStarredMessage(starredMsgs.get(((int)(starredMsgs.size()/4)))));
		starredMsgs.remove(((int)(starredMsgs.size()/4)));
		assertTrue(model.removeStarredMessage(starredMsgs.get(((int)(starredMsgs.size()/6)))));
		starredMsgs.remove(((int)(starredMsgs.size()/6)));
		retrievedStarredMsgs = testModel.getStarredMessages();
		assertTrue(retrievedStarredMsgs.equals(starredMsgs));
		assertEquals(starredMsgs.size(), retrievedStarredMsgs.size());
		testModel.clearStarredMessagesList();
		retrievedStarredMsgs = testModel.getStarredMessages();
		assertNotNull("retrievedStarredMsgs is null!", retrievedStarredMsgs);
		assertEquals(starredMsgs.size(), retrievedStarredMsgs.size());
		for (int i = 0; i < retrievedStarredMsgs.size(); i++) {
			assertTrue("i is " + i + " id " + retrievedStarredMsgs.get(i).getId() +  " vs " +
					starredMsgs.get(i).getId(),
					retrievedStarredMsgs.get(i).equals(starredMsgs.get(i)));
		}
		assertTrue(retrievedStarredMsgs.equals(starredMsgs));
	}
}
