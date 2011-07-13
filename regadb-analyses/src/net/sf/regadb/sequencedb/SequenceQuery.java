package net.sf.regadb.sequencedb;

import java.io.File;

import net.sf.regadb.db.OpenReadingFrame;

public interface SequenceQuery {
	public void process(OpenReadingFrame orf, int patientId, int isolateId, int sequenceId, File alignment); 
}
