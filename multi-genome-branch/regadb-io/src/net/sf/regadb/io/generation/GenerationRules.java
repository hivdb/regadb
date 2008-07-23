package net.sf.regadb.io.generation;

import java.util.ArrayList;

import net.sf.regadb.util.pair.Pair;

public class GenerationRules {
	public Class[] regaClasses_;
	public ArrayList<String> classToBeIgnored_ = new ArrayList<String>();
	public ArrayList<String> stringRepresentedFields_ = new ArrayList<String>();
	public ArrayList<String> pointerClasses_ = new ArrayList<String>();
	public ArrayList<String> nominalValues_ = new ArrayList<String>();
    
	public ArrayList<Pair<String, String>>  stringRepresentedFieldsRepresentationFields_ = new ArrayList<Pair<String, String>> ();
	public ArrayList<Pair<String, String>>  fieldsToBeIgnored_ = new ArrayList<Pair<String, String>> ();
	
	public boolean writeCsv = false;
	public boolean writeXml = false;
}
