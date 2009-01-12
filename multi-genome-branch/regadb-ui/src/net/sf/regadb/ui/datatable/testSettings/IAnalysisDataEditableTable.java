package net.sf.regadb.ui.datatable.testSettings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.MyComboBox;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;

import org.apache.commons.io.FileUtils;

import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WMemoryResource;
import eu.webtoolkit.jwt.WWidget;

public class IAnalysisDataEditableTable implements IEditableTable<AnalysisData>
{
    private FormWidget form_;
    private Analysis analysis_;
    private Transaction transaction_;
    private static final String[] headers = {"editableTable.analysis.analysisData.colName.name", "editableTable.analysis.analysisData.colName.file"};
    
    private static final int[] colWidths = {40,60};
    private ArrayList<String> inputFileNames_ = new ArrayList<String>();
    
    public IAnalysisDataEditableTable(FormWidget form)
    {
        form_ = form;
    }
    
    public WWidget[] getWidgets(AnalysisData type)
    {
        WWidget[] widgets = new WWidget[2];
        
        TextField tf = new TextField(InteractionState.Viewing, form_);
        tf.setText(type.getName());

        WMemoryResource resource = new WMemoryResource(type.getMimetype(), tf);
        WAnchor anchor = new WAnchor(resource, WWidget.lt(type.getName()));
        resource.setData(type.getData(), type.getData().length);
        
        anchor.setStyleClass("link");
        
        widgets[0] = tf;
        widgets[1] = anchor;
        
        return widgets;
    }
    
    public void changeData(AnalysisData type, WWidget[] widgets)
    {
        TextField tf = (TextField)widgets[0];
        WAnchor anchor = (WAnchor)widgets[1];
        
        type.setName(tf.text());
        WMemoryResource mem = (WMemoryResource)anchor.resource();
        type.setMimetype(mem.mimeType());
        type.setData(mem.data());
    }
    
    public void addData(WWidget[] widgets)
    {
        TextField tf = (TextField)widgets[0];
        WAnchor anchor = (WAnchor)widgets[1];
        
        WMemoryResource mem = (WMemoryResource)anchor.resource();
        
        AnalysisData data = new AnalysisData(analysis_, mem.mimeType());
        data.setName(tf.text());
        data.setData(mem.data());
        
        analysis_.getAnalysisDatas().add(data);
    }
    
    public void deleteData(AnalysisData type)
    {
        analysis_.getAnalysisDatas().remove(type);
        transaction_.delete(type);
    }
    
    public String[] getTableHeaders()
    {
        return headers;
    }
    
    public WWidget[] addRow()
    {
        WWidget[] widgets = new WWidget[2];
        
        MyComboBox cb = new MyComboBox();
        for(String ifn : inputFileNames_)
        {
            cb.addItem(WWidget.lt(ifn));
        }
        UploadFile upload = new UploadFile(getInteractionState(), form_);
        
        widgets[0] = cb;
        widgets[1] = upload;
        
        return widgets;
    }
    
    public WWidget[] fixAddRow(WWidget[] widgets)
    {
    	MyComboBox add_cb = (MyComboBox)widgets[0];
        UploadFile add_uf = (UploadFile)widgets[1];
        
        if(add_uf.getFileUpload()==null)
        {
            return null;
        }
        else
        {
            byte[] data = null;
            try 
            {
            	if(add_uf.getFileUpload().spoolFileName().equals(""))
            		return null;
                data = FileUtils.readFileToByteArray(new File(add_uf.getFileUpload().spoolFileName()));
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            
            if(data==null)
                return null;
            
            AnalysisData analysisData = new AnalysisData();
            analysisData.setData(data);
            analysisData.setMimetype(add_uf.getFileUpload().contentDescription());
            analysisData.setName(add_cb.currentText().value());
            
            return getWidgets(analysisData);
        }
    }
    
    public InteractionState getInteractionState()
    {
        return form_.getInteractionState();
    }

    public ArrayList<String> getInputFileNames() 
    {
        return inputFileNames_;
    }

    public void setInputFileNames(ArrayList<String> inputFileNames) 
    {
        inputFileNames_.clear();
        inputFileNames_.addAll(inputFileNames);
    }
    
    public void setAnalysis(Analysis analysis)
    {
        analysis_ = analysis;
    }

    public void setTransaction(Transaction transaction) 
    {
        transaction_ = transaction;
    }

    public void flush() 
    {
        transaction_.flush();
    }

	public int[] getColumnWidths() {
		return colWidths;
	}
}
