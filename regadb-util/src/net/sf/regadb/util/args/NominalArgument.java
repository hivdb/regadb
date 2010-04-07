package net.sf.regadb.util.args;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NominalArgument extends ValueArgument {
	private List<String> possibleValues = new ArrayList<String>();
	
	public NominalArgument(){
		super();
	}
	
	public NominalArgument(Collection<String> possibleValues){
		this();
		setPossibleValues(possibleValues);
	}
	
	public NominalArgument(String name){
		super(name);
	}
	
	public NominalArgument(String name, Collection<String> possibleValues){
		this(name);
		setPossibleValues(possibleValues);
	}
	
	public void setPossibleValues(Collection<String> possibleValues){
		this.possibleValues.clear();
		this.possibleValues.addAll(possibleValues);
		setValueDescription(this.possibleValues);
	}
	
	public final Collection<String> getPossibleValues(){
		return possibleValues;
	}
	
	@Override
	public boolean isValid(){
		return super.isValid() && (!isSet() || possibleValues.contains(getValue()));
	}
	
	private void setValueDescription(Collection<String> values){
		StringBuilder sb = new StringBuilder();
		for(String value : values){
			sb.append("|"+ value);
		}
		setValueDescription(sb.toString().substring(1));
	}
}
