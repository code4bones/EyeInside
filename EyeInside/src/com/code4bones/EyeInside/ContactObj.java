package com.code4bones.EyeInside;

import java.util.ArrayList;

import com.code4bones.utils.NetLog;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

public class ContactObj extends Object {

	public String name;
	public long id;
	final public ArrayList<String> phones = new ArrayList<String>();
	
	public static String NormalizePhone(String phone) {
		if ( phone == null ) {
			NetLog.e("Cannot normalize phone - null");
			return "<?>";
		}
		return phone.replace("(", "").replace(")", "").replace("-","").replace("+7", "8").replace(" ","").replace("+8","8");
	}
	
	public ContactObj(String name,String phone) {
		this.phones.add(phone);
		this.name = name;
	}
	
	public ContactObj(Cursor cur,Context context) {
		boolean hasPhone = cur.getInt(cur.getColumnIndex(PhoneLookup.HAS_PHONE_NUMBER)) == 1; // 1/0
			name = cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			id        = cur.getLong(cur.getColumnIndex(PhoneLookup._ID));
			String contactId = String.valueOf(id); 
			if ( hasPhone ) {
				Cursor ph = context.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null); 
					  if ( ph.moveToFirst() ) {
						  do {
							  String phone = ph.getString(ph.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));                 
							  phones.add(ContactObj.NormalizePhone(phone));
						  } while ( ph.moveToNext());
						  ph.close();
					  } // moveToFirst()
			} // hasPhone
		} // c-tor
	
	public String phoneArray() {
		String ph = new String();
		for ( String s : phones ) { 
			if ( ph.length() != 0 ) ph = ph.concat(",");
			ph = ph.concat(s);
		}
		return ph;
	}
	
	public boolean containsPhone(String phone) {
		for ( String s : phones ) {
			if ( s.compareTo( phone ) == 0 )
				return true;
		}
		return false;
	}
	
	public void Dump() {
		if ( phones.size() == 1 )
			NetLog.v("%d : %s = %s\r\n",id,name,phones.get(0));
		else {
			NetLog.v("%s:\r\n",name);
			for ( String p : phones ) {
				NetLog.v("  %s\r\n",p);
			}
		}
	}
}
