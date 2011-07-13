package net.sf.regadb.ui.form.log;

import java.io.File;
import java.util.Date;

import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextArea;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.file.FileUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WFileResource;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WWidget;

public class LogForm extends ObjectForm<File> {
	
    private WGroupBox propertiesGroup;
    private WGroupBox contentGroup;
    
    private FormTable propertiesTable;
    private WTable contentTable;

    private Label fileNameL;
    private TextField fileNameTF;
    
    private Label fileDateL;
    private DateField fileDateDF;
    
    private Label fileSizeL;
    private TextField fileSizeTF;
    
    private Label fileDownloadL;
    private WAnchor fileDownloadA;
    
    private TextArea fileContentTA;
    
    
    public LogForm(WString formName, InteractionState interactionState, ObjectTreeNode<File> node, File logFile) {
        super(formName, interactionState, node, logFile);
        init();
        fillData();
        addControlButtons();
    }
    
    protected void init(){
        propertiesGroup = new WGroupBox(tr("form.log.properties"),this);
        contentGroup = new WGroupBox(tr("form.log.content"),this);
        
        propertiesTable = new FormTable(propertiesGroup);
        contentTable = new WTable(contentGroup);
        
        fileNameL = new Label(tr("form.log.name"));
        fileNameTF = new TextField(getInteractionState(),this);
        propertiesTable.addLineToTable(fileNameL, fileNameTF);

        fileDateL = new Label(tr("form.log.date"));
        fileDateDF = new DateField(InteractionState.Viewing,this, RegaDBSettings.getInstance().getDateFormat());
        propertiesTable.addLineToTable(fileDateL, fileDateDF);
        
        fileSizeL = new Label(tr("form.log.size"));
        fileSizeTF = new TextField(getInteractionState(),this);
        propertiesTable.addLineToTable(fileSizeL, fileSizeTF);
        
        fileDownloadL = new Label(tr("form.log.download"));
        fileDownloadA = new WAnchor();
        propertiesTable.addLineToTable(new WWidget[]{fileDownloadL, fileDownloadA});

        fileContentTA = new TextArea(InteractionState.Viewing,this);
        fileContentTA.setStyleClass("code-area");
        contentTable.getElementAt(0,0).addWidget(fileContentTA);
    }
    
    protected void fillData(){
        if(exists(getObject())){
            fileNameTF.setText(getObject().getName());
            fileDateDF.setDate(new Date(getObject().lastModified()));
            fileSizeTF.setText(FileUtils.getHumanReadableFileSize(getObject()));
            
            if(!getObject().isDirectory()){
	            fileDownloadA.setText(getObject().getName() +" ["+ new Date(System.currentTimeMillis()).toString() +"]");
	            fileDownloadA.setRef(new WFileResource("text/txt", getObject().getAbsolutePath(), null).generateUrl());
            }
            try{
                fileContentTA.setText(parseContent(getObject()));
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    private boolean exists(File f){
        return (f != null && f.exists());
    }
    
    private String parseContent(File f){
        String content = null;
        if(!f.isDirectory()){
	        try{
	            content = org.apache.commons.io.FileUtils.readFileToString(f, null);
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }
        }
        else{
        	StringBuilder sb = new StringBuilder("directory listing:\n");
        	for(File ff : f.listFiles())
        		sb.append(ff.isDirectory() ? "+  " : "-  ")
        		  .append(ff.getName())
        		  .append("\n");
        	content = sb.toString();
        }
        return content;
    }

    @Override
    public void cancel() {
    }

    @Override
    public WString deleteObject() {
        if(exists(getObject())){
        	getObject().delete();
        }
        
        return null;
    }

    @Override
    public void saveData() {
    }
}
