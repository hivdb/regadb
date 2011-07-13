package net.sf.regadb.ui.form.testTestTypes;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

import org.apache.commons.io.FileUtils;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WMemoryResource;
import eu.webtoolkit.jwt.WString;

public class ResistanceInterpretationTemplateForm extends ObjectForm<ResistanceInterpretationTemplate>
{
    private WGroupBox templateGroup_;
    private FormTable templateTable_;
    private Label templateL;
    private TextField templateTF;
    private Label reportL;
    private FileUpload upload;
    
    public ResistanceInterpretationTemplateForm(WString formName, InteractionState interactionState,
    		ObjectTreeNode<ResistanceInterpretationTemplate> node, ResistanceInterpretationTemplate resRepTemplate) 
    {
        super(formName, interactionState, node, resRepTemplate);
        
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
                        getObject().setDocument(FileUtils.readFileToByteArray(new File(upload.getFileUpload().getSpoolFileName())));
                        getObject().setFilename(lastPartOfFilename(upload.getFileUpload().getClientFileName()));
                    } 
                    catch (IOException e) 
                    {
                        e.printStackTrace();
                    }
                    WMemoryResource memResource = new WMemoryResource("application/rtf");
                    memResource.suggestFileName("template.rtf");
                    memResource.setData(getObject().getDocument());
                    upload.setAnchor(getObject().getFilename(), memResource);
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
        	setObject(new ResistanceInterpretationTemplate());
        }
        else
        {
            templateTF.setText(getObject().getName());
            WMemoryResource memResource = new WMemoryResource("application/rtf");
            memResource.setData(getObject().getDocument());
            upload.setAnchor(getObject().getFilename(), memResource.generateUrl());
        }
    }
    
    @Override
    public void saveData()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        boolean notExists = getInteractionState()==InteractionState.Editing?true:t.getResRepTemplate(templateTF.text())==null;
        
        if(notExists && !upload.getFileUpload().getSpoolFileName().equals(""))
        {
        	getObject().setName(templateTF.text());
            update(getObject(), t);
            t.commit();
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
    }
    
    @Override
    public WString deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        t.delete(getObject());
        
        t.commit();
        
        return null;
    }
}
