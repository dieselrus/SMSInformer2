package ru.dsoft38.smsinformer;

import android.app.IntentService;
import android.content.Intent;

public class AlarmService extends IntentService {

	public static final String SET_ALARM = "com.example.cron_SET_ALARM"; 
	public static final String RUN_ALARM = "com.example.cron_RUN_ALARM";
	  
	public AlarmService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

	}

}
