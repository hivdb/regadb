package net.sf.regadb.ui.form.datasetSettings;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.i8n.WMessage;

public class DatasetAccessForm extends FormWidget
{
    private SettingsUser user_;
    
    private WGroupBox datasetGroup_;
    private EditableTable<DatasetAccess> datasetList_;
    private IDatasetAccessSelectionEditableTable iDatasetAccessSelectionEditableTable_;
    
    public DatasetAccessForm(InteractionState interactionState, WMessage formName, boolean literal, SettingsUser user)
    {
        super(formName, interactionState, literal);
        user_ = user;
        
        init();
        
        fillData();
    }
    
    public void init()
    {
        datasetGroup_ = new WGroupBox(tr("general.group.general"), this);
        
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
        List<String> datasets = new ArrayList<String>();
        ArrayList<WWidget> widgets= datasetList_.getAllWidgets(0);
        boolean contains=false;
        for(WWidget widget : widgets)
        {
            if(!(datasets.contains(((TextField)widget).text())))
            {
                datasets.add(((TextField)widget).text());
            }
        }
        if(widgets.size() != datasets.size())
        {
            MessageBox.showWarningMessage(tr("message.dataset.duplicate"));
        }
        else
        {
            Transaction t = RegaDBMain.getApp().createTransaction();
            iDatasetAccessSelectionEditableTable_.setTransaction(t);
            datasetList_.saveData();
            
            update(user_,t);
            t.commit();
            
            RegaDBMain.getApp().getTree().getTreeContent().datasetAccessSelected.setSelectedItem(user_);
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().datasetAccessSelected, RegaDBMain.getApp().getTree().getTreeContent().datasetAccessView);
        }
    }
    
    @Override
    public void cancel()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().datasetAccess, RegaDBMain.getApp().getTree().getTreeContent().datasetAccessSelect);
        }
        else
        {
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().datasetAccessSelected, RegaDBMain.getApp().getTree().getTreeContent().datasetAccessView);
        }
    }
    
    @Override
    public WMessage deleteObject()
    {
        return null;
    }

    @Override
    public void redirectAfterDelete() 
    {
        
    }
}
