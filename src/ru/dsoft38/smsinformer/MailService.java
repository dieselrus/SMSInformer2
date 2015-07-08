package ru.dsoft38.smsinformer;

import java.util.Calendar;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MailService extends IntentService {

	static boolean DEBUG_LOG = true;
	static String TAG_LOG = "SMSInformer";
	
	public static final String SET_ALARM = "ru.dsoft38.smsinformer_SET_ALARM"; 
	public static final String RUN_ALARM = "ru.dsoft38.smsinformer_RUN_ALARM";
	  
	final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    final String PORT = "995";
    final String host = "pop.yandex.ru";
    final String username = "gamza@cbs38.ru";
    final String password = "Jnrhjqcz";
    final String provider = "pop3";
    
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
    			GMailReader reader = new GMailReader("gamza@cbs38.ru", "Jnrhjqcz");
				Message[] msg = reader.readMail();
				Log.d("SendMail", "read ok");
			} catch (Exception e) {
				e.printStackTrace();
			}
            
			// Ставим новую задачу
			AlarmReceiver.scheduleAlarms(this, TIME);
		}

	}

	public class GMailReader extends Authenticator {

		private static final String TAG = "GMailReader";

		private String mailhost = "imap.yandex.ru";  
		private Session session;
		private Store store;

		public GMailReader(String user, String password) {

			Properties props = System.getProperties();
			if (props == null){
				Log.e(TAG, "Properties are null !!");
			}else{
				props.setProperty("mail.store.protocol", "imaps");            

				Log.d(TAG, "Transport: "+props.getProperty("mail.transport.protocol"));
				Log.d(TAG, "Store: "+props.getProperty("mail.store.protocol"));
				Log.d(TAG, "Host: "+props.getProperty("mail.imap.host"));
				Log.d(TAG, "Authentication: "+props.getProperty("mail.imap.auth"));
				Log.d(TAG, "Port: "+props.getProperty("mail.imap.port"));
			}
			try {
				session = Session.getDefaultInstance(props, null);
				store = session.getStore("imaps");
				store.connect(mailhost, user, password);
				Log.i(TAG, "Store: "+store.toString());
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}

		public synchronized Message[] readMail() throws Exception { 
			try { 
				Folder folder = store.getFolder("Inbox"); 
				folder.open(Folder.READ_ONLY);

				/*
	        	Message[] msgs = folder.getMessages(1, 10);
	        	FetchProfile fp = new FetchProfile(); 
	        	fp.add(FetchProfile.Item.ENVELOPE); 
	        	folder.fetch(msgs, fp);
				 */
				Message[] msgs = folder.getMessages();
				return msgs; 
			} catch (Exception e) { 
				Log.e("readMail", e.getMessage(), e); 
				return null; 
			} 
		} 

	}
}
