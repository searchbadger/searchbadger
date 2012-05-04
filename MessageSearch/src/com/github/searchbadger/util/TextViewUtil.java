package com.github.searchbadger.util;

import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;

public class TextViewUtil {

	// change author color here
	static public String authorTextColor = SearchBadgerApplication.getAppContext().getResources().getString(R.color.light_blue);

	static public Editable formatMessage(String author, String message) {
		SpannableStringBuilder editable = new SpannableStringBuilder();
		Spanned spanned = Html.fromHtml("<font color='#" + authorTextColor.substring(3) + "'>" + author + ": </font>");
		editable.append(spanned);
		editable.append(message);
		return editable;
	}
}
