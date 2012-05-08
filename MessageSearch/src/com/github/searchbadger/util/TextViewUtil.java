package com.github.searchbadger.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;

public class TextViewUtil {

	// change color here
	static public String authorTextColor = SearchBadgerApplication.getAppContext().getResources().getString(R.color.light_blue);
	static public String bodySelectedTextColor = SearchBadgerApplication.getAppContext().getResources().getString(R.color.light_green);
	static public String bodyRegexColor = SearchBadgerApplication.getAppContext().getResources().getString(R.color.yellow);

	static public Editable formatMessage(String author, String message) {
		if(author == null || message == null) return null;
		SpannableStringBuilder editable = new SpannableStringBuilder();
		Spanned spanned = Html.fromHtml("<font color='#" + authorTextColor.substring(3) + "'>" + author + ": </font>");
		editable.append(spanned);
		editable.append(message);
		return editable;
	}
	
	static public Editable formatMessageSelected(String author, String message) {
		if(author == null || message == null) return null;
		SpannableStringBuilder editable = new SpannableStringBuilder();
		Spanned spanned = Html.fromHtml("<font color='#" + authorTextColor.substring(3) + "'>" + author + ": </font>");
		editable.append(spanned);
		spanned = Html.fromHtml("<font color='#" + bodySelectedTextColor.substring(3) + "'>" + message + "</font>");
		editable.append(spanned);
		return editable;
	}
	
	static public Editable formatMessageSearch(String author, String message, Pattern pattern) {
		if(author == null || message == null || pattern == null) return null;
		SpannableStringBuilder editable = new SpannableStringBuilder();
		Spanned spanned = Html.fromHtml("<font color='#" + authorTextColor.substring(3) + "'>" + author + ": </font>");
		editable.append(spanned);
		Matcher matcher = pattern.matcher(message);
		spanned = Html.fromHtml(matcher.replaceAll("<font color='#" + bodyRegexColor.substring(3) + "'>$0</font>"));
		editable.append(spanned);
		return editable;
	}
}
