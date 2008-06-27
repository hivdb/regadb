package net.sf.regadb.io.db.util;

import net.sf.regadb.io.util.ILogger;

public class ConsoleLogger implements Logging, ILogger {

	protected static ConsoleLogger _singleton;
	
	private final boolean ENABLE_INFO_LOGGING = false;
	private final boolean ENABLE_UNINITIALIZEDVALUE_LOGGING = false;
	
	private boolean _isInfoEnabled = ENABLE_INFO_LOGGING;
	private boolean _isUninitializedValueEnabled = ENABLE_UNINITIALIZEDVALUE_LOGGING;
	
	public static ConsoleLogger getInstance()
	{
		if(_singleton == null)
			_singleton = new ConsoleLogger();
			
		return _singleton;
	}
	
	public void logInfo(String message)
	{
		if(_isInfoEnabled)
			System.out.println(message);
	}
	
	public void logError(String message)
	{
		System.err.println(message);
		
		System.err.println("CRITICAL ERROR...Stopping database export.");
		
		System.exit(0);
	}
	
	public void logWarning(String message)
	{
		System.err.println("WARNING: "+message);
	}
	
	public void logError(String patientID, String message)
	{
		System.err.println(message);
		
		System.err.println("Error occured during export of patient: "+patientID);
		
		System.err.println("CRITICAL ERROR...Stopping database export.");
		
		System.exit(0);
	}
	
	public void logWarning(String patientID, String message)
	{
		System.err.println("WARNING: "+message);
		
		System.err.println("Warning occured during export of patient: "+patientID);
	}
	
	public boolean isInfoEnabled()
	{
		return _isInfoEnabled;
	}
	
	public boolean isUninitializedValueEnabled()
	{
		return _isUninitializedValueEnabled;
	}
	
	public void setInfoEnabled(boolean enable)
	{
		_isInfoEnabled = enable;
	}
	
	public void setUninitializedValueEnabled(boolean enable)
	{
		_isUninitializedValueEnabled = enable;
	}
}
