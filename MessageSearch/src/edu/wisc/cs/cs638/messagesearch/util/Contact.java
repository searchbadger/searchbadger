package edu.wisc.cs.cs638.messagesearch.util;

import android.graphics.Bitmap;

public class Contact {
	private final int id;
	private final MessageSource source;
	private final String name;
	private final Bitmap picture;

	public Contact(int id, MessageSource source, String name, Bitmap picture) {
		this.id = id;
		this.source = source;
		this.name = name;
		this.picture = picture;
	}

	public int getId() {
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

}
