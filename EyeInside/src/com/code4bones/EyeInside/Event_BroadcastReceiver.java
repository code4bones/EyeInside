package com.code4bones.EyeInside;

import com.code4bones.utils.NetLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class Event_BroadcastReceiver extends BroadcastReceiver {

	public Event_BroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		CommandPool pool = CommandPool.getInstance();
		CommandObj cmd = pool.findCommand(intent);
		if ( cmd != null ) {
			try {
				cmd.Reply();
			} catch (Exception e) {
				cmd.replySMS("Ошибка \"%s\" : %s",cmd.mCommandName,e.getMessage());
			}
		}
	}

}
