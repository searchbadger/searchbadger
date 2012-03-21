package com.github.searchbadger.view;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.github.searchbadger.R;
import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.SelectedContacts;

public class ContactsActivity extends Activity {

	private static final String[] CONTACT_PROJECTION = new String[] {
			Contacts._ID, Contacts.DISPLAY_NAME };
	private int[] sources;
	private List<Contact> selectedContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_layout);
        

        // set the click event for the done button
        Button buttonDone = (Button) findViewById(R.id.buttonDone);
        buttonDone.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
        
        
        Intent intent = getIntent();
        sources = intent.getIntArrayExtra("messageSources");
        selectedContacts = SelectedContacts.getInstance().getSelectedContacts();
        

        ListView list = (ListView) findViewById(R.id.listView_contact);
        ListAdapter adapter = null;
		

		// check if only one source is selected
		if (sources.length != 1)
			return;

		// set the correct list adapter for the search type
		switch (sources[0]) {
		case 0: // TODO
			adapter = getListAdapterSMS();
			break;

		}
        list.setAdapter(adapter);

	}

	protected ListAdapter getListAdapterSMS() {
		
		// TODO Do we need to move this to the model since it's accessing
		//      the data?
		// Get a cursor with all people
		Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI,
				CONTACT_PROJECTION, null, null, null);
		startManagingCursor(cursor);

		ListAdapter adapter = new SMSContactSimpleCursorAdapter(this,
				R.layout.contacts_list_item, cursor,
				new String[] { Contacts.DISPLAY_NAME },
				new int[] { R.id.contact_name });
		return adapter;
	}

	protected class SMSContactSimpleCursorAdapter extends SimpleCursorAdapter {

		public SMSContactSimpleCursorAdapter(Activity context, int layout,
				Cursor c, String[] from, int[] to) {
			super(context, layout, c, from, to);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			super.bindView(view, context, cursor);

			// save the contact into the tag
			int contactId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contacts._ID)));
			String contactName = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
			Contact contact = new Contact(contactId,
					MessageSource.SMS,
					contactName,
					null);
			view.setTag(contact);

			// set the click listener for the checkbox
			CheckBox checkbox = (CheckBox) view
					.findViewById(R.id.contact_checkBox);
			checkbox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					OnContactSelector(v);
				}
			});
			// check the box if the contact is in the list
			if(selectedContacts.contains(contact))
				checkbox.setChecked(true);
			else
				checkbox.setChecked(false);
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
			selectedContacts.add(contact);
		else
			selectedContacts.remove(contact);

		// TODO remove this
		Toast.makeText(v.getContext(), selectedContacts.toString(),
				Toast.LENGTH_SHORT).show();
	}

}
