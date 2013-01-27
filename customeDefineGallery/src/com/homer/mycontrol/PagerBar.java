package com.homer.mycontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

public class PagerBar extends View {

	private static final int DEFAULT_BAR_BACKCOLOR = 0xaa777777;	// bar背景色
	private static final int DEFAULT_BAR_FORECOLOR = 0xaacccccc;	// bar前景色
	private static final int DEFAULT_FADE_DELAY = 2000;
	private static final int DEFAULT_FADE_DURATION = 500;

	private int numPages, currentPage, position;
	private Paint barForePaint, barBackPaint;
	private int fadeDelay, fadeDuration;
	private float ovalRadius;

	private Animation fadeOutAnimation;

	public PagerBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagerBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// 自定义属性
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.com_myapps_widget_PagerBar);
		int barBackColor = a.getColor(R.styleable.com_myapps_widget_PagerBar_barColor, DEFAULT_BAR_BACKCOLOR);				// bar背景色
		int barForeColor = a.getColor(R.styleable.com_myapps_widget_PagerBar_highlightColor, DEFAULT_BAR_FORECOLOR);	// bar前景色
		fadeDelay = a.getInteger(R.styleable.com_myapps_widget_PagerBar_fadeDelay, DEFAULT_FADE_DELAY);				// bar消失延迟时间
		fadeDuration = a.getInteger(R.styleable.com_myapps_widget_PagerBar_fadeDuration, DEFAULT_FADE_DURATION);	// bar消失动画时间
		ovalRadius = a.getDimension(R.styleable.com_myapps_widget_PagerBar_roundRectRadius, 2f);
		a.recycle();

		barBackPaint = new Paint();
		barBackPaint.setColor(barBackColor);

		barForePaint = new Paint();
		barForePaint.setColor(barForeColor);

		fadeOutAnimation = new AlphaAnimation(1f, 0f);
		fadeOutAnimation.setDuration(fadeDuration);
		fadeOutAnimation.setRepeatCount(0);
		fadeOutAnimation.setInterpolator(new LinearInterpolator());
		fadeOutAnimation.setFillEnabled(true);
		fadeOutAnimation.setFillAfter(true);
	}

	public int getNumPages() {
		return numPages;
	}

	public void setNumPages(int numPages) {
		if (numPages <= 0) {
			throw new IllegalArgumentException("numPages must be positive");
		}
		this.numPages = numPages;
		invalidate();		// 重绘View
		fadeOut();			// 设置bar消失效果
	}

	/** bar消失动画 */
	private void fadeOut() {
		if (fadeDuration > 0) {
			clearAnimation();
			fadeOutAnimation.setStartTime(AnimationUtils.currentAnimationTimeMillis() + fadeDelay);	//延迟fadeDelay后动画开始
			setAnimation(fadeOutAnimation);
		}
	}

	/**  @return  0 to numPages-1 */
	public int getCurrentPage() {
		return currentPage;
	}

	/** @param currentPage  0 to numPages-1  */
	public void setCurrentPage(int currentPage) {
		if (currentPage < 0 || currentPage >= numPages) {
			throw new IllegalArgumentException("currentPage parameter out of bounds");
		}
		if (this.currentPage != currentPage) {
			this.currentPage = currentPage;
			this.position = currentPage * getPageWidth();	// bar前景色滑动条的起始位置（像素值）
			invalidate();
			fadeOut();
		}
	}

	/** 获取View的宽度，即bar的宽度 */
	public int getPageWidth() {
		return getWidth() / numPages;	// getWidth()是PagerBar的宽度（减去了margin左右距离后）
	}

	/**  @param position     can be -pageWidth to pageWidth*(numPages+1)  */
	public void setPosition(int position) {
		if (this.position != position) {
			this.position = position;
			invalidate();
			fadeOut();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), ovalRadius, ovalRadius, barBackPaint);	// 绘制bar背景
		canvas.drawRoundRect(new RectF(position, 0, position + (getWidth() / numPages), getHeight()), ovalRadius, ovalRadius, barForePaint);	// 绘制bar前景
	}
}
