package com.hongyun.component;

import test.animation.LayoutMetaData;
import test.animation.Rotate3dAnimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class CoverFlow extends FrameLayout {
	public CoverFlow(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CoverFlow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// // 自定义属性
		// TypedArray a = context.obtainStyledAttributes(attrs,
		// R.styleable.com_myapps_widget_Pager);
		// pageWidthSpec = a.getDimensionPixelSize(
		// R.styleable.com_myapps_widget_Pager_pageWidth, SPEC_UNDEFINED);
		// a.recycle();
		//
		init();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------------->
	// <--------------------------------------------------------------------------------------------------------------------------------------------------------------
	private View mContainer;
	private static final boolean hasOrder = true;
	private static int fileIndex = 1;
	/** 用來存放中間layout的id, 初始化的時候不會用到。 **/
	private static int mMiddleId = -1;
	private static int startIndex = 0;
	private static int max_distance = 200;
	private static int mutex_distance = 15;
	private View mContainer1;
	private View mContainer2;
	private Context context;
	List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	/** 包装要滑动的layout的container **/
	FrameLayout fl = null;
	private int fl_len;
	/** 存放左边layout **/
	private Stack<Integer> mlstack;
	/** 存放右边layout **/
	private Stack<Integer> mrstack;
	/** 存放各个layout移动的信息，位置信息等 **/
	private Map<Integer, Map> mMap;

	// --------------------------------------------------------------------------------------------------------------------------------------------------------------->
	// <--------------------------------------------------------------------------------------------------------------------------------------------------------------
	private OnClickListener mRightClickListener;
	private OnClickListener mLeftClickListener;
	private static final int INVALID_SCREEN = -1;
	public static final int SPEC_UNDEFINED = -1;

	private static final int SNAP_VELOCITY = 1000;

	private int pageWidth; // 代码设置的pageWidth 注：在onMeasure方法中被设置。
	private int pageWidthSpec; // xml设置的pageWidth

	private boolean mFirstLayout = true;

	private int mCurrentPage;
	private int mNextPage = INVALID_SCREEN;

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	/**
	 * slop溢出，使溅出；这里是用来判断一个滑动在X轴或者Y轴的距离是否达到足够判断出方向的距离。
	 */
	private int mTouchSlop;
	private int mMaximumVelocity;

	private float mLastMotionX;
	/** 初始按下时，x的位置 **/
	private float mInitialMotionX;
	private float mLastMotionY;

	private final static int TOUCH_STATE_REST = 0; // scroll 未完成
	private final static int TOUCH_STATE_SCROLLING = 1; // scroll 滑动中
	private int mTouchState = TOUCH_STATE_REST; // scroll 起始默认状态

	private boolean mAllowLongPress;

	private Set<OnScrollListener> mListeners = new HashSet<OnScrollListener>(); // 监听事件接口
	private String TAG = "mao"; // adb logcat Pager:D
	// private String TAG = this.getClass().getSimpleName(); // adb logcat
	// Pager:D
	// *:S
	// 多个filter用空格隔开，最后一个表示所有的silent

	private void init() {
		mScroller = new Scroller(getContext());
		mCurrentPage = 0;

		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		// Distance a touch can
		// wander before we
		// think the user is
		// scrolling in
		// pixels
		// mTouchSlop = 200; //设置这个后滑动屏幕的时候，距离必须要大于这个值，才会scroll。
		Log.d(TAG, "mTouchSlop:" + mTouchSlop);
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity(); // fling最大速度
		Log.d(TAG, "mMaximumVelocity:" + mMaximumVelocity);

		if (mlstack == null)
			mlstack = new Stack<Integer>();
		if (mrstack == null)
			mrstack = new Stack<Integer>();
		if (mMap == null)
			mMap = new HashMap<Integer, Map>();
		mMiddleId = -1;
		startIndex = 0;

		fl = this;
		fl_len = fl.getChildCount();
		setLayoutVisibility();
	}

	private void setLayoutVisibility() {
		fl_len = fl.getChildCount();
		if (startIndex > fl_len - 1) {
			startIndex++;
		} else {
			fl.getChildAt(startIndex).setVisibility(View.VISIBLE);
			startIndex++;
		}
	}

	public void setCurrentPage(int currentPage) {
		mCurrentPage = Math.max(0, Math.min(currentPage, getChildCount())); // 非常好
		scrollTo(getScrollXForPage(mCurrentPage), 0);
		invalidate(); // 重绘View
	}

	int getCurrentPage() {
		return mCurrentPage;
	}

	public void setPageWidth(int pageWidth) {
		this.pageWidthSpec = pageWidth;
	}

	public int getPageWidth() {
		return pageWidth;
	}

	/** 获取whichPage的Pager起始x位置，whichPage从0开始计 */
	private int getScrollXForPage(int whichPage) {
		return (whichPage * pageWidth) - pageWidthPadding();
	}

	/** 返回View的 paddingwidth 一半(1/2) */
	int pageWidthPadding() {
		return ((getMeasuredWidth() - pageWidth) / 2);
	}

	/** 检查scroll状态，并设置绘制scroll缓存 */
	private void checkStartScroll(float x, float y) {
		final int xDiff = (int) Math.abs(x - mLastMotionX);
		final int yDiff = (int) Math.abs(y - mLastMotionY);

		boolean xMoved = xDiff > mTouchSlop;
		boolean yMoved = yDiff > mTouchSlop;

		if (xMoved || yMoved) {
			if (xMoved) {
				mTouchState = TOUCH_STATE_SCROLLING; // 设置为scrolling 状态
				enableChildrenCache();
			}
			if (mAllowLongPress) {
				mAllowLongPress = false;
				// final View currentScreen = getChildAt(mCurrentPage);
				// currentScreen.cancelLongPress(); // Cancels a pending long
				// press
			}
		}
	}

	void enableChildrenCache() {
		setChildrenDrawingCacheEnabled(true); // Enables or disables the drawing
												// cache for each child of this
												// viewGroup
		setChildrenDrawnWithCacheEnabled(true); // Tells the ViewGroup to draw
												// its children using their
												// drawing cache
	}

	void clearChildrenCache() {
		setChildrenDrawnWithCacheEnabled(false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.d(TAG,
				"------------------------------------onTouchEvent-------------------------------");
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		Log.d(TAG + "onTouchEvent", "MotionEvent.ACTION_MOVE, ev.getX()=" + x);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				Log.d(TAG + "onTouchEvent", "!mScroller.isFinished");
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			mInitialMotionX = x;
			Log.d(TAG + "onTouchEvent", "MotionEvent.ACTION_DOWN");
			break;

		case MotionEvent.ACTION_MOVE:
			if (mTouchState == TOUCH_STATE_REST) {
				checkStartScroll(x, y);
				Log.d(TAG + "onTouchEvent",
						"MotionEvent.ACTION_MOVE, mTouchState="
								+ "TOUCH_STATE_REST");
			} else if (mTouchState == TOUCH_STATE_SCROLLING) { // scrolling
																// 状态时，重绘view
				Log.d(TAG + "onTouchEvent",
						"MotionEvent.ACTION_MOVE, mTouchState="
								+ "TOUCH_STATE_SCROLLING");
				int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;

				// scrollBy(deltaX, 0); // 如果你把这个注释掉，就不会看到他有拖动的效果。
			}
			break;

		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				max_distance = fl.getWidth() / 2 - fl.getChildAt(0).getWidth()
						/ 2 - 10;
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();
				// ---------------------------------------------------------------------------------------
				// 这里的几个if else　不太明白，特别是最后的snapToDestination
				// ---------------------------------------------------------------------------------------
				if (Math.abs(velocityX) > SNAP_VELOCITY) {
					if ((mLastMotionX - mInitialMotionX) > 0) {
						animateToRight();
					} else {
						animateToLeft();
					}
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
			mTouchState = TOUCH_STATE_REST;
			Log.d(TAG + "onTouchEvent", "MotionEvent.ACTION_UP");
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.d(TAG + "onTouchEvent", "MotionEvent.ACTION_CANCEL");
			mTouchState = TOUCH_STATE_REST;
		}

		return true;
	}

	private void animateToLeft() {
		fl_len = this.getChildCount();
		if (fl_len == mrstack.size() + mlstack.size()) {
			if (mrstack.size() > 0) {
				mMiddleId = mrstack.pop();
				animateFromRight2Center(findViewById(mMiddleId));
			}
		} else {
			applyRotation(mMiddleId, 1, 0, 60);
		}
	}

	private void animateToRight() {
		fl_len = this.getChildCount();
		if (fl_len == mrstack.size() + mlstack.size()) {
			if (mlstack.size() > 0) {
				mMiddleId = mlstack.pop();
				animateFromRight2Center(findViewById(mMiddleId));
			}
		} else {
			applyRotationToRight(mMiddleId, 10, 0, -60);
		}
	}

	/**
	 * 把这个view 从右边移动到中间。
	 * 
	 * @param findViewById
	 */
	private void animateFromRight2Center(View findViewById) {
		findViewById.bringToFront();
		Log.d(TAG, "------------------------" + findViewById.getWidth() + ","
				+ findViewById.getHeight() + "---------------------------");
		final float centerX = findViewById.getWidth() / 2.0f;
		final float centerY = findViewById.getHeight() / 2.0f;
		Map map = mMap.get(findViewById.getId());
		float dist = (Float) map.get(LayoutMetaData.MOVE_DIS);
		AnimationSet set = new AnimationSet(false);
		// 1 旋转
		Animation rotation = getRotateAnimation(-60, 0, centerX, centerY,
				310.0f, true, false);
		rotation.setStartOffset(0);
		rotation.setDuration(3000);
		// 2 平移
		Animation anim = null;
		anim = getTranslateAnimation(0, -dist, 0, 0);
		anim.setStartOffset(2100);
		anim.setDuration(2000);
		// 3 放大
		Animation animScale = getScaleAnimation(0.6f);
		// 4 alpha 改变
		Animation alpha = new AlphaAnimation(0.1f, 1.0f);
		alpha.setInterpolator(new AccelerateDecelerateInterpolator());
		alpha.setDuration(1000);

		set.setFillAfter(true);
		// set.addAnimation(rotation);
		// set.addAnimation(animScale);
		// set.addAnimation(anim);
		set.addAnimation(alpha);
		findViewById.startAnimation(set);
	}

	/**
	 * 1 先缩放 2绕Y轴旋转 3 平移
	 * 
	 * @param position
	 * @param start
	 * @param end
	 */
	private void applyRotation(int objId, int position, float start, float end) {
		if (objId > 0) {
			mContainer = (RelativeLayout) findViewById(objId);
		} else {

			mContainer = fl.getChildAt(startIndex - 1);
		}
		float distance = max_distance - mlstack.size() * mutex_distance;
		int id = mContainer.getId();
		mlstack.add(id);
		if (mMap.containsKey(id)) {

		} else {
			Map map = new HashMap();
			map.put(LayoutMetaData.MOVE_DIS, distance);
			mMap.put(id, map);
		}
		Log.d(TAG, mContainer.getTag() + "");
		// Find the center of the container
		final float centerX = mContainer.getWidth() / 2.0f;
		final float centerY = mContainer.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		AnimationSet set = new AnimationSet(false);
		// --------------------------------------------------------------------------------------------------------------------------------------------------------------->
		// <--------------------------------------------------------------------------------------------------------------------------------------------------------------
		Animation animScale = getScaleAnimation(0.6f);

		// --------------------------------------------------------------------------------------------------------------------------------------------------------------->
		// <--------------------------------------------------------------------------------------------------------------------------------------------------------------
		Animation rotation = getRotateAnimation(start, end, centerX, centerY,
				310.0f, true, true);
		// anim3.setStartOffset(1100 + anim.getDuration());
		rotation.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				// try {
				// Thread.sleep(800);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				setLayoutVisibility();
			}
		});

		// --------------------------------------------------------------------------------------------------------------------------------------------------------------->
		// <--------------------------------------------------------------------------------------------------------------------------------------------------------------
		Animation anim = null;

		anim = getTranslateAnimation(0, -distance, 0, 0);
		anim.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {

			}
		});

		set.setFillAfter(true);
		set.addAnimation(rotation);
		// set.addAnimation(animScale);
		set.addAnimation(anim);
		mContainer.startAnimation(set);
		// mContainer.setAnimationCacheEnabled(true);
	}

	/**
	 * 到右边的动画
	 * 
	 * @param position
	 * @param start
	 * @param end
	 */
	private void applyRotationToRight(int objId, int position, float start,
			float end) {
		// Find the center of the container
		if (objId > 0) {
			mContainer1 = findViewById(objId);
		} else {
			mContainer1 = fl.getChildAt(startIndex - 1);
		}
		int id = mContainer1.getId();
		mrstack.add(id);

		float distance = max_distance - mrstack.size() * mutex_distance;
		if (mMap.containsKey(id)) {

		} else {
			Map map = new HashMap();
			map.put(LayoutMetaData.MOVE_DIS, distance);
			mMap.put(id, map);
		}
		Log.d(TAG, "------------------------" + mContainer1.getWidth() + ","
				+ mContainer1.getHeight() + "---------------------------");
		final float centerX = mContainer1.getWidth() / 2.0f;
		final float centerY = mContainer1.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		AnimationSet set = new AnimationSet(false);

		// 1 Animation animScale = getScaleAnimation();
		Animation animScale = getScaleAnimation(0.6f);
		animScale.setFillAfter(true);

		// 2
		Animation rotation = getRotateAnimation(start, end, centerX, centerY,
				310.0f, true, false);
		rotation.setFillAfter(true);
		// 3
		Animation anim = null;
		anim = getTranslateAnimation(0, distance, 0, 0);
		anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				setLayoutVisibility();
				// mContainer1.requestLayout();
				// Log.d(TAG,
				// "------------------------"+mContainer1.getWidth()+","+mContainer1.getHeight()
				// +"---------------------------");
			}
		});

		set.setFillAfter(true);
		set.addAnimation(rotation);
		// set.addAnimation(animScale);
		set.addAnimation(anim);

		mContainer1.startAnimation(set);
		// mContainer1.setAnimationCacheEnabled(true);
	}

	private Animation getScaleAnimation(float zoomFactor) {
		Animation animScale = new ScaleAnimation(1.0f, zoomFactor, 1.0f,
				zoomFactor, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		if (hasOrder) {
			animScale.setStartOffset(0);
			animScale.setDuration(800);
		}
		return animScale;
	}

	private Animation getRotateAnimation(float fromDegrees, float toDegrees,
			float centerX, float centerY, float depthZ, boolean reverse,
			boolean left) {
		final Rotate3dAnimation rotation = new Rotate3dAnimation(fromDegrees,
				toDegrees, centerX, centerY, depthZ, reverse, left);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		// d当animation结束，或者重复的时候listener会得到通知
		// rotation.setAnimationListener(new DisplayNextView(position));
		if (hasOrder) {
			rotation.setDuration(900);
			rotation.setStartOffset(900);
		}
		return rotation;
	}

	private Animation getTranslateAnimation(float fromXDelta, float toXDelta,
			float fromYDelta, float toYDelta) {
		Animation anim = new TranslateAnimation(fromXDelta, toXDelta,
				fromYDelta, toYDelta);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());

		// Animation anim3 = AnimationUtils.loadAnimation(this, R.anim.myanim3);
		anim.setFillAfter(true);
		// anim3.setFillAfter(true);
		if (hasOrder) {
			anim.setStartOffset(0);
			anim.setDuration(800);

		}
		return anim;
	}

	public boolean allowLongPress() {
		return mAllowLongPress;
	}

	/**
	 * 自定义接口 目的是为了PagerBar 起作用
	 */
	public static interface OnScrollListener {
		void onScroll(int scrollX);

		void onViewScrollFinished(int currentPage);
	}
}