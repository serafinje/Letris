package com.sera.android.letris.records.DAO;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LetrisDAO extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private Context context;

    // Datos de la BD
    private static final String DATABASE_NAME = "records.db";
    public class RECORDS {
    	public final static String TABLENAME="RECORDS";
    	public final static String _ID="_ID";
    	public final static String PLAYER="PLAYER";
    	public final static String RECORD="RECORD";
    	public final static String DATE="RDATE";
    }

    
	public LetrisDAO(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context=context;
	}
	
	//--------------------- Métodos del helper ------------------------
	@Override
	public SQLiteDatabase getWritableDatabase() {
		SQLiteDatabase db = super.getWritableDatabase();
		if (!db.isOpen()) {
			db = context.openOrCreateDatabase(
	                DATABASE_NAME,
	                SQLiteDatabase.OPEN_READWRITE, null);
		}
		return db;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE "+RECORDS.TABLENAME+" ("
				 + RECORDS._ID + " INTEGER PRIMARY KEY,"
				 + RECORDS.PLAYER + " TEXT,"
				 + RECORDS.RECORD + " INTEGER,"
				 + RECORDS.DATE + " TEXT"
				 + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
	}
		

	//----------------------- Consultas a la BD --------------------------
	public ArrayList<Record> getRecords() {
		ArrayList<Record> result = new ArrayList<Record>();
		
		SQLiteDatabase recordsDB = this.getWritableDatabase();
		Cursor c = recordsDB.query(RECORDS.TABLENAME,null,null,null,null,null,RECORDS.RECORD+" desc");
		c.moveToFirst();
		while (!c.isAfterLast()) {
			Record r = new Record();
			r.setPlayer(c.getString(c.getColumnIndex(RECORDS.PLAYER)));
			r.setRecord(c.getLong(c.getColumnIndex(RECORDS.RECORD)));
			r.setRecordDate(c.getString(c.getColumnIndex(RECORDS.DATE)));
			
			result.add(r);
			c.moveToNext();
		}
		c.close();
		recordsDB.close();
		
		return result;
	}

	
	public long addRecord(Record r)
	{
		SQLiteDatabase recordsDB = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RECORDS.PLAYER,r.getPlayer());
		values.put(RECORDS.RECORD,r.getRecord());
		values.put(RECORDS.DATE, r.getStrRecordDate());
		long rID = recordsDB.insert(RECORDS.TABLENAME, null, values);
		recordsDB.close();
		
		return rID;
	}
}

