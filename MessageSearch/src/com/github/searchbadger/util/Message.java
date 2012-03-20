package com.github.searchbadger.util;

public class Message {
	private final Contact contact;
	private final int Id;
	private final int threadId;
	private final String text;
	private final boolean isStarred;
	
	public Message(Contact contact, int Id, int threadId, String text, boolean isStarred){
		this.contact = contact;
		this.Id = Id;
		this.threadId = threadId;
		this.text = text;
		this.isStarred = isStarred;
	}
	
	public Contact getContact() {
		return contact;
	}
	
	public int getId() {
		return Id;
	}
	
	public int getThreadId() {
		return threadId;
	}
	
	public String getText() {
		return text;
	}
	
	public boolean isStarred() {
		return isStarred;
	}
	
	
}
