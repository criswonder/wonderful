
package test.wheel.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter extends AbstractWheelTextAdapter {
	private final String TAG=this.getClass().getSimpleName();
    /** The default min value */
    public static final int DEFAULT_MAX_VALUE = 9;

    /** The default max value */
    private static final int DEFAULT_MIN_VALUE = 0;
    
    
    /** Default text color */
    public static final int DEFAULT_TEXT_COLOR = 0xFF7c7c7c;
    /** Default text color */
    public static final int CURRENT_TEXT_COLOR = 0xFF7c7c7c;
    // Values
    private int minValue;
    private int maxValue;
    
    // format
    private String format;
    
    //bylc
    public int currentItem;
    
    private int current_text_color = CURRENT_TEXT_COLOR;
    
    /**
     * Constructor
     * @param context the current context
     */
    public NumericWheelAdapter(Context context) {
        this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    /**
     * Constructor
     * @param context the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue) {
        this(context, minValue, maxValue, null);
    }

    /**
     * Constructor
     * @param context the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format the format string
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue, String format) {
        super(context);
        
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index;
            return format != null ? String.format(format, value) : Integer.toString(value);
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return maxValue - minValue + 1;
    }    
    
    @Override
	public void chageItemAppearance(View v, boolean isCurrent)
    {
		//float density = context.getResources().getDisplayMetrics().density;
        //int textSize = (int)(DEFAULT_TEXT_SIZE * density);
    	TextView tv = (TextView)v;
    	if(isCurrent)
    	{
    		tv.setTextColor(current_text_color);
    		//tv.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
    		//tv.setTextSize( (float) (textSize*1.2) );
    	}
    	else
    	{
    		tv.setTextColor(DEFAULT_TEXT_COLOR);
    		//tv.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
    		//tv.setTextSize( textSize );
    	}	
    }
    
    public void setCurrentTextColor(int color)
    {
    	current_text_color = color;
    }
}
