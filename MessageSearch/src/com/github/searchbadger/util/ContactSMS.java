package com.github.searchbadger.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ContactSMS extends Contact implements Parcelable {

	protected List<String> addresses;
	
	public ContactSMS(String id, MessageSource source, String name, Bitmap picture, List<String> addresses) {
		super(id, source, name, picture);
		this.addresses = addresses;
	}
	
	public ContactSMS(Parcel in){
		super(in);
		addresses = new LinkedList<String>();
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
	public int describeContents() {
		Log.d("Contact","describeContents()");
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
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

	@Override
	public boolean contains(String address) {
		address = address.replace(" ", "");
		address = address.replace("-", "");
		if(addresses == null) return false;
		Iterator<String> itr = addresses.iterator();
		while(itr.hasNext()) {
			String contactAddress = itr.next();
			contactAddress = contactAddress.replace(" ", "");
			contactAddress = contactAddress.replace("-", "");
			if(contactAddress.equals(address))
				return true;
		}
		
		if(id.equals(address))
			return true;
		
		return false;
	}

}
