package test.animation;

import com.example.hongyunbasic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class TestAnimation1 extends Activity implements OnClickListener {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_animation);
		Button btn = (Button) findViewById(R.id.Button);
		btn.setOnClickListener(this);
	}

	public void onClick(View v) {
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.myanim);
		findViewById(R.id.TextView01).startAnimation(anim);
	}
}