package test.statelistdrawable;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.hongyunbasic.R;

/**
 * 在代码里面设置 drawablestates
 * http://stackoverflow.com/questions/5092649/android-how-to-update-the-selectorstatelistdrawable-programmatically
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

		states = new StateListDrawable();
		active = getResources().getDrawable(R.drawable.btn_product_list_active);
		inactive = getResources().getDrawable(R.drawable.btn_product_list);
		states.addState(new int[] { android.R.attr.state_pressed }, inactive);
		states.addState(new int[] { android.R.attr.state_checked }, inactive);
		states.addState(new int[] { android.R.attr.state_window_focused },
				active);

		btn.setBackgroundDrawable(states);
	}
}
