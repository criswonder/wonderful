package test.broadcastreceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.hongyunbasic.R;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);
		Notification notification = new Notification(R.drawable.ic_launcher,
				"boot done!", 1000);
		notification.contentView = new RemoteViews(context.getPackageName(),
				R.layout.my_status_window_layout);
		mNotificationManager.notify(1, notification);
	}
}
