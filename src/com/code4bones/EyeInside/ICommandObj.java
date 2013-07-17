package com.code4bones.EyeInside;

public interface ICommandObj {

	
	public int Invoke() throws Exception;
	public void Reply(Object ... argv) throws Exception;
	
}
