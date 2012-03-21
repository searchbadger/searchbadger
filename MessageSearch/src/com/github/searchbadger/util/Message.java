package com.github.searchbadger.util;

public class Message {
	private final Contact contact;
	private final long Id;
	private final long threadId;
	private final String text;
	private final boolean isStarred;
	
	public Message(Contact contact, long Id, long threadId, String text, boolean isStarred){
		this.contact = contact;
		this.Id = Id;
		this.threadId = threadId;
		this.text = text;
		this.isStarred = isStarred;
	}
	
	public Contact getContact() {
		return contact;
	}
	
	public long getId() {
		return Id;
	}
	
	public long getThreadId() {
		return threadId;
	}
	
	public String getText() {
		return text;
	}
	
	public boolean isStarred() {
		return isStarred;
	}
	
	
}
