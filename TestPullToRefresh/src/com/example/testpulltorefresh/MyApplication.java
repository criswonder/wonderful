package com.example.testpulltorefresh;

import com.xixi.android.XiXiApp;
import com.xixi.cache.ImageCache.RetainFragment;

public class MyApplication  extends XiXiApp{
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
