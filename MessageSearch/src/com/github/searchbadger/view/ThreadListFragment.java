package com.github.searchbadger.view;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.util.Message;
import com.github.searchbadger.util.SearchModel;
import com.github.searchbadger.util.TextViewUtil;

public class ThreadListFragment extends ListFragment {

	private SearchModel model = SearchBadgerApplication.getSearchModel();
	private List<Message> thread_msg;
	private int thread_index = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
	
	 /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static ThreadListFragment newInstance(int index, Message message) {
    	ThreadListFragment f = new ThreadListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putParcelable("message", message);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(null);
		
		
		Thread thread = new Thread(new Runnable() {
			public void run() {

					
					if(getArguments() == null) return;
					Message m = getArguments().getParcelable("message");
					if(m == null) return;
					thread_msg = model.getThread(m);

					try {
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								if(thread_msg == null) {
									setEmptyText(getString(R.string.thread_error));
									return;
								}
								thread_index = model.getLastThreadIndex();
								try {
									MessageArrayAdapter adapter = new MessageArrayAdapter(getActivity(), R.layout.search_result_list_item, thread_msg);
									setListAdapter(adapter);
									setEmptyText(getString(R.string.thread_error));

									Display display = getActivity().getWindowManager().getDefaultDisplay();
									int height = display.getHeight();
									
									// make the selected message visible
									ListView listView = getListView();
									listView.setSelectionFromTop(thread_index, height / 2 - 60);
								} catch(Exception e) {
								}
								
								
							}
						});
					} catch(Exception e) {
					}

				
			}

		});
		thread.start();
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
                	if(message != null) {
                		if(position == thread_index)
                			message.setText(TextViewUtil.formatMessageSelected(m.getAuthor(), m.getText()));                		
                		else
                			message.setText(TextViewUtil.formatMessage(m.getAuthor(), m.getText()));
                	}

                	// add the message date
                	TextView date = (TextView) v.findViewById(R.id.thread_date);
                	if(date != null) {
                		date.setText(dateFormat.format(m.getDate()));
                	}
                	
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
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		setEmptyText(getString(R.string.thread_error));
	}

	
}
