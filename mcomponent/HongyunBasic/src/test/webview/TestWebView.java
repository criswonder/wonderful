package test.webview;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TestWebView extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		WebView webview = new WebView(this);
		setContentView(webview);
		webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

		// String summary =
		// "<html><body>You scored <b>192</b> points.</body></html>";
		// webview.loadData(summary, "text/html", null);

		// String url = "http://www.baidu.com";
		// webview.loadUrl(url);

		webview.getSettings().setJavaScriptEnabled(true);

		final Activity activity = this;
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activities and WebViews measure progress with different
				// scales.
				// The progress meter will automatically disappear when we reach
				// 100%
				activity.setProgress(progress * 1000);
			}
		});
		webview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(activity, "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
		});

		webview.loadUrl("http://www.iteye.com/");
	}

}
