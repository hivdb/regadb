package be.kuleuven.rega.research.lowLevelViralLoad;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;

public class LowLevelViralLoadData {

	public Patient patient;
	public TestResult undetectableViralLoad;
	public TestResult lowViralLoad;
	public Therapy therapy;
	public ViralIsolate lowViralLoadIsolate;
	public ViralIsolate preLowViralLoadIsolate;
	
	
}
