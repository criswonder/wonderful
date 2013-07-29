package com.example.testpulltorefresh;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huewu.pla.lib.MultiColumnListView;
import com.xixi.cache.ImageCache.ImageCacheParams;
import com.xixi.cache.ImageFetcher;

public class MainActivity extends Activity {
	private static final String IMAGE_CACHE_DIR = "fk";
	MultiColumnListView listView;
	private ImageFetcher mImageFetcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_sample);
		
		ImageCacheParams cacheParams = new ImageCacheParams(this, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.1f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, 200);// 初始化resouce，创建http cache dir， 以及要显示的图片大小
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        // imagecache在这一步初始化，内存和文件的cache。注意 文件cache
        // 是在异步线程上做的
        mImageFetcher.addImageCache(MyApplication.getContext(), cacheParams); 
        
		listView = (MultiColumnListView)findViewById(R.id.list);
		//listView.setSelector(null);
		MyAdapter adapter = new MyAdapter();
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ImageMock.imageThumbUrls.length;
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return ImageMock.imageThumbUrls[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView = View.inflate(MainActivity.this, R.layout.item_sample, null);
			}
			TextView tv = (TextView)convertView.findViewById(R.id.text);
			tv.setText(getItem(position));
			ImageView image =(ImageView)convertView.findViewById(R.id.thumbnail);
			image.setAdjustViewBounds(true);
			mImageFetcher.loadImage(getItem(position), image);
			return convertView;
		}
		
	}
}
