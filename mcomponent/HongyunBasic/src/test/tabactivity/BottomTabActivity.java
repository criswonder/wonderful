package test.tabactivity;

import android.app.TabActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.hongyunbasic.R;

public class BottomTabActivity extends TabActivity {
	protected int width;
	private boolean setWidth;
	private Atask task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_tab_activity);
		
		final TextView tv = (TextView) findViewById(R.id.tv);
		tv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				if(!setWidth) {
					setWidth= true;
					width = tv.getMeasuredWidth();
				}
			}
		});
		
		Button btn =(Button)findViewById(R.id.button1);
		Button btn2 =(Button)findViewById(R.id.button2);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				task = new Atask();
				task.flag = true;
				task.execute();
			}
		});
		btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				task.flag = false;
			}
		});
	}
	
	@Override
	protected void onPostResume() {
		super.onPostResume();
		final TextView tv = (TextView) findViewById(R.id.tv);
//		Handler h = new Handler();
//		h.postDelayed(new Runnable() {
//			int w = 0;
//
//			@Override
//			public void run() {
//				for (int i = 0; i < 10; i++) {
//					if (w > width)
//						w = 0;
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					tv.setWidth(w);
//					tv.invalidate();
//					w += 10;
//				}
//			}
//		}, 2000);
	}

	private void changeWidth(final TextView tv) {
		int w = 0;
		for (int i = 0; i < 10; i++) {
			if (w > width)
				w = 0;
			try {
				Thread.sleep(1200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tv.setWidth(w);
			tv.requestLayout();
			tv.invalidate();
			w += 10;
		}
	}
	
	class Atask extends AsyncTask<Void, Integer, Void>{

		public boolean flag;
		public StringBuffer sb;

		@Override
		protected Void doInBackground(Void... params) {
			sb = new StringBuffer();
			while(flag) {
				for (int i = 1; i < 10; i++) {
					publishProgress(i);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			sb.delete(0, sb.length());
			final TextView tv = (TextView) findViewById(R.id.tv);
			for(int i=0;i<values[0];i++) {
				sb.append(".");
			}
			tv.setText(sb);
			tv.invalidate();
		}
	} 
}
