package test.alarmmanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

public class SetupAlarm extends Activity {
	AlarmManager alarms;
	PendingIntent alarmIntent;
	public static final String ACTION_REFRESH_EARTHQUAKE_ALARM =
			"fuck.fuck.ffff";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		alarms = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		String ALARM_ACTION;
		ALARM_ACTION =
				ACTION_REFRESH_EARTHQUAKE_ALARM;
		Intent intentToFire = new Intent(ALARM_ACTION);
		alarmIntent =
		PendingIntent.getBroadcast(this, 0, intentToFire, 0);

		int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
		long timeToRefresh = SystemClock.elapsedRealtime() +
		1*4*1000;
//		alarms.setInexactRepeating(alarmType, timeToRefresh,
//		1*4*1000, alarmIntent);
		alarms.set(alarmType,timeToRefresh, alarmIntent);
	}
}
