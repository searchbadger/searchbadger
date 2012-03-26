package com.github.searchbadger.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Message implements Parcelable {
	private final Contact contact;
	private final long Id;
	private final long threadId;
	private final Date date;
	private final String text;
	private final boolean isStarred;
	
	public Message(Contact contact, long Id, long threadId, Date date, String text, boolean isStarred){
		this.contact = contact;
		this.Id = Id;
		this.threadId = threadId;
		this.date = date;
		this.text = text;
		this.isStarred = isStarred;
	}
	
	public Message(Parcel in){		
		Date tempDate = null;
		
		contact = in.readParcelable(Contact.class.getClassLoader());
		Id = in.readLong();
		threadId = in.readLong();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			tempDate = (formatter).parse(in.readString());
		} catch (ParseException e) {}
		finally{
			date = tempDate;
		}
		
		text = in.readString();
		isStarred = Boolean.parseBoolean(in.readString());
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
	
	public Date getDate() {
		return date;
	}
	
	public String getText() {
		return text;
	}
	
	public boolean isStarred() {
		return isStarred;
	}
	
	

	public int describeContents() {
		Log.d("Message","describeContents()");
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(contact, flags);
		dest.writeLong(Id);
		dest.writeLong(threadId);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dest.writeString(formatter.format(date));
		
		
		dest.writeString(text);
		dest.writeString(String.valueOf(isStarred));
	}
	
	
	public static final Parcelable.Creator<Message> CREATOR =
	    	new Parcelable.Creator<Message>() {
	            public Message createFromParcel(Parcel in) {
						return new Message(in);
	            }
	 
	            public Message[] newArray(int size) {
	                return new Message[size];
	            }
	        };
}
