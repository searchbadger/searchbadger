package edu.wisc.cs.cs638.messagesearch.core;

import android.view.View;

public class MessageSearchController {
	private final MessageSearchController instance = new MessageSearchController();
	
	public MessageSearchController getInstance() {
		return instance;
	}
	
	public class SearchButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			
		}
	}
	
	public class StarredMessageListener implements View.OnClickListener {
		public void onClick(View v) {
		}
	}
	
	public class RecentSearchListener implements View.OnClickListener {
		public void onClick(View v) {
		}
	}
	
	public class ContactSourceListener implements View.OnClickListener {
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
