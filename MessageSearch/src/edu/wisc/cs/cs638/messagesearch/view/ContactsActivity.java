package edu.wisc.cs.cs638.messagesearch.view;

import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import edu.wisc.cs.cs638.messagesearch.R;
import edu.wisc.cs.cs638.messagesearch.core.MessageSearchController;
import edu.wisc.cs.cs638.messagesearch.core.MessageSearchModel;
import edu.wisc.cs.cs638.messagesearch.util.Contact;
import edu.wisc.cs.cs638.messagesearch.util.MessageSource;

public class ContactsActivity extends ListActivity {

	private static final String[] CONTACT_PROJECTION = new String[] {
			Contacts._ID, Contacts.DISPLAY_NAME };
	private final MessageSearchModel model = MessageSearchModel.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get the search sources
		List<MessageSource> searchSources = model.getSearchSources();

		// check if only one source is selected
		if (searchSources.size() != 1)
			return;

		// set the correct list adapter for the search type
		switch (searchSources.get(0)) {
		case SMS:
			setListAdapterSMS();
			break;

		}

	}

	protected void setListAdapterSMS() {
		// Get a cursor with all people
		Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI,
				CONTACT_PROJECTION, null, null, null);
		startManagingCursor(cursor);

		ListAdapter adapter = new SMSContactSimpleCursorAdapter(this,
				R.layout.contacts_list_item, cursor,
				new String[] { Contacts.DISPLAY_NAME },
				new int[] { R.id.contact_name });
		setListAdapter(adapter);
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
		}
	}

}
