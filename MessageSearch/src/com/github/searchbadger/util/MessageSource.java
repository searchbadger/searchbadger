package com.github.searchbadger.util;

import android.os.Parcel;
import android.os.Parcelable;


public enum MessageSource implements Parcelable {
	SMS, FACEBOOK, TWITTER, STARRED;

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(ordinal());
	}

	public static final Creator<MessageSource> CREATOR = new Creator<MessageSource>() {
		public MessageSource createFromParcel(final Parcel source) {
			return MessageSource.values()[source.readInt()];
		}

		public MessageSource[] newArray(final int size) {
			return new MessageSource[size];
		}
	};
}