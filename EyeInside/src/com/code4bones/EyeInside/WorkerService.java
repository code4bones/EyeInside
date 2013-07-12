package com.code4bones.EyeInside;

import java.util.Date;

import com.code4bones.utils.NetLog;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class WorkerService extends Service {

	private final IBinder mBinder = null;//new WorkerBinder();
	
	public WorkerService() {
		NetLog.v("WorkerService()");
		// TODO Auto-generated constructor stub
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
		wakeup.setAction("com.code4bones.EyeInside.wakeup");
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
