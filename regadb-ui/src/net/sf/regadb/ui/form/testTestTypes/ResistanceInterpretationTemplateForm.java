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
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;

import org.apache.commons.io.FileUtils;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WMemoryResource;
import eu.webtoolkit.jwt.WString;

public class ResistanceInterpretationTemplateForm extends FormWidget
{
    private ResistanceInterpretationTemplate resRepTemplate_;
    
    private WGroupBox templateGroup_;
    private FormTable templateTable_;
    private Label templateL;
    private TextField templateTF;
    private Label reportL;
    private FileUpload upload;
    
    public ResistanceInterpretationTemplateForm(InteractionState interactionState, WString formName, ResistanceInterpretationTemplate resRepTemplate) 
    {
        super(formName, interactionState);
        resRepTemplate_ = resRepTemplate;
        
        init();
        filldata();
    }
    
    private void init()
    {
        templateGroup_ = new WGroupBox(tr("form.resistance.report.template.editView.general"), this);
        templateTable_ = new FormTable(templateGroup_);
        templateL = new Label(tr("form.resistance.report.template.editView.name"));
        templateTF = new TextField(getInteractionState()==InteractionState.Editing?InteractionState.Viewing:getInteractionState(), this);
        templateTF.setMandatory(true);
        templateTable_.addLineToTable(templateL, templateTF);
        
        reportL = new Label(tr("form.resistance.report.template.editView.report"));
        final int row = templateTable_.getRowCount();
        templateTable_.putElementAt(row, 0, reportL);
        
        upload = new FileUpload(getInteractionState(), this);
        templateTable_.putElementAt(row, 1, upload);
        
        
        
        if(getInteractionState()==InteractionState.Adding || getInteractionState()==InteractionState.Editing)
        {
        	upload.getFileUpload().uploaded().addListener(this, new Signal.Listener() {
				public void trigger() {
                    try 
                    {
                        resRepTemplate_.setDocument(FileUtils.readFileToByteArray(new File(upload.getFileUpload().getSpoolFileName())));
                        resRepTemplate_.setFilename(lastPartOfFilename(upload.getFileUpload().getClientFileName()));
                    } 
                    catch (IOException e) 
                    {
                        e.printStackTrace();
                    }
                    WMemoryResource memResource = new WMemoryResource("application/rtf");
                    memResource.suggestFileName("template.rtf");
                    memResource.setData(resRepTemplate_.getDocument());
                    upload.setAnchor(resRepTemplate_.getFilename(), memResource);
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
            WMemoryResource memResource = new WMemoryResource("application/rtf");
            memResource.setData(resRepTemplate_.getDocument());
            memResource.suggestFileName(resRepTemplate_.getFilename());
            upload.setAnchor(resRepTemplate_.getFilename(), memResource.generateUrl());
        }
    }
    
    @Override
    public void saveData()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        boolean notExists = getInteractionState()==InteractionState.Editing?true:t.getResRepTemplate(templateTF.text())==null;
        
        if(notExists && !upload.getFileUpload().getSpoolFileName().equals(""))
        {
            resRepTemplate_.setName(templateTF.text());
            update(resRepTemplate_, t);
            t.commit();
            
            RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelected.setSelectedItem(resRepTemplate_);
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelected, RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateView);
        }
        else if(upload.getFileUpload().getSpoolFileName().equals(""))
        {
            UIUtils.showWarningMessageBox(this, tr("form.resistance.report.template.warning.noFileSpecified"));
        }
        else
        {
        	UIUtils.showWarningMessageBox(this, tr("form.resistance.report.template.warning.already.exists"));
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
    public WString deleteObject()
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
