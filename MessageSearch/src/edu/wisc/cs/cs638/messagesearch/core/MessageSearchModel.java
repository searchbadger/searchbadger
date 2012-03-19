package edu.wisc.cs.cs638.messagesearch.core;

import java.util.List;

import edu.wisc.cs.cs638.messagesearch.util.Message;
import edu.wisc.cs.cs638.messagesearch.util.Search;

public class MessageSearchModel {
	private final static MessageSearchModel instance = new MessageSearchModel();
	private Search _currentSearch;
	
	public static MessageSearchModel getInstance() {
		return instance;
	}
	
	public void search(Search filter) {
		_currentSearch = filter;
		// TODO: actually search
	}
	
	public Search getCurrentSearch() {
		return _currentSearch;
	}
	
	/*
	 * A list of recent searches is returned. 
	 * Note: not messages, these are actual searches.
	 * The search can be performed again by the user, who sees the filters set
	 * the searches are stored in a database so that we can access them again 
	 * even if the user shuts off their phone
	 */
	public List<Search> getRecentSearches() {
		
		return null;
	}
	
	/*
	 * TODO: interface with database for persistent storage of starred msgs
	 */
	public List<Message> getStarredMessages() {
		return null;
	}
	
	/*
	 * TODO: interface with database for persistent storage of starred msgs
	 */
	public boolean addStarredMessage(Message msg) {
		return true;
	}
	
	/*
	 * TODO: interface with database for persistent storage of starred msgs
	 */
	public boolean removeStarredMessage(Message msg) {
		return true;
	}	
	
	/*
	 * TODO Needs to access database. This probably will go through some other 
	 * class for SMSSearch or other search type
	 */
	public List<Message> getThread(Message msg) {
		return null;
	}
	
}
