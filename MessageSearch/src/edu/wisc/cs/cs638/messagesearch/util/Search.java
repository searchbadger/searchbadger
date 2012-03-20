package edu.wisc.cs.cs638.messagesearch.util;

import java.util.Date;
import java.util.List;


public class Search {
	private final String text;
	private final Date begin;
	private final Date end;
	private final List<MessageSource> sources;
	private final List<Contact> contacts;
	private SendReceiveType type;
	
	public Search(String text, Date begin, Date end,
			List<MessageSource> sources, List<Contact> contacts,
			SendReceiveType type) {
		this.text = text;
		this.begin = begin;
		this.end = end;
		this.sources = sources;
		this.contacts = contacts;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public Date getBegin() {
		return begin;
	}

	public Date getEnd() {
		return end;
	}

	public List<MessageSource> getSources() {
		return sources;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public SendReceiveType getType() {
		return type;
	}
	
	
}
