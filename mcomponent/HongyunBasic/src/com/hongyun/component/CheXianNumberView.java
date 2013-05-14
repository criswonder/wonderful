package com.hongyun.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.xixi.R;
import com.xixi.utils.MiscUtil;

public class CheXianNumberView extends View {
	private static final String TAG = "CheXianNumberView";
	private int cellCount = 5;
    private int duration = 2000;
    private int maxDistance = 1000;
    private int sleepTime = 30;
    private Drawable bg;
    private Drawable cellBg;
    private Drawable cellMask;
    private float cellPadding;
    private int cellTopY;
    private int centerY;
    private Paint paint;

	public CheXianNumberView(Context context) {
		super(context);
	}
	public CheXianNumberView(Context context, AttributeSet attrs){
		super(context,attrs);
		if(attrs!=null){
			TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CheXianNumberView);
			this.cellCount = typedArray.getInteger(R.styleable.CheXianNumberView_cellCount, cellCount);
			this.duration = typedArray.getInteger(R.styleable.CheXianNumberView_duration, duration);
			this.maxDistance = typedArray.getInteger(R.styleable.CheXianNumberView_maxDistance, maxDistance);
			this.sleepTime = typedArray.getInteger(R.styleable.CheXianNumberView_sleepTime, sleepTime);
			typedArray.recycle();
		}
		bg = getResources().getDrawable(R.drawable.bj_bg);
		cellBg = getResources().getDrawable(R.drawable.bj_num_bg);
		cellMask = getResources().getDrawable(R.drawable.bj_num_bg_mask);
	}
	
	public void loop() {

	}

	public void set(int cellCount, int duration, int maxDistance) {
		this.cellCount = cellCount;
		this.duration = duration;
		this.maxDistance = maxDistance;
	}

	public void setNumber(final int newNumber, final boolean animation) {
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawBg(canvas);
		drawNumbers(canvas);
		drawMasks(canvas);
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		bg.setBounds(0,0,w,h);
		cellPadding = (w-getCellBgWidth()*cellCount)/(cellCount+1);
		cellTopY = (h -getCellBgHeight() )/2;
		centerY = h/2;
	}
	public int getCellBgWidth(){
		return MiscUtil.getPxByDip(24);
	}
	public int getCellBgHeight(){
		return MiscUtil.getPxByDip(24);
	}
	private void drawMasks(Canvas canvas) {
		
	}
	private void drawNumbers(Canvas canvas) {
		
	}
	private void drawBg(Canvas canvas) {
		bg.draw(canvas);
		for(int i=0;i<cellCount;i++){
			cellBg.setBounds(getNumberBounds(i));
			cellBg.draw(canvas);
		}
	}
	private Rect getNumberBounds(int i){
		int cellX= (int)( getCellBgWidth()*i + cellPadding*(i+1) );
		return new Rect(cellX, cellTopY, cellX+getCellBgWidth(), cellTopY+getCellBgHeight());
	}

}
