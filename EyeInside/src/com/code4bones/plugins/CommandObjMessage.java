package com.code4bones.plugins;

import com.code4bones.EyeInside.CommandObj;
import com.code4bones.EyeInside.ICommandObjPlugin;
import com.code4bones.utils.NetLog;

public class CommandObjMessage extends CommandObj implements ICommandObjPlugin{

	public CommandObjMessage() {
		super("message","help");
	}
	
	
	@Override
	public CommandObjMessage getPlugin() {
		return new CommandObjMessage();
	}

	@Override
	public int Invoke() throws Exception {
		mCommandResult = "Hello World";
		NetLog.v("Message !");
		return CommandObj.ACK;
	}
	
}
