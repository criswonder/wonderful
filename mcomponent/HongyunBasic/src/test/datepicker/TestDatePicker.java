package test.datepicker;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.util.Log;
import android.widget.DatePicker;

public class TestDatePicker extends Activity {
	@Override
    protected Dialog onCreateDialog(int id) {
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        int cday = c.get(Calendar.DAY_OF_MONTH);
        switch (id) {
        case 1:
            final DatePickerDialog datePicker = new DatePickerDialog(this,
                    mDateSetListener, cyear, cmonth, cday);
            datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE,
                    "设定", datePicker);
            datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE,
                    "取消", datePicker);           
            return datePicker;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
        	Log.d("test", ""+year+"/"+monthOfYear+"/"+dayOfMonth);
        }
    };
    
    
    protected void onCreate(android.os.Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    };
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	showDialog(1);
    }
}
