package test.animation;

import com.example.hongyunbasic.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;

public class TestAnimation extends Activity implements OnClickListener {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_animation);
		Button btn = (Button) findViewById(R.id.Button);
		btn.setOnClickListener(this);
	}

	public void onClick(View v) {
		Animation anim = null;
		anim = new RotateAnimation(0.0f, +360.0f);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.setDuration(3000);
		findViewById(R.id.TextView01).startAnimation(anim);
	}
}