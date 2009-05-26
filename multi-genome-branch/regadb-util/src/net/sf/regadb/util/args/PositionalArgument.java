package net.sf.regadb.util.args;

public class PositionalArgument extends ValueArgument{
	private int position = -1;
	
	public PositionalArgument(String name){
		setName(name);
	}

	public PositionalArgument(int position){
		setPosition(position);
	}
	
	public PositionalArgument(int position, String description){
		
	}
	
	public PositionalArgument setPosition(int position) {
		this.position = position;
		return this;
	}

	public int getPosition() {
		return position;
	}
	
	public String toString(){
		if(isMandatory())
			return '<'+ getName() +'>';
		else
			return '['+ getName() +']';
	}
}
