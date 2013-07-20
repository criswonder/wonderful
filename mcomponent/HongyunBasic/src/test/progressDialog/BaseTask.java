package test.progressDialog;

import com.example.hongyunbasic.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;

/**
 * 让task在执行的时候显示loading，给用户一个提示
 * 
 * @author wonder
 * 
 */
public abstract class BaseTask extends AsyncTask<Void, Void, Void> {
	private ProgressDialog progressDialog;
	public boolean showProgressDialog = true;
	private String message = "云端同步中";
	private Context context;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (showProgressDialog) {
			showProgressBar();
		}
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		// Looper lp =Looper.getMainLooper();
		// Looper.prepare();
		// showProgressBar();
		// lp.quit();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (showProgressDialog) {
			try {
				progressDialog.dismiss();
				progressDialog = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void showProgressBar() {
		if (progressDialog == null) {
			AlertDialog.Builder b= new Builder(getRealContext());
			AlertDialog d = b.create();
			d.show();
			d.setContentView(R.layout.layout_progressbar);
//			progressDialog = new ProgressDialog(getRealContext());
//			progressDialog.setContentView(R.layout.layout_progressbar);
////			progressDialog.setMessage(message);
//			progressDialog.setIndeterminate(true);
//			progressDialog.setCancelable(false);
//			progressDialog.show();
		} else {
			progressDialog.show();
			progressDialog.setMessage(message);
		}
	}

	private Context getRealContext() {
		if(getContext()==null)
			return this.context;
		else
			return getContext();
	}

	public abstract Context getContext();
	
	public void setContext(Context context) {
		this.context = context;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
