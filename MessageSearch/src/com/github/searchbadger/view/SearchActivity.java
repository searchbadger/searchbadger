package com.github.searchbadger.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerController;
import com.github.searchbadger.core.SearchBadgerModel;
import com.github.searchbadger.util.Contact;
import com.github.searchbadger.util.MessageSource;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SearchGenerator;
import com.github.searchbadger.util.SendReceiveType;

public class SearchActivity extends Activity implements SearchGenerator {

	static private final int DATE_DIALOG_ID = 0;
	static public final int PICK_CONTACT_REQUEST = 1;
	static public final String INTENT_DATA_SOURCES = "messageSources";
	static public final String INTENT_DATA_CONTACTS = "selectedContacts";

	private LinearLayout layoutFilterDate;
	private LinearLayout layoutFilterContacts;
	private LinearLayout layoutFilterSentReceived;
	private LinearLayout layoutButtonSymbols;
	private CheckBox checkBoxFilterDate;
	private CheckBox checkBoxFilterContacts;
	private CheckBox checkBoxFilterSentReceived;
	private Button searchButton;
	private Button contactsButton;
	private ToggleButton smsButton;
	private ToggleButton facebookButton;
	private ToggleButton twitterButton;
	private ToggleButton starButton;
	private Button symbolPoundButton;
	private Button symbolStarButton;
	private Button symbolUnderscoreButton;
	private EditText searchInputText;
	private RadioGroup sendReceiveRadioGroup;
	private RadioGroup radioGroupDate;
	private Button beforeButton;
	private Button afterButton;
	private Button fromButton;
	private Button toButton;
	private TextView contactsText;
	private ImageButton clearSearchButton;

	private SearchBadgerController controller;
	private SearchBadgerModel model;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private int pickerButtonId;
	private DatePickerDialog pickerDate;
	private Date beforeDate;
	private Date afterDate;
	private Date fromDate;
	private Date toDate;
	
	private TextWatcher watcher;
	
