package net.sf.hivgensim.services;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.service.wts.SubtypeAnalysis;

public class SubtypeService extends SubtypeAnalysis {
	
	private String result;
	
	public SubtypeService(NtSequence ntSequence, Test test, Genome genome) {
		super(ntSequence, test, genome);		
	}
	
	public void processResults(){
		setResult(getOutputs().get(getTest().getAnalysis().getBaseoutputfile()).trim());
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
