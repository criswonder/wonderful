package com.coverflow;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class HelloAndroid extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		CoverFlow cf = new CoverFlow(this);
		// cf.setBackgroundResource(R.drawable.shape);
		cf.setBackgroundColor(Color.BLACK);
		cf.setAdapter(new ImageAdapter(this));
		ImageAdapter imageAdapter = new ImageAdapter(this);
		cf.setAdapter(imageAdapter);
		// cf.setAlphaMode(false);
		// cf.setCircleMode(false);
		cf.setSelection(2, true);
		cf.setAnimationDuration(1000);
		setContentView(cf);
	}

}