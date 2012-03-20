package edu.wisc.cs.cs638.messagesearch.util;

import java.util.Date;
import java.util.List;

public class SearchBuilder {

	private String text;
	private Date begin;
	private Date end;
	private Date before;
	private Date after;
	private List<MessageSource> sources;
	private List<Contact> contacts;
	private SendReceiveType type;
	List<MessageSource> messageSources;
	
	
	public Date getDateBefore() {
		return before;
	}
	public Date getDateAfter() {
		return after;
	}
	public Date getDateFrom() {
		return begin;
	}
	public Date getDateTo() {
		return end;
	}
	public void setDateBefore(Date date) {
		before = date;
	}
	public void setDateAfter(Date date) {
		after = date;
	}
	public void setDateFrom(Date date) {
		begin = date;
	}
	public void setDateTo(Date date) {
		end = date;
	}
	public List<MessageSource> getSearchSources() {
		return messageSources;
	}
	public void addMessageSource(MessageSource src) {
		messageSources.add(src);
	}
	public SendReceiveType getType() {
		return type;
	}
	public void setType(SendReceiveType srtype) {
		type = srtype;
	}
	public List<Contact> getContacts() {
		return contacts;
	}
	public boolean addContact(Contact c) {
		return contacts.add(c);
	}
	public boolean removeContact(Contact c) {
		return contacts.remove(c);
	}
	
	public Search genSearch() {
		return new Search(text, begin, end, before, after,
				messageSources, contacts, type);
	}
	
	
}
