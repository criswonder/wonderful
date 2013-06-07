
package test.progressDialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class TestProgressDialog extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ATask t = new ATask();
		t.execute();
	}
	
	class ATask extends BaseTask{

		@Override
		public Context getContext() {
			return TestProgressDialog.this;
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return super.doInBackground(arg0);
		}
	}
}
