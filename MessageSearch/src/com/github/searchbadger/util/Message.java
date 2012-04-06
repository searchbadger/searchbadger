package com.github.searchbadger.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Message implements Parcelable {
	private final String Id;
	private final String threadId;
	private final Date date;
	private final String text;
	private final String author;
	private final MessageSource source;
	private boolean isStarred;
	
	public Message(String Id, String threadId, String author, MessageSource source, Date date, String text, boolean isStarred){
		this.Id = Id;
		this.threadId = threadId;
		this.author = author;
		this.source = source;
		this.date = date;
		this.text = text;
		this.isStarred = isStarred;
	}
	
	public Message(Parcel in){		
		Date tempDate = null;
		
		Id = in.readString();
		threadId = in.readString();
		author = in.readString();
		source = in.readParcelable(MessageSource.class.getClassLoader());
		
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
	
	
	public String getId() {
		return Id;
	}
	
	public String getThreadId() {
		return threadId;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public MessageSource getSource() {
		return source;
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
	
	public boolean toogleStarred() {
		isStarred = !isStarred;
		return isStarred;
	}

	public int describeContents() {
		Log.d("Message","describeContents()");
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(Id);
		dest.writeString(threadId);
		dest.writeString(author);
		dest.writeParcelable(source, flags);
		
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
	

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Message))
			return false;
		Message that = (Message) o;
		return ((this.Id.toLowerCase().equals(that.Id.toLowerCase())) && 
				(this.threadId.toLowerCase().equals(that.threadId.toLowerCase())) &&
				(this.author.toLowerCase().equals(that.author.toLowerCase())) &&
				(this.source.equals(that.source)) &&
				(this.date.equals(that.date)) &&
				(this.text.equals(that.text)));
	}
}
