package com.code4bones.EyeInside;

import com.code4bones.utils.NetLog;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;

public class EyeAccessService extends AccessibilityService {

	public EyeAccessService() {
		NetLog.v("ACC: Service()");
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		NetLog.v("ACC: Event %s",event);

		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		// We are interested in all types of accessibility events.
		info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED;// TYPE_NOTIFICATION_STATE_CHANGED;
		// We want to provide specific type of feedback.
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;// | AccessibilityServiceInfo.FEEDBACK_HAPTIC | AccessibilityServiceInfo.FEEDBACK_VISUAL;
		// We want to receive events in a certain interval.
		info.notificationTimeout = 100;
		// We want to receive accessibility events only from certain packages.
		// only handle this package
		info.packageNames = new String[] {"com.android.mms"};
	
		setServiceInfo(info);
	
	}

	@Override
	public void onServiceConnected() {
		
		NetLog.v("ACC: Service Connected");
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		// We are interested in all types of accessibility events.
		info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED;// TYPE_NOTIFICATION_STATE_CHANGED;
		// We want to provide specific type of feedback.
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;// | AccessibilityServiceInfo.FEEDBACK_HAPTIC | AccessibilityServiceInfo.FEEDBACK_VISUAL;
		// We want to receive events in a certain interval.
		info.notificationTimeout = 100;
		// We want to receive accessibility events only from certain packages.
		// only handle this package
		info.packageNames = null;//new String[] {};
	
		setServiceInfo(info);
	}	
	@Override
	public void onInterrupt() {
		NetLog.v("ACC: Interrupted");
	}

}
