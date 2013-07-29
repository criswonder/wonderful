package test.wheel;

import test.wheel.adapter.NumericWheelAdapter;
import android.app.Activity;
import android.os.Bundle;

import com.example.hongyunbasic.R;

public class TestWheelView extends Activity {
	NumericWheelAdapter mSSYDJ1Adapter;//一排球的
	int mCountSSYDJ_1 = 2;
	int mMinCountSSYDJ = 1;
	int mMaxCountSSYDJ = 10;
	
	boolean mSSYDJ1Scrolled = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_wheelview);
		
		 WheelView zWheel = (WheelView)findViewById(R.id.ssydj1_wheel);
         mSSYDJ1Adapter = new NumericWheelAdapter(this, 10, 12);
         mSSYDJ1Adapter.setCurrentTextColor(0xffb22727);
         zWheel.setViewAdapter(mSSYDJ1Adapter);
         zWheel.setCurrentItem(1);
         zWheel.addChangingListener(mSSYDJChangedListener);
         zWheel.addScrollingListener(mSSYDJScrolledListener);     
	}
	
	 private OnWheelChangedListener mSSYDJChangedListener = new OnWheelChangedListener() {
	        @Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
	        	
	        	if(wheel.getId() == R.id.ssydj1_wheel)
	        	{
	        		mCountSSYDJ_1 = newValue + mMinCountSSYDJ;
	        	}
	        }
	    };
	    
	    
	    OnWheelScrollListener mSSYDJScrolledListener = new OnWheelScrollListener() {
	        @Override
			public void onScrollingStarted(WheelView wheel) {
	        	 
	        }
	        @Override
			public void onScrollingFinished(WheelView wheel) {
	        	 
	        	if(wheel.getId() == R.id.ssydj1_wheel)
	        	{
	        		mSSYDJ1Scrolled = false;
	        	}
	        }
	    };
	    
}
