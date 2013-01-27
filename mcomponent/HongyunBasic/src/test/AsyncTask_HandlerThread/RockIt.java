package test.AsyncTask_HandlerThread;

import com.example.hongyunbasic.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.text.NumberFormat;

/**
 * 从0开始数到某个数，并显示完成的百分比, 一个使用AsyncTask, 一个使用handler
 *
 * @author armstrong
 */
public class RockIt extends Activity {
	Button btn1, btn2;
	Context context;
	Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.btn_has_two);
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);

		context = this;
		btn1.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Test_AsyncTask ast = new Test_AsyncTask();
				ast.execute(0, 1000);
			}
		});
		btn2.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				handler = new MyHandler();
				Test_HandlerThread ht = new Test_HandlerThread("mm");
				ht.handleIt(0, 1000);
				Intent intent = new Intent("android.intent.action.BOOT_COMPLETED11");
				sendBroadcast(intent);
			}
		});

	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------
	// AsyncTask
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------
	public class Test_AsyncTask extends AsyncTask<Integer, Double, String> {

		@Override
		protected String doInBackground(Integer... params) {
			int start = params[0];
			int end = params[1];
			double per;
			while (start < end) {
				start++;
				per = start * 1.00 / end * 1.00;
				if (start % 20 == 0)
					publishProgress(per);
			}
			return "顺利完成！"; // 这里return的时候会调用下面的 onPostExecute 参数就是return的值
		}

		@Override
		protected void onProgressUpdate(Double... values) {
			super.onProgressUpdate(values);
			String str;
			NumberFormat nf = NumberFormat.getPercentInstance();
			nf.setMinimumFractionDigits(2);
			str = nf.format(values[0]);
			btn1.setText(str);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(context, result, Toast.LENGTH_LONG).show();
		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Handler & Thread
	// 要注意的是下面extends HandlerThread 或者Thread都没有区别
	// 搞不清楚HandlerTHread好在什么地方。
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------
	public class Test_HandlerThread extends HandlerThread {
		// public class Test_HandlerThread extends Thread {
		int start, end;

		public Test_HandlerThread(String name) {
			super(name);
		}

		public void handleIt(int start, int end) {
			this.start = start;
			this.end = end;

			start();
		}

		@Override
		public void run() {
			double per;
			Message msg;
			while (start < end) {
				start++;
				per = start * 1.00 / end * 1.00;
				if (start % 20 == 0) {
					String str;
					NumberFormat nf = NumberFormat.getPercentInstance();
					nf.setMinimumFractionDigits(2);
					str = nf.format(per);
					msg = new Message();
					Bundle data = new Bundle();
					data.putString("percentage", str);
					msg.setData(data);
					handler.sendMessage(msg);
					// btn2.setText(str);
				}
			}
			msg = new Message();
			msg.arg1 = 100;
			handler.sendMessage(msg);

		}
	}

	public class MyHandler extends Handler {
		public MyHandler() {

		}

		public MyHandler(Looper lp) {
			super(lp);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 100) {
				Toast.makeText(context, "順利完成了！", Toast.LENGTH_LONG).show();
			} else {
				Bundle data = msg.getData();
				String percentage = data.getString("percentage");
				btn2.setText(percentage);
			}
		}
	}
}
