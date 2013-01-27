package test.scrolllayout;

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
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.example.hongyunbasic.R;

public class VerticalScrollBounceView extends ViewGroup {

	private static final int INVALID_SCREEN = -1;
	public static final int SPEC_UNDEFINED = -1;

	private static final int SNAP_VELOCITY = 1000;

	private int pageWidth;		// 代码设置的pageWidth 注：在onMeasure方法中被设置。
	private int pageWidthSpec;	// xml设置的pageWidth

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
	private float mLastMotionX4Child;
	private float mLastMotionY;

	private final static int TOUCH_STATE_REST = 0;			// scroll 未完成
	private final static int TOUCH_STATE_SCROLLING = 1;	// scroll 滑动中
	private int mTouchState = TOUCH_STATE_REST;			// scroll 起始默认状态

	private boolean mAllowLongPress;
	LinearLayout subView;

	private Set<OnScrollListener> mListeners = new HashSet<OnScrollListener>();		// 监听事件接口
    private String TAG = this.getClass().getSimpleName(); //adb logcat Pager:D *:S 多个filter用空格隔开，最后一个表示所有的silent

	public VerticalScrollBounceView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VerticalScrollBounceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// 自定义属性
//		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.com_myapps_widget_Pager);
//		pageWidthSpec = a.getDimensionPixelSize(R.styleable.com_myapps_widget_Pager_pageWidth, SPEC_UNDEFINED);
//		a.recycle();

