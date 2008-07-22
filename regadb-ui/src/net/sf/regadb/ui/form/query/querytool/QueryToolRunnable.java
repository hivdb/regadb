package net.sf.regadb.ui.form.query.querytool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.exception.SQLGrammarException;

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
import com.pharmadm.custom.rega.queryeditor.port.QueryStatement;
import com.pharmadm.custom.rega.queryeditor.port.ScrollableQueryResult;
import com.pharmadm.custom.rega.queryeditor.port.hibernate.HibernateStatement;

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
	private String statusMsg = "";
	private QueryStatement statement;
	private Object mutex = new Object();
	private HashMap<String, String> errors;
	
	private enum Status {
		WAITING,
		RUNNING,
		FINISHED,
		FAILED,
		CANCELED
	}
	
	public QueryToolRunnable(Login copiedLogin, String fileName, QueryEditor editor) {
		this.fileName = fileName;
		this.login = copiedLogin;
		this.editor = editor;
		status = Status.WAITING;
		errors = new HashMap<String, String>();
		errors.put("write_error", new WMessage("form.query.querytool.label.status.failed.writeerror").value());
		errors.put("memory_error", new WMessage("form.query.querytool.label.status.failed.memoryerror").value());
		errors.put("sql_error", new WMessage("form.query.querytool.label.status.failed.sqlerror").value());
		errors.put("type_error", new WMessage("form.query.querytool.label.status.failed.typeerror").value());
	}
	
	public boolean isDone() {
		return status == Status.FINISHED;
	}
	
	public boolean isFailed() {
		return status == Status.FAILED;
	}
	
	public WMessage getStatusText() {
		if (status == Status.RUNNING) {
			return new WMessage(new WMessage("form.query.querytool.label.status.running").value() + statusMsg, true);			
		}
		else if (status == Status.FINISHED) {
			return new WMessage(new WMessage("form.query.querytool.link.result").value() + statusMsg, true);			
		}
		else if (status == Status.FAILED) {
			return new WMessage(new WMessage("form.query.querytool.label.status.failed").value() + statusMsg, true);			
		}
		else if (status == Status.CANCELED) {
			return new WMessage(new WMessage("form.query.querytool.label.status.canceling").value() + statusMsg, true);			
		}
		return new WMessage("form.query.querytool.label.status.initial");
	}

	public void run() {
        csvFile =  getOutputFile();

        if(process(csvFile)){
            status = Status.FINISHED;
        }
        else{
            status = Status.FAILED;
        }
        
		QueryToolThread.removeQueryThread(fileName);
	}
	
    private File getResultDir(){
        File queryDir = new File(RegaDBSettings.getInstance().getPropertyValue("regadb.query.resultDir") + File.separator + "querytool");
        if(!queryDir.exists()){
        	 queryDir.mkdirs();
        }
        return queryDir;
    }
    
    private File getOutputFile() {
        File queryDir = getResultDir();
        return new File(queryDir.getAbsolutePath()  + File.separator + fileName);
    }  
	
    public String getDownloadLink(){
    	if (isDone()) {
    		return new WFileResource("application/csv", csvFile.getAbsolutePath()).generateUrl();
    	}
    	return "";
    }		
	
    private boolean process(File csvFile){
    	boolean success = false;
        
    	Transaction t = login.createTransaction();
    	statement = new HibernateStatement(t);
    	
        try{
        	// create a copy of the query editor so the user can work on
        	// his query while this thread is running
        	QueryEditor newEditor = (QueryEditor) editor.clone();

        	// do the query with all tables we need fields of selected 
    		// so we can filter based on the objects
    		SelectionStatusList oldList = newEditor.getQuery().getSelectList();
    		SelectionStatusList newList = createSelectionList(oldList);
    		newList.setQuery(newEditor.getQuery());
    		newEditor.getQuery().setSelectList(newList);
    		ScrollableQueryResult result = getQueryResult(newEditor.getQuery(), statement);
			newEditor.getQuery().setSelectList(oldList);
	        status = Status.RUNNING;  
			
            if(result != null){
        		List<Selection> selections = getFlatSelectionList(newList);

        		Set<Dataset> userDatasets = new HashSet<Dataset>();
                for(DatasetAccess da : t.getSettingsUser().getDatasetAccesses()) {
                    userDatasets.add(da.getId().getDataset());
                }
            	
	            FileOutputStream os = new FileOutputStream(csvFile);
	            ExportToCsv csvExport = new ExportToCsv();
				Set<Integer> accessiblePatients = getAccessiblePatients(t);
	          
	            os.write(getHeaderLine(selections, newList.getSelectedColumnNames()).getBytes());
	          
	            int lines = 0;
	            int writtenLines = 0;
            	while (!result.isLast() && status != Status.CANCELED) {
            		Object[] o = null;
            		synchronized (mutex) {
                		o = result.get();
					}
            		writtenLines+= (processLine(o, t, os, csvExport, selections, userDatasets, accessiblePatients)?1:0);
            		lines++;
            		statusMsg = " (" + writtenLines + ")";
            	}
	            
	            os.close();
        		statusMsg = " (" + writtenLines + ")";
	            success = true;
            }
        }
        catch(IOException e){
        	statusMsg = ": " + errors.get("write_error");
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
        	statusMsg = ": " + errors.get("memory_error");
			e.printStackTrace();
		} catch (SQLException e) {
        	statusMsg = ": " + errors.get("sql_error");
			e.printStackTrace();
		} catch (IllegalStateException e) {
			statusMsg = ": " + errors.get("sql_error");
			e.printStackTrace();
		} catch (SQLGrammarException e ) {
			statusMsg = ": " + errors.get("sql_error");
			e.printStackTrace();
		}
		catch (ClassCastException e ) {
			statusMsg = ": " + errors.get("type_error");
			e.printStackTrace();
		}		
		catch (Exception e) {
			statusMsg = ": " + e.getMessage();
			e.printStackTrace();
		}
		statement.close();
    	t.clearCache();
        return success;
    }
    

    private Set<Integer> getAccessiblePatients(Transaction t) {
		ScrollableQueryResult result = new HibernateStatement(t).executeScrollableQuery("select pd.id.patient.patientIi from PatientDataset pd where pd.id.dataset.settingsUser.uid = '" + login.getUid() + "'", null);
		Set<Integer> results = new HashSet<Integer>();
		while (!result.isLast()) {
			results.add((Integer) result.get()[0]);
		}
		return results;
	}

	private boolean processLine(Object[] o, Transaction t, FileOutputStream os, ExportToCsv csvExport, List<Selection> selections, Set<Dataset> userDatasets, Set<Integer> accessiblePatients) throws IOException {
		String line = getLine(o, selections, userDatasets, csvExport, accessiblePatients);
		t.clearCache(o);
		if (line != "") {
			os.write(line.getBytes());
			return true;
		}
		return false;
    }
	
    private String getLine(Object[] array, List<Selection> selections, Set<Dataset> userDatasets, ExportToCsv csvExport, Set<Integer> accessiblePatients) {
		boolean lastTableAccess = false;
		boolean nullLine = true;
		
		String line = "";
		
		for (int j = 0 ; j < array.length ; j++) {
			if (selections.get(j) instanceof TableSelection) {
				lastTableAccess = (csvExport.getCsvLineSwitch(array[j], userDatasets, accessiblePatients) != null);
			}
			else if (selections.get(j) instanceof FieldSelection || selections.get(j) instanceof OutputSelection) {
				// if the first element is an outputselection selection list
				// changes made earlier guarantee that it is a static value
				// so it can be outputted regardless of access
				if (lastTableAccess || j == 0 && selections.get(j) instanceof OutputSelection) {
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
		
		if (line.length() != 0) {
			line= line.substring(0, line.length()-1) + "\n";			
		}
		return line;
    }

    private ScrollableQueryResult getQueryResult(Query query, QueryStatement statement) throws SQLException, OutOfMemoryError {
		String qstr = query.getQueryString();
		System.err.println(qstr);
		return statement.executeScrollableQuery(qstr, query.getPreparedParameters());
    }
    
    private String getHeaderLine(List<Selection> selections, List<String> columnNames) {
        String indexLine = "";
        for (int i = 0 ; i < selections.size(); i++) {
        	if (!(selections.get(i) instanceof TableSelection)) {
        		indexLine += columnNames.get(i) + ";";
        	}
        }   
        if (indexLine.length() != 0) {
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
    private TableSelection getTableSelectionFromOutputVariable(OutputVariable ovar) {
		ConfigurableWord firstWord = ovar.getExpression().getWords().get(0);
		TableSelection outputTable = null;
		if (firstWord instanceof FromVariable) {
			OutputVariable newOvar = new OutputVariable(ovar.getObject());
			newOvar.getExpression().addFromVariable((FromVariable) firstWord);
			outputTable = new TableSelection(newOvar, ((FromVariable) firstWord).getObject());
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
    
    public void cancel() {
    	synchronized (mutex) {
        	if (status == Status.RUNNING) {
        		status = Status.CANCELED;
        		statement.cancel();
        	}
		}
    }

	public boolean isRunning() {
		return status == Status.RUNNING;
	}
}
