package com.github.searchbadger.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.FriendsGetProfilePics;
import com.github.searchbadger.util.MessageSource;

public class ContactsActivity extends Activity {

	protected List<MessageSource> sources;
	protected List<Contact> selectedContacts;
	protected List<Contact> contacts;
	protected ContactsActivity thisActivity;
	protected EditText filterText;
	protected ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_layout);
        thisActivity = this;
        

        // set the click event for the done button
        Button buttonDone = (Button) findViewById(R.id.buttonDone);
        buttonDone.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				filterText.removeTextChangedListener(filterTextWatcher); 
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

		// add filter listener
		filterText = (EditText) findViewById(R.id.filter_box);
	    filterText.addTextChangedListener(filterTextWatcher);

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
						
						if(contacts == null) return;
				        ListView list = (ListView) findViewById(R.id.listView_contact);
						adapter = new ContactArrayAdapter(thisActivity,
								R.layout.contacts_list_item,
								contacts);
				        list.setAdapter(adapter);
					}
				});
				
			}

		});
		thread.start();

		
	}
	
	
	
	@Override
	protected void onDestroy() {
		try {
			filterText.removeTextChangedListener(filterTextWatcher); 
		} catch (Exception e) {}
		super.onDestroy();
	}



	private TextWatcher filterTextWatcher = new TextWatcher() {
	    public void afterTextChanged(Editable s) {}
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	    	// filter the contacts 
	    	((ContactArrayAdapter) adapter).getFilter().filter(s);
	    }
	};
	
	protected class ContactArrayAdapter extends ArrayAdapter<Contact> {

		private List<Contact> contacts;
		private List<Contact> contactsFiltered;
		private Filter filter;
		protected FriendsGetProfilePics friendsGetProfilePics;
		
		public ContactArrayAdapter(Context context, int textViewResourceId, List<Contact> objects) {
			super(context, textViewResourceId, objects);
			contactsFiltered = objects;
			contacts = objects;
			friendsGetProfilePics = new FriendsGetProfilePics();
			friendsGetProfilePics.setListener(this);
		}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.contacts_list_item, null);
                }
                Contact c = contactsFiltered.get(position);
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
                
                ImageView imageView = (ImageView) v.findViewById(R.id.contact_pic);
                switch(sources.get(0)) {
                case FACEBOOK:
                case TWITTER:
                	imageView.setImageBitmap(friendsGetProfilePics.getImage(
                    			c.getId(), c.getPictureUrl()));
                    break;
                    
                case SMS:
                	Bitmap img = loadContactPhoto(Integer.parseInt(c.getId()));
                	if(img != null) imageView.setImageBitmap(img);
                }
                
                return v;
        }
        
        @Override
		public int getCount() {
			return contactsFiltered.size();
		}

		@Override
        public Filter getFilter() {
            if (filter == null){
              filter  = new ContactFilter();
            }
            return filter;
        }

    	private class ContactFilter extends Filter{
    	
    	    @Override
    	    protected void publishResults(CharSequence prefix,
    	                                  FilterResults results) {
    	    	contactsFiltered = (ArrayList<Contact>)results.values;
    	        notifyDataSetChanged();
    	    }
    	
    	    protected FilterResults performFiltering(CharSequence prefix) {
    	          
				// perform the filter
				FilterResults results = new FilterResults();
				ArrayList<Contact> filterList = new ArrayList<Contact>();

				if (prefix != null && prefix.toString().length() > 0) {

					for (int index = 0; index < contacts.size(); index++) {
						Contact c = contacts.get(index);
						if (c.getName().toLowerCase().contains(prefix.toString().toLowerCase())) {
							filterList.add(c);
						}
					}
					results.values = filterList;
					results.count = filterList.size();
				} else {
					synchronized (contacts) {
						results.values = contacts;
						results.count = contacts.size();
					}
				}  

				return results;
    	    }
    	 }     
    	
	}
	

	
	protected void OnContactSelector(View v){

		// get the contact object
		if (v.getParent() instanceof View) {
			View parentView = (View) v.getParent();
			if (parentView.getTag() instanceof Contact) {
				Contact contact = (Contact) parentView.getTag();
				// add/remove contact
				if (v instanceof CheckBox) {
					CheckBox checkbox = (CheckBox) v;
					if (checkbox.isChecked())
						addContact(contact);
					else
						removeContact(contact);
				}
			}
		}

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

	public static Bitmap loadContactPhoto(long id) {
		ContentResolver contentResolver = SearchBadgerApplication.getAppContext().getContentResolver();
		Uri uri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, id);
		InputStream input = ContactsContract.Contacts
				.openContactPhotoInputStream(contentResolver, uri);
		if (input == null) {
			return null;
		}
		return BitmapFactory.decodeStream(input);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.default_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    
	        case R.id.menu_settings:
	        	ShowSettings();
	            return true;
	        case R.id.menu_help:
	        	ShowHelp();
	            return true;
	        case R.id.menu_about:
	        	ShowAbout();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void ShowSettings() {
		Intent intent = new Intent(this, AccountsActivity.class);
		startActivity(intent);
	}
	
	public void ShowHelp() {
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}
	
	public void ShowAbout() {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}
}
