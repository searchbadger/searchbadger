package com.github.searchbadger.view;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

public class RecentSearchActivity extends ListActivity {

    private SearchModel model = SearchBadgerApplication.getSearchModel();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

	}
	
    @Override
	public void onResume() {
		super.onResume();	
		List<Search> recentSearches = model.getRecentSearches();
		if(recentSearches == null) return;
		SearchArrayAdapter adapter = new SearchArrayAdapter(this, R.layout.recent_search_list_item, recentSearches);
		setListAdapter(adapter);

	}

	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {

		Search s = (Search) v.getTag();
        Log.d("SearchBadger", s.getText());
    }

	protected class SearchArrayAdapter extends ArrayAdapter<Search> {

		private List<Search> recentSearches;
		
		public SearchArrayAdapter(Context context, int textViewResourceId, List<Search> objects) {
			super(context, textViewResourceId, objects);
			recentSearches = objects;
		}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.recent_search_list_item, null);
                }
                Search s = recentSearches.get(position);
                if (s != null) {
                	// save the contact into the tag
        			v.setTag(s);
                	
                	// add the message
                	TextView message = (TextView) v.findViewById(R.id.recent_search_text);
                	if(message != null)
                		message.setText(s.getText());
                	
                	// add the date
                	TextView date = (TextView) v.findViewById(R.id.recent_search_date);
                	if(date != null)
                		date.setText("");
                	
                }
                return v;
        }
		
	}
}
