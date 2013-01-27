package com.homer.mycontrol;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final PagerBar control = (PagerBar) findViewById(R.id.control);		// 自定义Pager
		final Pager pager = (Pager) findViewById(R.id.pager);	// 自定义Pager
		control.setNumPages(pager.getChildCount());		// 设置control的Pager页数

		// HorizontalPager 自定义控件监听事件
		pager.addOnScrollListener(new Pager.OnScrollListener() {
			@Override
			public void onScroll(int scrollX) {
				float scale = (float) (pager.getPageWidth() * pager.getChildCount()) / (float) control.getWidth();
				control.setPosition((int) (scrollX / scale));
			}
			@Override
			public void onViewScrollFinished(int currentPage) {
				control.setCurrentPage(currentPage);
			}
		});

		Button b = (Button) findViewById(R.id.left);		// 向左按钮
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				pager.scrollLeft();
			}
		});
		b = (Button) findViewById(R.id.right);				// 向右按钮
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				pager.scrollRight();
			}
		});

	}
}
