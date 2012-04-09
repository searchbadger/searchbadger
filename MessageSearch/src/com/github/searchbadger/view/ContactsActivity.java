package com.github.searchbadger.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
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
	private List<Contact> contacts;
	private ContactsActivity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_layout);
        thisActivity = this;
        

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

		

		// show progress bar
		final ProgressDialog dialog = ProgressDialog.show(this, "", 
                "Please wait...", true);
		 
		Thread thread = new Thread(new Runnable() {
			public void run() {

				// perform search
		        contacts = SearchBadgerApplication.getSearchModel().getContacts(sources.get(0)); 
				
				runOnUiThread(new Runnable() {

					public void run() {
						try {
							// hide the progress bar
							dialog.dismiss();
					    } catch (Exception e) {
					    }
						

				        ListView list = (ListView) findViewById(R.id.listView_contact);
				        if(contacts == null) return;
						ListAdapter myadapter = new ContactArrayAdapter(thisActivity,
								R.layout.contacts_list_item,
								contacts);
				        list.setAdapter(myadapter);
					}
				});
				
			}

		});
		thread.start();

		

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
	
	protected List<Contact> getSelectedContacts() {
		return selectedContacts;
	}

}
