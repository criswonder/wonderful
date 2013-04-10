package test.statelistdrawable;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;

import com.example.hongyunbasic.R;

/**
 * 在代码里面设置 drawablestates
 * http://stackoverflow.com/questions/5092649/android-how-
 * to-update-the-selectorstatelistdrawable-programmatically
 * 
 * @author Andy Mao
 * 
 */
public class TestStateListDrawable extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_statelist_drawable);
		Button btn = (Button) findViewById(R.id.btn);
		RadioButton rb = (RadioButton) findViewById(R.id.rb);

		StateListDrawable states = new StateListDrawable();
		Drawable active = getResources().getDrawable(
				R.drawable.btn_product_list_active);
		Drawable inactive = getResources().getDrawable(
				R.drawable.btn_product_list);
		states.addState(new int[] { android.R.attr.state_pressed }, inactive);
		states.addState(new int[] { android.R.attr.state_checked }, inactive);
		states.addState(new int[] { android.R.attr.state_window_focused },
				active);

		rb.setBackgroundDrawable(states);

		// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		// ::::::::::::::::::::::::::::图片本来是有大小的，希望通过设置bg, 然后相应的view的大小会对image
		// wrap_content::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//		states = new StateListDrawable();
//		active = getResources().getDrawable(R.drawable.btn_product_list_active);
//		active.setBounds(0, 0, 100, 100);// not work
//		inactive.setBounds(0, 0, 100, 100);// not work
//		inactive = getResources().getDrawable(R.drawable.btn_product_list);
//		states.addState(new int[] { android.R.attr.state_pressed }, inactive);
//		states.addState(new int[] { android.R.attr.state_checked }, inactive);
//		states.addState(new int[] { android.R.attr.state_window_focused },
//				active);
//		states.setBounds(0, 0, 200, 200); // not work
//
//		btn.setBackgroundDrawable(states); // not work
//		btn.getBackground().setBounds(0, 0, 88, 88);// not work
//		btn.invalidate(); // not work
//		btn.layout(0, 0, 100, 100);// not work
//		btn.requestLayout();// not work

		// 想法：通过设置image的src看可以不 结果：还是不行
		ImageView image = (ImageView) findViewById(R.id.image);
		states = new StateListDrawable();
		active = getResources().getDrawable(R.drawable.btn_product_list_active);
		inactive = getResources().getDrawable(R.drawable.btn_product_list);
		states.addState(new int[] { android.R.attr.state_pressed }, inactive);
		states.addState(new int[] { android.R.attr.state_checked }, inactive);
		states.addState(new int[] { android.R.attr.state_window_focused },
				active);
		image.setImageDrawable(states);
		
		states = new StateListDrawable();
		active = getResources().getDrawable(R.drawable.btn_product_list_active);
		inactive = getResources().getDrawable(R.drawable.btn_product_list);
		states.addState(new int[] { android.R.attr.state_pressed }, inactive);
		states.addState(new int[] { android.R.attr.state_checked }, inactive);
		states.addState(new int[] { android.R.attr.state_window_focused },
				active);
		states.setBounds(0, 0, 10, 10); // not work

		btn.setBackgroundDrawable(states); // not work
		int dpi = getResources().getDisplayMetrics().densityDpi;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(active.getIntrinsicWidth()*dpi/160
				,active.getIntrinsicHeight()*dpi/160);
		btn.setLayoutParams(params);
		btn.invalidate(); // not work
		btn.requestLayout();// not work
		
	}
}
