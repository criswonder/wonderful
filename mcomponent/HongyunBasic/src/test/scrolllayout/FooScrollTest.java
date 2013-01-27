package test.scrolllayout;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.widget.LinearLayout;

public class FooScrollTest extends Activity {
 @Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	LinearLayout line = new LinearLayout(this);
	LayoutParams params = new LayoutParams();
	params.height = LayoutParams.FILL_PARENT;
	params.width = LayoutParams.FILL_PARENT;
	line.setLayoutParams(params);
	
	FooScroll scrollView = new FooScroll(this, null);
	scrollView.setLayoutParams(params);
	MyImageAdapter adapter = new MyImageAdapter(this);
	scrollView.setAdapter(adapter);
	
	
	line.addView(scrollView,params);
	
	setContentView(line);
 }
}
