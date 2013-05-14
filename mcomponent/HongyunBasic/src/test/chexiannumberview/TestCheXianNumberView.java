package test.chexiannumberview;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.hongyunbasic.R;
import com.hongyun.component.CheXianNumberView;
import com.xixi.android.MucangConfig;

public class TestCheXianNumberView extends Activity {
	private CheXianNumberView numberView;
	private TextView resultText;
	private Button btn;
	private Button loopBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MucangConfig.setApplication(getApplication());
		setContentView(R.layout.chexian_jisuan_chou_jiang);

		numberView = (CheXianNumberView) findViewById(R.id.chexian_prize_number_view);
		resultText = (TextView) findViewById(R.id.chexian_prize_result_text);
		btn = (Button) findViewById(R.id.chexian_prize_btn);
		loopBtn = (Button) findViewById(R.id.btn_loop);
		
		numberView.set(5, 14000, 800);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Loader task = new Loader();
				task.execute();
			}
		});
		loopBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				numberView.loop();
			}
		});
		

	}

	class Loader extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			numberView.setNumber(18986, true);
		}

	}
}
