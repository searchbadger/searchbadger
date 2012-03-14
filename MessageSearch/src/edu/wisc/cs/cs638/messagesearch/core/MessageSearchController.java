package edu.wisc.cs.cs638.messagesearch.core;

import android.content.Context;
import android.view.View;

public class MessageSearchController {
	private static final MessageSearchController instance = new MessageSearchController();
	
	public static final MessageSearchController getInstance() {
		return instance;
	}
	
	public class SearchButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			
		}
	}
	
	public final class StarredMessageListener implements View.OnClickListener {
		public void onClick(View v) {
		}
	}
	
	public final class RecentSearchListener implements View.OnClickListener {
		public void onClick(View v) {
		}
	}
	
	public final class ContactSourceListener implements View.OnClickListener {
		public void onClick(View v) {
		}
	}
	
	public class ContactSelector implements View.OnClickListener {
		public void onClick(View v) {
			
		}
	}
	
	public class ResultSelected implements View.OnClickListener {
		public void onClick(View v) {
			
		}
	}
}
