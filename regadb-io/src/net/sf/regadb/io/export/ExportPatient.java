package net.sf.regadb.io.export;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;

public interface ExportPatient <T> {
	public void start(Transaction t);
	public void exportPatient(Transaction t, Patient p);
	public void stop(Transaction t);
}
