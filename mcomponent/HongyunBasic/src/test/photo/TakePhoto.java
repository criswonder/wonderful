package test.photo;

import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hongyunbasic.R;

/**
 * 参看sdk https://developer.android.com/training/camera/photobasics.html
 * 
 * @author Andy Mao
 * 
 */
public class TakePhoto extends Activity {

	private static final int TAKE_PICTURE = 1;
	private Uri imageUri;
	private File photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_takephoto);

		findViewById(R.id.imageView1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

				// SimpleDateFormat timeStampFormat = new SimpleDateFormat(
				// "yyyy-MM-dd HH:mm:ss");
				// String filename = timeStampFormat.format(new Date());
				photo = new File(Environment.getExternalStorageDirectory(),
						"Pic.jpg");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
				imageUri = Uri.fromFile(photo);
				startActivityForResult(intent, TAKE_PICTURE);
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = imageUri;
				getContentResolver().notifyChange(selectedImage, null);
				ImageView imageView = (ImageView) findViewById(R.id.imageView1);
				ContentResolver cr = getContentResolver();
				galleryAddPic(selectedImage);
				Bitmap bitmap;
				try {
					bitmap = android.provider.MediaStore.Images.Media
							.getBitmap(cr, selectedImage);

					imageView.setImageBitmap(bitmap);
					Toast.makeText(this, selectedImage.toString(),
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
							.show();
					Log.e("Camera", e.toString());
				}

				try {
					Log.d("andymao", photo.getAbsolutePath());
					ExifInterface exif = new ExifInterface(
							photo.getAbsolutePath());
					String latidude = exif
							.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
					String longitude = exif
							.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
					Toast.makeText(
							TakePhoto.this,
							convertToDegree(latidude) + ":"
									+ convertToDegree(longitude),
							Toast.LENGTH_SHORT).show();
					// latidude=exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
					// longitude=exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
					//
					// exif.getAttributeDouble(ExifInterface.TAG_GPS_LATITUDE,0);
					// exif.getAttributeInt(ExifInterface.TAG_GPS_LATITUDE, 0);
					// Toast.makeText(TakePhoto.this,latidude+":"+longitude,
					// Toast.LENGTH_LONG).show();
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ExifInterface.html#TAG_GPS_LATITUDE
	 * 
	 * @param stringDMS
	 *            Format is "num1/denom1,num2/denom2,num3/denom3". like:
	 *            120/1,1/1,339331/10000
	 * @return 转换成float
	 */
	private Float convertToDegree(String stringDMS) {
		Float result = null;
		String[] DMS = stringDMS.split(",", 3);

		String[] stringD = DMS[0].split("/", 2);
		Double D0 = new Double(stringD[0]);
		Double D1 = new Double(stringD[1]);
		Double FloatD = D0 / D1;

		String[] stringM = DMS[1].split("/", 2);
		Double M0 = new Double(stringM[0]);
		Double M1 = new Double(stringM[1]);
		Double FloatM = M0 / M1;

		String[] stringS = DMS[2].split("/", 2);
		Double S0 = new Double(stringS[0]);
		Double S1 = new Double(stringS[1]);
		Double FloatS = S0 / S1;

		result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

		return result;

	};

	private void galleryAddPic(Uri uri) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(uri);
		this.sendBroadcast(mediaScanIntent);
	}
}
