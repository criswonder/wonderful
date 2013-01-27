package com.example.hongyunbasic;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class HelloAndroid extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		CoverFlow cf = new CoverFlow(this);
		cf.setBackgroundResource(R.drawable.shape);
		cf.setAdapter(new ImageAdapter(this));
		ImageAdapter imageAdapter = new ImageAdapter(this);
		cf.setAdapter(imageAdapter);
		// cf.setAlphaMode(false);
		// cf.setCircleMode(false);
		// cf.setSelection(3, true);
		cf.setAnimationDuration(1000);
		setContentView(cf);
	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;
		private Integer[] mImageIds = { R.drawable.a, R.drawable.b,
				R.drawable.d, R.drawable.e, R.drawable.c };

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ReflectionImage i = new ReflectionImage(mContext);

			i.setImageResource(mImageIds[position]);
			i.setLayoutParams(new CoverFlow.LayoutParams(96, 76));
			i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// Make sure we set anti-aliasing otherwise we get jaggies
			BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
			drawable.setAntiAlias(true);
			return i;
		}

		public float getScale(boolean focused, int offset) {
			return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
		}
	}
}