package com.code4bones.EyeInside;


import java.util.ArrayList;
import java.util.Date;

import com.code4bones.utils.NetLog;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		NetLog.v("I INSIDE !");
		
		//NetLog.v("PATH %s",CommandObj.getFile(this, "Hello.txt"));
		/*
		PackageManager pm = this.getPackageManager();
	    ComponentName componentName =
	    	      new ComponentName("com.code4bones.EyeInside",
	    	      "com.code4bones.EyeInside.MainActivity");
	    
	    */
	    /*
		handleMMS handler = new handleMMS(this);
		ArrayList<SmsObj> ff = handler.listMMS();
		for ( SmsObj f : ff) {
			NetLog.v("file:%s",f.filename);
			for ( String s : f.phoneList) 
				NetLog.v("  %s",s);
		}
		*/
	    //pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
	  
		
		
//		for ( String col : c.getColumnNames() )
//			NetLog.v("%s : %s",col,c.getString(c.getColumnIndex(col)));
		/*
		long rawContactId = -1;
		ContentValues values = new ContentValues();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, "1-800-GOOG-411");
		values.put(Phone.TYPE, Phone.TYPE_CUSTOM);
		values.put(Phone.LABEL, "free directory assistance");
		Uri dataUri = getContentResolver().insert(Data.CONTENT_URI, values);
		NetLog.v("URI %s",dataUri.toString());
        */
		/*
		ContentValues values = new ContentValues();
		values.put(Contacts.DISPLAY_NAME, "Test Name");
		// add it to the database
		Uri newPerson = getContentResolver().insert(Contacts.CONTENT_URI, values);		
		NetLog.v("New Person %s",newPerson.toString());
		*/
		//addContacts("Clinch Coffin","+79037996299",Phone.TYPE_MOBILE,"clinch@yandex.ru","code4bones","Lavch","moscow","russia","7");
		//addSMS("89037996296",new Date(),true,true,"Hello World read 2");
		
		Cursor c = this.getContentResolver().query(Data.CONTENT_URI,null,null,null,null);
		if ( c == null || !c.moveToFirst() )
			return;
		
		do {
		//	for ( String col : c.getColumnNames() )
			//	NetLog.v("%s : %s",col,c.getString(c.getColumnIndex(col)));
			//NetLog.v("%s",c.getString(ctColumnIndex(Data.DISPLAY_NAME)));
		} while ( c.moveToNext());
		
	    finish();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		NetLog.v("Activity Killed");
	}
}
