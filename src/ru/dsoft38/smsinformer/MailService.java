package ru.dsoft38.smsinformer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

public class MailService extends IntentService {

	public MailService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();

	}

}
