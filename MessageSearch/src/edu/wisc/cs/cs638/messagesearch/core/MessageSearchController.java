package edu.wisc.cs.cs638.messagesearch.core;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import edu.wisc.cs.cs638.messagesearch.view.ContactsActivity;

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
			// start the select contact activity
			Context context = v.getContext();
            Intent intent = new Intent();
            intent.setClass(context, ContactsActivity.class);
            context.startActivity(intent);
		}
	}
	
	public class ContactSelector implements View.OnClickListener {
		public void onClick(View v) {
			// display the contact id for now
			View parentView = (View)v.getParent();
    		String contactId = (String)parentView.getTag();
    		Toast.makeText(v.getContext(), contactId, Toast.LENGTH_SHORT).show();
		}
	}
	
	public class ResultSelected implements View.OnClickListener {
		public void onClick(View v) {
			
		}
	}
}
