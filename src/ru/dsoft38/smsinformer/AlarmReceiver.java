package ru.dsoft38.smsinformer;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	public AlarmReceiver() {
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		scheduleAlarms(context);
	}

	private void scheduleAlarms(Context context) {
		long NOW = Calendar.getInstance().getTimeInMillis();
		startAlarmService(context, NOW);
	}

	static void startAlarmService(Context context, long UTIME) {
		  Intent i = new Intent(context, AlarmService.class);
		  i.setAction(AlarmService.SET_ALARM);
		  i.putExtra("utime", UTIME);
		  context.startService(i);	 	  	  
	  }
}
