package com.example.hongyunbasic;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xixi.android.MucangConfig;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		MucangConfig.setApplication(getApplication());

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_SAMPLE_CODE + "1");

		List<Map> myData = new ArrayList<Map>();
		PackageManager pm = getPackageManager();
		List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);
		for (ResolveInfo info : list) {
			addItem(myData,
					info.activityInfo.name,
					activityIntent(
							info.activityInfo.applicationInfo.packageName,
							info.activityInfo.name));
		}
		ListView listv = (ListView) findViewById(R.id.list);
		SimpleAdapter adpter = new SimpleAdapter(this, (List) myData,
				android.R.layout.simple_list_item_1, new String[] { "title" },
				new int[] { android.R.id.text1 });

		listv.setAdapter(adpter);
		listv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map map = (Map) parent.getItemAtPosition(position);

				Intent intent = (Intent) map.get("intent");
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * 构造list里面的item点击时，发动的intent
	 * 
	 * @param pkg
	 * @param componentName
	 * @return
	 */
	protected Intent activityIntent(String pkg, String componentName) {
		Intent result = new Intent();
		result.setClassName(pkg, componentName);
		return result;
	}

	protected void addItem(List<Map> data, String name, Intent intent) {
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put("title", name);
		temp.put("intent", intent);
		data.add(temp);
	}

}
