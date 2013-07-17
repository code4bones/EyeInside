package com.code4bones.EyeInside;


import com.code4bones.utils.NetLog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;


public class SimChangeMonitor extends PhoneStateListener {

	private Context mContext = null;
	private CommandObj mCommand = null;
	private Handler mHandler = null;
	
	public void install(Context ctx,Handler handler) {
		mContext = ctx;
		mHandler = handler;
		TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(this, PhoneStateListener.LISTEN_SERVICE_STATE);
		NetLog.v("SimChangeMonitor - Started");
	}
	
	public void remove() {
		TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(this, 0);
		NetLog.v("SimChangeMonitor - Removed");
	}

	public void handleSimChange(int state)  {
		
		SimInfo si = new SimInfo(mContext);
		si.simState = state;
		NetLog.v("SIM EVENT %s",si);
		
		SimInfo old = new SimInfo();
		if ( !old.load(mContext) ) {
			if ( state == ServiceState.STATE_IN_SERVICE ) {
				if ( si.isValid() ) {
					si.save();
				}
				return;
		    }
		}
		
	   	new Thread(new Runnable() {
				public void run() {
					SimInfo check = new SimInfo(mContext);
					int nCount = 0;
					// бывает так, что симка не сразу поднимается,
					// поэтому ждем пока она проснется..
					while ( !check.isValid() && ++nCount < 10 ) {
						try {
							Thread.sleep(5000);
							check = null;
							check = new SimInfo(mContext);
							NetLog.v("Waiting SIM init (%d): %s",nCount,check);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} // while
					
					if ( !check.isValid() ) {
						mCommand.replySMS("Не удается получить состояние сим карты");
						return;
					}

					boolean changed = false;
					SimInfo old = new SimInfo();
					old.load(mContext);
					changed = check.isChanged(old);
					if ( changed ) {
						try {
							Message msg = mHandler.obtainMessage(0, changed);
							msg.sendToTarget();
							//mCommand.Reply("Смена сим карты,%s -> %s",old.shortDesc(),check.shortDesc());
							check.save();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} // run()
	    	}).start();
	}
	
	
	@Override
	public void onServiceStateChanged(ServiceState serviceState) {
	    super.onServiceStateChanged(serviceState);
	    handleSimChange(serviceState.getState());
	}
	
}
