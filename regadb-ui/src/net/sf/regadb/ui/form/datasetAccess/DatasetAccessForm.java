package net.sf.regadb.ui.form.datasetAccess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.i8n.WMessage;

public class DatasetAccessForm extends FormWidget
{
    private SettingsUser user_;
    
    private WGroupBox datasetGroup_;
    private EditableTable<DatasetAccess> datasetList_;
    private IDatasetAccessSelectionEditableTable iDatasetAccessSelectionEditableTable_;
    
    public DatasetAccessForm(InteractionState interactionState, WMessage formName, SettingsUser user)
    {
        super(formName, interactionState);
        user_ = user;
        
        init();
        
        fillData();
    }
    
    public void init()
    {
        datasetGroup_ = new WGroupBox(tr("form.dataset.editView.general"), this);
        
        addControlButtons();
    }
    
    private void fillData()
    {        
        List<DatasetAccess> das = new ArrayList<DatasetAccess>();
        for(DatasetAccess da : user_.getDatasetAccesses())
        {
            das.add(da);
        }
        
        iDatasetAccessSelectionEditableTable_ = new IDatasetAccessSelectionEditableTable(this, user_);
        datasetList_ = new EditableTable<DatasetAccess>(datasetGroup_, iDatasetAccessSelectionEditableTable_, das);
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
