package net.sf.regadb.ui.framework.forms.administrator;

import java.io.Serializable;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.WGroupBox;

public class UpdateForm extends FormWidget
{
    private WGroupBox testGroup_ = new WGroupBox(tr("form.update.central.server.test"));
    private WGroupBox attributesGroup_ = new WGroupBox(tr("form.update.central.server.attribute"));
    private WGroupBox drugsGroup_ = new WGroupBox(tr("form.update.central.server.drug"));
    
    public UpdateForm(InteractionState interactionState)
    {
        super(tr("form.update.central.server"), interactionState);
        init();
    }
    
    public void init()
    {
        addWidget(testGroup_);
        addWidget(attributesGroup_);
        addWidget(drugsGroup_);
    }
    
    @Override
    public void saveData()
    {        
        
    }
    
    @Override
    public void cancel()
    {
        
    }
    
    @Override
    public void deleteObject()
    {
        
    }

    @Override
    public void redirectAfterDelete() 
    {
        
    }
}
