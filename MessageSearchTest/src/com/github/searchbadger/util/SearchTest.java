package com.github.searchbadger.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.test.InstrumentationTestCase;

public class SearchTest extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
	}
	
	@Override
	protected void tearDown() throws Exception {
	}
	

	public void testSearch() {
		String text = "Hello World";
		Date begin = new Date();
		Date end = new Date();
		List<MessageSource> sources = new ArrayList<MessageSource>();
		sources.add(MessageSource.FACEBOOK);
		List<Contact> contacts = new ArrayList<Contact>();
		contacts.add(new Contact("23", null, null, null));
		SendReceiveType type = SendReceiveType.RECEIVED;
		
		Search s = new Search(text, begin, end, sources, contacts, type);
		Search s2 = new Search(s);

		// test getters and functions
		assertEquals(text, s.getText());
		assertEquals(begin, s.getBegin());
		assertEquals(end, s.getEnd());
		assertEquals(sources, s.getSources());
		assertEquals(contacts, s.getContacts());
		assertEquals(type, s.getType());
		
		assertEquals(s.getText(), s2.getText());
		assertEquals(s.getBegin(), s2.getBegin());
		assertEquals(s.getEnd(), s2.getEnd());
		assertEquals(s.getSources(), s2.getSources());
		assertEquals(s.getContacts(), s2.getContacts());
		assertEquals(s.getType(), s2.getType());
		
		assertEquals(false, new Search("abc", null, null, null, null, null).containsRegEx());
		assertEquals(true, new Search("#", null, null, null, null, null).containsRegEx());
		assertEquals(true, new Search("*", null, null, null, null, null).containsRegEx());
		assertEquals(true, new Search("_", null, null, null, null, null).containsRegEx());
		assertEquals(true, new Search("abc_", null, null, null, null, null).containsRegEx());
		assertEquals(true, new Search("ab#c", null, null, null, null, null).containsRegEx());
		assertEquals(true, new Search("*abc", null, null, null, null, null).containsRegEx());
		
		// TODO
		// right now, just call the function for code coverage
		// probably want to make sure they return the correct conversion
		new Search("[abc]", null, null, null, null, null).getFQLText();
		new Search("[abc]", null, null, null, null, null).getGlobText();
		new Search("[abc]", null, null, null, null, null).getJavaRegexText();
		
		new Search("a_bc", null, null, null, null, null).getFQLText();
		new Search("a_bc", null, null, null, null, null).getGlobText();
		new Search("a_bc", null, null, null, null, null).getJavaRegexText();
		
		new Search("ab*c", null, null, null, null, null).getFQLText();
		new Search("ab*c", null, null, null, null, null).getGlobText();
		new Search("ab*c", null, null, null, null, null).getJavaRegexText();
		
		new Search("abc#", null, null, null, null, null).getFQLText();
		new Search("abc#", null, null, null, null, null).getGlobText();
		new Search("abc#", null, null, null, null, null).getJavaRegexText();
		
		new Search("~/zx'cv;bcvn.d.;ero ksda zxcva!@#$%^&*&^(*&()(*_+hqweisjfklxzcvn,abc#][|?><,./\\{}", null, null, null, null, null).getFQLText();
		new Search("~/zx'cv;bcvn.d.;ero ksda zxcva!@#$%^&*&^(*&()(*_+hqweisjfklxzcvn,abc#][|?><,./\\{}", null, null, null, null, null).getGlobText();
		new Search("~/zx'cv;bcvn.d.;ero ksda zxcva!@#$%^&*&^(*&()(*_+hqweisjfklxzcvn,abc#][|?><,./\\{}", null, null, null, null, null).getJavaRegexText();
        
	}
	
}
