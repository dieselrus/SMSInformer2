package ru.dsoft38.smsinformer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmDb {

	private static final int DATABASE_VERSION = 1;	
	private SQLiteDatabase database; 
    private DatabaseOpenHelper databaseOpenHelper; 
    
    private static final String TB_SEND_SMS = "tbSendSms";
	private static final String SQL_CREATE_TB_SEND_SMS = "CREATE TABLE " + TB_SEND_SMS + 
			       " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, number TEXT, msg TEXT );";
	
	public void insertAlarm(String NUMBER, String MSG) {
		ContentValues data = new ContentValues();
		data.put("number", NUMBER);
		data.put("msg", MSG);
		
		open(); 
		database.insert(TB_SEND_SMS, null, data);
		close();            
    }
	
	public AlarmDb(Context context) {
		String DATABASE_NAME = "smsinformer";
		databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void open() throws SQLException {
		database = databaseOpenHelper.getWritableDatabase();
	}

	public void close() {
		if (database != null) database.close();
	}     

	private class DatabaseOpenHelper extends SQLiteOpenHelper {

		public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);                
		} 

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TB_SEND_SMS);
		} 

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {    	  

		} 
	}
}
