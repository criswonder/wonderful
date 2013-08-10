package test.galaxys4.popupdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;

import com.example.hongyunbasic.R;

public class TestLayout extends Activity implements OnClickListener {
	private AlertDialog mDialog;
	private View mSpenDialogCommentBtn;
	private View mSpenDialogFavBtn;
	private View mSpenDialogLeftBtn;
	private View mSpenDialogRightBtn;
	private View mSpenDialogWangWangBtn;
	private Activity mContext;

	private Gallery mGallery;
	private View ll_bottom_buttons;
	private TextView tv_numbers;

	private Animation mHideAnimation;

	private Animation mShowAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_testlayout);
		Button btn = (Button) findViewById(R.id.btn1);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSpenDialog();
			}
		});

		mHideAnimation = AnimationUtils.loadAnimation(this,
				R.anim.spen_btns_down_out);
		mShowAnimation = AnimationUtils.loadAnimation(this,
				R.anim.spen_btns_down_in);
		mShowAnimation.setAnimationListener(animationListener);
		mHideAnimation.setAnimationListener(animationListener);
	}

	public void showSpenDialog() {
		if (mDialog == null) {
			AlertDialog.Builder builder = new Builder(mContext);
			mDialog = builder.create();
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.show();
			WindowManager.LayoutParams params = mDialog.getWindow()
					.getAttributes();
			params.gravity = Gravity.CENTER;
			params.width = WindowManager.LayoutParams.FILL_PARENT;
			mDialog.getWindow().setAttributes(params);
			View cv = View.inflate(mContext.getApplicationContext(),
					R.layout.dialog_spen_goods_detail1, null);
			mDialog.getWindow().setContentView(cv);

			// mSpenDialogImage = (ImageView) mDialog.getWindow().findViewById(
			// R.id.imageView1);
			// TaoLog.Logd(TAG_SPEN, "image url=" + mSearchObj.picUrl);
			// String url = TaoToolBox.picUrlProcess(mSearchObj.picUrl,
			// TBImageQuailtyStrategy.CDN_SIZE_320);
			// TaoLog.Logd(TAG_SPEN, "after picUrlProcess image url=" + url);
			// mImagePoolBinder.setImageDrawable(url, mSpenDialogImage);
			// mSpenDialogImage.setOnClickListener(clickListenerForSpenDialog);
			mGallery = (Gallery) mDialog.getWindow().findViewById(
					R.id.big_gallery);
			tv_numbers = (TextView) mDialog.getWindow().findViewById(
					R.id.tv_numbers);
			mSpenDialogCommentBtn = mDialog.getWindow().findViewById(
					R.id.btn_comment);
			ll_bottom_buttons = mDialog.getWindow().findViewById(
					R.id.ll_bottom_buttons);
			mSpenDialogLeftBtn = mDialog.getWindow()
					.findViewById(R.id.btn_left);
			mSpenDialogRightBtn = mDialog.getWindow().findViewById(
					R.id.btn_right);
			mSpenDialogFavBtn = mDialog.getWindow().findViewById(R.id.btn_fav);
			mSpenDialogWangWangBtn = mDialog.getWindow().findViewById(
					R.id.btn_wangwang);

			mSpenDialogLeftBtn.setOnClickListener(this);
			mSpenDialogRightBtn.setOnClickListener(this);

			// mDialog.getWindow().getDecorView()
			// .setOnTouchListener(spenTouchListener);
			// mSpenDialogFavBtn.setOnTouchListener(spenTouchListener);
			// mSpenDialogCommentBtn.setOnTouchListener(spenTouchListener);
			// mSpenDialogWangWangBtn.setOnTouchListener(spenTouchListener);
			// mGallery.setOnTouchListener(spenTouchListener);
			// mSpenDialogLeftBtn.setOnTouchListener(spenTouchListener);
			// mSpenDialogRightBtn.setOnTouchListener(spenTouchListener);
			// tv_numbers.setOnTouchListener(spenTouchListener);

			// register listener
			// mSpenDialogCommentBtn
			// .setOnClickListener(clickListenerForSpenDialog);
			// mSpenDialogFavBtn.setOnClickListener(clickListenerForSpenDialog);
			// mSpenDialogLeftBtn.setOnClickListener(clickListenerForSpenDialog);
			// mSpenDialogRightBtn.setOnClickListener(clickListenerForSpenDialog);
			// mSpenDialogWangWangBtn
			// .setOnClickListener(clickListenerForSpenDialog);

		} else {
			// String url = TaoToolBox.picUrlProcess(mSearchObj.picUrl,
			// TBImageQuailtyStrategy.CDN_SIZE_430);
			// if (!mImagePoolBinder.setImageDrawable(url, mSpenDialogImage)) {
			// mSpenDialogImage.setImageResource(R.drawable.tupian_bg1);
			// }
			mDialog.show();
		}
	}

	@Override
	public void onClick(View v) {

		if (v == mSpenDialogLeftBtn) {

			if (ll_bottom_buttons != null) {
				if (ll_bottom_buttons.getVisibility() == View.VISIBLE) {
					ll_bottom_buttons.startAnimation(mHideAnimation);
				} else {
					ll_bottom_buttons.startAnimation(mShowAnimation);
				}
			}

		} else if (v == mSpenDialogRightBtn) {

		}
	}

	AnimationListener animationListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (animation == mShowAnimation) {
				ll_bottom_buttons.setVisibility(View.VISIBLE);
			} else if (animation == mHideAnimation) {
				ll_bottom_buttons.setVisibility(View.INVISIBLE);
			}
		}

		// ll_bottom_buttons.setVisibility(View.INVISIBLE);
		// } else {
		// ll_bottom_buttons.setVisibility(View.VISIBLE);
	};
}
