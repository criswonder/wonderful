package com.example.third;

import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import com.example.*;

public class TestDatePicker extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testdatepicker);

		DatePicker picker = (DatePicker) findViewById(R.id.datePicker1);
		try {
			Field f[] = picker.getClass().getDeclaredFields();
			for (Field field : f) {
				if (field.getName().equals("mYearPicker")) {
					field.setAccessible(true);
					Object yearPicker = new Object();
					yearPicker = field.get(picker);
					((View) yearPicker).setVisibility(View.GONE);
				}
			}
		} catch (SecurityException e) {
			Log.d("ERROR", e.getMessage());
		} catch (IllegalArgumentException e) {
			Log.d("ERROR", e.getMessage());
		} catch (IllegalAccessException e) {
			Log.d("ERROR", e.getMessage());
		}
	}

}
