package com.example.third;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements OnGestureListener, OnTouchListener {

	private ViewFlipper mFlipper;
	GestureDetector mGestureDetector;
	private int mCurrentLayoutState;
	private static final int FLING_MIN_DISTANCE = 100;
	private static final int FLING_MIN_VELOCITY = 200;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mFlipper = (ViewFlipper) findViewById(R.id.flipper);
		//注册一个用于手势识别的类
		mGestureDetector = new GestureDetector(this);
		//给mFlipper设置一个listener
		mFlipper.setOnTouchListener(this);
		mCurrentLayoutState = 0;
		//允许长按住ViewFlipper,这样才能识别拖动等手势
		mFlipper.setLongClickable(true);
	}

	/**
	 * 此方法在本例中未用到，可以指定跳转到某个页面
	 * @param switchTo
	 */
	public void switchLayoutStateTo(int switchTo) {
		while (mCurrentLayoutState != switchTo) {
			if (mCurrentLayoutState > switchTo) {
				mCurrentLayoutState--;
				mFlipper.setInAnimation(inFromLeftAnimation());
				mFlipper.setOutAnimation(outToRightAnimation());
				mFlipper.showPrevious();
			} else {
				mCurrentLayoutState++;
				mFlipper.setInAnimation(inFromRightAnimation());
				mFlipper.setOutAnimation(outToLeftAnimation());
				mFlipper.showNext();
			}

		}
		;
	}

	/**
	 * 定义从右侧进入的动画效果
	 * @return
	 */
	protected Animation inFromRightAnimation() {
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	/**
	 * 定义从左侧退出的动画效果
	 * @return
	 */
	protected Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	/**
	 * 定义从左侧进入的动画效果
	 * @return
	 */
	protected Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	/**
	 * 定义从右侧退出时的动画效果
	 * @return
	 */
	protected Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * 用户按下触摸屏、快速移动后松开即触发这个事件
	 * e1：第1个ACTION_DOWN MotionEvent
	 * e2：最后一个ACTION_MOVE MotionEvent
	 * velocityX：X轴上的移动速度，像素/秒
	 * velocityY：Y轴上的移动速度，像素/秒
	 * 触发条件 ：
	 * X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
	 */
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
	            && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
	        // 当像左侧滑动的时候
			//设置View进入屏幕时候使用的动画
			mFlipper.setInAnimation(inFromRightAnimation());
			//设置View退出屏幕时候使用的动画
			mFlipper.setOutAnimation(outToLeftAnimation());
			mFlipper.showNext();
	    } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
	            && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
	    	// 当像右侧滑动的时候
			mFlipper.setInAnimation(inFromLeftAnimation());
			mFlipper.setOutAnimation(outToRightAnimation());
			mFlipper.showPrevious();
	    }
		return false;
	}

	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean onTouch(View v, MotionEvent event) {
		// 一定要将触屏事件交给手势识别类去处理（自己处理会很麻烦的）
		return mGestureDetector.onTouchEvent(event);
	}
}
