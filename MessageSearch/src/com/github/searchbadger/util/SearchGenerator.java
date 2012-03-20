package com.github.searchbadger.util;

import java.util.List;

public interface SearchGenerator {
	public Search generateSearch();
	public List<MessageSource> getMessageSources();
}
