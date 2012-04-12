package com.github.searchbadger.util;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.test.InstrumentationTestCase;

public class ContactTest extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
	}
	
	@Override
	protected void tearDown() throws Exception {
	}
	

	public void testContact() {
		String id = "99";
		MessageSource source = MessageSource.STARRED;
		String name = "me";
		Bitmap picture = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		
		// test constructor
		Contact c = null;
		c = new Contact(id, source, name, picture);
		assertNotNull(c);
		
		// test getters and functions
		assertEquals(id, c.getId());
		assertEquals(source, c.getSource());
		assertEquals(name, c.getName());
		assertEquals(picture, c.getPicture());
		assertEquals(0, c.describeContents());
		assertEquals("" + name + " | " + id + " | " + source, c.toString());
		assertEquals(true, c.contains(id));
		assertEquals(false, c.contains("999"));
		
		// test equals
		Contact c2 = new Contact(id, source, name, picture);
		Contact c3 = new Contact("100", source, name, picture);
		Object c4 = new Object();
		assertEquals(true, c.equals(c));
		assertEquals(true, c.equals(c2));
		assertEquals(false, c.equals(c3));
		assertEquals(false, c.equals(c4));
		
		// test parcelable		
		Parcel p = Parcel.obtain();
        p.writeParcelable(c, 0);
        p.setDataPosition(0);
        Contact c5 = p.readParcelable(Contact.class.getClassLoader());
        assertEquals(c, c5);
        p.recycle();
        
        Contact[] contacts = Contact.CREATOR.newArray(5);
        assertEquals(5, contacts.length);
        
	}
	
}
