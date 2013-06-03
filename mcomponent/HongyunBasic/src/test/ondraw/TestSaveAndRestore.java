package test.ondraw;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.example.hongyunbasic.R;

public class TestSaveAndRestore extends Activity {
	ImageView imageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(new CustomeView(this));
		setContentView(R.layout.activity_test_save_and_restore);

		LinearLayout ll = (LinearLayout) findViewById(R.id.main_layout);
		CustomeView cust = new CustomeView(this);
		cust.setBackgroundColor(Color.parseColor("#ffffffff"));
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ll.addView(cust, params);
		

		imageView = (ImageView)findViewById(R.id.imageView1);
		ImageGetter task = new ImageGetter();
		task.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_test_save_and_restore, menu);
		return true;
	}
	
	
	class ImageGetter extends AsyncTask<Void, Void, Void> {
		String mUrl="http://192.168.16.239/qiandh/default/201305/06/11072372184262.jpg";
		Bitmap resultBitmap;
        @Override
        protected Void doInBackground(Void... params) {

            InputStream inputStream = null;
            FileOutputStream out = null;
            try {
                String fileName =  "aa_icon";
                File picFolder = getDir("fuck", MODE_PRIVATE);
                File pic = new File(picFolder.getAbsolutePath() + File.separator + fileName);
                if (!pic.exists()) {
                    URL u = new URL(mUrl);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(8000);
                    if (conn.getResponseCode() == 200) {
                        inputStream = conn.getInputStream();
                        byte[] buffer = new byte[8192];
                        out = new FileOutputStream(pic);
                        int len;
                        while ((len = inputStream.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                        out.flush();
                    }
                } else {

                }
                inputStream = new BufferedInputStream(new FileInputStream(pic));

                int markerWidth = 50;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                // BitmapFactory.decodeFile(pic.getAbsolutePath(), options);

                int realHeight = options.outHeight;
                int realWidth = options.outWidth;
                int sampleSize = realWidth / markerWidth;
                int sampleSize1 = realHeight / markerWidth;
                sampleSize = Math.max(sampleSize, sampleSize1);
                sampleSize = Math.max(1, sampleSize);

                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                options.inInputShareable = true;
                options.inPurgeable = true;
                options.inSampleSize = sampleSize; //下载原图片的1/10

                inputStream = new BufferedInputStream(new FileInputStream(pic));
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                } catch (Exception e) {
                    System.gc();
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                }
                if (bitmap == null)
                    return null;
                 
                resultBitmap = bitmap;
//                BitmapManager.putBitmap(fileName, resultBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (out != null)
                        out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;

        

        }

        @Override
        protected void onPostExecute(Void result) {
            if (resultBitmap != null) {
            	imageView.setImageBitmap(resultBitmap);
            }
            super.onPostExecute(result);
        }

    }

}
