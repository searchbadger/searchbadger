package com.github.searchbadger.view;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.SimpleAdapter;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.util.SearchModel;

public class ThreadListFragment extends ListFragment {

	private SearchModel model = SearchBadgerApplication.getSearchModel();
	 /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static ThreadListFragment newInstance(int index) {
    	ThreadListFragment f = new ThreadListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
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
		List<Map<String,String>> thread= model.getThread(getArguments().getInt("index", 0));
		if(thread == null) return;
		
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), thread,
				R.layout.thread_list_item, new String[] { "Message", "Date", "From" },
						new int[] { R.id.thread_text });

		setListAdapter(adapter);
	}

}
