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
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class DatasetAccessForm extends FormWidget
{
    private SettingsUser user_;
    
    private WGroupBox datasetGroup_;
    private EditableTable<DatasetAccess> datasetList_;
    private IDatasetAccessSelectionEditableTable iDatasetAccessSelectionEditableTable_;
    
    public DatasetAccessForm(InteractionState interactionState, WString formName, SettingsUser user)
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
            UIUtils.showWarningMessageBox(this, tr("form.dataset.access.edit.warning"));
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
    public WString deleteObject()
    {
        return null;
    }

    @Override
    public void redirectAfterDelete() 
    {
        
    }
}
