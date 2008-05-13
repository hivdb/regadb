package net.sf.regadb.ui.form.query.querytool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.pharmadm.custom.rega.queryeditor.OutputSelection;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.QueryEditor;
import com.pharmadm.custom.rega.queryeditor.Selection;
import com.pharmadm.custom.rega.queryeditor.SelectionChangeListener;
import com.pharmadm.custom.rega.queryeditor.SelectionListChangeListener;
import com.pharmadm.custom.rega.queryeditor.SelectionStatusList;
import com.pharmadm.custom.rega.queryeditor.TableSelection;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryResult;
import com.pharmadm.custom.rega.queryeditor.port.QueryStatement;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.exportCsv.ExportToCsv;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WFileResource;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WLabel;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

public class RunGroupBox extends WGroupBox {
	
	private QueryEditor editor;
	
	private WContainerWidget content;
	
    private WLabel statusL_;
    private WText status_;
    private WLabel linkL_;
    private WAnchor link_;
    private WLabel runL_;
    private WPushButton run_;
	
	
	public RunGroupBox(QueryEditor editor, WContainerWidget parent) {
		super(tr("form.query.querytool.group.run"), parent);
		setStyleClass("resultfield");
		this.editor = editor;
		
		content = new WContainerWidget(this);
		content.setStyleClass("content");
		
		editor.getQuery().getSelectList().addSelectionChangeListener(new SelectionChangeListener() {
			public void selectionChanged() {
				updateStatus();
			}
		});
		
		editor.addSelectionListChangeListener(new SelectionListChangeListener() {
			public void listChanged() {
				updateStatus();
			}
		});
		

		
		updateStatus();
	}
	
	public void updateStatus() {
		Query query = editor.getQuery();
		content.clear();
		
        if (!query.isValid()) {
        	showWarning("form.query.querytool.message.unassigned");
        }
        else if (!query.hasFromVariables()) {
        	showWarning("form.query.querytool.message.noselection");
        }
        else if (!query.getSelectList().isAnythingSelected()) {
        	showWarning("form.query.querytool.message.emptyselection");
        }
        else {
    		WTable resultTable_ = new WTable(content);
            runL_ = new WLabel(new WMessage("form.query.querytool.label.run"));
            resultTable_.putElementAt(0, 0, runL_);
            run_ = new WPushButton(tr("form.query.querytool.pushbutton.run"));
            resultTable_.putElementAt(0, 1, run_);

            linkL_ = new WLabel(new WMessage("form.query.querytool.label.result"));
            resultTable_.putElementAt(2, 0, linkL_);
            link_ = new WAnchor("dummy", lt(""));
            resultTable_.putElementAt(2, 1, link_);
            
            statusL_ = new WLabel(new WMessage("form.query.querytool.label.status"));
            resultTable_.putElementAt(1, 0, statusL_);
            status_ = new WText(new WMessage("form.query.querytool.label.status.initial"));
            resultTable_.putElementAt(1, 1, status_);

            run_.clicked.addListener(new SignalListener<WMouseEvent>() {
    			public void notify(WMouseEvent a) {
    				runQuery();
    			}
            });
        	
        	
        }
	}
	
	private void showWarning(String message) {
		WText text = new WText(tr(message));
		text.setStyleClass("warning");
		content.addWidget(text);
	}

