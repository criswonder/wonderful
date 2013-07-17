package test.looperhandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hongyunbasic.R;

/**
 * 使用新线程的Handler sendmessage，然后再handlemessage的时候更新ui，不会出错的原因是这个新的线程使用了MainHandler
 * @author wonder
 *
 */
public class CopyOfHandlerTestActivity extends Activity implements
		Button.OnClickListener {

	public TextView tv;

	private myThread myT;

	Button bt1, bt2;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.handler_test);

		bt1 = (Button) findViewById(R.id.a);

		bt2 = (Button) findViewById(R.id.b);

		tv = (TextView) findViewById(R.id.tv);

		bt1.setId(1);// 为两个button设置ID，此ID用于后面判断是哪个button被按下

		bt2.setId(2);

		bt1.setOnClickListener(this);// 增加监听器

		bt2.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		// TODO Auto-generated method stub

		switch (v.getId()) {// 按键事件响应，如果是第一个按键将启动一个新线程

		case 1:

			myT = new myThread();

			myT.start();

			break;

		case 2:

			finish();

			break;

		default:

			break;

		}

	}

	class myThread extends Thread

	{

		private EHandler mHandler;

		public void run()

		{

			Looper myLooper, mainLooper;
//			Looper.prepare();
			myLooper = Looper.myLooper();// 得到当前线程的Looper

			mainLooper = Looper.getMainLooper();// 得到UI线程的Looper

			String obj;

			if (myLooper == null)// 判断当前线程是否有消息循环Looper

			{

				mHandler = new EHandler(mainLooper);

				obj = "current thread has no looper!";// 当前Looper为空，EHandler用mainLooper对象构造

			}

			else

			{

				mHandler = new EHandler(myLooper);// 当前Looper不为空，EHandler用当前线程的Looper对象构造

				obj = "This is from current thread.";

			}

			mHandler.removeMessages(0);// 清空消息队列里的内容

			Message m = mHandler.obtainMessage(1, 1, 1, obj);

			mHandler.sendMessage(m);// 发送消息
//			Looper.loop();

		}

	}

	class EHandler extends Handler

	{

		public EHandler(Looper looper)

		{

			super(looper);

		}

		@Override
		public void handleMessage(Message msg) // 消息处理函数

		{

			tv.setText((String) msg.obj);// 设置TextView内容

		}

	}

}