		init();
	}

	private void init() {
		mScroller = new Scroller(getContext());
		mCurrentPage = 0;

		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop(); //Distance a touch can wander before we think the user is scrolling in pixels
		//mTouchSlop = 200; //设置这个后滑动屏幕的时候，距离必须要大于这个值，才会scroll。
		Log.d(TAG, "mTouchSlop:"+mTouchSlop);
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();	// fling最大速度
		Log.d(TAG, "mMaximumVelocity:"+mMaximumVelocity);
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

	//-------------------------------------------------------------------------------------
    //Called by a parent to request that a child update its values for mScrollX and mScrollY
	//if necessary. This will typically be done if the child is animating a scroll using a
	//Scroller object.
    //-------------------------------------------------------------------------------------
	@Override
	public void computeScroll() {		// update  mScrollX and mScrollY  of View
	    Log.d(TAG, "--------------------------------computeScroll-----------------------------------");
	    Log.d(TAG, "mScroller.getCurrX()="+mScroller.getCurrX()+",mScroller.getCurrY()="+mScroller.getCurrY());

		if (mScroller.computeScrollOffset()) {//我认为只要在滚动当中 这个就是返回true
		    //-------------------------------------------------------------------------------------
		    //Set the scrolled position of your view.
		    //This will cause a call to onScrollChanged(int, int, int, int) and the view will
		    //be invalidated.注：scrollTo是view定义的方法，用它可以scroll到指定的位置。上面说
		    //scrollTo-->onScrollChanged 且view will be invalidated，那是不是下面的postInvalidate可以不要
		    //调用，事实证明注释了也没发现什么问题。
		    //-------------------------------------------------------------------------------------
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();			// invalidate the View from a non-UI thread
		} else if (mNextPage != INVALID_SCREEN) {
			mCurrentPage = mNextPage;
			mNextPage = INVALID_SCREEN;
			clearChildrenCache();
		}
	}

	//-------------------------------------------------------------------------------------
	//Called by draw to draw the child views. This may be overridden by derived classes to
	//gain control just before its children are drawn (but after its own view has been drawn).
	//-------------------------------------------------------------------------------------
	/**
	 * 会调用自定义的接口。如控制PagerBar control的显示。
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {	// draw the child views
	    Log.d(TAG, "-------------------------------dispatchDraw------------------------------------");
		final long drawingTime = getDrawingTime();	// 绘制childView
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			drawChild(canvas, getChildAt(i), drawingTime);
		}

		for (OnScrollListener mListener : mListeners) {	// 自定义接口
			Log.d("dispatchDraw", getScrollX()+"");//滑动后x的位置都是固定的
			int adjustedScrollX = getScrollX() + pageWidthPadding();
			mListener.onScroll(adjustedScrollX);
			if (adjustedScrollX % pageWidth == 0) {	// scroll finished
				mListener.onViewScrollFinished(adjustedScrollX / pageWidth);
			}
		}
	}

	//-------------------------------------------------------------------------------------
    //Measure the view and its content to determine the measured width and the measured height.
	//This method is invoked by measure(int, int) and should be overriden by subclasses to
	//provide accurate and efficient measurement of their contents.
    //-------------------------------------------------------------------------------------
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(getLayoutParams().height == LayoutParams.FILL_PARENT){

		}else{
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.d(TAG, "--------------------------------onMeasure-----------------------------------");
		pageWidth = (pageWidthSpec == SPEC_UNDEFINED) ? getMeasuredWidth() : pageWidthSpec;
		pageWidth = Math.min(pageWidth, getMeasuredWidth());

		final int count = getChildCount();
		View child = null;
		for (int i = 0; i < count; i++) {
//			widthMeasureSpec = MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY);
			MeasureSpec.getSize(heightMeasureSpec);
			child = getChildAt(i);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);
			child.measure(widthMeasureSpec, heightMeasureSpec);
			int childHeight  = child.getMeasuredHeight();
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		if(getLayoutParams().height == LayoutParams.FILL_PARENT){

		}else{
			setMeasuredDimension(child.getMeasuredWidth(), child.getMeasuredHeight());
		}
//		if (mFirstLayout) {	// 第一次显示Pager时，page的位置
//			scrollTo(getScrollXForPage(mCurrentPage), mScroller.getCurrY());
//			mFirstLayout = false;
//		}
	}
	/**
	 * Called from layout when this view should assign a size and position to each of its children.
	 * Derived classes with children should override this method and call layout on each of their
	 *  their children.
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int childLeft = 0;
		Log.d(TAG, "---------------------------------onLayout----------------------------------");
		final int count = getChildCount();	// 绘制childView
		int startHeight =0;
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				 int childHeight= child.getMeasuredHeight();
				 startHeight = (bottom-top)/2-childHeight/2;
 				child.layout(0	, startHeight,  childWidth,startHeight+childHeight);
				startHeight += childHeight;
//				child.layout(0, 0, childWidth, childLeft + child.getMeasuredHeight());
//				childLeft += childWidth;
			}
		}
	}
//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//		int childLeft = 0;
//		Log.d(TAG, "---------------------------------onLayout----------------------------------");
//		final int count = getChildCount();	// 绘制childView
//		for (int i = 0; i < count; i++) {
//			final View child = getChildAt(i);
//			if (child.getVisibility() != View.GONE) {
//				final int childWidth = child.getMeasuredWidth();
//				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
////				child.layout(0, 0, childWidth, childLeft + child.getMeasuredHeight());
//				childLeft += childWidth;
//			}
//		}
//	}

	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
	    Log.d(TAG, "----------------------------------requestChildRectangleOnScreen---------------------------------");
		int screen = indexOfChild(child);
		if (screen != mCurrentPage || !mScroller.isFinished()) {
			return true;
		}
		return false;
	}

	/**
	 * Override this to customize how your ViewGroup requests focus within its children.
	 */
	@Override
	protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
	    Log.d(TAG, "--------------------------------onRequestFocusInDescendants-----------------------------------");
		int focusableScreen;
		if (mNextPage != INVALID_SCREEN) {	// childView获取焦点
			focusableScreen = mNextPage;
		} else {
			focusableScreen = mCurrentPage;
		}
		getChildAt(focusableScreen).requestFocus(direction, previouslyFocusedRect);	// 赋值childView焦点
		return false;
	}

	/**
	 * This method is the last chance for the focused view and its ancestors to respond to an arrow key.
	 *  This is called when the focused view did not consume the key internally, nor could the view
	 *  system find a new view in the requested direction to give focus to.
	 *  这个方法将在下面两种情况下面调用：1当前获得焦点的view没有消费这个key event. 2view system在这个key event
	 *  的传播方向上面找不到可以处理这个事件的对象。
	 */
	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
	    Log.d(TAG, "------------------------------dispatchUnhandledMove-------------------------------------");
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
	/**
	 * Add any focusable views that are descendants of this view
	 * (possibly including this view if it is focusable itself) to views.
	 * If we are in touch mode, only add views that are also focusable in touch mode.
	 */
	@Override
	public void addFocusables(ArrayList<View> views, int direction) {
	    Log.d(TAG, "------------------------------addFocusables-------------------------------------");
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
	/**
	 * Implement this method to intercept all touch screen motion events.
	 * This allows you to watch events as they are dispatched to your children,
	 * and take ownership of the current gesture at any point.
	 *
	 * 注释掉了原来的实现，也没发现什么问题，貌似没啥大用。
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
	    /*if(true)
	        return true;*/
	    return super.onInterceptTouchEvent(ev);
	    /*Log.d(TAG, "---------------------------------onInterceptTouchEvent----------------------------------");
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {	// 正在滑动中
		    //---------------------------------------------------------------------------------------
		    //这里的return true的解释不太明白
		    //---------------------------------------------------------------------------------------
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

		return mTouchState != TOUCH_STATE_REST;*/
	}

	/** 检查scroll状态，并设置绘制scroll缓存 */
	private void checkStartScroll(float x, float y) {
		final int xDiff = (int) Math.abs(x - mLastMotionX);
		final int yDiff = (int) Math.abs(y - mLastMotionY);

		boolean xMoved = xDiff > mTouchSlop;
		boolean yMoved = yDiff > mTouchSlop;

		if (xMoved || yMoved) {
			if (xMoved ||yMoved) {
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

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
	    Log.d(TAG, "------------------------------------onTouchEvent-------------------------------");
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float y = ev.getY();
//		Log.d(TAG+"onTouchEvent", "MotionEvent.ACTION_MOVE, ev.getX()="+x);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
			    Log.d(TAG+"onTouchEvent", "!mScroller.isFinished");
				mScroller.abortAnimation();
			}
			mLastMotionY = y;
			Log.d(TAG+"onTouchEvent", "MotionEvent.ACTION_DOWN");
			break;

		case MotionEvent.ACTION_MOVE:
			int childTop =getChildAt(0).getTop();
			int scrollY = getScrollY();
			Log.i("onTouchEvent", "getScrollY="+scrollY+"getChildAt(0).getTop()="+childTop+",y="+y);
			if(scrollY == childTop){
				Log.i("onTouchEvent1", "y = "+y+",mLastMotion4child="+mLastMotionX4Child);
				mLastMotionX4Child  = y;
				break;
			}
			if(scrollY>childTop){
				//don't scroll this view, it's time to scroll the child view
				if(mLastMotionX4Child<=0){
					mLastMotionX4Child = y;
				}
				int deltaY = (int) (mLastMotionX4Child-y);
				Log.i("onTouchEvent1", "y = "+y+",mLastMotion4child="+mLastMotionX4Child+",deltaY="+deltaY);
				if(deltaY>=0){
					mLastMotionX4Child = y;
					View child = getChildAt(0);
					LinearLayout ll = (LinearLayout)child;
					if(ll.getChildAt(1) instanceof LinearLayout){
						subView = (LinearLayout)ll.getChildAt(1);
						subView.scrollBy(0, deltaY/2);
					}else{
						// do nothing
					}
				}else{
					mLastMotionX4Child = y;
					Log.i("onTouchEvent2", "y = "+y+",mLastMotion4child="+mLastMotionX4Child+",deltaY="+deltaY);
					if(subView==null){

					}else{
						Log.i("onTouchEvent2", "subView.getScrollY()"+subView.getScrollY()+",subView.GETtOP="+subView.getTop());
						if (subView.getScrollY()> 0) {
							subView.scrollBy(0, deltaY / 2);
						} else{
							scrollBy(0, deltaY / 2);
							subView.scrollTo(0, 0);
						}
					}
				}

			}else{
				if (mTouchState == TOUCH_STATE_REST) {
					checkStartScroll(0, y);
					Log.d(TAG+"onTouchEvent", "MotionEvent.ACTION_MOVE, mTouchState="+"TOUCH_STATE_REST");
				} else if (mTouchState == TOUCH_STATE_SCROLLING) {	// scrolling 状态时，重绘view
					Log.d(TAG+"onTouchEvent", "MotionEvent.ACTION_MOVE, mTouchState="+"TOUCH_STATE_SCROLLING");
					int deltaY = (int) (mLastMotionY - y);
					mLastMotionY = y;

					scrollBy(0, deltaY/2); //如果你把这个注释掉，就不会看到他有拖动的效果。
				}

			}

			break;

		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();
				if(subView!=null){
					subView.scrollTo(0, 0);
				}

				//---------------------------------------------------------------------------------------
				//这里的几个if else　不太明白，特别是最后的snapToDestination
				//---------------------------------------------------------------------------------------
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
			Log.d(TAG+"onTouchEvent", "MotionEvent.ACTION_UP");
			break;
		case MotionEvent.ACTION_CANCEL:
		    Log.d(TAG+"onTouchEvent", "MotionEvent.ACTION_CANCEL");
			mTouchState = TOUCH_STATE_REST;
		}

		return true;
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
		Log.d(TAG, "getScrollX()="+getScrollX()+",newX="+newX);
		final int delta = newX - getScrollX();
		//getScrollX 是SDK的方法。
		int scrollY = getScrollY();

		Log.d("snapToPage", "getScrolly()="+getScrollY()+",newX="+newX);
		mScroller.startScroll(0, scrollY, 0, -scrollY,800);
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
	    Log.d(TAG, "---------------------------------onSaveInstanceState----------------------------------");
		final SavedState state = new SavedState(super.onSaveInstanceState());
		state.currentScreen = mCurrentPage;	 	// save InstanceState
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {	// 恢复状态
	    Log.d(TAG, "---------------------------------onRestoreInstanceState----------------------------------");
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

	/** 自定义接口
	 * 目的是为了PagerBar 起作用*/
	public static interface OnScrollListener {
		void onScroll(int scrollX);
		void onViewScrollFinished(int currentPage);
	}
}
