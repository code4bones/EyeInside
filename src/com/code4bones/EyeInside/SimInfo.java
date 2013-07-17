package com.code4bones.EyeInside;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.code4bones.utils.NetLog;

public class SimInfo {

	private Context src;
	private String simSerial;
	private String line1Number;
	
	public int simState;

	final String simStateStr[] = new String[]{"ACTIVE","OUT OF SERIVCE","EMERGENCY","POWER OFF"};
	
	public SimInfo() {
		simState = 0;
		simSerial = "";
		line1Number = "";
	}
	
	public SimInfo (Context _src )
	{
		this.src = _src;
		requestSimInfo();
	}
	
	public boolean load(Context _src) {
		this.src = _src;
		SharedPreferences prefs = src.getSharedPreferences("simchange", 1);
		this.simSerial = prefs.getString("sim.id", "");
		this.line1Number = prefs.getString("sim.num", "");
		this.simState = prefs.getInt("sim.state", 0);
		//NetLog.v("SimInfo loaded: %s",this);
		return isValid();
	}
	
	public String getStateName() {
		return this.simStateStr[simState];
	}
	
	public String shortDesc() {
		return String.format("%s:%s", getStateName(),this.simSerial);
	}
	
	public void save() {
		SharedPreferences prefs = src.getSharedPreferences("simchange", 1);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("sim.id",this.simSerial);
		edit.putString("sim.num", this.line1Number);
		edit.putInt("sim.state", this.simState);
		edit.commit();
	}
	
	public boolean waitInfo() {
	    	return true;
	}
	
	private void requestSimInfo ()
	{
		try {
			TelephonyManager telephone = (TelephonyManager)src.getSystemService(Context.TELEPHONY_SERVICE);
			simSerial = telephone.getSimSerialNumber();
			
			String number = telephone.getLine1Number();
			line1Number = number != null? number: "";
		} catch (Exception e) {
			NetLog.e("Exception request sim info: %s",e.getMessage());
		}
	}
	
	public boolean isValid() {
		return (this.simSerial != null && this.simSerial.length() > 0);
	}
	
	public boolean isChanged(SimInfo other) {
		if ( this.isValid() && other.isValid() )
			return this.simSerial.equals(other.simSerial) == false; 
		// default - not changed
		return false;
	}
	
	public String toString() {
		String str = String.format("SimSerial %s, Phone # %s,state %s",simSerial,line1Number,simStateStr[simState]);
		return str;
	}
	
}	
