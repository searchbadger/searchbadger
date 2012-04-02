package com.github.searchbadger.view;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.util.SearchModel;

public class SearchResultListFragment extends ListFragment {

	private boolean mDualPane;
    private int mCurCheckPosition = 0;
    private SearchModel model = SearchBadgerApplication.getSearchModel();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		List<Map<String,String>> results = model.getSearchResultsMap();
		if(results == null) return;

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), results,
				R.layout.search_result_list_item, new String[] { "Message",
						"Date" }, new int[] { R.id.search_result_text,
						R.id.search_result_date });

		setListAdapter(adapter);

		/*Cursor resultCursor = model.getResultCursor();
		CursorAdapter resultsAdapter = new SimpleCursorAdapter(getActivity(), 
				R.layout.search_result_list_item,
				resultCursor,new String[] { "Message", "Date" }, 
				new int[] { R.id.search_result_text, R.id.search_result_date });
		setListAdapter(resultsAdapter);*/
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
            intent.putExtra("index", index);
            startActivity(intent);
        }
    }

}
