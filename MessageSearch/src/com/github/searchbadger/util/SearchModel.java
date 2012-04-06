package com.github.searchbadger.util;

import java.util.List;
import java.util.Map;

public interface SearchModel {
	public void search(Search filter);
	public List<Contact> getContacts(MessageSource source);
	public List<Map<String,String>> getSearchResultsMap();
	public List<Message> getSearchResults();
	public Search getCurrentSearch();
	public List<Message> getThread(int index);
	public List<Search> getRecentSearches();
	public List<Message> getStarredMessages();
	public boolean addStarredMessage(Message msg);
	public boolean removeStarredMessage(Message msg) ;
	public boolean containsStarredMessage(Message message);
}
