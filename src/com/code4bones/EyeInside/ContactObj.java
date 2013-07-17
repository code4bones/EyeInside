package com.code4bones.EyeInside;

import java.util.ArrayList;

import com.code4bones.utils.NetLog;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;

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
	
	public static void addContact(Context context,String name,String phone,String email,String org) throws RemoteException, OperationApplicationException {
		ContactObj.addContactEx(context, name, phone, Phone.TYPE_MOBILE,email, org, null,null,null,null);
	}
	
	public static void addContactEx(Context context,String name, String number, int numberType
		    , String email, String organization, String street, String city
		    , String region, String postcode) throws RemoteException, OperationApplicationException {
		    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		    int rawContactInsertIndex = ops.size();

		    ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
		            .withValue(RawContacts.ACCOUNT_TYPE, null)
		            .withValue(RawContacts.ACCOUNT_NAME, null)
		            .build());

		    ops.add(ContentProviderOperation
		            .newInsert(Data.CONTENT_URI)
		            .withValueBackReference(Data.RAW_CONTACT_ID,rawContactInsertIndex)
		            .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
		            .withValue(StructuredName.DISPLAY_NAME, name)
		            .build());

		    if ( number != null )
		    ops.add(ContentProviderOperation
		            .newInsert(Data.CONTENT_URI)
		            .withValueBackReference(Data.RAW_CONTACT_ID,   rawContactInsertIndex)
		            .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
		            .withValue(Phone.NUMBER, number)
		            .withValue(Phone.TYPE, numberType)
		          //.withValue(Phone.TYPE, Phone.TYPE_MOBILE) //Use constants for type
		            .build());

		    if ( email != null )
		    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
		            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
		            .withValue(Data.MIMETYPE,Email.CONTENT_ITEM_TYPE)
		            .withValue(Email.DATA, email)
		            //If I add Email.TYPE People(Phone's contacts application) 
		            //doesn't work any more.
		            //The error is: "Unfortunately contacts have stopped working"
		            // .withValue(Email.TYPE,Email.TYPE_MOBILE)
		            .build());

		    if ( organization != null )
		    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
		           .withValueBackReference(Data.RAW_CONTACT_ID, 0)
		           .withValue(Data.MIMETYPE,Organization.CONTENT_ITEM_TYPE)
		           .withValue(Organization.COMPANY, organization)
		           .build());

		    if ( street != null && city != null && region != null && postcode != null )
		    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
		           .withValueBackReference(Data.RAW_CONTACT_ID, 0)
		           .withValue(Data.MIMETYPE,StructuredPostal.CONTENT_ITEM_TYPE)
		           .withValue(StructuredPostal.STREET, street)
		           .withValue(StructuredPostal.CITY, city)
		           .withValue(StructuredPostal.REGION, region)
		           .withValue(StructuredPostal.POSTCODE, postcode)
		           .build());

		        context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		}	
	
}
