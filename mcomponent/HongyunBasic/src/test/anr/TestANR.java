package test.anr;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class TestANR extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button btn = new Button(this);
		btn.setText("fukkk");
		LayoutParams params = new LayoutParams(100, 100);
		setContentView(btn,params);
		long end = System.currentTimeMillis()+9000;
		while(System.currentTimeMillis()<end){
			Log.d("andymao", "in while loop");
//			try {
//				Thread.sleep(300);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		
//		try {
//			Thread.sleep(6000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		long end = System.currentTimeMillis()+9000;
		while(System.currentTimeMillis()<end){
			Log.d("andymao", "in while loop");
//			try {
//				Thread.sleep(300);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
}
