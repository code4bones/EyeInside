package com.code4bones.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


public class BackgroundTask<Result,Param> extends AsyncTask<Param, Void, Result> {

	public ProgressDialog progress = null;
	public Context context = null;
	public boolean showProgress;
	//public Result sourceBeacon = null;

	public BackgroundTask(Context context,boolean withProgress ) {
		this.context = context;
		this.showProgress = withProgress;
	}
	
	public void onComplete(Result result) {
		
	}
	
	@Override
	protected void onPreExecute() {
		if ( showProgress ) {
		    progress = new ProgressDialog(context);
		    progress.setTitle("Подождите...");
		    progress.setMessage("Обработка запроса...");
		    progress.setIndeterminate(true);
		    progress.setCancelable(true);
		    progress.show();
		}
	  }
	
	@Override
	protected Result doInBackground(Param ... arg0) {
		return null; 
	}
	
	protected void onPostExecute(Result result) {
		if ( showProgress )
			progress.dismiss();
		onComplete(result);
  }	
	
	public void exec(Param ... params) {
		this.execute(params);
	}
};  // class
