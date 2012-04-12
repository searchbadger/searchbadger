package com.github.searchbadger.util;

import java.util.Date;

import android.os.Parcel;
import android.test.InstrumentationTestCase;

public class MessageTest extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
	}
	
	@Override
	protected void tearDown() throws Exception {
	}
	

	public void testContact() {
		String Id = "77";
		String threadId = "23";
		Date date = new Date();
		String text = "Foo Bar";
		String author = "me";
		MessageSource source = MessageSource.TWITTER;
		boolean isStarred = true;
		
		// test constructor
		Message m = null;
		m = new Message(Id, threadId, author, source, date, text, isStarred);
		assertNotNull(m);
		
		// test getters and functions
		assertEquals(Id, m.getId());
		assertEquals(threadId, m.getThreadId());
		assertEquals(date, m.getDate());
		assertEquals(text, m.getText());
		assertEquals(author, m.getAuthor());
		assertEquals(source, m.getSource());
		assertEquals(isStarred, m.isStarred());
		assertEquals(0, m.describeContents());
		assertEquals(!isStarred, m.toogleStarred());
		
		// test equals
		Message m2 = new Message(m);
		Message m3 = new Message("372", threadId, author, source, date, text, isStarred);
		Object m4 = new Object();
		assertEquals(true, m.equals(m));
		assertEquals(true, m.equals(m2));
		assertEquals(false, m.equals(m3));
		assertEquals(false, m.equals(m4));
		
		// test parcelable		
		Parcel p = Parcel.obtain();
        p.writeParcelable(m, 0);
        p.setDataPosition(0);
        Message m5 = p.readParcelable(Message.class.getClassLoader());
		assertEquals(m5.getId(), m.getId());
		assertEquals(m5.getThreadId(), m.getThreadId());
		assertEquals(m5.getDate().getTime(), m.getDate().getTime());
		assertEquals(m5.getText(), m.getText());
		assertEquals(m5.getAuthor(), m.getAuthor());
		assertEquals(m5.getSource(), m.getSource());
		assertEquals(m5.isStarred(), m.isStarred());
        assertEquals(true, m.equals(m5));
        p.recycle();
        
        Message[] messages = Message.CREATOR.newArray(5);
        assertEquals(5, messages.length);
        
        // test compare
        assertEquals(true, m.compareTo(m2) == 0);
		Message m6 = new Message("372", threadId, author, source, new Date(1), text, isStarred);
        assertEquals(true, m.compareTo(m6) < 0);
	}
	
}
