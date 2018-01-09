package com.sera.android.letris.records.DAO;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

public class Record
{
	private int _id;
	private String player;
	private long record;
	private Date recordDate;

	
	public Record() {}
	
	public Record(String player, long record, String recordDate) {
		super();
		this.player = player;
		this.record = record;
		setRecordDate(recordDate);
	}
	
	public int getId() {		return _id;	}
	public void setId(int _id) {		this._id = _id;	}
	
	public String getPlayer() {		return player;	}
	public void setPlayer(String player) {		this.player = player;	}
	
	public long getRecord() {		return record;	}
	public void setRecord(long record) {		this.record = record;	}
	
	public Date getRecordDate() {		return recordDate;	}
	public String getStrRecordDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(recordDate);
	}
	public String getLocaleRecordDate(Context context) {
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        return dateFormat.format(recordDate);
	}
	
	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}
	public void setRecordDate(String recordDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			this.recordDate = sdf.parse(recordDate);
		} catch (Exception e) {
			Log.e(this.getClass().getName(), e.getMessage(),e);
			this.recordDate = null;
		}
	}
}
