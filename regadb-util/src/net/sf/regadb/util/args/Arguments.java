package net.sf.regadb.util.args;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Arguments{
	@SuppressWarnings("serial")
	public static class ArgumentException extends Exception{
		public ArgumentException(String msg){
			super(msg);
		}
	}
	
	private Map<String, Argument> arguments = new HashMap<String, Argument>();
	private ArrayList<PositionalArgument> posArguments = new ArrayList<PositionalArgument>();
	
	
	public void add(Argument a){
		arguments.put(a.getName(),a);
	}
	
	public void add(PositionalArgument a){
		a.setPosition(posArguments.size());
		posArguments.add(a);
		arguments.put(a.getName(),a);
	}
	
	public Argument addArgument(String name, boolean mandatory){
		Argument a = new Argument(name).setMandatory(mandatory);
		add(a);
		return a;
	}
	public ValueArgument addValueArgument(String name, String valueDescription, boolean mandatory){
		ValueArgument a = (ValueArgument)new ValueArgument(name).setValueDescription(valueDescription).setMandatory(mandatory);
		add(a);
		return a;
	}
	public PositionalArgument addPositionalArgument(String name, boolean mandatory){
		PositionalArgument a = (PositionalArgument)new PositionalArgument(name).setMandatory(mandatory);
		add(a);
		return a;
	}
	public NominalArgument addNominalArgument(String name, Collection<String> possibleValues, boolean mandatory){
		NominalArgument a = (NominalArgument)new NominalArgument(name, possibleValues).setMandatory(mandatory);
		add(a);
		return a;
	}
	
	public Argument get(String name){
		return arguments.get(name);
	}
	
	public PositionalArgument get(int position){
		if(posArguments.size() <= position)
			return null;
		else
			return posArguments.get(position);
	}
	
	public Collection<Argument> getAll(){
		return arguments.values();
	}
	
	public void parse(String args[]) throws ArgumentException{
		int positionalCount = 0;
		for(int i = 0; i < args.length; ++i){
			Argument a;
			if(args[i].startsWith("-")){
				String name = args[i].substring(1);
				a = get(name);
				
				if(a == null)
					throw new ArgumentException("unrecognized argument: "+ name);

				if(a instanceof ValueArgument){
					if(args.length > i+1)
						((ValueArgument)a).setValue(args[++i]);
					else
						throw new ArgumentException("no value for argument: "+ a.getName());
				}
				
				
			}
			else{
				a = get(positionalCount);
				
				if(a == null){
					a = new PositionalArgument("arg"+positionalCount);
					add((PositionalArgument)a);
				}
				
				((PositionalArgument)a).setValue(args[i]);
				
				++positionalCount;
			}
			a.setSet(true);
		}
	}
	
	public boolean isValid(){
		for(Argument a : getAll())
			if(!a.isValid())
				return false;
		return true;
	}
	
	public boolean handle(String args[]){
		try {
			parse(args);
		} catch (ArgumentException e) {
			e.printStackTrace();
		}
		
		if(isValid())
			return true;
		else{
			printUsage(System.err);
			
			for(Argument a : getAll()){
				if(!a.isValid()){
					System.err.println("invalid value for argument: "+ a.toString());
				}
			}

			return false;
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for(Argument a : getAll())
			if(!(a instanceof PositionalArgument))
				sb.append(a.toString() +' ');
		for(PositionalArgument a : posArguments)
			sb.append(a.toString() +' ');
		
		return sb.toString();
	}
	
	public static void main(String args[]){
		Arguments as = new Arguments();

		as.addPositionalArgument("user",true);
		as.addPositionalArgument("pass",true);
		as.addValueArgument("db","database-name",false);
		as.addArgument("force",false);
		as.addNominalArgument("do", Arrays.asList("y","n"), false);
		
		as.handle(args);
		
		if(as.isValid())
			as.printValues(System.out);
	}

	public void printUsage(PrintStream out) {
		out.println("Usage: "+ toString());
	}
	
	public void printValues(PrintStream out){
		out.println("arg\tset\tvalue");
		out.println("---\t---\t-----");
		for(Argument a : getAll()){
			out.print(a.getName() +'\t'+ a.isSet());
			if(a instanceof ValueArgument)
				out.print('\t'+ ((ValueArgument)a).getValue());
			out.println();
		}
	}
}
