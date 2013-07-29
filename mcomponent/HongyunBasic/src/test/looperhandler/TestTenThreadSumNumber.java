package test.looperhandler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class TestTenThreadSumNumber {

	private static final boolean needSlowDown = false;
	
	private Handler mainThreadHandler;
	/**
	 * @param args
	 */
	public static void fuck() {
		TestTenThreadSumNumber obj =  new TestTenThreadSumNumber();
		MainThread mt = obj.new MainThread();
		mt.start();

	}
	public class MainThread extends Thread{
		
		int sum;
		
		public MainThread(){
			super();
		}
		@Override
		public void run() {
			super.run();
			Looper.prepare();
			mainThreadHandler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if(msg.arg1>0){
						Log.d("mainthread arg=", msg.arg1+"");
						sum+=msg.arg1;
						Log.d("mainthread sum=", sum+"");
					}
				}
			};
			WorkerThread th1 = new WorkerThread(1, 1000);
			th1.start();
			WorkerThread th2 = new WorkerThread(1001, 3000);
			th2.start();
			WorkerThread th3 = new WorkerThread(3001, 4000);
			th3.start();
			Looper.loop();
		}
	}
	
	
	public class WorkerThread extends Thread{
		int mStartNum;
		int endNum;
		public WorkerThread(int start,int end){
			this.mStartNum = start;
			this.endNum = end;
		}
		@Override
		public void run() {
			super.run();
			if(endNum<mStartNum){
				return;
			}
			int sum= 0;
			while(mStartNum<endNum){
				sum+=mStartNum;
				if(needSlowDown){
					Log.d("worker thread ","name="+Thread.currentThread().getName()+" num="+mStartNum+"");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
				}
				mStartNum++;
			}
			Message msg = new Message();
			msg.arg1 = sum;
			mainThreadHandler.sendMessage(msg);
			Log.d("worker thread ","name="+Thread.currentThread().getName()+"finish sum="+sum+"");
		}
	}
	
}
