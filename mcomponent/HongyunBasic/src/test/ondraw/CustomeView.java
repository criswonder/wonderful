package test.ondraw;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.hongyunbasic.R;

public class CustomeView extends View {

	private static final String TAG = "CustomeView";
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
//		normalTest(canvas);
//		saveAndRestoreTest(canvas);
//		joinBitmap(canvas);
//		joinBitmap2(canvas);
//		drawCircleImage(canvas);
		drawJbtyJoinBitmap(canvas);
	}
	private void drawJbtyJoinBitmap(Canvas canvas) {
		String fuck1 = "我看看啊已";
		Bitmap bitmap = getJbtyPopupJoinBitmap(fuck1,R.drawable.hi_boy);
		canvas.drawBitmap(bitmap, 0, 10, null);
		fuck1 = "我看看";
		bitmap = getJbtyPopupJoinBitmap(fuck1,R.drawable.hi_girl);
		canvas.drawBitmap(bitmap, 0, 10+bitmap.getHeight(), null);
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
	/**
	 * 目的：获得千岛湖项目，游记地图上面的合成图片
	 * @param canvas
	 */
	private void joinBitmap2(Canvas canvas) {
		canvas.drawBitmap(getBitmap(), 0, 0, null);
	}
	/**
	 * 目的：获得千岛湖项目，游记地图上面的合成图片
	 * @param canvas
	 */
	private void joinBitmap(Canvas canvas) {
		Paint paint = new Paint();
//		paint.setAntiAlias(true);
 
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.location);
		Log.d(TAG, "height="+bmp.getHeight());
		Log.d(TAG, "width="+bmp.getWidth());
		canvas.drawBitmap(bmp,0,0,null);
		int startX = (bmp.getWidth()-49)/2;
		
		
		int markerWidth = 100;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.paper,options);

        int realHeight = options.outHeight;
        int realWidth = options.outWidth;
        int sampleSize = realWidth / markerWidth ;
        int sampleSize1 = realHeight / markerWidth ;
        sampleSize = Math.max(sampleSize, sampleSize1);
        sampleSize = Math.max(1, sampleSize);
        
        options= new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        options.inInputShareable = true;
        options.inPurgeable = true;
        options.inSampleSize = sampleSize;
        
        
        
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
        		R.drawable.paper,options);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 设置想要的大小
        int newWidth = 49;
        int newHeight = 49;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        
        Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, 
        		height, 
        		matrix,
        		true);
		canvas.drawBitmap(newbm,startX,startX,null);
		
	}
	/**
	 * 目的：获得千岛湖项目，游记地图上面的合成图片
	 * @param canvas
	 */
	private Bitmap getBitmap() {
//		paint.setAntiAlias(true);
		
		Bitmap mainbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location);
		Log.d(TAG, "height="+mainbitmap.getHeight());
		Log.d(TAG, "width="+mainbitmap.getWidth());
		Bitmap returnBitmap = mainbitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(returnBitmap);
		int startX = (mainbitmap.getWidth()-49)/2;
		
		
		int markerWidth = 100;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.drawable.paper,options);
		
		int realHeight = options.outHeight;
		int realWidth = options.outWidth;
		int sampleSize = realWidth / markerWidth ;
		int sampleSize1 = realHeight / markerWidth ;
		sampleSize = Math.max(sampleSize, sampleSize1);
		sampleSize = Math.max(1, sampleSize);
		
		options= new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		options.inInputShareable = true;
		options.inPurgeable = true;
		options.inSampleSize = sampleSize;
		
		
		
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.paper,options);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 设置想要的大小
		int newWidth = 49;
		int newHeight = 49;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		
		Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, 
				height, 
				matrix,
				true);
		canvas.drawBitmap(newbm,startX,startX,null);
		
		return returnBitmap;
	}
	
	private void drawCircleImage(Canvas c) {
		Bitmap bitmapimg = BitmapFactory.decodeResource(getResources(), R.drawable.a);
		
		Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                bitmapimg.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmapimg.getWidth() / 2,
                bitmapimg.getHeight() / 2, bitmapimg.getWidth() / 4, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
		
        
        c.drawBitmap(output, 10,10,null);
	}
	
	private Bitmap getJbtyPopupJoinBitmap(String text,int rightIconResId) {
		if(text.length()>4) {
			text = text.substring(0, 4)+"...";
		}
		
		Paint mPaint= new Paint();
		mPaint.setColor(Color.parseColor("#000000"));
		mPaint.setTextSize(35);
		mPaint.setAntiAlias(true);
//		mPaint.setStrokeWidth(5);
//		mPaint.setStrokeCap(Paint.Cap.ROUND);
//		mPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));
		// ...
		float w = mPaint.measureText(text, 0, text.length());

		Drawable drawable1 = getResources().getDrawable(rightIconResId);
		int rightDrawableWidth = drawable1.getIntrinsicWidth();
		int rightDrawableHeight = drawable1.getIntrinsicHeight();
		

		int bitmapHeight = 70;
		int textAndIconGap = 25; 
		int bitmapWidth = (int) (w+rightDrawableWidth+textAndIconGap*2);
		
		Bitmap returnBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_4444);
		Canvas canvas = new Canvas(returnBitmap);
		Drawable drawable = getResources().getDrawable(R.drawable.popup_jbty);
		Rect bounds= new Rect(0, 0, bitmapWidth, bitmapHeight);
		drawable.setBounds(bounds);
		drawable.draw(canvas);
		
		
		canvas.drawText(text, 10, bitmapHeight/2, mPaint);
		int hiIconMarginTop = 5;
		Rect bounds1= new Rect(bitmapWidth-rightDrawableWidth-textAndIconGap, hiIconMarginTop, bitmapWidth-textAndIconGap, rightDrawableHeight+hiIconMarginTop);
		drawable1.setBounds(bounds1);
		drawable1.draw(canvas);
		
		
		return returnBitmap;
	}
}
