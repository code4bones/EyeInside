package com.code4bones.EyeInside;

import android.content.Context;

public class CommandInvoker implements Runnable {

	private String mPhone;
	private String mSource;
	private CommandPool mPool = null;
	private Context mContext = null;
	private boolean mLive = false;
	
	public CommandInvoker(Context context,CommandPool pool,String phone,String source) {
		this.mPhone = phone;
		this.mSource = source;
		this.mPool = pool;
		this.mContext = context;
		this.mLive = true;
		if ( this.mPool.mInitialized == false ) mPool.Init(this.mContext);
	}

	public CommandInvoker(Context context,CommandPool pool,String phone,String source,boolean live) {
		this.mPhone = phone;
		this.mSource = source;
		this.mPool = pool;
		this.mContext = context;
		this.mLive = live;
		if ( this.mPool.mInitialized == false ) mPool.Init(this.mContext);
	}
	
	public void run() {
		mPool.Execute(mPhone, mSource,mLive);
	}
}
