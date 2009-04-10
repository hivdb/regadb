package net.sf.regadb.io.export;

import net.sf.regadb.db.Patient;

public interface ExportPatient <T> {
	public void start();
	public void exportPatient(Patient p);
	public void stop();
}
