package com.code4bones.EyeInside;

import android.content.Context;
import android.content.Intent;

public class MyIntent extends Intent {

	public String mName = null;
	public Object mObj = null;
	public CommandObj mCmd = null;
	
	public MyIntent(Context context,Class<?> cls,CommandObj cmd) {
		super(context,cls);
		mCmd = cmd;
	}
	
	public MyIntent() {
		// TODO Auto-generated constructor stub
	}

}
