package test.ptr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hongyunbasic.R;
import com.xixi.component.MyListView;
import com.xixi.component.MyListView.OnRefreshListener;

public class TestPullToRefresh extends Activity implements OnRefreshListener {
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	Random random = new Random();
	private Map<String, Object> map;
	MyListView mListView;
	MyAdapter adapter ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_ptr);
		int a = 1;
		for (int i = 0; i < 20; i++) {
			a++;
			map = new HashMap<String, Object>();
			map.put("img", R.drawable.ic_launcher);
			map.put("title", "G" + a);
			map.put("info", "google" + a);
			mData.add(map);
		}
		 mListView = (MyListView) findViewById(R.id.lst);
		mListView.setonRefreshListener(this);
		 adapter = new MyAdapter(getApplicationContext());
		mListView.setAdapter(adapter);
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return mData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = new TextView(getApplicationContext());
			tv.setText(random.nextInt() + "");
			return tv;
		}

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				adapter.notifyDataSetChanged();
				mListView.onRefreshComplete();
			}

		}.execute();
	}

	private OnRefreshListener refreshListener = new OnRefreshListener() {

		public void onRefresh() {

		}
	};

}
