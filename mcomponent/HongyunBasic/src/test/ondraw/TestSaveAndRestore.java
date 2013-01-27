package test.ondraw;

import com.example.hongyunbasic.R;
import com.example.hongyunbasic.R.layout;
import com.example.hongyunbasic.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class TestSaveAndRestore extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(new CustomeView(this));
		setContentView(R.layout.activity_test_save_and_restore);
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.main_layout);
		 CustomeView cust = new CustomeView(this);
		 cust.setBackgroundColor(Color.parseColor("#ffffffff"));
		 LayoutParams params = new LayoutParams(300, 300);
		 ll.addView(cust, params);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_test_save_and_restore, menu);
		return true;
	}
	
}
