package com.github.searchbadger.view;

import java.util.List;

import com.github.searchbadger.core.MessageSearchController;
import com.github.searchbadger.core.MessageSearchModel;
import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.MessageSource;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import edu.wisc.cs.cs638.messagesearch.R;

public class ContactsActivity extends Activity {

	private static final String[] CONTACT_PROJECTION = new String[] {
			Contacts._ID, Contacts.DISPLAY_NAME };
	private final MessageSearchModel model = MessageSearchModel.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_layout);

        ListView list = (ListView) findViewById(R.id.listView_contact);
        ListAdapter adapter = null;
		
		// get the search sources
		List<MessageSource> searchSources = model.getSearchSources();

		// check if only one source is selected
		if (searchSources.size() != 1)
			return;

		// set the correct list adapter for the search type
		switch (searchSources.get(0)) {
		case SMS:
			adapter = getListAdapterSMS();
			break;

		}
        list.setAdapter(adapter);

        // set the click event for the done button
        Button buttonDone = (Button) findViewById(R.id.buttonDone);
        buttonDone.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
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
			// TODO Auto-generated constructor stub
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
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
			checkbox.setOnClickListener(MessageSearchController.getInstance().new ContactSelector());

			// check the box if the contact is in the list
			List<Contact> contacts = model.getContacts();
			if(contacts.contains(contact))
				checkbox.setChecked(true);
			else
				checkbox.setChecked(false);
		}
	}

}
