package be.kuleuven.rega.research.conserved.selector;

import be.kuleuven.rega.research.conserved.Sequence;
import be.kuleuven.rega.research.conserved.Selector;

public class TreatmentSelector implements Selector {
	public enum Mode {
		Treated,
		Naive,
		All;
	};
	
	private Mode mode;
	
	public TreatmentSelector(Mode mode) {
		this.mode = mode;
	}
	
	public boolean selectSequence(Sequence s) {
		switch(mode) {
			case Treated: 	return s.drugs.size()>0;
			case Naive:		return s.drugs.size()==0;
			case All:		return true;
			
			default:		throw new RuntimeException("Invalid mode");
		}
	}
}
