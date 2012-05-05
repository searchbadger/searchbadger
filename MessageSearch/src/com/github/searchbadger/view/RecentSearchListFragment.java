package com.github.searchbadger.view;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.searchbadger.R;
import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.util.Search;
import com.github.searchbadger.util.SearchModel;

public class RecentSearchListFragment extends ListFragment {
	private List<Search> recentSearches;

    private SearchModel model = SearchBadgerApplication.getSearchModel();
    
	@Override
	public void onResume() {
		super.onResume();
		this.setListAdapter(null);
		
		
		if(!model.hasRecentSearchesBeenLoaded()) {
			
			// if this is the first time, run in separate thread and
			// show progress bar since the first get might be slow 
			final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", 
	                "Loading. Please wait...", true);
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
	
					recentSearches = null;
					List<Search> searches = model.getRecentSearches();
					if(searches != null) {
					
						// make a copy of the recent searches in case the list is changed 
						recentSearches = new ArrayList<Search>(searches.size());
					    for(Search item: searches) {
					    	recentSearches.add(new Search(item));
					    }
					}
			    
					getActivity().runOnUiThread(new Runnable() {
	
						public void run() {
							SearchArrayAdapter adapter = new SearchArrayAdapter(getActivity(), R.layout.recent_search_list_item, recentSearches);
							setListAdapter(adapter);
							if (recentSearches == null) {
								setEmptyText(getString(R.string.recent_error));
							} else {							
								if(recentSearches.size() == 0)
									setEmptyText(getString(R.string.no_recent_searches));
							}
	
							try {
								// hide the progress bar
								dialog.dismiss();
							}
							catch(Exception e) {}
						}
					});
					
				}
	
			});
			thread.start();

		}
		else {

			// not first time so just load 
			recentSearches = null;
			List<Search> searches = model.getRecentSearches();
			if(searches != null) {
			
				// make a copy of the recent searches in case the list is changed 
				recentSearches = new ArrayList<Search>(searches.size());
			    for(Search item: searches) {
			    	recentSearches.add(new Search(item));
			    }
			}

			SearchArrayAdapter adapter = new SearchArrayAdapter(getActivity(), R.layout.recent_search_list_item, recentSearches);
			setListAdapter(adapter);
			if (recentSearches == null) {
				setEmptyText(getString(R.string.recent_error));
			} else {							
				if(recentSearches.size() == 0)
					setEmptyText(getString(R.string.no_recent_searches));
			}

		}

	}

	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        // store the data into the application object
		Search s = (Search) v.getTag();
        SearchBadgerApplication.pushRecentSearch(s);
        
        // switch tab to the search tab
        if (this.getActivity().getParent() instanceof MainTabActivity) {
            MainTabActivity tabView = (MainTabActivity) this.getActivity().getParent();
            tabView.getTabHost().setCurrentTab(0);
        }
    }

	protected class SearchArrayAdapter extends ArrayAdapter<Search> {

		private List<Search> recentSearches;
		
		@Override
		public int getCount() {
			if (this.recentSearches == null) {
				return 0;
			}
			return super.getCount();
		}
		
		public SearchArrayAdapter(Context context, int textViewResourceId, List<Search> objects) {
			super(context, textViewResourceId, objects);
			recentSearches = objects;
		}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        		if (recentSearches == null) {
        			return null;
        		}
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.recent_search_list_item, null);
                }
                Search s = recentSearches.get(position);
                if (s != null) {
                	// save the contact into the tag
        			v.setTag(s);
                	
                	// add the message
                	TextView message = (TextView) v.findViewById(R.id.recent_search_text);
                	if(message != null){
                		if(s.getText().length() == 0)
                			message.setText(Html.fromHtml("<i>blank</i>"));
                		else
                			message.setText(s.getText());
                	}
                	
                	// add the date
                	TextView date = (TextView) v.findViewById(R.id.recent_search_date);
                	if(date != null)
                		date.setText("");
                	
                }
                return v;
        }
		
	}
}
