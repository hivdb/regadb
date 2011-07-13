package net.sf.regadb.ui.form.query.querytool;

import net.sf.regadb.db.TestResult;

import com.pharmadm.custom.rega.queryeditor.FieldExporter;

public class GSSExporter extends FieldExporter {
	private static final String[] names = new String[]{ "drug","level","description","sir","gss","mutations","remarks" };

	public GSSExporter(String variableName){
		super(variableName);
		setColumns(names);
	}
	
	@Override
	public String getValue(Object o, int i) {
		TestResult v = (TestResult)o;
		String xml = new String(v.getData());
		String open = '<' + getColumn(i) +'>';
		String close = "</" + getColumn(i) +'>';
		
		int a = xml.indexOf(open) + open.length();
		int b = xml.indexOf(close, i);
		
		return xml.substring(a,b);
	}
}
