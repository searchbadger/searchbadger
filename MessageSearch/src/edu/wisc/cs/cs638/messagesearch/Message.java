package edu.wisc.cs.cs638.messagesearch;

public class Message {
	Contact contact;
	int Id;
	int threadId;
	String text;
	boolean isStarred;
	
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
