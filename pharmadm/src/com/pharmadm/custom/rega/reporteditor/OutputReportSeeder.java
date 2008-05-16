package com.pharmadm.custom.rega.reporteditor;

import java.util.Collection;

import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.VariableType;

public interface OutputReportSeeder {
	public OutputVariable getAssignedVariable(ObjectListVariable olvar);
	public Collection getAvailableOutputVariables(VariableType type);
	public void assign(ObjectListVariable olvar, OutputVariable ovar);	
}
