package ru.dsoft38.smsinformer;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class MailService extends IntentService {

	static boolean DEBUG_LOG = true;
	static String TAG_LOG = "SMSInformer";
	
	public static final String SET_ALARM = "ru.dsoft38.smsinformer_SET_ALARM"; 
	public static final String RUN_ALARM = "ru.dsoft38.smsinformer_RUN_ALARM";
	// Флаги для отправки и доставки SMS
    private static final String SENT_SMS_FLAG = "SENT_SMS";
    
	public MailService(String name) {
		super(name);
	}

	public MailService() {
	    super("MailService");
	}
	
	public String getDateStr(Calendar c) {
		return new StringBuilder().append(c.get(Calendar.DAY_OF_MONTH)).append(".")
				                  .append(c.get(Calendar.MONTH) + 1).append(".")
				                  .append(c.get(Calendar.YEAR)).append(" ")				                  
				                  .append(c.get(Calendar.HOUR_OF_DAY)).append(":")				                  
				                  .append(c.get(Calendar.MINUTE))				                  
				                  .toString();  
	 }
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		long TIME = extras.getLong("utime");
        
		// Регистрация на оповещения об отправке и доставке СМС
        registerReceiver(new SentReceiver(), new IntentFilter(SENT_SMS_FLAG));
        //registerReceiver(deliverReceiver, new IntentFilter(DELIVER_SMS_FLAG));
		
		if(intent.getAction().equalsIgnoreCase(SET_ALARM)){
			AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(this, MailService.class);
			long UTIME = TIME + 2 * 60 * 1000;
			i.putExtra("utime", UTIME);
			i.setAction(MailService.RUN_ALARM);
			
			PendingIntent pi = PendingIntent.getService(this,  0, i, PendingIntent.FLAG_UPDATE_CURRENT);
			mgr.cancel(pi);
			mgr.set(AlarmManager.RTC_WAKEUP, TIME, pi);	
			
			if (DEBUG_LOG) {
      	    	Calendar cal = Calendar.getInstance();
      	    	cal.setTimeInMillis(UTIME);
      	    	Log.d(TAG_LOG, "Сигнализация установлена: время = " + getDateStr(cal) + "( " + UTIME + " )");      	    	
      	    }
		} else if (intent.getAction().equalsIgnoreCase(RUN_ALARM)) { // Сигнализация сработала!
			if (DEBUG_LOG) {
	    		  Calendar cal = Calendar.getInstance();
	   	    	  cal.setTimeInMillis(TIME);
	    		  Log.d(TAG_LOG, "Аларм! время = " + getDateStr(cal) + "( " + TIME + " )");    
	    	}
			
            try {
            	// Проверяем почту		
    			MailReader reader = new MailReader(this, "gamza@cbs38.ru", "Jnrhjqcz");

    			// Если получили письма, отправляем СМС
    			if(reader.readMail()){
    				SMSSend sms = new SMSSend(this);
    				sms.send();
    			}
    			
				//Log.d("SendMail", "read ok");
			} catch (Exception e) {
				e.printStackTrace();
			}
            
			// Ставим новую задачу
			AlarmReceiver.scheduleAlarms(this, TIME);
		}

	}

}
