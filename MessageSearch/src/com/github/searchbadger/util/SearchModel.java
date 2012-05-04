package com.github.searchbadger.util;

import java.util.List;
import java.util.Map;

public interface SearchModel {
	public void search(Search filter);
	public List<Contact> getContacts(MessageSource source);
	public List<Message> getSearchResults();
	public Search getCurrentSearch();
	public List<Message> getThread(Message message);
	public List<Message> getLastThread();
	public int getLastThreadIndex();
	public void resetLastThread();
	public List<Search> getRecentSearches();
	public List<Message> getStarredMessages();
	public boolean addStarredMessage(Message msg);
	public boolean removeStarredMessage(Message msg) ;
	public boolean containsStarredMessage(Message message);
	public boolean addRecentSearch(Search search);
	public void clearRecentSearches();
}
