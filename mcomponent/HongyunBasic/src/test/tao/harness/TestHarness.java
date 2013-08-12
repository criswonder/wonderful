package test.tao.harness;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.hongyunbasic.R;

public class TestHarness extends Activity {
	ListView listView;
	private View header;
	private TextView floatingHead;
	private Rect mVisibleRect = new Rect();
	private int mFloatingCategoryHeight;
	protected String TAG="TestHarness";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		testTaoSearchListActivityFloatingTopbar();
	}

	private void testTaoSearchListActivityFloatingTopbar() {
		setContentView(R.layout.activity_testharness_listview);
		listView = (ListView) findViewById(R.id.listView1);
		floatingHead = (TextView) findViewById(R.id.tv_head);
		header = View.inflate(getApplicationContext(),
				R.layout.layout_listview_header, null);
		mFloatingCategoryHeight = floatingHead.getMeasuredHeight();
		listView.addHeaderView(header);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (floatingHead != null) {
					header.getLocalVisibleRect(mVisibleRect);
					ViewParent parent = header.getParent();
					String log1 = String.format("mVisibleRect.bottom=%s,mVisibleRect.top=%d," +
							"mFloatingCategoryHeight=%d", 
							mVisibleRect.bottom,mVisibleRect.top,mFloatingCategoryHeight);
					Log.d(TAG, log1);
					
					if (parent == null
							|| (mVisibleRect.bottom - mVisibleRect.top <= mFloatingCategoryHeight)
							|| header.getPaddingTop() < 0) {
						floatingHead.setVisibility(View.VISIBLE);
					} else {
						floatingHead.setVisibility(View.GONE);
					}

				}

			}
		});

		Cursor c = getContentResolver().query(People.CONTENT_URI, null, null,
				null, null);
		startManagingCursor(c);

		@SuppressWarnings("deprecation")
		ListAdapter adapter = new SimpleCursorAdapter(this,
		// Use a template that displays a text view
				android.R.layout.simple_list_item_1,
				// Give the cursor to the list adatper
				c,
				// Map the NAME column in the people database to...
				new String[] { People.NAME },
				// The "text1" view defined in the XML template
				new int[] { android.R.id.text1 });
		listView.setAdapter(adapter);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && floatingHead!=null){
			mFloatingCategoryHeight = floatingHead.getMeasuredHeight();
		}
	}

}
