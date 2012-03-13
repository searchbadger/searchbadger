package edu.wisc.cs.cs638.messagesearch.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import edu.wisc.cs.cs638.messagesearch.R;


public class SearchActivity extends Activity {

	private LinearLayout layoutFilterDate;
	private LinearLayout layoutFilterContacts;
	private LinearLayout layoutFilterSentReceived;
	private CheckBox checkBoxFilterDate;
	private CheckBox checkBoxFilterContacts;
	private CheckBox checkBoxFilterSentReceived;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        
         layoutFilterDate = (LinearLayout) findViewById(R.id.linearFilterDateOptions);
         layoutFilterContacts = (LinearLayout) findViewById(R.id.linearFilterContactsOptions);
         layoutFilterSentReceived = (LinearLayout) findViewById(R.id.linearFilterSentReceivedOptions);
         checkBoxFilterDate = (CheckBox) findViewById(R.id.checkBoxFilterDate);
         checkBoxFilterContacts = (CheckBox) findViewById(R.id.checkBoxFilterContacts);
         checkBoxFilterSentReceived = (CheckBox) findViewById(R.id.checkBoxFilterSentReceived);
    	
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
        
        
        // update the filters
        toggleFilterDate();
        toggleFilterContacts();
        toggleFilterSentReceived();
     
    }
    
    private void toggleFilterSentReceived() {
    	if(checkBoxFilterSentReceived.isChecked())
    		layoutFilterSentReceived.setVisibility(View.VISIBLE);
        else
        	layoutFilterSentReceived.setVisibility(View.GONE);
	}

    private void toggleFilterContacts() {
    	if(checkBoxFilterContacts.isChecked())
    		layoutFilterContacts.setVisibility(View.VISIBLE);
        else
        	layoutFilterContacts.setVisibility(View.GONE);
	}

	private void toggleFilterDate()
    {
    	if(checkBoxFilterDate.isChecked())
        	layoutFilterDate.setVisibility(View.VISIBLE);
        else
        	layoutFilterDate.setVisibility(View.GONE);
    }

    
    
}