	private void runQuery() {
        run_.disable();
        status_.setText(tr("form.query.querytool.label.status.running"));

        try{
            File csvFile =  getOutputFile();
            
            if(process(csvFile)){
                setDownloadLink(csvFile);
                status_.setText(tr("form.query.querytool.label.status.finished"));
            }
            else{
                status_.setText(tr("form.query.querytool.label.status.failed"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
            status_.setText(tr("form.query.querytool.label.status.failed"));
        }
        
        run_.enable();
	}
	
    public void setDownloadLink(File file){
        link_.label().setText(lt("Download Query Result [" + new Date(System.currentTimeMillis()).toString() + "]"));
        link_.setRef(new WFileResource("application/csv", file.getAbsolutePath()).generateUrl());
    }	
    private File getResultDir(){
        File queryDir = new File(RegaDBSettings.getInstance().getPropertyValue("regadb.query.resultDir") + File.separatorChar + "querytool");
        if(!queryDir.exists()){
        	queryDir.mkdir();
        }
        return queryDir;
    }
    
    private File getOutputFile() 
    {
        File queryDir = getResultDir();
        return new File(queryDir.getAbsolutePath() + File.separatorChar + getFileName() + ".csv");
    }  
    
    @SuppressWarnings("unchecked")
    protected boolean process(File csvFile){
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        
		QueryStatement st = null;
		QueryResult result = null;
		
		
		SelectionStatusList selectList = new SelectionStatusList();
		SelectionStatusList oldList = editor.getQuery().getSelectList();
		selectList.setQuery(editor.getQuery());
		
		for (Selection  sel : oldList.getSelections()) {
			Selection clone = null;
			if (sel instanceof TableSelection) {
				clone = new TableSelection((OutputVariable) sel.getObjectSpec(), sel.isSelected());
				for (Selection subSelection : clone.getSubSelections()) {
					subSelection.setSelected(false);
				}
				System.err.println("composed");
			}
			else if (sel instanceof OutputSelection) {
				clone = new OutputSelection((OutputVariable) sel.getObjectSpec(), sel.isSelected());
				System.err.println("simple");
			}
			
			if (clone != null) {
				selectList.getSelections().add(clone);
			}
		}
		editor.getQuery().setSelectList(selectList);
		
		
		try {
			st = DatabaseManager.getInstance().getDatabaseConnector().createScrollableReadOnlyStatement();
			result = st.executeQuery(editor.getQuery().getQueryString());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		List<String> columnNames = editor.getQuery().getSelectList().getSelectedColumnNames();
        
        if(result != null){
            try{
                FileOutputStream os = new FileOutputStream(csvFile);
                ExportToCsv csvExport = new ExportToCsv();
    
                Set<Dataset> userDatasets = new HashSet<Dataset>();
                for(DatasetAccess da : t.getSettingsUser().getDatasetAccesses()) {
                    userDatasets.add(da.getId().getDataset());
                }
                
                if(result.size()>0) {
                    if(result.getColumnCount() == 1)
                    {
                        os.write((getCsvHeaderSwitchNoComma(result.get(0,0), csvExport)+"\n").getBytes());
                    }
                    else
                    {
                        
                        for(int i = 0; i < result.getColumnCount() - 1; i++)
                        {
                            os.write((getCsvHeaderSwitchNoComma(result.get(0, i), csvExport)+",").getBytes());
                        }
        
                        os.write((getCsvHeaderSwitchNoComma(result.get(0, result.getColumnCount() - 1), csvExport)+"\n").getBytes());
                    }
                }
                
                for(int i = 0 ; i < result.size() ; i++)
                {
                    if(result.getColumnCount() == 1)
                    {
                        os.write((getCsvLineSwitchNoComma(result.get(i, 0), csvExport, userDatasets)+"\n").getBytes());
                    }
                    else
                    {
                        for(int j = 0; j <result.getColumnCount() - 1; j++)
                        {
                            os.write((getCsvLineSwitchNoComma(result.get(i, j), csvExport, userDatasets)+",").getBytes());
                        }
                        os.write((getCsvLineSwitchNoComma(result.get(i, result.getColumnCount() - 1), csvExport, userDatasets)+"\n").getBytes());
                    }
                }
                
                os.close();
                st.close();
                return true;
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        t.commit();
        editor.getQuery().setSelectList(oldList);
        
        return false;
    }
    
    public String getCsvLineSwitchNoComma(Object o, ExportToCsv csvExport, Set<Dataset> datasets) {
        String temp = csvExport.getCsvLineSwitch(o, datasets);
        if(temp==null)
            return temp;
        temp = temp.substring(0, temp.length()-1);
        return temp;
    }
    
    public String getCsvHeaderSwitchNoComma(Object o, ExportToCsv csvExport) {
        String temp = csvExport.getCsvHeaderSwitch(o);
        if(temp==null)
            return temp;
        temp = temp.substring(0, temp.length()-1);
        return temp;
    }    
    

	private String getFileName() {
		return tr("file.query.querytool").value();
	}	
}
