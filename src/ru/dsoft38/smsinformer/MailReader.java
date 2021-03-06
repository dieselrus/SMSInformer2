package ru.dsoft38.smsinformer;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import android.content.Context;
import android.util.Log;

public class MailReader extends Authenticator{

	private static final String TAG = "MailReader";

	private String mailhost = "imap.yandex.ru";  
	private Session session;
	private Store store;
	private Context context;

	public MailReader(Context context, String user, String password) {

		this.context = context;
		
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

	public synchronized boolean readMail() throws Exception { 
		try { 
			Folder folder = store.getFolder("Inbox"); 
			folder.open(Folder.READ_WRITE);

			/*
        	Message[] msgs = folder.getMessages(1, 10);
        	FetchProfile fp = new FetchProfile(); 
        	fp.add(FetchProfile.Item.ENVELOPE); 
        	folder.fetch(msgs, fp);
			 */
			
			// ��� ������
			//Message[] msgs = folder.getMessages();
			
			// ������ �� ����������� ������
		    Flags seen = new Flags(Flags.Flag.SEEN);
		    FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
		    Message msgs[] = folder.search(unseenFlagTerm);		    	
		    
		    // ������� ������ � ��
		    AlarmDb db = new AlarmDb(context);
		    
		    // ��������� ��� ����� ������, ������� �� ������ ���� � ������� ������ � ��
		    for(int i = msgs.length - 1; i >= 0; i--){
		    	String subject = msgs[i].getSubject().toString().trim(); //��������� ���� ������
		    	
		    	if(!subject.equalsIgnoreCase("SMSINFORMER")){
		    		continue;
		    	}
		    	
		    	// ����� ������
		    	String content = msgs[i].getContent().toString().trim();
		    	
		    	/*<PhoneList> </PhoneList>*/
		    	int firstPos = content.indexOf("<PhoneList>");
		    	int endPos = content.indexOf("</PhoneList>");
		    	String PhoneList = content.substring(firstPos + 11, endPos).trim();
		    	
		    	/*<GroupID> </GroupID>*/
		    	firstPos = content.indexOf("<GroupID>");
		    	endPos = content.indexOf("</GroupID>");
		    	String GroupID = content.substring(firstPos + 9, endPos).trim();
		    	
		    	/*<MessageText> </MessageText>*/
		    	firstPos = content.indexOf("<MessageText>");
		    	endPos = content.indexOf("</MessageText>");
		    	
		    	if(endPos - (firstPos + 13) > 60){
		    		endPos = firstPos + 13 + 59;
		    	}
		    	
		    	String MSG = content.substring(firstPos + 13, endPos).trim(); // ��� 60 ��������
		    	
		    	db.insertAlarm(PhoneList, GroupID, MSG);
		    	
		    }
		    
		    // ������� ��� �����������
		    //folder.setFlags(msgs, new Flags(Flags.Flag.SEEN), true);
		    folder.close(false); 
	        store.close();
			
			return true; 
		} catch (Exception e) { 
			Log.e("readMail", e.getMessage(), e); 
			return false; 
		} 
	} 

}
