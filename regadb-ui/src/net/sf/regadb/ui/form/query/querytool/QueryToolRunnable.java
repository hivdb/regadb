package net.sf.regadb.ui.form.query.querytool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.FieldSelection;
import com.pharmadm.custom.rega.queryeditor.FromVariable;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.OutputSelection;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.QueryEditor;
import com.pharmadm.custom.rega.queryeditor.Selection;
import com.pharmadm.custom.rega.queryeditor.SelectionStatusList;
import com.pharmadm.custom.rega.queryeditor.TableSelection;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.exportCsv.ExportToCsv;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.witty.wt.WFileResource;
import net.sf.witty.wt.i8n.WMessage;

public class QueryToolRunnable implements Runnable {
	private QueryEditor editor;
	private Login login;
	private String fileName;
	private Status status;
	private File csvFile;
	private String statusMsg;
	
	private enum Status {
		waiting,
		running,
		finished,
		failed
	}
	
	public QueryToolRunnable(Login copiedLogin, String fileName, QueryEditor editor) {
		this.fileName = fileName;
		this.login = copiedLogin;
		this.editor = editor;
		status = Status.waiting;
	}
	
	public boolean isDone() {
		return status == Status.finished;
	}
	
	public boolean isFailed() {
		return status == Status.failed;
	}
	
	public WMessage getStatusText() {
		if (status == Status.running) {
			return new WMessage("form.query.querytool.label.status.running");			
		}
		else if (status == Status.finished) {
			return new WMessage("form.query.querytool.label.status.finished");			
		}
		else if (status == Status.failed) {
			return new WMessage(new WMessage("form.query.querytool.label.status.failed").value() + (statusMsg == null?"":": " + new WMessage(statusMsg).value() ), true);			
		}
		return new WMessage("form.query.querytool.label.status.initial");
	}

	public void run() {
        csvFile =  getOutputFile();
        status = Status.running;  
        
        if(process(csvFile)){
            status = Status.finished;
        }
        else{
            status = Status.failed;
        }
        
		QueryToolThread.removeQueryThread(fileName);
	}
	
    private File getResultDir(){
        File queryDir = new File(RegaDBSettings.getInstance().getPropertyValue("regadb.query.resultDir") + File.separatorChar + "querytool");
        if(!queryDir.exists()){
        	queryDir.mkdir();
        }
        return queryDir;
    }
    
    private File getOutputFile() {
        File queryDir = getResultDir();
        return new File(queryDir.getAbsolutePath() + File.separatorChar + fileName);
    }  
	
    public String getDownloadLink(){
    	if (isDone()) {
    		return new WFileResource("application/csv", csvFile.getAbsolutePath()).generateUrl();
    	}
    	return "";
    }		
	
    @SuppressWarnings("unchecked")
    protected boolean process(File csvFile){
    	boolean success = false;
        
        Transaction t = login.createTransaction();
        
        try{
    		// do the query with all tables we need fields of selected 
    		// so we can filter based on the objects
    		SelectionStatusList oldList = editor.getQuery().getSelectList();
    		SelectionStatusList newList = createSelectionList(oldList);
    		newList.setQuery(editor.getQuery());
    		editor.getQuery().setSelectList(newList);
    		List result = null;
			result = getQueryResult(editor.getQuery(), t);
    		editor.getQuery().setSelectList(oldList);
    		
            if(result != null){
        		List<Selection> selections = getFlatSelectionList(newList);

        		Set<Dataset> userDatasets = new HashSet<Dataset>();
                for(DatasetAccess da : t.getSettingsUser().getDatasetAccesses()) {
                    userDatasets.add(da.getId().getDataset());
                }
            	
	            FileOutputStream os = new FileOutputStream(csvFile);
	            ExportToCsv csvExport = new ExportToCsv();
	          
	            os.write(getHeaderLine(selections, newList.getSelectedColumnNames()).getBytes());
	          
            	for (Object o : result) {
        			os.write(getLine(o, selections, userDatasets, csvExport).getBytes());
            	}
	            
	            os.close();
	            success = true;
            }
        }
        catch(IOException e){
        	statusMsg = "form.query.querytool.label.status.failed.writeerror";
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
        	statusMsg = "form.query.querytool.label.status.failed.memoryerror";
			e.printStackTrace();
		} catch (SQLException e) {
        	statusMsg = "form.query.querytool.label.status.failed.sqlerror";
			e.printStackTrace();
		} catch (IllegalStateException e) {
			statusMsg = "form.query.querytool.label.status.failed.sqlerror";
			e.printStackTrace();
		} catch (Exception e) {
			statusMsg = null;
			e.printStackTrace();
		}
        t.commit();
        
        return success;
    }
    


	
    private String getLine(Object o, List<Selection> selections, Set<Dataset> userDatasets, ExportToCsv csvExport) {
		boolean lastTableAccess = false;
		boolean nullLine = true;
		
		String line = "";
		Object[] array = getObjectArray(o, selections);
		
		for (int j = 0 ; j < array.length ; j++) {
			if (selections.get(j) instanceof TableSelection) {
				lastTableAccess = (csvExport.getCsvLineSwitch(array[j], userDatasets) != null);
			}
			else if (selections.get(j) instanceof FieldSelection || selections.get(j) instanceof OutputSelection) {
				if (lastTableAccess) {
					line += array[j];
					nullLine = false;
				}
				else {
					line += "null";
				}
				line += ";";
			}
		}
    	
		if (nullLine) {
			line = "";
		}
		
		if (!line.isEmpty()) {
			line= line.substring(0, line.length()-1) + "\n";			
		}
		return line;
    }
    
