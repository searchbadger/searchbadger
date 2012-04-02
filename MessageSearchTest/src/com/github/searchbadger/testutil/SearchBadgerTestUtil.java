package com.github.searchbadger.testutil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.ContactSMS;
import com.github.searchbadger.util.MessageSource;


public class SearchBadgerTestUtil {

	public static final String SMS_URI = "content://sms";
	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String DATE = "date";
	public static final String TYPE = "type";
	public static final String BODY = "body";

	public static final int MESSAGE_TYPE_INBOX = 1;
	public static final int MESSAGE_TYPE_SENT = 2;
	
	public static void addSmsToDatabase( String address, long date, int type, String body ){
		ContentResolver contentResolver = SearchBadgerApplication.getAppContext().getContentResolver();
		
	    ContentValues values = new ContentValues();
	    values.put( ADDRESS, address );
	    values.put( DATE, date );
	    values.put( TYPE, type );
	    values.put( BODY, body );

	    // insert row into the SMS database
	    contentResolver.insert( Uri.parse( SMS_URI ), values );
	}
	
	

	public static void clearSmsDatabase() {
		ContentResolver contentResolver = SearchBadgerApplication.getAppContext().getContentResolver();
		
	    // clear the SMS database
	    contentResolver.delete( Uri.parse( SMS_URI ), null, null );
	}
	
	public static long addContactToDatabase(String firstname, String lastname, String number) {

		ContentResolver contentResolver = SearchBadgerApplication.getAppContext().getContentResolver();
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();
        
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .build());
        //INSERT NAME
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                //.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, szFullname) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastname) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstname) // Name of the person
                .build());
        //INSERT PHONE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build()); // 
        
    	// SAVE CONTACT IN BCR Structure
		Uri newContactUri = null;
		//PUSH EVERYTHING TO CONTACTS
        try
        {
            ContentProviderResult[] res = contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            if (res!=null && res[0]!=null) {
            	newContactUri = res[0].uri;	
            	// contact:content://com.android.contacts/raw_contacts/612
            	
            	Cursor c = contentResolver.query(newContactUri, new String[]{BaseColumns._ID}, null, null, null);
            	try {
            	    c.moveToFirst();
            	    long id = c.getLong(0);

                	Log.v("SearchBadger","" + id);
            	    return id;
            	} finally {
            	    c.close();
            	}
            }
        }
        catch (RemoteException e)
        { 
            // error
        	newContactUri = null;
        }
        catch (OperationApplicationException e) 
        {
            // error
        	newContactUri = null;
        }  
        
        return 0;
	}
	
	public static void clearContactDatabase() {

		ContentResolver contentResolver = SearchBadgerApplication.getAppContext().getContentResolver();
		
	    // clear the contact database
		Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
	            null, null, null, null);
	    while (cur.moveToNext()) {
	        try{
	            String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
	            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
	            System.out.println("The uri is " + uri.toString());
	            contentResolver.delete(uri, null, null);
	        }
	        catch(Exception e)
	        {
	            System.out.println(e.getStackTrace());
	        }
	    }
	}
	

	public static void addDefaultSmsDatabase() {

		Date date = new Date();
		// This is the test sms database that will be left after the tests have been run
		// so that we have some data we can manually search for.
		// Feel free to add any messages here
		// We could make a function to populate message from an input file if 
		// everyone wants their own custom test SMS database
		
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Foo Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Food Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar1 Inbox");
		addSmsToDatabase("1-111-111-1111", date.getTime(), MESSAGE_TYPE_INBOX, "Bar10 Inbox");
		
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_SENT, "Hello World Sent");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		addSmsToDatabase("2-222-222-2222", date.getTime(), MESSAGE_TYPE_INBOX, "Hello World Inbox");
		
	}

	public static void addDefaultContactDatabase() {
		addContactToDatabase("Homer", "Simpson", "1-111-111-1111");  
		addContactToDatabase("Marge", "Simpson", "2-222-222-2222");  
		addContactToDatabase("Lisa", "Simpson", "3-333-333-3333");   
		addContactToDatabase("Bart", "Simpson", "4-444-444-4444");   
		addContactToDatabase("Maggie", "Simpson", "5-555-555-5555"); 
	}

}
