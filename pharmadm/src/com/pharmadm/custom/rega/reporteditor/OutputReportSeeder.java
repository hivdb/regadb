package com.pharmadm.custom.rega.reporteditor;

import java.util.Collection;

import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;

public interface OutputReportSeeder {
	public OutputVariable getAssignedVariable(ObjectListVariable olvar);
	public Collection getAvailableOutputVariables(DbObject obj);
	public void assign(ObjectListVariable olvar, OutputVariable ovar);	
}
