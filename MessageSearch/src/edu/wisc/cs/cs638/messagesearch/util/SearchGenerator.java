package edu.wisc.cs.cs638.messagesearch.util;

import java.util.List;

public interface SearchGenerator {
	public Search generateSearch();
	public List<MessageSource> getMessageSources();
}
