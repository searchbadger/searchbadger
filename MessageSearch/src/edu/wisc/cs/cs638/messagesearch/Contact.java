package edu.wisc.cs.cs638.messagesearch;

import android.graphics.Bitmap;

public class Contact {
	private int id;
	private MessageSource source;
	private String name;
	private Bitmap picture;
	
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
}
