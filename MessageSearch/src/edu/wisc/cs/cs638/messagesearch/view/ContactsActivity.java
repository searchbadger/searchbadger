package edu.wisc.cs.cs638.messagesearch.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import edu.wisc.cs.cs638.messagesearch.R;

public class ContactsActivity extends ListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// TODO Remove the following
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();

		HashMap<String, String> dataMap;

		dataMap = new HashMap<String, String>();
		dataMap.put("Name", "John Doe");
		dataMap.put("Search", "CS638-1");
		dataList.add(dataMap);

		dataMap = new HashMap<String, String>();
		dataMap.put("Name", "Mr Incredibles");
		dataMap.put("Search", "Apples");
		dataList.add(dataMap);
		
		dataMap = new HashMap<String, String>();
		dataMap.put("Name", "Jane Doe");
		dataList.add(dataMap);

		SimpleAdapter adapter = new SimpleAdapter(this, dataList,
				R.layout.contacts_list_item, new String[] { "Name" },
						new int[] { R.id.contacts_text });

		setListAdapter(adapter);

	}

}
