package edu.wisc.cs.cs638.messagesearch.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import edu.wisc.cs.cs638.messagesearch.R;

public class SearchResultActivity extends ListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// TODO Remove the following
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();

		HashMap<String, String> dataMap;

		dataMap = new HashMap<String, String>();
		dataMap.put("Date", "12/25/2009\n11:00 AM");
		dataMap.put("Message", "This is a test message");
		dataList.add(dataMap);

		dataMap = new HashMap<String, String>();
		dataMap.put("Date", "1/1/2010\n11:00 AM");
		dataMap.put("Message", "This is yet another test message");
		dataList.add(dataMap);
		
		dataMap = new HashMap<String, String>();
		dataMap.put("Date", "1/1/2010\n11:00 AM");
		dataMap.put("Message", "This is a very very very very very very very very very very very very very long message");
		dataList.add(dataMap);
		

		SimpleAdapter adapter = new SimpleAdapter(this, dataList,
				R.layout.search_result_list_item, new String[] { "Message",
						"Date" }, new int[] { R.id.search_result_text,
						R.id.search_result_date });

		setListAdapter(adapter);

	}

}
