package edu.wisc.cs.cs638.messagesearch.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import edu.wisc.cs.cs638.messagesearch.R;

public class ThreadActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// TODO Remove the following
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();

		HashMap<String, String> dataMap;

		dataMap = new HashMap<String, String>();
		dataMap.put("Message", "John Does: Hi");
		dataList.add(dataMap);

		dataMap = new HashMap<String, String>();
		dataMap.put("Message", "Jane Does: Hello");
		dataList.add(dataMap);

		dataMap = new HashMap<String, String>();
		dataMap.put("Message", "John Does: Bye");
		dataList.add(dataMap);

		dataMap = new HashMap<String, String>();
		dataMap.put("Message", "Jane Does: See ya");
		dataList.add(dataMap);

		SimpleAdapter adapter = new SimpleAdapter(this, dataList,
				R.layout.thread_list_item, new String[] { "Message" },
						new int[] { R.id.thread_text });

		setListAdapter(adapter);

	}

}
