package edu.wisc.cs.cs638.messagesearch.core;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.wisc.cs.cs638.messagesearch.*;
import edu.wisc.cs.cs638.messagesearch.util.*;
import edu.wisc.cs.cs638.messagesearch.view.*;

public class MessageSearchController {
	private static final MessageSearchController instance = 
		new MessageSearchController();
	private final MessageSearchModel model = MessageSearchModel.getInstance();

	public static final MessageSearchController getInstance() {
		return instance;
	}

	public class SearchButtonListener implements View.OnClickListener {
		private SearchGenerator srchGen;
		public SearchButtonListener(SearchGenerator srchGen) {
			this.srchGen = srchGen;
		}
		public void onClick(View v) {
			model.search(srchGen.generateSearch());
			Intent resActIntent = new Intent(v.getContext(), 
					SearchResultActivity.class);
			v.getContext().startActivity(resActIntent);
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

	public final class ContactButtonListener implements View.OnClickListener {
		private SearchGenerator srchGen;
		public ContactButtonListener(SearchGenerator gen) {
			srchGen = gen;
		}
		public void onClick(View v) {
			// get the Message sources to pass to ContactsActivity
			List<MessageSource> sources = srchGen.getMessageSources();
			
			// start the select contact activity
			Context context = v.getContext();
			Intent intent = new Intent();
			intent.setClass(context, ContactsActivity.class);
			// TODO: need to put the Message Source list in the intent
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
			// TODO remove this
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
		
		private SearchActivity searchActiviy;
		
		public SearchSourceSelected(SearchActivity activity) {
			searchActiviy = activity;
		}
		
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
			// TODO: remove this
			// add/remove search type
			if (button.isChecked())
				model.addSearchSource(searchSource);
			else
				model.removeSearchSource(searchSource);
			

			// disable/enable the contact filter
			searchActiviy.updateContactFilter();

			// TODO remove this
			List<MessageSource> searchSources = model.getSearchSources();
			Toast.makeText(v.getContext(), searchSources.toString(),
					Toast.LENGTH_SHORT).show();
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
		
		private SearchActivity searchActiviy;
		
		public DateRangeSelected(SearchActivity activity){
			searchActiviy = activity;
		}
		
		public void onCheckedChanged(RadioGroup group, int selectedId) {
			
			// return if this is a clear check
			if(selectedId == -1) return;

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
				endDate = searchActiviy.getDateBefore();
				endDate.setHours(23);
				endDate.setMinutes(59);
				endDate.setSeconds(59);
				break;
				
			case R.id.radioAfter:
				beginDate = searchActiviy.getDateAfter();
				endDate = null;
				beginDate.setHours(0);
				beginDate.setMinutes(0);
				beginDate.setSeconds(0);
				break;
				
			case R.id.radioFrom:
				beginDate = searchActiviy.getDateFrom();
				endDate = searchActiviy.getDateTo();
				beginDate.setHours(0);
				beginDate.setMinutes(0);
				beginDate.setSeconds(0);
				endDate.setHours(23);
				endDate.setMinutes(59);
				endDate.setSeconds(59);
				break;
				
			default:
				return;
			}
			
			// update the model
			model.setBeginDate(beginDate);
			model.setEndDate(endDate);

		}
	}
	

	// the callback received when the user sets the date in the dialog

	public class DatePickerSelected implements DatePickerDialog.OnDateSetListener {
		
		private SearchActivity searchActiviy;
		
		public DatePickerSelected(SearchActivity activity){
			searchActiviy = activity;
		}

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			// update with new date selected
			RadioGroup radioGroupDate  = (RadioGroup) searchActiviy.findViewById(R.id.radioGroupDate);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DATE, dayOfMonth);
			switch (searchActiviy.getDatePickerId()) {
			case R.id.buttonBefore:
				searchActiviy.setDateBefore(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioBefore);
				break;
			case R.id.buttonAfter:
				searchActiviy.setDateAfter(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioAfter);
				break;
			case R.id.buttonFrom:
				searchActiviy.setDateFrom(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioFrom);
				break;
			case R.id.buttonTo:
				searchActiviy.setDateTo(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioFrom);
				break;
			}

			searchActiviy.updateDates();
			

		}
	}
}
