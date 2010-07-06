package net.sf.regadb.ui.form.singlePatient.chart;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.io.util.StandardObjects;
import eu.webtoolkit.jwt.chart.Axis;

public class ViralLoadSeries extends LimitedValueSeries {
	private Genome genome;
	
	public ViralLoadSeries(Genome genome, Axis axis) {
		super(null, axis);
		setGenome(genome);
		setValueType(ValueTypes.LIMITED_NUMBER);
	}
	
	public Genome getGenome(){
		return genome;
	}
	
	public void setGenome(Genome genome){
		this.genome = genome;
	}

	@Override
	public String getName(){
		return "Viral Load ("+ getGenome().getOrganismName() +") (log10)";
	}

	@Override
	public boolean isOk(TestResult tr){
		if(tr.getTestDate() == null || getResults().containsKey(tr.getTestDate()))
			return false;
		
		TestType tt = tr.getTest().getTestType();
		return isViralLoadCopies(tt) || isViralLoadLog(tt);
	}
	
	private boolean isViralLoadCopies(TestType tt){
		return tt.getDescription().equals(StandardObjects.getViralLoadDescription())
			&& tt.getGenome().getOrganismName().equals(getGenome().getOrganismName());
	}
	
	private boolean isViralLoadLog(TestType tt){
		return tt.getDescription().equals(StandardObjects.getViralLoadLog10Description())
			&& tt.getGenome().getOrganismName().equals(getGenome().getOrganismName());
	}
	
	@Override
	public Object getValue(TestResult tr){
		if(isViralLoadCopies(tr.getTest().getTestType()))
			return Math.log10((Double)super.getValue(tr));
		else
			return super.getValue(tr);
	}
}
