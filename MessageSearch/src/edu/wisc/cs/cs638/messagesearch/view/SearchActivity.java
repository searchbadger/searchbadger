package edu.wisc.cs.cs638.messagesearch.view;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.wisc.cs.cs638.messagesearch.R;
import edu.wisc.cs.cs638.messagesearch.core.MessageSearchController;
import edu.wisc.cs.cs638.messagesearch.core.MessageSearchModel;
import edu.wisc.cs.cs638.messagesearch.util.MessageSource;

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
	private MessageSearchController controller;
	private MessageSearchModel model;

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

		// update the filters
		toggleFilterDate();
		toggleFilterContacts();
		toggleFilterSentReceived();
		updateContactFilter();

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
		
		
		// TODO this is suppose to be done by the controller but
		// how do we make the controller enable/disable the contact filter?
		
		// add/remove search type
		if (button.isChecked())
			model.addSearchSource(searchSource);
		else
			model.removeSearchSource(searchSource);

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

}