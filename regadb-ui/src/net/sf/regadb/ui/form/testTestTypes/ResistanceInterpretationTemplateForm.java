package net.sf.regadb.ui.form.testTestTypes;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMemoryResource;
import net.sf.witty.wt.i8n.WMessage;

import org.apache.commons.io.FileUtils;

public class ResistanceInterpretationTemplateForm extends FormWidget
{
    private ResistanceInterpretationTemplate resRepTemplate_;
    
    private WGroupBox templateGroup_;
    private FormTable templateTable_;
    private Label templateL;
    private TextField templateTF;
    private Label reportL;
    private FileUpload upload;
    
    public ResistanceInterpretationTemplateForm(InteractionState interactionState, WMessage formName, boolean literal, ResistanceInterpretationTemplate resRepTemplate) 
    {
        super(formName, interactionState, literal);
        resRepTemplate_ = resRepTemplate;
        
        init();
        filldata();
    }
    
    private void init()
    {
        templateGroup_ = new WGroupBox(tr("general.group.general"), this);
        templateTable_ = new FormTable(templateGroup_);
        templateL = new Label(tr("general.name"));
        templateTF = new TextField(getInteractionState()==InteractionState.Editing?InteractionState.Viewing:getInteractionState(), this);
        templateTF.setMandatory(true);
        templateTable_.addLineToTable(templateL, templateTF);
        
        reportL = new Label(tr("report.report"));
        final int row = templateTable_.numRows();
        templateTable_.putElementAt(row, 0, reportL);
        
        upload = new FileUpload(getInteractionState(), this);
        templateTable_.putElementAt(row, 1, upload);
        
        
        
        if(getInteractionState()==InteractionState.Adding || getInteractionState()==InteractionState.Editing)
        {
        	upload.getFileUpload().uploaded.addListener(new SignalListener<WEmptyEvent>() {
				public void notify(WEmptyEvent a) {
                    try 
                    {
                        resRepTemplate_.setDocument(FileUtils.readFileToByteArray(new File(upload.getFileUpload().spoolFileName())));
                        resRepTemplate_.setFilename(lastPartOfFilename(upload.getFileUpload().clientFileName()));
                    } 
                    catch (IOException e) 
                    {
                        e.printStackTrace();
                    }
                    upload.setAnchor(lt(resRepTemplate_.getFilename()), new WMemoryResource("application/rtf", resRepTemplate_.getDocument()).generateUrl());
				}
        	});
        }
        
        addControlButtons();
    }
    
    private String lastPartOfFilename(final String fileName)
    {
        String toReturn = fileName;
        int pathSeparatorPos = fileName.lastIndexOf(File.separatorChar);
        if(pathSeparatorPos!=-1)
            toReturn = fileName.substring(fileName.lastIndexOf(File.separatorChar)+1);
        return toReturn;
    }
    

    
    private void filldata()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            resRepTemplate_ = new ResistanceInterpretationTemplate();
        }
        else
        {
            templateTF.setText(resRepTemplate_.getName());
            upload.setAnchor(lt(resRepTemplate_.getFilename()), new WMemoryResource("application/rtf", resRepTemplate_.getDocument()).generateUrl());
        }
    }
    
    @Override
    public void saveData()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        boolean notExists = getInteractionState()==InteractionState.Editing?true:t.getResRepTemplate(templateTF.text())==null;
        
        if(notExists && upload.getFileUpload().spoolFileName()!=null)
        {
            resRepTemplate_.setName(templateTF.text());
            update(resRepTemplate_, t);
            t.commit();
            
            RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelected.setSelectedItem(resRepTemplate_);
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelected, RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateView);
        }
        else if(upload.getFileUpload().spoolFileName()==null)
        {
            MessageBox.showWarningMessage(tr("message.report.noFileSpecified"));
        }
        else
        {
            MessageBox.showWarningMessage(tr("message.report.alreadyexists"));
        }
    }
    
    @Override
    public void cancel()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().resRepTemplate, RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelect);
        }
        else
        {
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelected, RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateView);
        }
    }
    
    @Override
    public WMessage deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        t.delete(resRepTemplate_);
        
        t.commit();
        
        return null;
    }
    
    @Override
    public void redirectAfterDelete() 
    {
        RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelected.setSelectedItem(null);
    }
}
