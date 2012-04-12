package com.github.searchbadger.util;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.test.InstrumentationTestCase;

public class ContactSMSTest extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
	}
	
	@Override
	protected void tearDown() throws Exception {
	}
	

	public void testContactSMS() {
		String id = "99";
		MessageSource source = MessageSource.STARRED;
		String name = "me";
		Bitmap picture = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		List<String> addresses = new LinkedList<String>();
		addresses.add("1-111-111-1111");
		
		// test constructor
		ContactSMS c = null;
		c = new ContactSMS(id, source, name, picture, addresses);
		ContactSMS c2 = new ContactSMS(id, source, name, picture, null);
		assertNotNull(c);
		
		// test getters and functions
		assertEquals(1, c.getAddresses().size());
		assertEquals("1-111-111-1111", c.getAddresses().get(0));
		assertEquals(name, c.getName());
		assertEquals(picture, c.getPicture());
		assertEquals(0, c.describeContents());
		assertTrue(c.toString().length() > 0);
		assertEquals(true, c.contains(id));
		assertEquals(true, c.contains("1-111-111-1111"));
		assertEquals(false, c.contains("999"));
		assertEquals(false, c2.contains("1-111-111-1111"));
				
		// test parcelable		
		Parcel p = Parcel.obtain();
        p.writeParcelable(c, 0);
        p.setDataPosition(0);
        ContactSMS c5 = p.readParcelable(ContactSMS.class.getClassLoader());
        assertEquals(c, c5);
        p.recycle();
        
        ContactSMS[] contacts = ContactSMS.CREATOR.newArray(5);
        assertEquals(5, contacts.length);
        
	}
	
}
