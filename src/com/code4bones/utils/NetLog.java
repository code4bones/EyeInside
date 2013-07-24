package com.code4bones.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.soundforge.musicboost.R;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class NetLog {

	private static class NetLogHolder {
		private static final NetLog INSTANCE = new NetLog();
	};
	
	private PrintStream ps = null; 
	private String sLogFile = null;
	private String TAG = null;
	private boolean isInitialized;
	
	public PrintStream getPrintStream() {
		return ps;
	}
	
	public NetLog() {
		Log.w("NETLOG","Logger created");
		this.isInitialized = false;
	}
	
	public static NetLog getInstance() {
		return NetLogHolder.INSTANCE;
	}
	
	public PrintStream Init(String tag,String sLogFileName,boolean removeIfExists) {
		try {
			if ( this.isInitialized ) {
				Log.w(tag,"NetLog is already initialized");
				return ps;
			}
			
			TAG = tag;
			sLogFile = Environment.getExternalStorageDirectory() + "/" + sLogFileName;
			File file = new File(sLogFile);
			
			if ( ps == null ) {
				ps = new PrintStream(new FileOutputStream(file,removeIfExists == false));
				NetLog.w("********* LOGGER STARTED ********* \n");
			} else
				NetLog.w("********* LOGGER ACQUIRED ********* \n");

		} catch ( Exception e ) {
			Log.v(TAG,"NetLog Error " + e.toString());
			return null;
		}
		this.isInitialized = true;
		return ps;
	}
	
	public static String getTimeStamp(String dateFormat) {
        Date currentTime = new Date();
		SimpleDateFormat df = new SimpleDateFormat(dateFormat == null?"dd.MM HH:mm:ss ":dateFormat);
		return df.format(currentTime);
	}

	public void writeToFile(String severety,String fmt,Object ... args) {
		if ( ps == null ) 
			return;
		
		synchronized (ps) {
			ps.printf("%s | %s | ",NetLog.getTimeStamp(null),severety);
			//String msg = String.format(fmt, args);
			ps.printf(fmt,args);
			//msg = msg.replace("\r", "").replace("\n", ""); 
			ps.printf("\r\n");
		}
	}
	
	public String getTag() {
		return TAG;
	}
	
	public static void v(String fmt,Object ... args) {
		getInstance().writeToFile("V",fmt,args);
		Log.v(getInstance().getTag(),String.format(fmt, args));
	}

	public static void e(String fmt,Object ... args) {
		getInstance().writeToFile("E",fmt,args);
		Log.e(getInstance().getTag(),String.format(fmt, args));
	}

	public static void w(String fmt,Object ... args) {
		getInstance().writeToFile("W",fmt,args);
		Log.w(getInstance().getTag(),String.format(fmt, args));
	}

	public static void i(String fmt,Object ... args) {
		getInstance().writeToFile("I",fmt,args);
		Log.i(getInstance().getTag(),String.format(fmt, args));
	}
	
	
	public static void Dump() {
		File file = new File(getInstance().sLogFile);
		try {
			String line = null;
			BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file)));
			while ((line = br.readLine()) != null ) 
				Log.v(getInstance().getTag(),line);
			br.close();
		} catch ( Exception e ) {
			Log.v(getInstance().getTag(),"NetLog.Dump()=>"+e.toString());
		}
	}
	
	public static void MsgBox(Context ctx,String sTitle,String fmt,Object ... args) {
		String msg = String.format(fmt, args);
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ctx);                      
	    dlgAlert.setTitle(sTitle); 
	    dlgAlert.setMessage(msg); 
	    dlgAlert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        }
	   });
	    dlgAlert.setCancelable(true);
	    dlgAlert.create().show();
	}
	
	public static void MsgBox(Context ctx, DialogInterface.OnClickListener onClick, String sTitle,String fmt,Object ... args) {
		String msg = String.format(fmt, args);
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ctx);                      
	    dlgAlert.setTitle(sTitle); 
	    dlgAlert.setMessage(msg); 
	    dlgAlert.setPositiveButton("OK", onClick);
	    dlgAlert.setCancelable(true);
	    dlgAlert.create().show();
	}
	
	
	public static void Toast(Context ctx,String fmt, Object ... args) {
		String msg = String.format(fmt, args);
		Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
	}
	
	public static void Notify(Context context,String sTitle,String fmt,Object ... args) {

        NotificationManager nm = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
       
              int icon = R.drawable.ic_launcher;
              long when = System.currentTimeMillis();
		
             String msg = String.format(fmt, args);

             Notification notification =  new Notification( icon, sTitle, when);
     
            Intent notificationIntent = new Intent("", null);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, Intent.FLAG_ACTIVITY_NO_USER_ACTION);
     
            notification.setLatestEventInfo(context,sTitle, msg,contentIntent);
            nm.notify( 1, notification );        
	}
	
};
