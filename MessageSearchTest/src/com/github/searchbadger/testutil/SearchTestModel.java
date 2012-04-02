package com.github.searchbadger.testutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.ContactSMS;
import com.github.searchbadger.util.Message;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SearchModel;

public class SearchTestModel implements SearchModel {

	private List<Map<String,String>> searchResults;
	private List<Message> searchResultMessages;

	private List<Contact> contacts = new ArrayList<Contact>();
	
	
	public SearchTestModel() {
		super();

		List<String> addresses = new ArrayList<String>();
		
		addresses.clear();
		addresses.add("1-111-111-1111");
		contacts.add(new ContactSMS(1, MessageSource.SMS, "Homer Simpson", null, addresses));
		
		addresses.clear();
		addresses.add("2-222-222-2222");
		contacts.add(new ContactSMS(2, MessageSource.SMS, "Marge Simpson", null, addresses));
		
		addresses.clear();
		addresses.add("3-333-333-3333");
		contacts.add(new ContactSMS(3, MessageSource.SMS, "Lisa Simpson", null, addresses));
		
		addresses.clear();
		addresses.add("4-444-444-4444");
		contacts.add(new ContactSMS(4, MessageSource.SMS, "Bart Simpson", null, addresses));
		
		addresses.clear();
		addresses.add("5-555-555-5555");
		contacts.add(new ContactSMS(5, MessageSource.SMS, "Maggie Simpson", null, addresses));
	}

	public void search(Search filter) {
	}
	
	public List<Map<String,String>> getSearchResultsMap() {
		return searchResults;
	}
	
	public List<Message> getSearchResults() {
		return searchResultMessages;
	}
	
	public Search getCurrentSearch() {
		return null;
	}
	
	public List<Search> getRecentSearches() {
		return null;
	}
	
	public List<Message> getStarredMessages() {
		return null;
	}
	
	public boolean addStarredMessage(Message msg) {
		return true;
	}
	
	public boolean removeStarredMessage(Message msg) {
		return true;
	}	
	
	public List<Map<String,String>> getThread(int index) {
		return null;
	}
	
	public List<Contact> getContacts(MessageSource source) {
		return contacts;
	}

}
