package edu.wisc.cs.cs638.messagesearch.core;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.wisc.cs.cs638.messagesearch.R;
import edu.wisc.cs.cs638.messagesearch.util.Contact;
import edu.wisc.cs.cs638.messagesearch.util.MessageSource;
import edu.wisc.cs.cs638.messagesearch.util.SendReceiveType;
import edu.wisc.cs.cs638.messagesearch.view.ContactsActivity;

public class MessageSearchController {
	private static final MessageSearchController instance = new MessageSearchController();
	private final MessageSearchModel model = MessageSearchModel.getInstance();

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

			// get the contact object
			if (!(v.getParent() instanceof View))
				return;
			View parentView = (View) v.getParent();
			if (!(parentView.getTag() instanceof Contact))
				return;
			Contact contact = (Contact) parentView.getTag();

			// add/remove contact
			if (!(v instanceof CheckBox))
				return;
			CheckBox checkbox = (CheckBox) v;
			if (checkbox.isChecked())
				model.addContact(contact);
			else
				model.removeContact(contact);

			// TODO remove this
			Toast.makeText(v.getContext(), model.getContacts().toString(),
					Toast.LENGTH_SHORT).show();
		}
	}

	public class ResultSelected implements View.OnClickListener {
		public void onClick(View v) {

		}
	}
	
	public class SearchSourceSelected implements View.OnClickListener {
		public void onClick(View v) {
			if (!(v instanceof ToggleButton))
				return;
			ToggleButton button = (ToggleButton) v;

			// determine the search type
			MessageSource searchSource = null;
			switch (v.getId()) {
			case R.id.toggleButtonTypeSMS:
				searchSource = MessageSource.SMS;
				break;
			case R.id.toggleButtonTypeFacebook:
				searchSource = MessageSource.FACEBOOK;
				break;
			case R.id.toggleButtonTypeTwitter:
				searchSource = MessageSource.TWITTER;
				break;
			case R.id.toggleButtonTypeStar:
				searchSource = MessageSource.STARRED;
				break;
			}
			
			// add/remove search type
			if (button.isChecked())
				model.addSearchSource(searchSource);
			else
				model.removeSearchSource(searchSource);
		}
	}

	public class SendReceiveTypeSelected implements RadioGroup.OnCheckedChangeListener {
		public void onCheckedChanged(RadioGroup group, int selectedId) {

			// set the send/receive type
			switch (selectedId) {
			case R.id.radioSent:
				model.setType(SendReceiveType.SENT);
				break;

			case R.id.radioReceived:
				model.setType(SendReceiveType.RECEIVED);
				break;
			}

		}
	}
	
	public class DateRangeSelected implements RadioGroup.OnCheckedChangeListener {
		
		private Date before;
		private Date after;
		private Date from;
		private Date to;
		
		public DateRangeSelected(Date before, Date after, Date from, Date to){
			this.before = before;
			this.after = after;
			this.from = from;
			this.to = to;
		}
		
		public void onCheckedChanged(RadioGroup group, int selectedId) {

			// set the default begin and end date to be today
			Calendar cal = Calendar.getInstance();
			Date beginDate = new Date();
			Date endDate = new Date();
			beginDate.setHours(0);
			beginDate.setMinutes(0);
			beginDate.setSeconds(0);
			endDate.setHours(23);
			endDate.setMinutes(59);
			endDate.setSeconds(59);
			
			// set the begin and end date
			switch (selectedId) {
			case R.id.radioToday:
				break;

			case R.id.radioYesterday:
		        cal.setTime(beginDate);
		        cal.add(Calendar.DATE, -1);
		        beginDate = cal.getTime();
				break;
				
			case R.id.radioPaskWeek:
		        cal.setTime(beginDate);
		        cal.add(Calendar.DATE, -7);
		        beginDate = cal.getTime();
				break;
				
			case R.id.radioPastMonth:
		        cal.setTime(beginDate);
		        cal.add(Calendar.DATE, beginDate.getDate() * -1 + 1);
		        beginDate = cal.getTime();
				break;
				
			case R.id.radioBefore:
				beginDate = null;
				endDate = before;
				endDate.setHours(23);
				endDate.setMinutes(59);
				endDate.setSeconds(59);
				break;
				
			case R.id.radioAfter:
				beginDate = after;
				endDate = null;
				beginDate.setHours(0);
				beginDate.setMinutes(0);
				beginDate.setSeconds(0);
				break;
				
			case R.id.radioFrom:
				beginDate = from;
				endDate = to;
				beginDate.setHours(0);
				beginDate.setMinutes(0);
				beginDate.setSeconds(0);
				endDate.setHours(23);
				endDate.setMinutes(59);
				endDate.setSeconds(59);
				break;
			}

			// TODO remove this
			String str1 = beginDate == null ? "before" : beginDate.toString();
			String str2 = endDate == null ? "after" : endDate.toString();
			Toast.makeText(group.getContext(), str1 + " | " + str2,
					Toast.LENGTH_SHORT).show();
		}
	}
}
