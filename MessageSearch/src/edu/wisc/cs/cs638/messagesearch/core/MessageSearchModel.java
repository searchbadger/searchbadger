package edu.wisc.cs.cs638.messagesearch.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

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
	
	private Cursor currentSearch;
	private final static String projectionList[] = {"_id", "person", "date", "body"};
	
	
	public static MessageSearchModel getInstance() {
		return instance;
	}
	
	public void search(Search filter) {
		// SMS content provider uri 
		String url = "content://sms/inbox"; 
		Uri uri = Uri.parse(url); 
	
		ArrayList<String> selectionArgList = new ArrayList<String>();
		
		String selection = "";
		String arg = "";
		
		// Go through each possible search type, and build SQL query
		if (filter.getText() != null){
			selection += "body LIKE ?";
			arg = "%" + filter.getText() + "&";
			
			selectionArgList.add(arg);
		}
		if (filter.getContacts() != null){
			List<Contact> contacts = filter.getContacts();
			Iterator<Contact> iter = contacts.iterator();
			
			if (selection.length() > 0)
				selection += "and";
				
			selection += "(";
			while (iter.hasNext()){
				Contact c = iter.next();
				selection += "person = ?";
				arg = c.getName();
				
				selectionArgList.add(arg);
			}
			selection += ")";
			
		}
		
		if (filter.getBegin() != null){
			if (selection.length() > 0)
				selection += "and";
			
			selection += "date >= ?";
			arg = filter.getBegin().toString();
			
			selectionArgList.add(arg);
		}
		
		if (filter.getEnd() != null){
			if (selection.length() > 0)
				selection += "and";
			
			selection += "date <= ?";
			arg = filter.getBegin().toString();
			
			selectionArgList.add(arg);
		}
		
		String[] selectionArgArray = selectionArgList.toArray(new String[selectionArgList.size()]);
		
		// Make query to content provider and store cursor to table returned
		currentSearch = Activity.managedQuery(uri, projectionList, selection, selectionArgArray, "");
		
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

	/*
	 * Returns the begin date
	 */
	public Date getBeginDate() {
		return _begin;
	}
	
	public void setBeginDate(Date date) {
		_begin = date;
	}

	/*
	 * Returns the end date
	 */
	public Date getEndDate() {
		return _end;
	}
	
	public void setEndDate(Date date) {
		_end = date;
	}
	
	
	public List<Message> getThread(Message msg) {
		return null;
	}
	
}
