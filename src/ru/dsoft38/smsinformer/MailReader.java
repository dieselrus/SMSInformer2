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

import android.util.Log;

public class MailReader extends Authenticator{

	private static final String TAG = "GMailReader";

	private String mailhost = "imap.yandex.ru";  
	private Session session;
	private Store store;

	public MailReader(String user, String password) {

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
			folder.open(Folder.READ_WRITE);

			/*
        	Message[] msgs = folder.getMessages(1, 10);
        	FetchProfile fp = new FetchProfile(); 
        	fp.add(FetchProfile.Item.ENVELOPE); 
        	folder.fetch(msgs, fp);
			 */
			
			// Все письма
			//Message[] msgs = folder.getMessages();
			
			// Только не прочитанные письма
		    Flags seen = new Flags(Flags.Flag.SEEN);
		    FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
		    Message msgs[] = folder.search(unseenFlagTerm);		    	
		    
		    // Удалим из массива не нужные письма письма (другая тема)
		    for(int i = msgs.length - 1; i >= 0; i--){
		    	String subject = msgs[i].getSubject().toString().toLowerCase().trim(); //Получение темы письма
		    	
		    	if(!subject.equalsIgnoreCase("SMSINFORMER")){
		    		continue;
		    	}
		    	
		    	
		    	
		    }
		    
		    // Пометим как прочитанные
		    folder.setFlags(msgs, new Flags(Flags.Flag.SEEN), true);
		    folder.close(false); 
	        store.close();
	        //db.close();
			
			return msgs; 
		} catch (Exception e) { 
			Log.e("readMail", e.getMessage(), e); 
			return null; 
		} 
	} 

}
