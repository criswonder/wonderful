
package test.tabactivity;

import android.app.TabActivity;
import android.os.Bundle;

import com.example.hongyunbasic.R;

public class BottomTabActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_tab_activity);
	}
}
