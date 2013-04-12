package net.sf.regadb.ui.form.datasetSettings;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class DatasetAccessForm extends ObjectForm<SettingsUser>
{
    private WGroupBox datasetGroup_;
    private EditableTable<DatasetAccess> datasetList_;
    private IDatasetAccessSelectionEditableTable iDatasetAccessSelectionEditableTable_;
    
    public DatasetAccessForm(WString formName, InteractionState interactionState, ObjectTreeNode<SettingsUser> node, SettingsUser user)
    {
        super(formName, interactionState, node, user);
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
        for(DatasetAccess da : getObject().getDatasetAccesses())
        {
            das.add(da);
        }
        
        iDatasetAccessSelectionEditableTable_ = new IDatasetAccessSelectionEditableTable(this, getObject());
        datasetList_ = new EditableTable<DatasetAccess>(datasetGroup_, iDatasetAccessSelectionEditableTable_, das);
    }
    
    @Override
    public void saveData()
    {
        List<String> datasets = new ArrayList<String>();
        ArrayList<WWidget> widgets= datasetList_.getAllWidgets(0);

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
            
            update(getObject(),t);
            t.commit();
        }
    }
    
    @Override
    public void cancel()
    {
    }
    
    @Override
    public WString deleteObject()
    {
        return null;
    }
}
