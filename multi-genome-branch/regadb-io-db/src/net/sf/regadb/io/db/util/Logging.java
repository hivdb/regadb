package net.sf.regadb.io.db.util;

public interface Logging {
	
	public void logInfo(String message);
	
	public void logError(String message);
	public void logWarning(String message);
	
	public void logError(String patientID, String message);
	public void logWarning(String patientID, String message);
}
