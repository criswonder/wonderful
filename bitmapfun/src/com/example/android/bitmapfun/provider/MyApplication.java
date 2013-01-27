package com.example.android.bitmapfun.provider;

import android.app.Application;

import com.example.android.bitmapfun.util.ImageCache.RetainFragment;

public class MyApplication  extends Application{
	public static RetainFragment findFragmentByTag(String tag){
		return null;
	}
	public static MyApplication getContext(){
		return application;
	}
	private static MyApplication application;
	
	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
	}

}
