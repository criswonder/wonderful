package com.example.hongyunbasic;

import android.app.Activity;
import android.os.Bundle;

public class TestReflectionImage extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ReflectionImage fi = new ReflectionImage(this);
		fi.setImageResource(R.drawable.e);
		setContentView(fi);
	}
}
