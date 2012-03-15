package edu.wisc.cs.cs638.messagesearch.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.wisc.cs.cs638.messagesearch.R;
import edu.wisc.cs.cs638.messagesearch.core.MessageSearchController;
import edu.wisc.cs.cs638.messagesearch.core.MessageSearchController.SearchSourceSelected;
import edu.wisc.cs.cs638.messagesearch.core.MessageSearchModel;
import edu.wisc.cs.cs638.messagesearch.util.MessageSource;
import edu.wisc.cs.cs638.messagesearch.util.SendReceiveType;

public class SearchActivity extends Activity {

	static final int DATE_DIALOG_ID = 0;

	private LinearLayout layoutFilterDate;
	private LinearLayout layoutFilterContacts;
	private LinearLayout layoutFilterSentReceived;
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

	private MessageSearchController controller;
	private MessageSearchModel model;

	private SearchSourceSelected searchSourceSelectedListener;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private final Date before = new Date();
	private final Date after = new Date();
	private final Date from = new Date();
	private final Date to = new Date();
	private int pickerButtonId;
	private DatePickerDialog pickerDate;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);

		model = MessageSearchModel.getInstance();
		controller = MessageSearchController.getInstance();
		
		pickerDate = new DatePickerDialog(this, pickerDateSetListener, 0, 0, 0);

		layoutFilterDate = (LinearLayout) findViewById(R.id.linearFilterDateOptions);
		layoutFilterContacts = (LinearLayout) findViewById(R.id.linearFilterContactsOptions);
		layoutFilterSentReceived = (LinearLayout) findViewById(R.id.linearFilterSentReceivedOptions);
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

		// set the onClick events
		searchSourceSelectedListener = controller.new SearchSourceSelected();
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
		checkBoxFilterSentReceived
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						toggleFilterSentReceived();
					}
				});
		searchButton.setOnClickListener(controller.new SearchButtonListener());
		contactsButton
				.setOnClickListener(controller.new ContactSourceListener());

		smsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				selectSearchSources(v);
			}
		});
		facebookButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				selectSearchSources(v);
			}
		});
		twitterButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				selectSearchSources(v);
			}
		});
		starButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				selectSearchSources(v);
			}
		});
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
		sendReceiveRadioGroup
				.setOnCheckedChangeListener(controller.new SendReceiveTypeSelected());
		radioGroupDate
				.setOnCheckedChangeListener(controller.new DateRangeSelected(
						before, after, from, to));
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

		// update the filters
		radioGroupDate.check(R.id.radioToday);
		toggleFilterDate();
		toggleFilterContacts();
		toggleFilterSentReceived();
		updateContactFilter();
		updateSendReceiveFilter();
		updateDates();

	}

	public void toggleFilterSentReceived() {
		if (checkBoxFilterSentReceived.isChecked())
			layoutFilterSentReceived.setVisibility(View.VISIBLE);
		else
			layoutFilterSentReceived.setVisibility(View.GONE);
	}

	public void toggleFilterContacts() {
		if (checkBoxFilterContacts.isChecked())
			layoutFilterContacts.setVisibility(View.VISIBLE);
		else
			layoutFilterContacts.setVisibility(View.GONE);
	}

	public void toggleFilterDate() {
		if (checkBoxFilterDate.isChecked())
			layoutFilterDate.setVisibility(View.VISIBLE);
		else
			layoutFilterDate.setVisibility(View.GONE);
	}

	public void selectSearchSources(View v) {

		// let the controller change the model
		searchSourceSelectedListener.onClick(v);

		// disable/enable the contact filter
		updateContactFilter();

		// TODO remove this
		List<MessageSource> searchSources = model.getSearchSources();
		Toast.makeText(v.getContext(), searchSources.toString(),
				Toast.LENGTH_SHORT).show();
	}

	public void updateContactFilter() {

		// disable/enable the contact filter
		List<MessageSource> searchSources = model.getSearchSources();
		boolean enableFilterContact = false;
		if (searchSources.size() == 1) {
			switch (searchSources.get(0)) {
			case SMS:
			case FACEBOOK:
			case TWITTER:
				enableFilterContact = true;
				break;
			}
		}
		if (enableFilterContact == false) {
			checkBoxFilterContacts.setChecked(false);
			toggleFilterContacts();
		}
		checkBoxFilterContacts.setEnabled(enableFilterContact);

	}

	public void updateSendReceiveFilter() {

		// select the correct type
		SendReceiveType type = model.getType();
		switch (type) {
		case SENT:
			sendReceiveRadioGroup.check(R.id.radioSent);
			break;

		case RECEIVED:
			sendReceiveRadioGroup.check(R.id.radioReceived);
			break;
		}
	}

	public void updateDates() {
		
		// update the dates shown on the buttons
		beforeButton.setText(dateFormat.format(before));
		afterButton.setText(dateFormat.format(after));
		fromButton.setText(dateFormat.format(from));
		toButton.setText(dateFormat.format(to));
	}

	public void insertTextSymbol(View v) {

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

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return pickerDate;
		}
		return null;
	}

	public void showDatePicker(View v) {

		// determine the date picker type
		Calendar cal = Calendar.getInstance();
		switch (v.getId()) {
		case R.id.buttonBefore:
			pickerButtonId = R.id.buttonBefore;
			cal.setTime(before);
			break;
		case R.id.buttonAfter:
			pickerButtonId = R.id.buttonAfter;
			cal.setTime(after);
			break;
		case R.id.buttonFrom:
			pickerButtonId = R.id.buttonFrom;
			cal.setTime(from);
			break;
		case R.id.buttonTo:
			pickerButtonId = R.id.buttonTo;
			cal.setTime(to);
			break;
		default:
			return;
		}

		// update and show the date picker
		pickerDate.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE));
		showDialog(DATE_DIALOG_ID);
	}

	// the callback received when the user sets the date in the dialog
	private DatePickerDialog.OnDateSetListener pickerDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			// update with new date selected
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DATE, dayOfMonth);
			switch (pickerButtonId) {
			case R.id.buttonBefore:
				before.setTime(cal.getTime().getTime());
				break;
			case R.id.buttonAfter:
				after.setTime(cal.getTime().getTime());
				break;
			case R.id.buttonFrom:
				from.setTime(cal.getTime().getTime());
				break;
			case R.id.buttonTo:
				to.setTime(cal.getTime().getTime());
				break;
			}

			updateDates();
		}
	};

}