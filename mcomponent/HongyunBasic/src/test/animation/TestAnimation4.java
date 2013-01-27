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
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 使用自己的控件
 * 
 * @author armstrong
 * 
 */
public class TestAnimation4 extends Activity implements OnClickListener {
	private static final String TAG = "mao";
	private static final boolean hasOrder = true;
	private static int fileIndex = 1;
	/** 用來存放中間layout的id, 初始化的時候不會用到。 **/
	private static int mMiddleId = -1;
	private static int startIndex = 0;
	private static int max_distance = 200;
	private static int mutex_distance = 15;
	private RelativeLayout mContainer;
	private ViewGroup mContainer1;
	private ViewGroup mContainer2;
	private Context context;
	List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	/** 包装要滑动的layout的container **/
	FrameLayout fl = null;
	private int fl_len;
	/** 存放左边layout **/
	private Stack<Integer> mlstack;
	/** 存放右边layout **/
	private Stack<Integer> mrstack;
	/** 存放各个layout移动的信息，位置信息等 **/
	private Map<Integer, Map> mMap;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_form_animation_custome_control);

		if (mlstack == null)
			mlstack = new Stack<Integer>();
		if (mrstack == null)
			mrstack = new Stack<Integer>();
		if (mMap == null)
			mMap = new HashMap<Integer, Map>();
		mMiddleId = -1;
		startIndex = 0;
		fl = (FrameLayout) findViewById(R.id.layout_container);
		fl_len = fl.getChildCount();

		Button leftControlBtn = (Button) findViewById(R.id.button_left);
		Button rControlBtn = (Button) findViewById(R.id.button_right);
		leftControlBtn.setOnClickListener(this);
		rControlBtn.setOnClickListener(this);
		context = this;

		setLayoutVisibility();
	}

	private void setLayoutVisibility() {
		if (startIndex > fl_len - 1) {
			startIndex++;
		} else {
			fl.getChildAt(startIndex).setVisibility(View.VISIBLE);
			startIndex++;
		}
	}

	public void onClick(View v) {
		max_distance = fl.getWidth() / 2 - fl.getChildAt(0).getWidth() / 2 - 10;
		if (v.getId() == R.id.button_left) {
			if (mMiddleId > 0) {
				applyRotation(mMiddleId, 1, 0, 60);
				mMiddleId = -1;
			} else {
				if (startIndex > fl_len) {
					// fl 裏面的layout都被分配到了兩邊，那麼右边的stack顶上的元素要移动到中间来
					if (mrstack.size() > 0) {
						Integer rightTopId = mrstack.pop();
						if (rightTopId != null) {
							View layout = findViewById(rightTopId);
							animateFromRight2Center(layout);
							printflzChildrenOrder();
							layout.bringToFront();
							startIndex--;
							mMiddleId = rightTopId;
						}
					}
				} else {
					applyRotation(-1, 1, 0, 60);
				}
			}

		} else if (v.getId() == R.id.button_right) {
			if (mMiddleId > 0) {
				applyRotationToRight(mMiddleId, 10, 0, -60);
				mMiddleId = -1;
			} else {
				if (startIndex > fl_len) {
					if (mlstack.size() > 0) {
						Integer leftTopId = mlstack.pop();
						startIndex--;
						if (leftTopId != null) {
							View layout = findViewById(leftTopId);
							layout.bringToFront();
							printflzChildrenOrder();
							animateFromRight2Center(layout);
							mMiddleId = leftTopId;
						}
					}
				} else {
					applyRotationToRight(-1, 10, 0, -60);
				}
			}

		}
	}

	private void printflzChildrenOrder() {
		for (int i = 0; i < fl_len; i++)
			Log.d(TAG, fl.getChildAt(i).getTag() + "");
	}

	/**
	 * 把这个view 从右边移动到中间。
	 * 
	 * @param findViewById
	 */
	private void animateFromRight2Center(View findViewById) {
		final float centerP = fl.getWidth() / 2.0f;
		Log.d(TAG, "------------------------" + findViewById.getWidth() + ","
				+ findViewById.getHeight() + "---------------------------");
		final float centerX = findViewById.getWidth() / 2.0f;
		final float centerY = findViewById.getHeight() / 2.0f;
		Map map = mMap.get(findViewById.getId());
		float dist = (Float) map.get(LayoutMetaData.MOVE_DIS);
		AnimationSet set = new AnimationSet(false);
		// 1 旋转
		Animation rotation = getRotateAnimation(-60, 0, centerX, centerY,
				310.0f, true, false);
		rotation.setStartOffset(0);
		rotation.setDuration(3000);
		// 2 平移
		Animation anim = null;
		anim = getTranslateAnimation(0, -dist, 0, 0);
		anim.setStartOffset(2100);
		anim.setDuration(2000);
		// 3 放大
		Animation animScale = getScaleAnimation(0.6f);
		// 4 alpha 改变
		Animation alpha = new AlphaAnimation(0.1f, 1.0f);
		alpha.setInterpolator(new AccelerateDecelerateInterpolator());
		alpha.setDuration(1000);

		set.setFillAfter(true);
		// set.addAnimation(rotation);
		// set.addAnimation(animScale);
		// set.addAnimation(anim);
		set.addAnimation(alpha);
		findViewById.startAnimation(set);
	}

	/**
	 * 1 先缩放 2绕Y轴旋转 3 平移
	 * 
	 * @param position
	 * @param start
	 * @param end
	 */
	private void applyRotation(int objId, int position, float start, float end) {
		if (objId > 0) {
			mContainer = (RelativeLayout) findViewById(objId);
		} else {
			mContainer = (RelativeLayout) fl.getChildAt(startIndex - 1);
		}
		float distance = max_distance - mlstack.size() * mutex_distance;
		int id = mContainer.getId();
		mlstack.add(id);
		if (mMap.containsKey(id)) {

		} else {
			Map map = new HashMap();
			map.put(LayoutMetaData.MOVE_DIS, distance);
			mMap.put(id, map);
		}
		Log.d(TAG, mContainer.getTag() + "");
		// Find the center of the container
		final float centerX = mContainer.getWidth() / 2.0f;
		final float centerY = mContainer.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		AnimationSet set = new AnimationSet(false);
		// --------------------------------------------------------------------------------------------------------------------------------------------------------------->
		// <--------------------------------------------------------------------------------------------------------------------------------------------------------------
		Animation animScale = getScaleAnimation(0.6f);

		// --------------------------------------------------------------------------------------------------------------------------------------------------------------->
		// <--------------------------------------------------------------------------------------------------------------------------------------------------------------
		Animation rotation = getRotateAnimation(start, end, centerX, centerY,
				310.0f, true, true);
		// anim3.setStartOffset(1100 + anim.getDuration());
		rotation.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				// try {
				// Thread.sleep(800);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				setLayoutVisibility();
			}
		});

		// --------------------------------------------------------------------------------------------------------------------------------------------------------------->
		// <--------------------------------------------------------------------------------------------------------------------------------------------------------------
		Animation anim = null;

		anim = getTranslateAnimation(0, -distance, 0, 0);
		anim.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {

			}
		});

		set.setFillAfter(true);
		set.addAnimation(rotation);
		// set.addAnimation(animScale);
		set.addAnimation(anim);
		mContainer.startAnimation(set);
		mContainer.setAnimationCacheEnabled(true);
		// if (position == 0) {
		// anim = getTranslateAnimation(0, -170, 0, 0);
		// set.addAnimation(anim);
		// mContainer.startAnimation(set);
		// mContainer.setAnimationCacheEnabled(true);
		// }
		// if (position == 1) {
		// anim = getTranslateAnimation(0, -190, 0, 0);
		// set.addAnimation(anim);
		// mContainer2.startAnimation(set);
		// mContainer2.setAnimationCacheEnabled(true);
		// }
	}

	/**
	 * 到右边的动画
	 * 
	 * @param position
	 * @param start
	 * @param end
	 */
	private void applyRotationToRight(int objId, int position, float start,
			float end) {
		// Find the center of the container
		if (objId > 0) {
			mContainer1 = (RelativeLayout) findViewById(objId);
		} else {
			mContainer1 = (RelativeLayout) fl.getChildAt(startIndex - 1);
		}
		int id = mContainer1.getId();
		mrstack.add(id);

		float distance = max_distance - mrstack.size() * mutex_distance;
		if (mMap.containsKey(id)) {

		} else {
			Map map = new HashMap();
			map.put(LayoutMetaData.MOVE_DIS, distance);
			mMap.put(id, map);
		}
		Log.d(TAG, "------------------------" + mContainer1.getWidth() + ","
				+ mContainer1.getHeight() + "---------------------------");
		final float centerX = mContainer1.getWidth() / 2.0f;
		final float centerY = mContainer1.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		AnimationSet set = new AnimationSet(false);

		// 1 Animation animScale = getScaleAnimation();
		Animation animScale = getScaleAnimation(0.6f);
		animScale.setFillAfter(true);

		// 2
		Animation rotation = getRotateAnimation(start, end, centerX, centerY,
				310.0f, true, false);
		rotation.setFillAfter(true);
		// 3
		Animation anim = null;
		anim = getTranslateAnimation(0, distance, 0, 0);
		anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				setLayoutVisibility();
				// mContainer1.requestLayout();
				// Log.d(TAG,
				// "------------------------"+mContainer1.getWidth()+","+mContainer1.getHeight()
				// +"---------------------------");
			}
		});

		set.setFillAfter(true);
		set.addAnimation(rotation);
		// set.addAnimation(animScale);
		set.addAnimation(anim);

		mContainer1.startAnimation(set);
		mContainer1.setAnimationCacheEnabled(true);
	}

	private Animation getScaleAnimation(float zoomFactor) {
		Animation animScale = new ScaleAnimation(1.0f, zoomFactor, 1.0f,
				zoomFactor, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		if (hasOrder) {
			animScale.setStartOffset(0);
			animScale.setDuration(800);
		}
		return animScale;
	}

	private Animation getRotateAnimation(float fromDegrees, float toDegrees,
			float centerX, float centerY, float depthZ, boolean reverse,
			boolean left) {
		final Rotate3dAnimation rotation = new Rotate3dAnimation(fromDegrees,
				toDegrees, centerX, centerY, depthZ, reverse, left);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		// d当animation结束，或者重复的时候listener会得到通知
		// rotation.setAnimationListener(new DisplayNextView(position));
		if (hasOrder) {
			rotation.setDuration(900);
			rotation.setStartOffset(900);
		}
		return rotation;
	}

	private Animation getTranslateAnimation(float fromXDelta, float toXDelta,
			float fromYDelta, float toYDelta) {
		Animation anim = new TranslateAnimation(fromXDelta, toXDelta,
				fromYDelta, toYDelta);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());

		// Animation anim3 = AnimationUtils.loadAnimation(this, R.anim.myanim3);
		anim.setFillAfter(true);
		// anim3.setFillAfter(true);
		if (hasOrder) {
			anim.setStartOffset(0);
			anim.setDuration(800);

		}
		return anim;
	}

	private final class DisplayNextView implements Animation.AnimationListener {
		private final int mPosition;

		private DisplayNextView(int position) {
			mPosition = position;
		}

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			// animation.cancel();
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