package com.github.searchbadger.util;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Contact implements Parcelable {
	protected long id;
	protected MessageSource source;
	protected String name;
	protected Bitmap picture;

	public Contact(long id, MessageSource source, String name, Bitmap picture) {
		this.id = id;
		this.source = source;
		this.name = name;
		this.picture = picture;
	}
	
	public Contact(Parcel in){
		id = in.readLong();
		source = MessageSource.valueOf(in.readString());
		name = in.readString();
		picture = in.readParcelable(Bitmap.class.getClassLoader());
		
	}

	public long getId() {
		return id;
	}

	public MessageSource getSource() {
		return source;
	}

	public String getName() {
		return name;
	}

	public Bitmap getPicture() {
		return picture;
	}

	@Override
	public String toString() {
		return name + " | " + id + " | " + source;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Contact))
			return false;
		Contact that = (Contact) o;
		return (this.id == that.id) && (this.source == that.source);
	}

	public int describeContents() {
		Log.d("Contact","describeContents()");
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(source.name());
		dest.writeString(name);
		dest.writeParcelable(picture, flags);
	}
	
	public static final Parcelable.Creator<Contact> CREATOR =
	    	new Parcelable.Creator<Contact>() {
	            public Contact createFromParcel(Parcel in) {
						return new Contact(in);
	            }
	 
	            public Contact[] newArray(int size) {
	                return new Contact[size];
	            }
	        };

}
