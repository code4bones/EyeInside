package com.code4bones.EyeInside;

public interface ICommandObjPlugin {
	
	public CommandObj getPlugin();
	public int Invoke() throws Exception;
	public void Reply(Object ... argv) throws Exception;

}
