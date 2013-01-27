package com.example.third;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Scroller;

public class Pager extends ViewGroup {

	private static final int INVALID_SCREEN = -1;
	public static final int SPEC_UNDEFINED = -1;

	private static final int SNAP_VELOCITY = 1000;

	private int pageWidth;		// 代码设置的pageWidth
	private int pageWidthSpec;	// xml设置的pageWidth

	private boolean mFirstLayout = true;

	private int mCurrentPage;
	private int mNextPage = INVALID_SCREEN;

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private int mTouchSlop;
	private int mMaximumVelocity;

	private float mLastMotionX;
	private float mLastMotionY;

	private final static int TOUCH_STATE_REST = 0;			// scroll 未完成
	private final static int TOUCH_STATE_SCROLLING = 1;	// scroll 滑动中
	private int mTouchState = TOUCH_STATE_REST;			// scroll 起始默认状态

	private boolean mAllowLongPress;

	private Set<OnScrollListener> mListeners = new HashSet<OnScrollListener>();		// 监听事件接口

	public Pager(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Pager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// 自定义属性
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.com_myapps_widget_Pager);
		pageWidthSpec = a.getDimensionPixelSize(R.styleable.com_myapps_widget_Pager_pageWidth, SPEC_UNDEFINED);
		a.recycle();

