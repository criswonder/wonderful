package test.scrolllayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Scroller;

public class FooScroll extends AdapterView<Adapter> {
	private Adapter mAdapter;
	private Scroller mScroller;
	private float mLastMotionX;
	
	public FooScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
		// init stuff
		mScroller = new Scroller(getContext());
		
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		//what need to do here is to register datasetObserver TODO
		mAdapter = adapter;
		
		//Then call setSelection function TODO
		setSelection(0);
	}

	@Override
	public View getSelectedView() {
		// TODO Auto-generated method stub
		return mAdapter.getView(0, null, null);
	}

	@SuppressWarnings("unused")
	@Override
	public void setSelection(int position) {
		Log.i("andymao", "setSelection get called at " + System.currentTimeMillis());
		View view = getSelectedView();
		
		ViewGroup.LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		boolean result = addViewInLayout(view, -1, null,true);
		// 原来这里调用 addViewInLayout方法来重复添加view结果都显示不出来。
		for(int i=1;i<3;i++){
			view = mAdapter.getView(i, null, null);
			addViewInLayout(view, -1, p);
		}
//		requestLayout();
	}
	@Override
	public int getSelectedItemPosition() {
		// TODO Auto-generated method stub
		return super.getSelectedItemPosition();
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		
		int childLeft = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth,
						child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
		
	}
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		getChildCount();
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		Log.i(FooScroll.class.getSimpleName()+"; widthMeasureSpec", MeasureSpec.getSize(widthMeasureSpec)+"");
		Log.i(FooScroll.class.getSimpleName()+"; heightMeasureSpec", MeasureSpec.getSize(heightMeasureSpec)+"");
	}
	//－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
	//－－－－－－－－－－let us scroll－－－－－－－－－－－－－
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float x = ev.getX();
		switch(action){
			case MotionEvent.ACTION_DOWN:
			{
				mLastMotionX = x;
			}
			break;
			case MotionEvent.ACTION_MOVE:
			{
				int deltaX = (int)(x - mLastMotionX);
				Log.i("onInterceptTouchEvent", "x = "+x+", deltaX="+deltaX);
				Log.i("onInterceptTouchEvent", "getScrollX = "+getScrollX()+", getLeft="+getLeft()+", getRight="+getRight());
				mLastMotionX = x;
				if(getScrollX()>=480*2&&deltaX>0)
				{
					
				}else{
					scrollBy(deltaX, 0);
				}
			}
			break;
			case MotionEvent.ACTION_UP:
			{
				
			}
			break;
			case MotionEvent.ACTION_CANCEL:
			{
				Log.i(this.getClass().getSimpleName(), "action_cancel happened!!!!");
			}
			break;
		}
		
		return super.onInterceptTouchEvent(ev);
	}
	
	
	
	
}
