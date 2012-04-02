package com.github.searchbadger.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.core.SearchBadgerModel;
import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.MessageSource;

public class ContactsActivity extends Activity {

	private List<MessageSource> sources;
	private List<Contact> selectedContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_layout);
        

        // set the click event for the done button
        Button buttonDone = (Button) findViewById(R.id.buttonDone);
        buttonDone.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putParcelableArrayListExtra("selectedContacts", (ArrayList<Contact>)selectedContacts);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
        
        
        Intent intent = getIntent();
        if(intent != null) {
	        sources = intent.getParcelableArrayListExtra("messageSources");
	        selectedContacts = intent.getParcelableArrayListExtra("selectedContacts");
        }
        
		// check if only one source is selected
        if(sources == null) return;
        if(selectedContacts == null) return;
		if(sources.size() != 1) return;

        ListView list = (ListView) findViewById(R.id.listView_contact);
        List<Contact> contacts = SearchBadgerApplication.getSearchModel().getContacts(sources.get(0));
        if(contacts == null) return;
		ListAdapter myadapter = new ContactArrayAdapter(this,
				R.layout.contacts_list_item,
				contacts);
        list.setAdapter(myadapter);

	}
	
	protected class ContactArrayAdapter extends ArrayAdapter<Contact> {

		private List<Contact> contacts;
		
		public ContactArrayAdapter(Context context, int textViewResourceId, List<Contact> objects) {
			super(context, textViewResourceId, objects);
			contacts = objects;
		}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.contacts_list_item, null);
                }
                Contact c = contacts.get(position);
                if (c != null) {
                	// save the contact into the tag
        			v.setTag(c);
                	
                	// add the name
                	TextView name = (TextView) v.findViewById(R.id.contact_name);
                	if(name != null)
                		name.setText(c.getName());

        			// set the click listener for the checkbox
        			CheckBox checkbox = (CheckBox) v.findViewById(R.id.contact_checkBox);
        			checkbox.setOnClickListener(new View.OnClickListener() {
        				public void onClick(View v) {
        					OnContactSelector(v);
        				}
        			});
        			// check the box if the contact is in the list
        			if(selectedContacts.contains(c))
        				checkbox.setChecked(true);
        			else
        				checkbox.setChecked(false);
                }
                return v;
        }
		
	}
	
	protected void OnContactSelector(View v){

		// get the contact object
		if (!(v.getParent() instanceof View))
			return;
		View parentView = (View) v.getParent();
		if (!(parentView.getTag() instanceof Contact))
			return;
		Contact contact = (Contact) parentView.getTag();

		// add/remove contact
		if (!(v instanceof CheckBox))
			return;
		CheckBox checkbox = (CheckBox) v;
		if (checkbox.isChecked())
			addContact(contact);
		else
			removeContact(contact);

	}
	

	protected void addContact(Contact contact) {
		if(contact == null) return;
		if(selectedContacts.contains(contact)) return;
		selectedContacts.add(contact);
	}
	
	protected boolean removeContact(Contact contact) {
		return selectedContacts.remove(contact); 
	}

}
