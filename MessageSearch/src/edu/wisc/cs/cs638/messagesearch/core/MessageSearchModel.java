package edu.wisc.cs.cs638.messagesearch.core;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.wisc.cs.cs638.messagesearch.util.Contact;
import edu.wisc.cs.cs638.messagesearch.util.Message;
import edu.wisc.cs.cs638.messagesearch.util.MessageSource;
import edu.wisc.cs.cs638.messagesearch.util.Search;
import edu.wisc.cs.cs638.messagesearch.util.SendReceiveType;

public class MessageSearchModel {
	private final static MessageSearchModel instance = new MessageSearchModel();
	private final List<MessageSource> _sources = new LinkedList<MessageSource>();
	private final List<Contact> _contacts = new LinkedList<Contact>();
	private Date _begin = new Date();
	private Date _end = new Date();
	private SendReceiveType _type = SendReceiveType.SENT;
	
	public static MessageSearchModel getInstance() {
		return instance;
	}
	
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
		return _contacts;
	}
	
	public void addContact(Contact contact) {
		_contacts.add(contact);
	}
	
	public boolean removeContact(Contact contact) {
		return _contacts.remove(contact);
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

	/*
	 * Returns the currently selected send/received type
	 */
	public SendReceiveType getType() {
		return _type;
	}
	
	public void setType(SendReceiveType type) {
		_type = type;
	}
	
	
	public List<Message> getThread(Message msg) {
		return null;
	}
	
}
