package com.code4bones.EyeInside;

import java.util.ArrayList;
import java.util.Calendar;

import com.code4bones.utils.NetLog;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		NetLog.v("I INSIDE !");
		
		//NetLog.v("PATH %s",CommandObj.getFile(this, "Hello.txt"));
		
		PackageManager pm = this.getPackageManager();
	    ComponentName componentName =
	    	      new ComponentName("com.code4bones.EyeInside",
	    	      "com.code4bones.EyeInside.MainActivity");
	    
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
