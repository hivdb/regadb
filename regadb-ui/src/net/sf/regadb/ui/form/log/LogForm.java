package net.sf.regadb.ui.form.log;

import java.io.File;
import java.util.Date;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextArea;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.file.FileUtils;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WFileResource;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.i8n.WMessage;

public class LogForm extends FormWidget {
    private File logFile = null;

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
    
    
    public LogForm(WMessage formName, InteractionState interactionState, File logFile) {
        super(formName, interactionState);
        this.logFile = logFile;
        
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
        fileDateDF = new DateField(InteractionState.Viewing,this);
        propertiesTable.addLineToTable(fileDateL, fileDateDF);
        
        fileSizeL = new Label(tr("form.log.size"));
        fileSizeTF = new TextField(getInteractionState(),this);
        propertiesTable.addLineToTable(fileSizeL, fileSizeTF);
        
        fileDownloadL = new Label(tr("form.log.download"));
        fileDownloadA = new WAnchor();
        propertiesTable.addLineToTable(new WWidget[]{fileDownloadL, fileDownloadA});

        fileContentTA = new TextArea(InteractionState.Viewing,this);
        fileContentTA.setStyleClass("code-area");
        contentTable.putElementAt(0,0,fileContentTA);
    }
    
    protected void fillData(){
        if(exists(logFile)){
            fileNameTF.setText(logFile.getName());
            fileDateDF.setDate(new Date(logFile.lastModified()));
            fileSizeTF.setText(FileUtils.getHumanReadableFileSize(logFile));
            
            fileDownloadA.label().setText(lt(logFile.getName() +" ["+ new Date(System.currentTimeMillis()).toString() +"]"));
            fileDownloadA.setRef(new WFileResource("text/txt", logFile.getAbsolutePath()).generateUrl());
            
            try{
                fileContentTA.setText(parseContent(logFile));
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
        try{
            content = org.apache.commons.io.FileUtils.readFileToString(f, null);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public void cancel() {
    }

    @Override
    public WMessage deleteObject() {
        if(exists(logFile)){
            logFile.delete();
        }
        
        return null;
    }

    @Override
    public void redirectAfterDelete() {
        RegaDBMain.getApp().getTree().getTreeContent().logSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().logSelectedItem.setSelectedItem(null);
    }

    @Override
    public void saveData() {
    }
}
