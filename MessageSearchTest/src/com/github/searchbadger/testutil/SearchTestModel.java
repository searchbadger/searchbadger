package com.github.searchbadger.testutil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.ContactSMS;
import com.github.searchbadger.util.Message;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SearchModel;

public class SearchTestModel implements SearchModel {

	public List<Message> searchResultMessages = new ArrayList<Message>();
	public List<Contact> contactsSMS = new ArrayList<Contact>();
	public List<Contact> contactsFacebook = new ArrayList<Contact>();
	public List<Search> searches = new ArrayList<Search>();
	public int sleepDelay = 0;
	
	public SearchTestModel() {
		super();

		List<String> addresses = new ArrayList<String>();
		
		addresses.clear();
		addresses.add("1-111-111-1111");
		contactsSMS.add(new ContactSMS("1", MessageSource.SMS, "Homer Simpson", null, addresses));
		
		addresses.clear();
		addresses.add("2-222-222-2222");
		contactsSMS.add(new ContactSMS("2", MessageSource.SMS, "Marge Simpson", null, addresses));
		
		addresses.clear();
		addresses.add("3-333-333-3333");
		contactsSMS.add(new ContactSMS("3", MessageSource.SMS, "Lisa Simpson", null, addresses));
		
		addresses.clear();
		addresses.add("4-444-444-4444");
		contactsSMS.add(new ContactSMS("4", MessageSource.SMS, "Bart Simpson", null, addresses));
		
		addresses.clear();
		addresses.add("5-555-555-5555");
		contactsSMS.add(new ContactSMS("5", MessageSource.SMS, "Maggie Simpson", null, addresses));
		

		searchResultMessages.add(new Message("1", "10", "You", MessageSource.SMS, new Date(), "message 1", false));
		searchResultMessages.add(new Message("2", "11", "You", MessageSource.FACEBOOK, new Date(), "message 2", false));
		searchResultMessages.add(new Message("3", "12", "Me", MessageSource.TWITTER, new Date(), "message 3", false));
		searchResultMessages.add(new Message("4", "13", "You", MessageSource.SMS, new Date(), "message 4", false));
		searchResultMessages.add(new Message("5", "14", "Me", MessageSource.FACEBOOK, new Date(), "message 5", false));
		
		List<MessageSource> sources = new ArrayList<MessageSource>();
		sources.add(MessageSource.SMS);
		searches.add(new Search("Hello World", null, null, sources, null, null));
		searches.add(new Search("Foo Bar", null, null, sources, null, null));
		searches.add(new Search("Bye World", null, null, sources, null, null));
		
		contactsFacebook.add(new Contact("1", MessageSource.FACEBOOK, "John Doe 1", null));
		contactsFacebook.add(new Contact("2", MessageSource.FACEBOOK, "John Doe 2", null));
		contactsFacebook.add(new Contact("3", MessageSource.FACEBOOK, "John Doe 3", null));
		contactsFacebook.add(new Contact("4", MessageSource.FACEBOOK, "John Doe 4", null));
		contactsFacebook.add(new Contact("5", MessageSource.FACEBOOK, "John Doe 5", null));
	}

	public void search(Search filter) {
	}
	
	public List<Message> getSearchResults() {
		try {
			Thread.sleep(sleepDelay);
		} catch (InterruptedException e) {
		}
		
		return searchResultMessages;
	}
	
	public Search getCurrentSearch() {
		return null;
	}
	
	public List<Search> getRecentSearches() {
		try {
			Thread.sleep(sleepDelay);
		} catch (InterruptedException e) {
		}
		return searches;
	}
	
	public List<Message> getStarredMessages() {
		return getSearchResults();
	}
	
	public boolean addStarredMessage(Message msg) {
		return true;
	}
	
	public boolean removeStarredMessage(Message msg) {
		return true;
	}	
	
	public List<Message> getThread(Message message) {
		return getSearchResults();
	}
	
	public List<Contact> getContacts(MessageSource source) {
		try {
			Thread.sleep(sleepDelay);
		} catch (InterruptedException e) {
		}
		
		if(source == MessageSource.FACEBOOK) 
			return contactsFacebook;
		
		return contactsSMS;
	}

	public boolean containsStarredMessage(Message message) {
		if(message.getId().equals("2")) {
			return true;
		}
		return false;
	}

	public boolean addRecentSearch(Search search) {
		return false;
	}

	public void clearRecentSearches() {
	}

	public List<Message> getLastThread() {
		return null;
	}

	public void resetLastThread() {		
	}

	public int getLastThreadIndex() {
		return 0;
	}

	public boolean hasRecentSearchesBeenLoaded() {
		return false;
	}

}
