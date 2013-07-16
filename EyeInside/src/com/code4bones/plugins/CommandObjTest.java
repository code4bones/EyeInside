package com.code4bones.plugins;

import com.code4bones.EyeInside.CommandObj;
import com.code4bones.EyeInside.ICommandObj;
import com.code4bones.EyeInside.ICommandObjPlugin;
import com.code4bones.utils.NetLog;


public class CommandObjTest extends CommandObj implements ICommandObjPlugin {

	public CommandObjTest(){
		NetLog.v("Plugin: CommandObjTest() default");
	}
	
	public CommandObjTest(String name, String help) {
		super(name, help);
	}

	@Override
	public CommandObjTest getPlugin() {
		return new CommandObjTest("test","Pluging");
	}
	
	@Override
	public int Invoke() throws Exception {
		mCommandResult = "Plugin - invoked!";
		NetLog.v("Hello from Plugin ");
		return CommandObj.ACK;
	}

	@Override
	public void Reply(Object... argv) throws Exception {
		NetLog.v("PLUGIN: Reply");
	}

}
