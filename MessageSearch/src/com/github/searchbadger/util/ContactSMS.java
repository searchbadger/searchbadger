package com.github.searchbadger.util;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ContactSMS extends Contact implements Parcelable {

	protected List<String> addresses;
	
	public ContactSMS(long id, MessageSource source, String name, Bitmap picture, List<String> addresses) {
		super(id, source, name, picture);
		this.addresses = addresses;
	}
	
	public ContactSMS(Parcel in){
		super(in);
		id = in.readLong();
		source = MessageSource.valueOf(in.readString());
		name = in.readString();
		picture = in.readParcelable(Bitmap.class.getClassLoader());
		in.readStringList(addresses);
	}
	
	public List<String> getAddresses() {
		return addresses;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(name + " | " + id + " | " + source + " | ");
		for(int i = 0; i < addresses.size(); i++) {
			str.append(addresses.get(i) + ", ");
		}
		return str.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ContactSMS))
			return false;
		ContactSMS that = (ContactSMS) o;
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
		dest.writeStringList(addresses);
	}
	
	public static final Parcelable.Creator<ContactSMS> CREATOR =
	    	new Parcelable.Creator<ContactSMS>() {
	            public ContactSMS createFromParcel(Parcel in) {
						return new ContactSMS(in);
	            }
	 
	            public ContactSMS[] newArray(int size) {
	                return new ContactSMS[size];
	            }
	        };

}
