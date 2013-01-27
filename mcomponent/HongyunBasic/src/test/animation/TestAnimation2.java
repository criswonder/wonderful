package test.animation;

import com.example.hongyunbasic.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TestAnimation2 extends Activity implements OnClickListener {
	private static final String TAG = "mao";
	private static int fileIndex = 1;
	private RelativeLayout mContainer;
	private ViewGroup mContainer1;
	private ViewGroup mContainer2;
	private Context context;
	List<Bitmap> bitmaps = new ArrayList<Bitmap>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_form_animation);

		FrameLayout fl = (FrameLayout) findViewById(R.id.layout_container);
		int total = fl.getChildCount();

		for (int i = 0; i < total - 2; i++) {
			mContainer = (RelativeLayout) fl.getChildAt(i);
			Bitmap b = loadBitmapFromView(mContainer);
			bitmaps.add(b);

			saveBitmap(b);
		}
		mContainer = (RelativeLayout) findViewById(R.id.layout_leftest);
		mContainer1 = (ViewGroup) findViewById(R.id.layout_rightest);
		mContainer1 = (ViewGroup) findViewById(R.id.layout_rightest);
		mContainer2 = (ViewGroup) findViewById(R.id.layout_1);

		Button btn = (Button) findViewById(R.id.Button);
		Button btn2 = (Button) findViewById(R.id.Button21);
		Button btn3 = (Button) findViewById(R.id.Button__layout_1);
		btn.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		context = this;
	}

	public void onClick(View v) {
		/*
		 * Rotate3dAnimation rotation = new Rotate3dAnimation(90, 180, centerX,
		 * centerY, 310.0f, false);
		 * 
		 * Animation anim = AnimationUtils.loadAnimation(this, R.anim.myanim);
		 * findViewById(R.id.layout).startAnimation(anim);
		 */
		if (v.getId() == R.id.Button21) {
			applyRotationToRight(10, 0, -60);
		} else if (v.getId() == R.id.Button) {
			applyRotation(0, 0, 60);

		} else if (v.getId() == R.id.Button__layout_1) {
			applyRotation(1, 0, 60);
		}
	}

	private void applyRotation(int position, float start, float end) {
		// Find the center of the container
		final float centerX = mContainer.getWidth() / 2.0f;
		final float centerY = mContainer.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		AnimationSet set = new AnimationSet(false);

		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
				centerX, centerY, 310.0f, true, true);
		rotation.setDuration(1000);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		// d当animation结束，或者重复的时候listener会得到通知
		rotation.setAnimationListener(new DisplayNextView(position));

		Animation anim = AnimationUtils.loadAnimation(this, R.anim.myanim);
		// Animation anim3 = AnimationUtils.loadAnimation(this, R.anim.myanim3);
		anim.setFillAfter(true);
		// anim3.setFillAfter(true);

		anim.setStartOffset(1100);
		// anim3.setStartOffset(1100 + anim.getDuration());

		// set.addAnimation(anim);
		set.addAnimation(rotation);
		// set.addAnimation(anim3);
		set.setFillAfter(true);

		if (position == 0) {
			mContainer.startAnimation(set);
			mContainer.setAnimationCacheEnabled(true);
		}
		if (position == 1) {
			mContainer2.startAnimation(set);
			mContainer2.setAnimationCacheEnabled(true);
		}
	}

	/**
	 * 到右边的动画
	 * 
	 * @param position
	 * @param start
	 * @param end
	 */
	private void applyRotationToRight(int position, float start, float end) {
		// Find the center of the container
		final float centerX = mContainer1.getWidth() / 2.0f;
		final float centerY = mContainer1.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		AnimationSet set = new AnimationSet(false);

		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
				centerX, centerY, 310.0f, true, false);
		rotation.setDuration(800);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		// d当animation结束，或者重复的时候listener会得到通知
		rotation.setAnimationListener(new DisplayNextView(position));

		Animation anim = AnimationUtils.loadAnimation(this, R.anim.myanim1);
		anim.setFillAfter(true);

		anim.setStartOffset(900);
		set.addAnimation(rotation);
		set.addAnimation(anim);
		set.setFillAfter(true);

		mContainer1.startAnimation(set);
	}

	private final class DisplayNextView implements Animation.AnimationListener {
		private final int mPosition;

		private DisplayNextView(int position) {
			mPosition = position;
		}

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			mContainer = (RelativeLayout) findViewById(R.id.layout_leftest);
			mContainer.post(new SwapViews(mPosition));
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	/**
	 * This class is responsible for swapping the views and start the second
	 * half of the animation.
	 */
	private final class SwapViews implements Runnable {
		private final int mPosition;

		public SwapViews(int position) {
			mPosition = position;
		}

		public void run() {
			final float centerX = mContainer.getWidth() / 2.0f;
			final float centerY = mContainer.getHeight() / 2.0f;
			// Rotate3dAnimation rotation;
			//
			// rotation.setDuration(500);
			// rotation.setFillAfter(true);
			// rotation.setInterpolator(new DecelerateInterpolator());
			//
			// mContainer.startAnimation(rotation);
			// mContainer.clearAnimation();
			// Animation anim = AnimationUtils.loadAnimation(context,
			// R.anim.myanim);
			// anim.setFillAfter(true);
			// mContainer.startAnimation(anim);
			if (mPosition > 0) {

			} else {
				// RelativeLayout.LayoutParams params = new
				// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				// RelativeLayout.LayoutParams.WRAP_CONTENT);
				// params.addRule(RelativeLayout.CENTER_IN_PARENT,
				// RelativeLayout.TRUE);
				// mContainer1.setLayoutParams(params);

				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				mContainer.setLayoutParams(params);

				// try {
				// Thread.sleep(2000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

				// mContainer = (ViewGroup)findViewById(R.id.layout_leftest);
				// mContainer.getAnimation().setZAdjustment(Animation.ZORDER_NORMAL);
				// Animation anim = AnimationUtils.loadAnimation(context,
				// R.anim.myanim);
				// anim.setFillAfter(true);
				// mContainer.startAnimation(anim);

			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// Bitmap b = loadBitmapFromView(mContainer);
		// Bitmap b =
		// loadBitmapFromView(createProgrammeView(this,200,200,"title","time"));
		/*
		 * Handler handler = new Handler(); handler.postDelayed(new Runnable() {
		 * 
		 * public void run() { // TODO Auto-generated method stub ImageView
		 * image = (ImageView) findViewById(R.id.image);
		 * image.setBackgroundColor(R.drawable.bule); for(Bitmap z :bitmaps){
		 * image.setImageBitmap(z); image.invalidate(); try {
		 * Thread.sleep(1000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } } } }, 2000);
		 */

	}

	public void saveBitmap(Bitmap bm) {
		try {
			String mBaseFolderPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/";
			String mFilePath = mBaseFolderPath + fileIndex + "abcd.jpg";
			fileIndex++;
			File file = new File(mFilePath);
			if (!file.exists())
				file.createNewFile();
			FileOutputStream stream = new FileOutputStream(mFilePath);
			bm.compress(CompressFormat.JPEG, 100, stream);
			stream.flush();
			stream.close();
		} catch (Exception e) {
			Log.e("Could not save", e.toString());
		}
	}

	public static RelativeLayout createProgrammeView(Context context,
			int width, int height, String title, String time) {
		RelativeLayout.LayoutParams params;

		// Layout Root View (RelativeLayout)
		RelativeLayout rlv = new RelativeLayout(context);
		params = new RelativeLayout.LayoutParams(width, height);
		rlv.setLayoutParams(params);
		rlv.setPadding(3, 3, 3, 3);
		rlv.setBackgroundResource(R.drawable.bule);

		// Layout Title
		TextView tv = new TextView(context);
		params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		tv.setId(100);
		tv.setLayoutParams(params);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setSingleLine(true);
		tv.setEllipsize(TruncateAt.END);
		tv.setTextColor(R.drawable.white);
		tv.setTextSize(11);
		tv.setText(title);
		rlv.addView(tv);

		// Layout Start Time
		tv = new TextView(context);
		params = new RelativeLayout.LayoutParams(16,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, 100);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.setMargins(0, 4, 0, 0);
		tv.setId(101);
		tv.setLayoutParams(params);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setSingleLine(true);
		tv.setEllipsize(null);
		tv.setTextColor(Color.parseColor("#FFFFFFFF"));
		tv.setTextSize(10);
		tv.setText(time);
		rlv.addView(tv);

		return rlv;
	}

	public static Bitmap loadBitmapFromView(RelativeLayout v) {
		Log.d(TAG,
				"--------------------------------------v.getHeight()="
						+ v.getHeight() + ",v.getWidth()=" + v.getWidth()
						+ "-----------------------");
		v.measure(MeasureSpec.makeMeasureSpec(v.getLayoutParams().width,
				MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
				v.getLayoutParams().height, MeasureSpec.EXACTLY));
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		Bitmap b = Bitmap.createBitmap(v.getHeight(), v.getWidth(),
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.draw(c);
		return b;
	}
}