package com.github.searchbadger.util;

import java.util.LinkedList;
import java.util.List;


public class SelectedContacts {
	private static final SelectedContacts instance = new SelectedContacts();
	private List<Contact> selectedContacts = new LinkedList<Contact>(); 

	public static final SelectedContacts getInstance() {
		return instance;
	}
	

	public List<Contact> getSelectedContacts() {
		return selectedContacts;
	}
	
	public void clearContacts() {
		selectedContacts.clear();
	}
	
	public void addContact(Contact contact) {
		if(contact == null) return;
		if(selectedContacts.contains(contact)) return;
		selectedContacts.add(contact);
	}
	
	public boolean removeContact(Contact contact) {
		return selectedContacts.remove(contact);
	}
}
