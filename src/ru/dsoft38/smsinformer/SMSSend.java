package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.util.Log;

public class SMSSend {
	private final String LOG_TAG = "Send SMS";
	
	private Context context;
	
	// ����� ��� �������� � �������� SMS
    private String SENT_SMS_FLAG = "SENT_SMS";
    private String DELIVER_SMS_FLAG = "DELIVER_SMS";

    private PendingIntent sentPIn = null;
    private PendingIntent deliverPIn = null;
	
    private int currentSMSNumberIndex = 0;
    private String[] arrayNum = null;
    private String smsText = null;
    private String currentID = null;
	
	public SMSSend(Context context) {
		
		this.context = context;
		
		Intent sentIn = new Intent(SENT_SMS_FLAG);
        sentPIn = PendingIntent.getBroadcast(this.context, 0, sentIn, 0);

        Intent deliverIn = new Intent(DELIVER_SMS_FLAG);
        deliverPIn = PendingIntent.getBroadcast(this.context, 0, deliverIn, 0);
               
	}

	public void send(){
		AlarmDb db = new AlarmDb(context);
		db.open();
		Cursor c = db.select_SMS_DATA();
		
		
		if (c != null) {
			if (c.moveToFirst()) {
				currentID = c.getString(0);
				arrayNum = c.getString(1).split(";");
				smsText = c.getString(3);				
			}			
		}
		
		db.close();
		// ��������� ��������
		sendingSMS();

	}
	
	private void sendingSMS(){
		if ( currentSMSNumberIndex >= arrayNum.length ) {
			currentSMSNumberIndex = 0;
            return;
        }
		
		// ������� �� ������ �������
        String num = arrayNum[currentSMSNumberIndex].replace("-", "").replace(";", "").replace(" ", "").trim();
        
		// ��������� ����� ������ 11 �������� ��� 12, ���� � +
        if (num.length() == 11 || (num.substring(0, 1).equals("+") && num.length() == 12)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(num, null, smsText, sentPIn, deliverPIn);
        }
	}
	
	BroadcastReceiver sentReceiver = new BroadcastReceiver() {     
        @Override
        public void onReceive(Context c, Intent in) {
        	// ����������� ������� ��� ������� ��������� � ������
            currentSMSNumberIndex++;
            
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Log.d(LOG_TAG, "��������� ����������!");
                    //currentSMSNumberIndex++;
                    
                    AlarmDb db = new AlarmDb(context);
                    db.delete_SMS_DATA(currentID);
                    
                    sendingSMS();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF :
                    Log.d(LOG_TAG, "���������� ������ ��������!");
                    //sendingSMS();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU :
                    Log.d(LOG_TAG, "�������� ��������, ��������� � �������� PDU (protocol description unit)!");
                    //sendingSMS();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Log.d(LOG_TAG, "��� �������� �������� ����������� ��������!");
                    //sendingSMS();
                    break;
                default:
                    // sent SMS message failed
                    Log.d(LOG_TAG, "��������� �� ����������!");
                    //sendSMS();
                    break;
            }
        }
    };

    BroadcastReceiver deliverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent in) {
            // � ����������� �� ������ � �������� ��� ������� ���. ��������� �������� ���������� ��� (��������� ��� ����� ���� ����� ��������)
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Log.d(LOG_TAG, "��������� ����������!");
                    //sendSMS();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Log.d(LOG_TAG, "!���������� ������ ��������!");
                    //sendSMS();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU :
                    Log.d(LOG_TAG, "!�������� ��������, ��������� � �������� PDU (protocol description unit)!");
                    //sendSMS();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Log.d(LOG_TAG, "!��� �������� �������� ����������� ��������!");
                    //sendSMS();
                    break;
                default:
                    // sent SMS message failed
                    Log.d(LOG_TAG, "��������� �� ����������!");
                    //sendSMS();
                    break;
            }
        }
    };
}
