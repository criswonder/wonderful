package test.drawn;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * I am trying to use HorizontalScrollView as an wrapper to MyView so it can stretch as I want, but it failed!
 * @author wonder
 *
 */
public class DrawnDiagram extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(600, 20);
		
//		HorizontalScrollView hs = new HorizontalScrollView(this);
//		hs.setBackgroundColor(Color.GREEN);

		MyView myView = new MyView(this, null);
		myView.setBackgroundColor(Color.parseColor("#544545"));
//		hs.addView(myView,p);
		
		setContentView(myView);
		
	}
	
	class MyView extends LinearLayout{

		public MyView(Context context, AttributeSet attrs) {
			super(context, attrs);
//			dispatchDraw(new Canvas());
		}
		@Override
		protected void onDraw(Canvas canvas) {
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			RectF rect=new RectF(10, 40, 50, 200);
			canvas.drawRect(rect, paint);
			
			
			 rect=new RectF(500, 40, 550, 200);
			canvas.drawRect(rect, paint);
			
			canvas.drawLine(0, 200, 400, 200, paint);
			canvas.drawText("23:00-45:00", 0, 214, paint);
			
			canvas.drawText("23KM", 0, 30, paint);
			
			this.requestLayout();
			super.onDraw(canvas);
		}
	}

}