	protected List<Contact> selectedContacts = new ArrayList<Contact>();


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);

		model = SearchBadgerModel.getInstance();
		controller = SearchBadgerController.getInstance();
		
		/* Android 2.3 DatePickerDiaglog cannot handle the original date passed
		 *  to its constructor being all zeros, while the ICS (4.0) DatePickerDialog
		 *  can.  So, we now pass in a date of February 1, 2012. 
		 */
		pickerDate = new DatePickerDialog(this, this.new DatePickerSelected(this), 2012, 1, 1);

		layoutFilterDate = (LinearLayout) findViewById(R.id.linearFilterDateOptions);
		layoutFilterContacts = (LinearLayout) findViewById(R.id.linearFilterContactsOptions);
		layoutFilterSentReceived = (LinearLayout) findViewById(R.id.linearFilterSentReceivedOptions);
		layoutButtonSymbols = (LinearLayout) findViewById(R.id.linearLayoutButtonSymbols);
		checkBoxFilterDate = (CheckBox) findViewById(R.id.checkBoxFilterDate);
		checkBoxFilterContacts = (CheckBox) findViewById(R.id.checkBoxFilterContacts);
		checkBoxFilterSentReceived = (CheckBox) findViewById(R.id.checkBoxFilterSentReceived);
		searchButton = (Button) findViewById(R.id.buttonSearch);
		contactsButton = (Button) findViewById(R.id.buttonSelectContacts);
		smsButton = (ToggleButton) findViewById(R.id.toggleButtonTypeSMS);
		facebookButton = (ToggleButton) findViewById(R.id.toggleButtonTypeFacebook);
		twitterButton = (ToggleButton) findViewById(R.id.toggleButtonTypeTwitter);
		starButton = (ToggleButton) findViewById(R.id.toggleButtonTypeStar);
		symbolPoundButton = (Button) findViewById(R.id.buttonSymbolPound);
		symbolStarButton = (Button) findViewById(R.id.buttonSymbolStar);
		symbolUnderscoreButton = (Button) findViewById(R.id.buttonSymbolUnderscore);
		searchInputText = (EditText) findViewById(R.id.editTextSearch);
		sendReceiveRadioGroup = (RadioGroup) findViewById(R.id.radioGroupSentReceived);
		radioGroupDate = (RadioGroup) findViewById(R.id.radioGroupDate);
		beforeButton = (Button) findViewById(R.id.buttonBefore);
		afterButton = (Button) findViewById(R.id.buttonAfter);
		fromButton = (Button) findViewById(R.id.buttonFrom);
		toButton = (Button) findViewById(R.id.buttonTo);
		contactsText = (TextView) findViewById(R.id.textViewSelectContacts);
		clearSearchButton = (ImageButton) findViewById(R.id.clear_button);

		// set the onClick events
		checkBoxFilterDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleFilterDate();
			}
		});
		checkBoxFilterContacts.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleFilterContacts();
			}
		});
		checkBoxFilterSentReceived.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleFilterSentReceived();
			}
		});
		searchButton.setOnClickListener(controller.new 
				SearchButtonListener(this));
		contactsButton.setOnClickListener(controller.new 
				ContactButtonListener(this));

		View.OnClickListener searchSourceSelected = new View.OnClickListener() {
			public void onClick(View v) {
				updateSourceSelection(v);
			}
		};
		smsButton.setOnClickListener(searchSourceSelected);
		facebookButton.setOnClickListener(searchSourceSelected);
		twitterButton.setOnClickListener(searchSourceSelected);
		starButton.setOnClickListener(searchSourceSelected);

		symbolPoundButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				insertTextSymbol(v);
			}
		});
		symbolStarButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				insertTextSymbol(v);
			}
		});
		symbolUnderscoreButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				insertTextSymbol(v);
			}
		});

		// XXX radioGroupDate.setOnCheckedChangeListener(controller.new DateRangeSelected(this));

		beforeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDatePicker(v);
			}
		});
		afterButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDatePicker(v);
			}
		});
		fromButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDatePicker(v);
			}
		});
		toButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDatePicker(v);
			}
		});
		
		searchInputText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				toggleButtonSymbols(hasFocus);
			}
		});
		
		clearSearchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				searchInputText.setText("");
			}
		});
		
		// this will color the regex symbols
		watcher = new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				searchInputText.removeTextChangedListener(watcher);
				int startSelection = searchInputText.getSelectionStart();
				int stopSelection = searchInputText.getSelectionEnd();
				searchInputText.setText(colorRegexString(searchInputText.getText().toString()));
				searchInputText.setSelection(startSelection, stopSelection); // restore selection
				searchInputText.addTextChangedListener(watcher);
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {}
		};
		searchInputText.addTextChangedListener(watcher);

		// update the filters
		radioGroupDate.check(R.id.radioToday);
		sendReceiveRadioGroup.check(R.id.radioSent);
		smsButton.toggle();
		
		toggleFilterDate();
		toggleFilterContacts();
		toggleFilterSentReceived();
		
		
		beforeDate = new Date();
		afterDate = new Date();
		fromDate = new Date();
		toDate = new Date();
		updateDateButtons();
		updateTextContacts();
		
		searchInputText.requestFocus();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// XXX updateTextContacts();
	}


	private int getDatePickerId() {
		return pickerButtonId;
	}

	private void toggleFilterSentReceived() {
		if (checkBoxFilterSentReceived.isChecked())
			layoutFilterSentReceived.setVisibility(View.VISIBLE);
		else
			layoutFilterSentReceived.setVisibility(View.GONE);
	}

	private void toggleFilterContacts() {
		if (checkBoxFilterContacts.isChecked())
			layoutFilterContacts.setVisibility(View.VISIBLE);
		else
			layoutFilterContacts.setVisibility(View.GONE);
	}

	private void toggleFilterDate() {
		if (checkBoxFilterDate.isChecked())
			layoutFilterDate.setVisibility(View.VISIBLE);
		else
			layoutFilterDate.setVisibility(View.GONE);
	}
	
	private void toggleButtonSymbols(boolean hasFocus) {
		if(hasFocus)
			layoutButtonSymbols.setVisibility(View.VISIBLE);
		else
			layoutButtonSymbols.setVisibility(View.GONE);
	}

	private void insertTextSymbol(View v) {

		// determine the button type
		String symbol = "";
		switch (v.getId()) {
		case R.id.buttonSymbolPound:
			symbol = "#";
			break;
		case R.id.buttonSymbolStar:
			symbol = "*";
			break;
		case R.id.buttonSymbolUnderscore:
			symbol = "_";
			break;
		}

		// insert the symbol selected
		// note: the selection start can be the end index if the
		// text was selected backward
		int startSelection = searchInputText.getSelectionStart();
		int endSelection = searchInputText.getSelectionEnd();
		int start = startSelection;
		int end = endSelection;
		if (endSelection < startSelection) {
			start = endSelection;
			end = startSelection;
		}
		String searchText = searchInputText.getText().toString();
		String leftOfSelection = searchText.substring(0, start);
		String rightOfSelection = searchText.substring(end);
		searchText = leftOfSelection + symbol + rightOfSelection;
		searchInputText.setText(searchText);
		searchInputText.setSelection(start + 1);
	}


	private void updateSourceSelection(View v) {
		
		// this will enable/disable the select contact button depending on the sources selected
		List<MessageSource> sources = getMessageSources();
		if(sources.size() != 1) {
			contactsText.setText("The contact selection filter is only available when a single source is selected.");
			contactsButton.setEnabled(false);
		} else {
			updateTextContacts();
			contactsButton.setEnabled(true);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return pickerDate;
		}
		return null;
	}

	private void showDatePicker(View v) {

		// determine the date picker type
		Calendar cal = Calendar.getInstance();
		switch (v.getId()) {
		case R.id.buttonBefore:
			pickerButtonId = R.id.buttonBefore;
			cal.setTime(beforeDate);
			break;
		case R.id.buttonAfter:
			pickerButtonId = R.id.buttonAfter;
			cal.setTime(afterDate);
			break;
		case R.id.buttonFrom:
			pickerButtonId = R.id.buttonFrom;
			cal.setTime(fromDate);
			break;
		case R.id.buttonTo:
			pickerButtonId = R.id.buttonTo;
			cal.setTime(toDate);
			break;
		default:
			return;
		}

		// update and show the date picker
		pickerDate.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE));
		showDialog(DATE_DIALOG_ID);
	}
	
	//the callback received when the user sets the date in the dialog

	private void setDateBefore(Date date) {
		beforeDate = date;
	}
	private void setDateAfter(Date date) {
		afterDate = date;
	}
	private void setDateFrom(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		this.calToBeginningOfDay(cal);
		fromDate = cal.getTime();
	}
	private void setDateTo(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		this.calToEndOfDay(cal);
		toDate = cal.getTime();
	}
	/*
	 * update the text on the before, after, from and to buttons
	 * to match the date picked
	 */
	private void updateDateButtons() {
		beforeButton.setText(dateFormat.format(beforeDate));
		afterButton.setText(dateFormat.format(afterDate));
		fromButton.setText(dateFormat.format(fromDate));
		toButton.setText(dateFormat.format(toDate));
	}


	private class DatePickerSelected implements DatePickerDialog.OnDateSetListener {

		private SearchActivity searchActivity;

		public DatePickerSelected(SearchActivity activity){
			searchActivity = activity;
		}

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			// update with new date selected
			RadioGroup radioGroupDate  = (RadioGroup) searchActivity.findViewById(R.id.radioGroupDate);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DATE, dayOfMonth);
			switch (searchActivity.getDatePickerId()) {
			case R.id.buttonBefore:
				searchActivity.setDateBefore(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioBefore);
				break;
			case R.id.buttonAfter:
				searchActivity.setDateAfter(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioAfter);
				break;
			case R.id.buttonFrom:
				searchActivity.setDateFrom(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioFrom);
				break;
			case R.id.buttonTo:
				searchActivity.setDateTo(cal.getTime());
				radioGroupDate.clearCheck();
				radioGroupDate.check(R.id.radioFrom);
				break;
			}

			searchActivity.updateDateButtons();


		}
	}

	private void calToBeginningOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}
	
	private void calToEndOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
	}
	
	public Search generateSearch() {
		// TODO Auto-generated method stub
		String text;
		Date begin;
		Date end;
		List<MessageSource> sources; 
		List<Contact> contacts;
		SendReceiveType type;

		// get text
		text = searchInputText.getText().toString();
		
		Calendar cal = Calendar.getInstance();
		// get begin date
		// get end date
		if (checkBoxFilterDate.isChecked()) {
			switch (radioGroupDate.getCheckedRadioButtonId()) {
			case R.id.radioToday:
				calToBeginningOfDay(cal);
				begin = cal.getTime();
				calToEndOfDay(cal);
				end = cal.getTime();
				break;
			case R.id.radioSinceYesterday:
				calToEndOfDay(cal);
				end = cal.getTime();
				cal.add(Calendar.DAY_OF_YEAR, -1);
				calToBeginningOfDay(cal);
				begin = cal.getTime();
				break;
			case R.id.radioPaskWeek:
				calToEndOfDay(cal);
				end = cal.getTime();
				cal.add(Calendar.DAY_OF_YEAR, -7);
				calToBeginningOfDay(cal);
				begin = cal.getTime();
				break;
				
			case R.id.radioPastMonth:
				calToEndOfDay(cal);
				end = cal.getTime();
				cal.add(Calendar.MONTH, -1);
				calToBeginningOfDay(cal);
				begin = cal.getTime();
				break;
				
			case R.id.radioBefore:
				cal.setTime(beforeDate);
				cal.add(Calendar.DAY_OF_YEAR, -1);
				this.calToEndOfDay(cal);
				end = cal.getTime();
				begin = null;
				break;
				
			case R.id.radioAfter:
				cal.setTime(this.afterDate);
				cal.add(Calendar.DAY_OF_YEAR, 1);
				this.calToBeginningOfDay(cal);
				begin = cal.getTime();
				end = null;
				break;
				
			case R.id.radioFrom:
				begin = this.fromDate;
				end = this.toDate;
				break;
			default:
				begin = null;
				end = null;
				break;
			}
		} else {
			begin = null;
			end = null;
		}

		// get sources
		sources = getMessageSources();
		
		// get selectedContacts
		contacts = null;
		if (checkBoxFilterContacts.isChecked()) {
			contacts = selectedContacts;			
		}

		// get type
		if (checkBoxFilterSentReceived.isChecked()) {
			switch (sendReceiveRadioGroup.getCheckedRadioButtonId()) {
			case R.id.radioSent:
				type = SendReceiveType.SENT;
				break;

			case R.id.radioReceived:
				type = SendReceiveType.RECEIVED;
				break;

			default :
				type = null;
			}
		} else {
			type = null;
		}


		return new Search(text, begin, end, sources, contacts, type);
	}

	public List<MessageSource> getMessageSources() {
		List<MessageSource> sources = new ArrayList<MessageSource>(4);
		// check if sms is picked
		if (smsButton.isChecked()) {
			sources.add(MessageSource.SMS);
		}
		// check if fb is picked
		if (facebookButton.isChecked()) {
			sources.add(MessageSource.FACEBOOK);
		}
		// check if twitter is picked
		if (twitterButton.isChecked()) {
			sources.add(MessageSource.TWITTER);
		}
		// check if starred is picked
		if (starButton.isChecked()) {
			sources.add(MessageSource.STARRED);
		}
		return sources;
	}	
	
	public List<Contact> getSelectedContacts() {
		return selectedContacts;
	}
	
	protected Editable colorRegexString(String string) {
		SpannableStringBuilder editable = new SpannableStringBuilder();
		
		char letter;
		Spanned spanned;
		for(int i = 0; i < string.length(); i++) {
			letter = string.charAt(i);
			if(letter == '#' || letter == '*' || letter == '_') {
				spanned = Html.fromHtml("<font color='#46a546'><b>" + letter + "</b></font>");
				editable.append(spanned);
			} else {
				editable.append(letter);
			}
		}
		
		return editable;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                if(data != null) {
        	        selectedContacts = data.getParcelableArrayListExtra(INTENT_DATA_CONTACTS);
        	        updateTextContacts();
                }
            }
        }
	}

	public void updateTextContacts() {
		
		StringBuilder listNames = new StringBuilder();
		
		// create and show the list of selected contacts
		listNames.append("Selected contacts: ");
		if(selectedContacts.size() == 0) {
			listNames.append("None");
		}
		else {
			Iterator<Contact> iterator = selectedContacts.iterator();
			while (iterator.hasNext()) {
				listNames.append(iterator.next().getName());
				if(iterator.hasNext())
					listNames.append(", ");
			} 
		}
		contactsText.setText(listNames);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.default_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    
	        case R.id.menu_settings:
	        	ShowSettings();
	            return true;
	        case R.id.menu_help:
	        	ShowHelp();
	            return true;
	        case R.id.menu_about:
	        	ShowAbout();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void ShowSettings() {
		Intent intent = new Intent(this, AccountsActivity.class);
		startActivity(intent);
	}
	
	public void ShowHelp() {
		
	}
	
	public void ShowAbout() {
		
	}
	
}