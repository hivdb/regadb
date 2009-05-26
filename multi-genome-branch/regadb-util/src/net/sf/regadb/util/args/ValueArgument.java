package net.sf.regadb.util.args;

public class ValueArgument extends Argument{
	private String value = null;
	private String valueDescription = "value";
	
	public ValueArgument(){
		
	}
	
	public ValueArgument(String name){
		setName(name);
	}

	public ValueArgument setValue(String value) {
		this.value = value;
		return this;
	}

	public String getValue() {
		return value;
	}
	
	public String getValueDescription(){
		return valueDescription;
	}
	
	public ValueArgument setValueDescription(String valueDescription){
		this.valueDescription = valueDescription;
		return this;
	}

	public String toString(){
		String base = '-'+ getName() +" <"+ getValueDescription() +'>';
		if(isMandatory())
			return base;
		else
			return "[ "+ base +" ]";
	}
}
