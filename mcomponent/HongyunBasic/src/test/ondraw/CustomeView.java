package test.ondraw;

import com.example.hongyunbasic.R;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomeView extends View {

	private Paint backgroundPaint;
	private Paint mlinePaint;
	public CustomeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public CustomeView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.GREEN);
		mlinePaint = new Paint();
		mlinePaint.setColor(Color.RED);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
/*		normalTest(canvas);*/
		saveAndRestoreTest(canvas);
	}
	private void saveAndRestoreTest(Canvas canvas) {
		int px = getMeasuredWidth();
		int py = getMeasuredHeight();
		
		canvas.drawRect(0,0,px,py,backgroundPaint);
		canvas.save();
		canvas.rotate(90,px/2,py/2); //如果这里不指定 中心点的坐标，则有可能下面的线看不见，canvas.rotate(90);
		canvas.drawLine(px/2, 0, 0, py/2, mlinePaint);
		canvas.drawLine(px/2, 0, px, py/2, mlinePaint);
		canvas.drawLine(px/2, 0, px/2, py, mlinePaint);
//		canvas.drawLine(py/2, 0, 0, py/2, mlinePaint);
//		canvas.drawLine(px/2, 0, px/2, py, mlinePaint);
//		canvas.drawLine(px, py/2, px/2, 0, mlinePaint);
		
		canvas.restore();
		canvas.drawCircle(px-10, py-10, 8, mlinePaint);
	}
	private void normalTest(Canvas canvas) {
		float startX=0f;
		float startY=0f;
		float stopX=this.getWidth();
		float stopY=this.getHeight();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		canvas.drawLine(startX, startY, stopX/2, stopY, paint );
		canvas.drawLine(stopX/2, stopY, stopX, startY, paint );
		canvas.drawLine(stopX/2, startY, stopX/2, stopY, paint );
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		/*canvas.drawColor(android.R.color.darker_gray); //这样使用是没有效果的，要像下面这样。
		canvas.drawColor(getResources().getColor(android.R.color.darker_gray));
		getResources().getColor(android.R.color.darker_gray);*/
		canvas.drawBitmap(bmp,10,10,null);
	}

}