		init();
	}

	private void init() {
		mScroller = new Scroller(getContext());
		mCurrentPage = 0;

		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();	// fling最大速度
	}

	public void setCurrentPage(int currentPage) {
		mCurrentPage = Math.max(0, Math.min(currentPage, getChildCount()));		// 非常好
		scrollTo(getScrollXForPage(mCurrentPage), 0);
		invalidate();	// 重绘View
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

	/** 返回View的 paddingwidth 一半(1/2)*/
	int pageWidthPadding() {
		return ((getMeasuredWidth() - pageWidth) / 2);
	}

	@Override
	public void computeScroll() {		// update  mScrollX and mScrollY  of View
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();			// invalidate the View from a non-UI thread
		} else if (mNextPage != INVALID_SCREEN) {
			mCurrentPage = mNextPage;
			mNextPage = INVALID_SCREEN;
			clearChildrenCache();
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {	// draw the child views
		
		final long drawingTime = getDrawingTime();	// 绘制childView
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			drawChild(canvas, getChildAt(i), drawingTime);
		}

		for (OnScrollListener mListener : mListeners) {	// 自定义接口
			int adjustedScrollX = getScrollX() + pageWidthPadding();
			mListener.onScroll(adjustedScrollX);
			if (adjustedScrollX % pageWidth == 0) {	// scroll finished
				mListener.onViewScrollFinished(adjustedScrollX / pageWidth);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		pageWidth = (pageWidthSpec == SPEC_UNDEFINED) ? getMeasuredWidth() : pageWidthSpec;
		pageWidth = Math.min(pageWidth, getMeasuredWidth());

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY);
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		if (mFirstLayout) {	// 第一次显示Pager时，page的位置
			scrollTo(getScrollXForPage(mCurrentPage), mScroller.getCurrY());
			mFirstLayout = false;
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int childLeft = 0;

		final int count = getChildCount();	// 绘制childView
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
		int screen = indexOfChild(child);
		if (screen != mCurrentPage || !mScroller.isFinished()) {
			return true;
		}
		return false;
	}

	@Override	
	protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
		int focusableScreen;
		if (mNextPage != INVALID_SCREEN) {	// childView获取焦点 
			focusableScreen = mNextPage;
		} else {
			focusableScreen = mCurrentPage;
		}
		getChildAt(focusableScreen).requestFocus(direction, previouslyFocusedRect);	// 赋值childView焦点
		return false;
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (direction == View.FOCUS_LEFT) {
			if (getCurrentPage() > 0) {			// 如果左侧Pager不是第一个（0），则向左滑一页
				snapToPage(getCurrentPage() - 1);
				return true;
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (getCurrentPage() < getChildCount() - 1) {	// 如果左侧Pager不是第一个（0），则向左滑一页
				snapToPage(getCurrentPage() + 1);
				return true;
			}
		}
		return super.dispatchUnhandledMove(focused, direction);
	}

	@Override
	public void addFocusables(ArrayList<View> views, int direction) {
		getChildAt(mCurrentPage).addFocusables(views, direction);
		if (direction == View.FOCUS_LEFT) {
			if (mCurrentPage > 0) {
				getChildAt(mCurrentPage - 1).addFocusables(views, direction);
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (mCurrentPage < getChildCount() - 1) {
				getChildAt(mCurrentPage + 1).addFocusables(views, direction);
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {	// 正在滑动中
			return true;
		}

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			if (mTouchState == TOUCH_STATE_REST) {
				checkStartScroll(x, y);
			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mAllowLongPress = true;

			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;	// scroll 完成后，重置状态
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			clearChildrenCache();
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
	
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		Log.d("andy", "getScrollX()="+getScrollX());
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;
			
		case MotionEvent.ACTION_MOVE:
			if (mTouchState == TOUCH_STATE_REST) {
				checkStartScroll(x, y);
			} else if (mTouchState == TOUCH_STATE_SCROLLING) {	// scrolling 状态时，重绘view
				int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;
	
				if (getScrollX() < 0 || getScrollX() > getChildAt(getChildCount() - 1).getLeft()) {
					deltaX /= 2;
				}
				scrollBy(deltaX, 0);
			}
			break;
			
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();
	
				if (velocityX > SNAP_VELOCITY && mCurrentPage > 0) {
					snapToPage(mCurrentPage - 1);
				} else if (velocityX < -SNAP_VELOCITY && mCurrentPage < getChildCount() - 1) {
					snapToPage(mCurrentPage + 1);
				} else {
					snapToDestination();
				}
	
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
		}
	
		return true;
	}

	/** 检查scroll状态，并设置绘制scroll缓存 */
	private void checkStartScroll(float x, float y) {
		final int xDiff = (int) Math.abs(x - mLastMotionX);
		final int yDiff = (int) Math.abs(y - mLastMotionY);

		boolean xMoved = xDiff > mTouchSlop;
		boolean yMoved = yDiff > mTouchSlop;

		if (xMoved || yMoved) {
			if (xMoved) {
				mTouchState = TOUCH_STATE_SCROLLING;		// 设置为scrolling 状态
				enableChildrenCache();
			}
			if (mAllowLongPress) {
				mAllowLongPress = false;
				final View currentScreen = getChildAt(mCurrentPage);
				currentScreen.cancelLongPress();	// Cancels a pending long press
			}
		}
	}

	void enableChildrenCache() {
		setChildrenDrawingCacheEnabled(true);		// Enables or disables the drawing cache for each child of this viewGroup
		setChildrenDrawnWithCacheEnabled(true);	// Tells the ViewGroup to draw its children using their drawing cache
	}

	void clearChildrenCache() {
		setChildrenDrawnWithCacheEnabled(false);
	}

	private void snapToDestination() {
		final int startX = getScrollXForPage(mCurrentPage);
		int whichPage = mCurrentPage;
		if (getScrollX() < startX - getWidth() / 8) {
			whichPage = Math.max(0, whichPage - 1);
		} else if (getScrollX() > startX + getWidth() / 8) {
			whichPage = Math.min(getChildCount() - 1, whichPage + 1);
		}

		snapToPage(whichPage);
	}

	void snapToPage(int whichPage) {
		enableChildrenCache();

		boolean changingPages = whichPage != mCurrentPage;

		mNextPage = whichPage;

		View focusedChild = getFocusedChild();
		if (focusedChild != null && changingPages && focusedChild == getChildAt(mCurrentPage)) {
			focusedChild.clearFocus();
		}

		final int newX = getScrollXForPage(whichPage);
		final int delta = newX - getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		invalidate();
	}

	/** 向左滑动 */
	public void scrollLeft() {
		if (mNextPage == INVALID_SCREEN && mCurrentPage > 0 && mScroller.isFinished()) {
			snapToPage(mCurrentPage - 1);
		}
	}

	/** 向右滑动 */
	public void scrollRight() {
		if (mNextPage == INVALID_SCREEN && mCurrentPage < getChildCount() - 1 && mScroller.isFinished()) {
			snapToPage(mCurrentPage + 1);
		}
	}

	public int getScreenForView(View v) {
		int result = -1;
		if (v != null) {
			ViewParent vp = v.getParent();
			int count = getChildCount();
			for (int i = 0; i < count; i++) {
				if (vp == getChildAt(i)) {
					return i;
				}
			}
		}
		return result;
	}

	public boolean allowLongPress() {
		return mAllowLongPress;
	}

	/** 保存状态 */
	public static class SavedState extends BaseSavedState {
		int currentScreen = -1;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentScreen = in.readInt();
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {	// get data written by Parcelable.writeToParcel()	
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {			// create  array of the Parcelable 
				return new SavedState[size];
			}
		};
		
		@Override
		public void writeToParcel(Parcel out, int flags) {		// set data to parcel
			super.writeToParcel(out, flags);
			out.writeInt(currentScreen);
		}

	}

	@Override
	protected Parcelable onSaveInstanceState() {		// 保存状态
		final SavedState state = new SavedState(super.onSaveInstanceState());
		state.currentScreen = mCurrentPage;	 	// save InstanceState
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {	// 恢复状态
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());	// get InstanceState
		if (savedState.currentScreen != INVALID_SCREEN) {
			mCurrentPage = savedState.currentScreen;	
		}
	}

	public void addOnScrollListener(OnScrollListener listener) {
		mListeners.add(listener);
	}

	public void removeOnScrollListener(OnScrollListener listener) {
		mListeners.remove(listener);
	}

	/** 自定义接口 */
	public static interface OnScrollListener {
		void onScroll(int scrollX);
		void onViewScrollFinished(int currentPage);
	}
}
