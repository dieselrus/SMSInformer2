package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SentReceiver extends BroadcastReceiver {
	private final String LOG_TAG = "SentReceiver";
	
	public SentReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
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

}
