package edu.wisc.cs.cs638.messagesearch.view;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
	
	private MessageSearchController controller;
	private MessageSearchModel model;
	
	private SearchSourceSelected searchSourceSelectedListener;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);

		model = MessageSearchModel.getInstance();
		controller = MessageSearchController.getInstance();

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
		sendReceiveRadioGroup.setOnCheckedChangeListener(controller.new SendReceiveTypeSelected());


		// update the filters
		toggleFilterDate();
		toggleFilterContacts();
		toggleFilterSentReceived();
		updateContactFilter();
		updateSendReceiveFilter();

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
		switch(type)
		{
		case SENT:
			sendReceiveRadioGroup.check(R.id.radioSent);
			break;
			
		case RECEIVED:
			sendReceiveRadioGroup.check(R.id.radioReceived);
			break;
		}
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
		//       text was selected backward
		int startSelection = searchInputText.getSelectionStart();
		int endSelection = searchInputText.getSelectionEnd();
		int start = startSelection;
		int end = endSelection;
		if(endSelection < startSelection) {
			start = endSelection;
			end = startSelection;
		}
		String searchText = searchInputText.getText().toString();
		String leftOfSelection = searchText.substring(0, start);
		String rightOfSelection = searchText.substring(end);
		searchText = leftOfSelection + symbol + rightOfSelection;
		searchInputText.setText(searchText);
		searchInputText.setSelection(start+1);
	
	}
	

}