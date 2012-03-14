package edu.wisc.cs.cs638.messagesearch.core;

import java.util.List;

import edu.wisc.cs.cs638.messagesearch.util.Contact;
import edu.wisc.cs.cs638.messagesearch.util.Message;
import edu.wisc.cs.cs638.messagesearch.util.MessageSource;
import edu.wisc.cs.cs638.messagesearch.util.Search;

public class MessageSearchModel {
	
	List<MessageSource> _sources;
	
	public void search(Search filter) {
		
	}
	
	public Search getCurrentSearch() {
		
		return null;
	}
	
	public List<Message> getStarredMessages() {
		
		return null;
	}
	
	public List<Message> getRecentSearches() {
		
		return null;
	}
	
	public boolean addStarredMessage(Message msg) {
		return true;
	}
	
	public boolean removeStarredMessage(Message msg) {
		return true;
	}
	
	/*
	 * if a single source is selected, returns list of contacts from that source
	 * otherwise returns null
	 */
	public List<Contact> getContacts() {
		return null;
	}
	
	/*
	 * Returns the currently selected message sources
	 */
	public List<MessageSource> getSearchSources() {
		return _sources;
	}
	
	public void addSearchSource(MessageSource source) {
		_sources.add(source);
	}
	
	public boolean removeSearchSource(MessageSource source) {
		return _sources.remove(source);
	}
	
	public List<Message> getThread(Message msg) {
		return null;
	}
	
}
