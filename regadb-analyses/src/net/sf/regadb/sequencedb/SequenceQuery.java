package net.sf.regadb.sequencedb;

import net.sf.regadb.db.OpenReadingFrame;

public interface SequenceQuery {
	public void process(OpenReadingFrame orf, String id, String alignment); 
}