    private Object[] getObjectArray(Object o, List<Selection> selections) {
  		Object[] array;
  		
  		if (selections.size() == 1) {
  			array = new Object[1];
  			array[0] = o;
  		}
  		else {
  			 array = (Object[])o;              			
  		}
  		return array;
  	}

	@SuppressWarnings("unchecked")
	private List getQueryResult(Query query, Transaction t) throws SQLException, OutOfMemoryError {
		String qstr = editor.getQuery().getQueryString();
		System.err.println(qstr);
		org.hibernate.Query q = t.createQuery(qstr);
		List result = q.list();
		return result;
    }
    
    private String getHeaderLine(List<Selection> selections, List<String> columnNames) {
        String indexLine = "";
        for (int i = 0 ; i < selections.size(); i++) {
        	if (!(selections.get(i) instanceof TableSelection)) {
        		indexLine += columnNames.get(i) + ";";
        	}
        }   
        if (! indexLine.isEmpty()) {
        	indexLine = indexLine.substring(0, indexLine.length()-1);       	
        }
        return indexLine + "\n";
    }
    
	/** 
	 * Decomposed the composed selection in the given selection status list
     * into their components and puts all the simple selections in a list
     */
    private List<Selection> getFlatSelectionList(SelectionStatusList list) {
		List<Selection> selections = new ArrayList<Selection>();
		for (Selection sel : list.getSelections()) {
			if (sel.isSelected()) {
				selections.add(sel);
			}
			if (sel instanceof TableSelection) {
				for (Selection subSel : sel.getSubSelections()) {
					if (subSel.isSelected()) {
						selections.add(subSel);
					}
				}
			}
		}
		return selections;
	}
    
    /**
     * create a copy of the given {@link SelectionStatusList} where
     * all tables are selected
     * @param list
     * @return
     */
    private SelectionStatusList createSelectionList(SelectionStatusList list) {
		SelectionStatusList selectList = new SelectionStatusList();
    	
		for (Selection  sel : list.getSelections()) {
			Selection clone = null;
			if (sel instanceof TableSelection) {
				if (sel.isSelected()) {
					clone = new TableSelection((OutputVariable) sel.getObjectSpec(), sel.isSelected());
					List<Selection> subSelections = new ArrayList<Selection>();
					List<Selection> origSubSelections = new ArrayList<Selection>();
					subSelections.addAll(clone.getSubSelections());
					origSubSelections.addAll(sel.getSubSelections());
					
					
					boolean selected = false;
					for (int i = 0 ; i < origSubSelections.size() ; i++) {
						selected = origSubSelections.get(i).isSelected() || selected;
						subSelections.get(i).setSelected(origSubSelections.get(i).isSelected());
					}
					
					// select the clone if one of the children is selected
					if (selected) {
						clone.setSelected(true);
					}
				}
			}
			else if (sel instanceof OutputSelection) {
				OutputVariable ovar = (OutputVariable) sel.getObjectSpec();
				TableSelection outputTable = getTableSelectionFromOutputVariable(ovar);
				if (outputTable != null && sel.isSelected()) {
					selectList.getSelections().add(outputTable);
				}
				clone = new OutputSelection(ovar, sel.isSelected());
			}
			
			if (clone != null) {
				selectList.getSelections().add(clone);
			}
		}
		return selectList;
    }
    
    
    /**
     * finds the table that is the base of this outputvariable
     * and return a selection of this table
     * @param ovar
     * @return
     */
    public TableSelection getTableSelectionFromOutputVariable(OutputVariable ovar) {
		ConfigurableWord firstWord = ovar.getExpression().getWords().get(0);
		TableSelection outputTable = null;
		if (firstWord instanceof FromVariable) {
			outputTable = new TableSelection(ovar, ((FromVariable) firstWord).getTableName());
			outputTable.setSelected(true);
		}
		else if (firstWord instanceof InputVariable) {
			outputTable = getTableSelectionFromOutputVariable(((InputVariable) firstWord).getOutputVariable());
		}
		else if (firstWord instanceof OutputVariable) {
			outputTable = getTableSelectionFromOutputVariable((OutputVariable) firstWord);
		}
		return outputTable;
    }    
}
