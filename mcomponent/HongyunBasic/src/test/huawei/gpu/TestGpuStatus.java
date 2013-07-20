/**
 * 
 */
package test.huawei.gpu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author wb-maohongyun
 *
 */
public class TestGpuStatus extends Activity {
	private static final String HARDWARE_UI_PROPERTY = "persist.sys.ui.hw";
	private Boolean mForceGPU = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button btn = new Button(this);
		btn.setWidth(100);
		btn.setHeight(100);
		
		final TextView tv1 = new TextView(this);
		tv1.setHeight(200);
		tv1.setWidth(200);
		tv1.setBackgroundColor(Color.RED);
		
		LinearLayout line = new LinearLayout(this);
		line.setOrientation(LinearLayout.VERTICAL);
		line.addView(btn);
		line.addView(tv1);
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					Class<?> c = Class.forName("android.os.SystemProperties");
					Method m = c.getMethod("getBoolean", new Class[] {
							String.class, boolean.class });
					mForceGPU  = (Boolean) m.invoke(c, new Object[] {
							HARDWARE_UI_PROPERTY, false });
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tv1.setText(String.valueOf(mForceGPU));
			}
		});
		
		setContentView(line);
	}
}
