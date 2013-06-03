package test.photo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.hongyunbasic.R;

public class ChoosePhotoFromAlbumOrCamera extends Activity {

	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;

	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;

	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/fkkk/aa");

	private File mCurrentPhotoFile;// 照相机拍照得到的图片

	private ImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_choose_photo_from_album_or_camera);

		image = (ImageView) findViewById(R.id.imageView1);
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doPickPhotoAction();
			}
		});
	}

	private void doPickPhotoAction() {
		Context context = ChoosePhotoFromAlbumOrCamera.this;

		// Wrap our context to inflate list items using correct theme
		final Context dialogContext = new ContextThemeWrapper(context,
				android.R.style.Theme_Light);
		String cancel = "返回";
		String[] choices;
		choices = new String[2];
		choices[0] = "拍照"; // 拍照
		choices[1] = "从相册中选择"; // 从相册中选择
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle("NMM");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0: {
							String status = Environment
									.getExternalStorageState();
							if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
								doTakePhoto();// 用户点击了从照相机获取
							} else {
								showToast("没有SD卡");
							}
							break;

						}
						case 1:
							doPickPhotoFromGallery();// 从相册中去获取
							break;
						}
					}
				});
		builder.setNegativeButton(cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		builder.create().show();
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	protected void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
			PHOTO_DIR.mkdirs();// 创建照片的存储目录
			//=======================================================================
			//为什么使用下面的getPhotoFileName() 返回的日期文件名称 会导致拍完照后，activity不会finish
			//=======================================================================
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "照相机的intent没找到@！！", Toast.LENGTH_LONG).show();
		}
	}

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	/**
	 * 用当前时间给取得的图片命名
	 * =======================================================================
	 */
	private String getPhotoFileName() {
		if(true)
			return "abc.png";
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyy-MM-dd HH:mm:ss");
		String string = dateFormat.format(date) + ".jpg";
		return string;
	}

	// 请求Gallery程序
	protected void doPickPhotoFromGallery() {
		try {
			PHOTO_DIR.mkdirs();// 创建照片的存储目录
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			showToast("Gallery程序没找到！！");
		}
	}

	// 封装请求Gallery的intent
	public  Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
		intent.setType("image/*");
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
//		intent.putExtra("outputX", 80);
//		intent.putExtra("outputY", 80);
//		intent.putExtra("return-data", true);
		return intent;
	}

	// 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
			Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoFile.getAbsolutePath());
			final Bitmap photo = data.getParcelableExtra("data");
			// 下面就是显示照片了
			System.out.println(photo);
			Drawable drawable = new BitmapDrawable(photo);
			// 缓存用户选择的图片
			// img = getBitmapByte(photo);
			// mEditor.setPhotoBitmap(photo);
			image.setImageDrawable(drawable);
			System.out.println("set new photo");
			break;
		}
		case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			doCropPhoto(mCurrentPhotoFile);
			break;
		}
		}
	}

	protected void doCropPhoto(File f) {
		try {
			// 启动gallery去剪辑这个照片
			final Intent intent = getCropImageIntent(Uri.fromFile(f));
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (Exception e) {
			Toast.makeText(this, "启动gallery去剪辑这个照片 出错", Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * Constructs an intent for image cropping. 调用图片剪辑程序
	 */
	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		return intent;
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
}
