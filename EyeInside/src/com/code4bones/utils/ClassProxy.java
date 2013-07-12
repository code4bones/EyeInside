package com.code4bones.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ClassProxy extends Object {

	private Method[] mMethods;
	private Class<?> mClass; 
	private Field[] mFields;
	private Constructor<?>[] mConstructors;
	private Constructor<?> mConstructor;
	final private HashMap<String,String> mTypeMap = new HashMap<String,String>();
	
	public Object object;
	
	private void initJavaType() {
		mTypeMap.put("int",Integer.class.getName());
		mTypeMap.put("long", Long.class.getName());
		mTypeMap.put("boolean", Boolean.class.getName());
		mTypeMap.put("double",Double.class.getName());
		mTypeMap.put("float", Float.class.getName());
		mTypeMap.put("char", Character.class.getName());
		mTypeMap.put("java.lang.Object[]",Object[].class.getName());
	}
	
	public ClassProxy(String packageName,Class<?> ... params) throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		mClass = Class.forName(packageName); 
		mMethods = mClass.getDeclaredMethods();
		mFields = mClass.getFields();
		mConstructors = mClass.getConstructors();
		mConstructor = mClass.getConstructor(params);
		initJavaType();
	}
  
	public ClassProxy Init(Object ... args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		object = mConstructor.newInstance(args);
		return this;
	}
	
	private String toJavaType(Class<?> type) {
		String name = type.getName();
		if ( mTypeMap.containsKey(name))
			return mTypeMap.get(name);
		else {
			NetLog.e("Type %s not handled",name);
		}
		return name;
	}
	
	private Method findMethod(String name,Class<?> ... args ) {
		for ( Method m : mMethods ) {
			if  ( m.getName().compareTo(name) == 0 ) {
				//NetLog.v("Found: %s",m.toGenericString());
				Class<?>[] paramTypes =  m.getParameterTypes();
				if ( paramTypes.length != args.length )
					continue;
				int matched = 0;
				int len = args.length;
				for ( int nParam = 0; nParam < len; nParam ++ ) {
					String paramName1 = toJavaType(paramTypes[nParam]);
					if ( paramName1.compareTo (args[nParam].getName()) == 0 )
						matched++;
				} // for Params
				if  ( matched == len  ) {
					return m;
				} // method found
			} // methods matched
		} // for methods
		return null;
	}

	
	public ClassProxy callSelf(String name,Object ... args) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		object = call(name,args);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T call(String name,Object ... args) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class<?>[] params = new Class<?>[args.length];
		int count = 0;
		for ( Object o : args ) {
			//NetLog.v("PARAM %s => %s",o.getClass(),o.toString());
			params[count++]= o.getClass();
		}
		Method method = findMethod(name,params); 
		if  ( method == null )
			throw new NoSuchMethodException(name);
		return (T) method.invoke(object, args);
	}
	
	
	public void listFields() throws IllegalArgumentException, IllegalAccessException {
		NetLog.v("** Fields: %d",mFields.length);
		for ( Field f : mFields ) {
			Object value = f.get(f.getName());
			NetLog.v(" %s %s => %s",f.getGenericType().toString(),f.getName(),value.toString());
		}
	}

	private void listConstructors() {
		NetLog.v("** Constructors: %d",mConstructors.length);
		for ( Constructor<?> c : mConstructors ) {
			NetLog.v("   %s", c.toGenericString());
		}
	}
	
	public void Dump() throws IllegalArgumentException, IllegalAccessException {
		NetLog.v("\r\n\r\n");
		NetLog.v("**** CLASS %s",mClass.getName());
		listFields();
		listConstructors();
		NetLog.v("** Methods: %d",mMethods.length);
		for ( Method  m : mMethods ) {
			NetLog.v("    %s", m.toGenericString());
		}
		NetLog.v("\r\n\r\n");
	}
	
}
