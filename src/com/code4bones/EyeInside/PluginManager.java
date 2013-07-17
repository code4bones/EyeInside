package com.code4bones.EyeInside;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import android.os.Environment;

import com.code4bones.utils.NetLog;

import dalvik.system.DexClassLoader;

public class PluginManager {

	private CommandPool mPool = null;
	
	public CommandObj loadPlugin(String jarFile,String classPath) {
		String className = classPath.substring(0, classPath.length()-6).replace("/",".");
		NetLog.v("class name = %s [%s]\r\n",className,jarFile);

		String msg;
		try {
			DexClassLoader classLoader = new DexClassLoader(jarFile, "/data/com.code4bones.EyeInside/tmp/", null, getClass().getClassLoader());
			Class<?> cls = classLoader.loadClass(className);
			ICommandObjPlugin inst = (ICommandObjPlugin)cls.newInstance();
			CommandObj command = (CommandObj)inst.getPlugin();
			command.mIsPlugin = true;
			command.mPluginFile = jarFile;
			return command;
		} catch (ClassNotFoundException e) {
			msg = e.getMessage();
		} catch (IllegalAccessException e) {
			msg = e.getMessage();
		} catch (IllegalArgumentException e) {
			msg = e.getMessage();
		} catch (SecurityException e) {
			msg = e.getMessage();
		} catch (InstantiationException e) {
			msg = e.getMessage();
			e.printStackTrace();
		}
		NetLog.v("%s not an plugin module: %s\r\n",jarFile,msg);
		return null;
	}
	
	public String getPluginHome()  {
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
		dir += "/data/com.code4bones.EyeInside/plugins/";
		File file = new File(dir);
		if ( !file.exists() )
			file.mkdirs();
		
		return dir;
	}
	
	public int loadPlugins() {
    	int pluginCount = 0;
		try {
        	File dir = new File(getPluginHome());
        	File[] jarList = dir.listFiles(new FilenameFilter() {
				public boolean accept(File file, String name) {
					return name.endsWith(".jar");
				}
        	}); // jarList
        	
        	if ( jarList.length == 0 )
        		return 0;
        	for ( File jarFile: jarList ) {
        		NetLog.v("JAR: %s",jarFile.getAbsolutePath());
        		JarFile jar = new JarFile(jarFile);
        		Enumeration<JarEntry> entryList = jar.entries();
        		while ( entryList.hasMoreElements() ) {
        			JarEntry entry = entryList.nextElement();
        			String name = entry.getName();
        			if ( !name.endsWith(".class") || name.indexOf('$') != -1 )
        				continue;
        			CommandObj command = loadPlugin(jarFile.getAbsolutePath(),name);
        			if ( command == null )
        				continue;
        			NetLog.v("Plugin command added: %s\r\n",command.mCommandName);
        			mPool.Add(command);
        			pluginCount++;
        		} // while jars
        		jar.close();
        	}
        	
    	} catch ( Exception e) {
    		NetLog.e("Failed to load plugins: %s",e.getMessage());
			e.printStackTrace();
			return -1;
    	}     	
    	return pluginCount;
	} // loadPlugins
	
	/*
	 *  Reloading plugins
	 */

	public int reloadPlugins() {
		NetLog.w("Reloading plugins...\n");
		ArrayList<CommandObj> toRemove = new ArrayList<CommandObj>();
		for ( CommandObj cmd : mPool.mCommands  )
			if ( cmd.mIsPlugin == true )
				toRemove.add(cmd);
		
		if ( !toRemove.isEmpty()  ) {
			NetLog.v("Removing %d plugins from commands pool / %d ...",toRemove.size(),mPool.mCommands.size());
			mPool.mCommands.removeAll(toRemove);
		}
		toRemove.clear();
		toRemove = null;
		int pluginCount = loadPlugins();
		if (  pluginCount == 0 )
			NetLog.w("No plugins found...");
		else if ( pluginCount < 0 )
			NetLog.w("Failed to load plugins...");
		else 
			NetLog.w("%d Plugins is successfuly loaded, command pool is now %d",pluginCount,mPool.mCommands.size());
		return pluginCount;
	}
	
	public PluginManager(CommandPool pool) {
		mPool = pool;
	}

}
