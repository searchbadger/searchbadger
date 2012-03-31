package com.github.searchbadger.util;

import java.util.Date;
import java.util.List;


public class Search {
	private final String text;
	private final Date begin;
	private final Date end;
	private final List<MessageSource> sources;
	private final List<Contact> contacts;
	private SendReceiveType type;
	
	public Search(String text, Date begin, Date end,
			List<MessageSource> sources, List<Contact> contacts,
			SendReceiveType type) {
		this.text = text;
		this.begin = begin;
		this.end = end;
		this.sources = sources;
		this.contacts = contacts;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public Date getBegin() {
		return begin;
	}

	public Date getEnd() {
		return end;
	}

	public List<MessageSource> getSources() {
		return sources;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public SendReceiveType getType() {
		return type;
	}

	public String getGlobText() {
		StringBuilder glob = new StringBuilder();
		
		// escape special char
		String tmpText = text;
		tmpText = tmpText.replace("[", "'[[]");
		tmpText = tmpText.replace("{", "'[{]");
		tmpText = tmpText.replace("?", "'[?]");
		
		
		// make glob search be case insensitive
		String textLowerCase = tmpText.toLowerCase();
		String textUpperCase = tmpText.toUpperCase();
		char letterLowerCase, letterUpperCase;
		for(int i = 0; i < tmpText.length(); i++) {
			letterLowerCase = textLowerCase.charAt(i);
			letterUpperCase = textUpperCase.charAt(i);
			if(letterLowerCase == letterUpperCase) {
				glob.append(letterLowerCase);
			} else {
				glob.append("[" + letterLowerCase + letterUpperCase + "]");
			}
		}
		
		
		// add regex
		tmpText = "*" + glob.toString() + "*";
		tmpText = tmpText.replace("#", "[0-9]");
		tmpText = tmpText.replace("_", "?");
		
		return tmpText;
	}
	
}
