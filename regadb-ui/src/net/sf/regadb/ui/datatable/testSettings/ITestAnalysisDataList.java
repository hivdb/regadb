package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;
import net.sf.witty.wt.WFileUpload;
import net.sf.witty.wt.WWidget;

public class ITestAnalysisDataList implements IEditableTable<AnalysisData>
{
	private FormWidget form_;
    private static final String [] headers_ = {"editableTable.testAnalysis.colName.name", 
    											"editableTable.testAnalysis.colName.data",
    											};
    private Test test_;	
    private Analysis analysis_;
    private Transaction transaction_;
    public ITestAnalysisDataList(FormWidget form, Test test)
    {
    form_=form;
    test_ = test;
    }

	public void addData(WWidget[] widgets) 
	{
		
		  AnalysisData an = new AnalysisData(test_.getAnalysis(),((TextField) widgets[0]).text(), ((WFileUpload) widgets[1]));
	      test_.getAnalysis().getAnalysisDatas().add(an);
	}
	
	public InteractionState getInteractionState() 
	{
		return form_.getInteractionState();
	}

	public String[] getTableHeaders() 
	{
		return headers_;
	}

	

	public void changeData(AnalysisData analysisData, WWidget[] widgets) 
	{
		analysisData.setName(((TextField)widgets[0]).getFormText());
		analysisData.setData(((WFileUpload)widgets[1]));
		Analysis a = new Analysis();
		//a.setAnalysisDatas(analysisData);
		test_.setAnalysis(a);
		
	}

	public void deleteData(AnalysisData analysisData) 
	{
		test_.getAnalysis().getAnalysisDatas().remove(analysisData);
		transaction_.delete(analysisData);
	}

	public WWidget[] getWidgets(AnalysisData analysisData) 
	{
		TextField tf = new TextField(form_.getInteractionState(), form_);
		WFileUpload fu = new WFileUpload(form_);
        WWidget[] widgets = new WWidget[2];
        
        widgets[0]=tf;
        widgets[2]=fu;
           
        return widgets;
	}

	
	public void setTransaction(Transaction t) 
	{
		this.transaction_ = t;
		
	}

}
