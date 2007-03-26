package net.sf.regadb.util.hibernate;

import java.util.ArrayList;

import net.sf.regadb.util.pair.Pair;

public class HibernateFilterConstraint
{
	public String clause_;
	public ArrayList<Pair<String, Object>> arguments_ = new ArrayList<Pair<String, Object>>();
}
