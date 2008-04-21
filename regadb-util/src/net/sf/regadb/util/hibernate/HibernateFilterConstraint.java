package net.sf.regadb.util.hibernate;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.util.pair.Pair;

public class HibernateFilterConstraint
{
	public String clause_;
	public ArrayList<Pair<String, Object>> arguments_ = new ArrayList<Pair<String, Object>>();
	
	public String getClause(){
		return clause_;
	}
	
	public void setClause(String clause){
		clause_ = clause;
	}
	
	public List<Pair<String,Object>> getArguments(){
		return arguments_;
	}
	
	public void addArgument(String s, Object o){
		arguments_.add(new Pair<String,Object>(s,o));
	}
}
