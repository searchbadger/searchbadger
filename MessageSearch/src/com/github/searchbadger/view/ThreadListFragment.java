package com.github.searchbadger.view;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.util.Message;
import com.github.searchbadger.util.SearchModel;

public class ThreadListFragment extends ListFragment {

	private SearchModel model = SearchBadgerApplication.getSearchModel();
	 /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static ThreadListFragment newInstance(Message message) {
    	ThreadListFragment f = new ThreadListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putParcelable("message", message);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(getArguments() == null) return;
		Message m = getArguments().getParcelable("message");
		if(m == null) return;
		List<Message> thread= model.getThread(m);
		if(thread == null) return;
		MessageArrayAdapter adapter = new MessageArrayAdapter(getActivity(), R.layout.search_result_list_item, thread);
		setListAdapter(adapter);
	}


	@Override
	public void onResume() {
		super.onResume();
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
                    v = vi.inflate(R.layout.thread_list_item, null);
                }
                Message m = messages.get(position);
                if (m != null) {
                	// save the contact into the tag
        			v.setTag(m);
                	
                	// add the message
                	TextView message = (TextView) v.findViewById(R.id.thread_text);
                	if(message != null)
                		message.setText(m.getAuthor() + ": " + m.getText());
                	
                	
                }
                return v;
        }
		
	}
	
}
