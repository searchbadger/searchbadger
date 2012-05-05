package com.github.searchbadger.view;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

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

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.core.SearchBadgerController;
import com.github.searchbadger.util.Message;
import com.github.searchbadger.util.SearchModel;
import com.github.searchbadger.util.TextViewUtil;

public class SearchResultListFragment extends ListFragment {

	private boolean mDualPane;
    private SearchModel model = SearchBadgerApplication.getSearchModel();
    private SearchBadgerController controller = SearchBadgerController.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
    private List<Message> results;
    private Pattern pattern;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		pattern = Pattern.compile(model.getCurrentSearch().getJavaRegexText(), Pattern.CASE_INSENSITIVE);
		results = model.getSearchResults();
		MessageArrayAdapter adapter = new MessageArrayAdapter(getActivity(), R.layout.search_result_list_item, results);
		setListAdapter(adapter);
	}
	 
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View retVal = super.onCreateView(inflater, container, savedInstanceState);
    	
    	return retVal;
		
	}
    


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		if (model.getSearchResults() == null) {
			setEmptyText(getString(R.string.search_error));
		} else {
			setEmptyText(getString(R.string.no_results));
		}
	}


	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		Message message = (Message) v.getTag();
        showDetails(position, message);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    private void showDetails(int index, Message message) {
    	

		// Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View threadFrame = getActivity().findViewById(R.id.thread_view);
        mDualPane = threadFrame != null && threadFrame.getVisibility() == View.VISIBLE;

        if (mDualPane) {

            
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            ThreadListFragment details = (ThreadListFragment)
                    getFragmentManager().findFragmentById(R.id.thread_view);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.
                details = ThreadListFragment.newInstance(index, message);

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
            intent.putExtra("index", index);
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
        		if (messages == null) {
        			return null;
        		}
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
                	TextView message = (TextView) v.findViewById(R.id.messages_text);
                	if(message != null)
                		message.setText(TextViewUtil.formatMessageSearch(m.getAuthor(), m.getText(), pattern));
                	
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
                		case TWITTER:
                    		icon.setImageResource(R.drawable.twitter_selected);
                			break;
                		}
                	}

        			// set the click listener for the star button
        			CheckBox starButton = (CheckBox) v.findViewById(R.id.starred_checkbox);
        			starButton.setOnClickListener(controller.new StarredMessageListener());
        			// check the star if the message is starred
        			if(model.containsStarredMessage(m))
        				starButton.setChecked(true);
        			else
        				starButton.setChecked(false);
                }
                return v;
        }

		@Override
		public int getCount() {
			if (this.messages == null) {
				return 0;
			}
			return super.getCount();
		}
	}
	
	
}