package net.sf.regadb.ui.form.testTestTypes;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFileUpload;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMemoryResource;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

import org.apache.commons.io.FileUtils;

public class ResistanceInterpretationTemplateForm extends FormWidget
{
    private ResistanceInterpretationTemplate resRepTemplate_;
    
    private WGroupBox templateGroup_;
    private WTable templateTable_;
    private Label templateL;
    private TextField templateTF;
    private WFileUpload uploadFile_;
    private WPushButton uploadButton_;
    private Label reportL;
    private WAnchor reportLink;
    
    public ResistanceInterpretationTemplateForm(InteractionState interactionState, WMessage formName, ResistanceInterpretationTemplate resRepTemplate) 
    {
        super(formName, interactionState);
        resRepTemplate_ = resRepTemplate;
        
        init();
        filldata();
    }
    
    private void init()
    {
        templateGroup_ = new WGroupBox(tr("form.resistance.report.template.editView.general"), this);
        templateTable_ = new WTable(templateGroup_);
        templateTable_.setStyleClass("editable-table");
        templateL = new Label(tr("form.resistance.report.template.editView.name"));
        templateTF = new TextField(getInteractionState()==InteractionState.Editing?InteractionState.Viewing:getInteractionState(), this);
        templateTF.setMandatory(true);
        addLineToTable(templateTable_, templateL, templateTF);
        
        reportL = new Label(tr("form.resistance.report.template.editView.report"));
        final int row = templateTable_.numRows();
        templateTable_.putElementAt(row, 0, reportL);
        
        reportLink = new WAnchor("dummy", lt(""), templateTable_.elementAt(row, 1));
        reportLink.setStyleClass("link");
        
        if(getInteractionState()==InteractionState.Adding || getInteractionState()==InteractionState.Editing)
        {
            uploadFile_ = new WFileUpload(templateTable_.elementAt(row, 2));
            uploadButton_ = new WPushButton(tr("form.resistance.report.template.uploadButton"), templateTable_.elementAt(row, 3));
            
            uploadButton_.clicked.addListener(new SignalListener<WMouseEvent>()
            {
                public void notify(WMouseEvent a)
                {
                    uploadFile_.upload();
                    uploadButton_.setEnabled(false);
                }
            });
            
            uploadFile_.uploaded.addListener(new SignalListener<WEmptyEvent>()
            {
                public void notify(WEmptyEvent a) 
                {
                    reportLink.setHidden(uploadFile_.clientFileName()==null);
                    uploadButton_.setEnabled(true);
                    try 
                    {
                        resRepTemplate_.setDocument(FileUtils.readFileToByteArray(new File(uploadFile_.spoolFileName())));
                        resRepTemplate_.setFilename(lastPartOfFilename(uploadFile_.clientFileName()));
                    } 
                    catch (IOException e) 
                    {
                        e.printStackTrace();
                    }
                    setAnchor();
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
    
    private void setAnchor()
    {
        reportLink.label().setText(lt(resRepTemplate_.getFilename()));
        reportLink.setRef(new WMemoryResource("application/rtf", resRepTemplate_.getDocument()).generateUrl());
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
            setAnchor();
        }
    }
    
    @Override
    public void saveData()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        boolean notExists = getInteractionState()==InteractionState.Editing?true:t.getResRepTemplate(templateTF.text())==null;
        
        if(notExists && uploadFile_.spoolFileName()!=null)
        {
            resRepTemplate_.setName(templateTF.text());
            update(resRepTemplate_, t);
            t.commit();
            
            RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelected.setSelectedItem(resRepTemplate_);
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateSelected, RegaDBMain.getApp().getTree().getTreeContent().resRepTemplateView);
        }
        else if(uploadFile_.spoolFileName()==null)
        {
            MessageBox.showWarningMessage(tr("form.resistance.report.template.warning.noFileSpecified"));
        }
        else
        {
            MessageBox.showWarningMessage(tr("form.resistance.report.template.warning.already.exists"));
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
