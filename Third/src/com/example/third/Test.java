package com.example.third;

import com.example.third.CustomeScrollView.Direction;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class Test extends Activity {

	Pager view2;

	Button btn;

	protected boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		testHorizontalScrollView();
//		testCustomeScrollView();
		testScrollView();
	}

	private void testScrollView() {
		setContentView(R.layout.activity_main3);
		final LinearLayout view2 = (LinearLayout)findViewById(R.id.page2);
		final View view1 = findViewById(R.id.page1);
		final View view3 = findViewById(R.id.page3);
		final View main=findViewById(R.id.main);
		final boolean toRight = true;
		final boolean bs[]={true};
		view2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(bs[0]){
				view2.scrollTo(-200, 0);
				view1.bringToFront();
				bs[0]= false;
				}else{
					
					view2.scrollTo(0, 0);
					view2.bringToFront();
					bs[0]= true;
				}
			}
		});
		
	}

	private void testHorizontalScrollView() {
		setContentView(R.layout.activity_main1);
		view2 = (Pager) findViewById(R.id.secondlayout1);
		view2.setVisibility(View.GONE);
		HorizontalScrollView hs= new HorizontalScrollView(this);
		
		/*btn = (Button) findViewById(R.id.btn);
		flag = true;
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flag) {

					view2.setVisibility(View.VISIBLE);
					flag = false;
				} else {
					view2.setVisibility(View.GONE);
					flag = true;
				}
			}
		});*/
	}
	
	Button left ;
	Button right ;
	
	private void testCustomeScrollView() {
		setContentView(R.layout.activity_customescrollview_test);
		final CustomeScrollView cus = (CustomeScrollView)findViewById(R.id.my_cust_view);
		left = (Button) findViewById(R.id.btn_left);
		right = (Button)findViewById(R.id.btn_right);
		
		left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cus.moveTo(Direction.LEFT, 400);
			}
		});
		
		right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cus.moveTo(Direction.RIGHT, 400);
				right.scrollTo(-10, 0);
			}
		});
		
	}
}
