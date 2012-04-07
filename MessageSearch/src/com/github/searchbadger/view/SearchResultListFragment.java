package com.github.searchbadger.view;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.util.Message;
import com.github.searchbadger.util.SearchModel;

public class SearchResultListFragment extends ListFragment {

	private boolean mDualPane;
    private int mCurCheckPosition = 0;
    private SearchModel model = SearchBadgerApplication.getSearchModel();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private List<Message> results;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		results = model.getSearchResults();
		if(results == null) return;
		MessageArrayAdapter adapter = new MessageArrayAdapter(getActivity(), R.layout.search_result_list_item, results);
		setListAdapter(adapter);

	}
	

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    private void showDetails(int index) {
    	

		// Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View threadFrame = getActivity().findViewById(R.id.thread_view);
        mDualPane = threadFrame != null && threadFrame.getVisibility() == View.VISIBLE;

        mCurCheckPosition = index;

        if (mDualPane) {

            
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            ThreadListFragment details = (ThreadListFragment)
                    getFragmentManager().findFragmentById(R.id.thread_view);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.
                details = ThreadListFragment.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.thread_view, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), ThreadActivity.class);
            intent.putExtra("message", results.get(index));
            startActivity(intent);
        }
    }


	protected class MessageArrayAdapter extends ArrayAdapter<Message> {

		private List<Message> messages;
		
		public MessageArrayAdapter(Context context, int textViewResourceId, List<Message> objects) {
			super(context, textViewResourceId, objects);
			messages = objects;
		}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.search_result_list_item, null);
                }
                Message m = messages.get(position);
                if (m != null) {
                	// save the contact into the tag
        			v.setTag(m);
                	
                	// add the message
                	TextView message = (TextView) v.findViewById(R.id.search_result_text);
                	if(message != null)
                		message.setText(m.getAuthor() + ": " + m.getText());
                	
                	// add the date
                	TextView date = (TextView) v.findViewById(R.id.search_result_date);
                	if(date != null)
                		date.setText(dateFormat.format(m.getDate()));
                	
                	// set the icon
                	ImageView icon = (ImageView) v.findViewById(R.id.image_search_result);
                	if(icon != null)
                	{
                		switch(m.getSource()) {
                		case SMS:
                    		icon.setImageResource(R.drawable.sms_selected);
                			break;
                		case FACEBOOK:
                    		icon.setImageResource(R.drawable.facebook_selected);
                			break;
                		}
                	}

        			// set the click listener for the star button
        			CheckBox starButton = (CheckBox) v.findViewById(R.id.search_result_checkbox);
        			starButton.setOnClickListener(new View.OnClickListener() {
        				public void onClick(View v) {
        					OnStarSelector(v);
        				}
        			});
        			// check the star if the message is starred
        			if(model.containsStarredMessage(m))
        				starButton.setChecked(true);
        			else
        				starButton.setChecked(false);
                }
                return v;
        }
		
	}
	
	protected void OnStarSelector(View v){

		// get the message object
		if (!(v.getParent() instanceof View))
			return;
		View parentView = (View) v.getParent();
		if (!(parentView.getTag() instanceof Message))
			return;
		Message message = (Message) parentView.getTag();

		// add/remove contact
		if (!(v instanceof CheckBox))
			return;
		CheckBox starButton = (CheckBox) v;
		if (starButton.isChecked())
			model.addStarredMessage(message);
		else
			model.removeStarredMessage(message);

	}
	
}
