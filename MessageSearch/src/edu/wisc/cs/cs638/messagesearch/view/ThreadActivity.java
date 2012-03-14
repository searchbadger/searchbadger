package edu.wisc.cs.cs638.messagesearch.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ThreadActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            ThreadListFragment details = new ThreadListFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }
	
	/*
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
	*/

}
