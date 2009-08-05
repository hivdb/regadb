package net.sf.regadb.util.args;

public class Argument {
	private boolean set = false;
	private boolean mandatory = false;

	public Argument(){
		
	}
	public Argument(String name) {
		setName(name);
	}

	public Argument setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
		return this;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public Argument setSet(boolean set) {
		this.set = set;
		return this;
	}

	public boolean isSet() {
		return set;
	}
	
	private String name;
	
	public Argument setName(String name) {
		this.name = name;
		return this;
	}

	public String getName() {
		return name;
	}
	
	public boolean isValid(){
		return isSet() || !isMandatory();
	}
	
	public String toString(){
		String base = '-' + getName();
		if(isMandatory())
			return base;
		else
			return "[ "+ base +" ]";
	}
}
