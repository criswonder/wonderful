package test.animation;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.hongyunbasic.R;

public class TestTranslateAnimation extends Activity {

	private TextView tv1;

	private Animation ani;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_translate_animation);

		tv1 = (TextView) findViewById(R.id.textView1);

		/**
		 * test_translate_animation 中textview1加上container和不加container效果是不一样的
		 */
		ani = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.shake);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				tv1.startAnimation(ani);
			}
		}, 2000);
	}
}
