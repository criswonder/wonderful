package com.example.third;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
/**
 * 指定一个方向（常量），一个距离。这个view将在这个方向上移动指定的距离
 * @author armstrong
 *
 */
public class CustomeScrollView extends View {
	public enum Direction{
		RIGHT,LEFT
	}
	private static final String TAG = "CustomeScrollView";

	public CustomeScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void moveTo(Direction direction, int distance){
		if(Direction.RIGHT==direction){
			scrollBy(-distance, 0);
		}else if(Direction.LEFT==direction){
			scrollBy(distance, 0);
		}else {
			throw new IllegalArgumentException();
		}
		invalidate();
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d(TAG, "----------------------------onDraw get called------------------------");
	}
}
