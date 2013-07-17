package com.code4bones.EyeInside;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class CommandArgs extends Object {

	final private HashMap<String,String> params = new HashMap<String,String>();
	final private ArrayList<String> list = new ArrayList<String>();
	
	public CommandArgs(String source) {
		String[] param = source.split(";");
		if ( param.length == 0 )
			return;
		
		for ( String arg : param )
			createParam(arg.trim());
	}

	private void createParam(String param) {
		
		String[] keyVal = param.split(":");
		if ( keyVal.length == 0 || keyVal.length == 1 ) {
			if ( param.length() > 0 )
				list.add(param);
		} else {
			String key =keyVal[0];
			String val = keyVal[1];

			for ( int idx = 2; idx < keyVal.length; idx++ )
				val = val.concat(":").concat(keyVal[idx]);
			
			params.put(key, val);
		}
	}

	public boolean hasOpt(String key) {
		for ( String s : list ) 
			if ( s.compareToIgnoreCase(key) == 0 )
				return true;
		return false;
	}
	
	public int optCount() {
		return list.size();
	}
	
	public int argCount() {
		return params.size();
	}
	
	public boolean hasArg(String key) {
		return params.containsKey(key);
	}
	
	public String getOpt(int idx) throws Exception {
		if ( idx > list.size() )
			throw new Exception(String.format("Option index is out of bounds: size = %d,index = %d",list.size(),idx));
		return list.get(idx);
	}
	
	public String[] toArray() {
		String[] arr = list.toArray(new String[list.size()]);
		return arr;
	}
	
	public String strValue(String key) throws Exception {
		if ( params.containsKey(key) == false )
			throw new Exception("Не указан параметр '"  + key + "'");
		return strValue(key,"");
	}
	
	public static boolean toBoolean(String value) {
		int ch = value.charAt(0);   
		return ch == '1' || ch == 'y' || ch == 'Y' || ch == 't' || ch == 'T';
	}
	
	public boolean boolValue(String key) {
		   if ( params.containsKey(key) == false )
			   return false;
		   String str = params.get(key);
		   return CommandArgs.toBoolean(str);
	}
	
	public long dateValue(String key) throws ParseException {
	       SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		   if ( params.containsKey(key) == false )
			   return 0;
    	    String str = params.get(key);
			Date date = df.parse(str);
			return date.getTime();
	}
	
	public String strValue(String key,String defValue) {
		if ( params.containsKey(key) == false )
			return defValue;
		return params.get(key);
	}
	
	public int intValue(String key) throws Exception {
		if ( params.containsKey(key) == false )
			throw new Exception("Parameter "  + key + "not found");
		return intValue(key,0);
	}
	
	public int intValue(String key,int defValue) {
		if ( params.containsKey(key) == false )
			return defValue;
		
			String val = params.get(key);
			return Integer.parseInt(val);
	}

	public String toString() {
		String res = "";
		Set<String> keys = params.keySet();
		for ( String key : keys ) {
			String val = params.get(key);
			if ( res.length() != 0 ) res += "|";
			res += String.format("%s->%s", key,val);
		}
		for ( String opt : list) {
			if ( res.length() != 0 ) res += "|";
			res += String.format("%s", opt);
		}
		return res;
	}

	public static String haveNO(String opt) {
		if ( opt.startsWith("no "))
			return opt.substring(3).trim();
		return null;
	}
	
}
