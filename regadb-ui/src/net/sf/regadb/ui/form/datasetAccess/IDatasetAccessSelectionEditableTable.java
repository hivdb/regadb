package net.sf.regadb.ui.form.datasetAccess;

import java.util.Set;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.DatasetAccessId;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.i8n.WMessage;

public class IDatasetAccessSelectionEditableTable implements IEditableTable<DatasetAccess>
{
    private FormWidget form_;
    private SettingsUser user_;
    private Transaction transaction_;
    private Set<DatasetAccess> currentUserDatasetAccess;
    private SettingsUser currentUser_;
    
    private static final String [] headers_ = {"editableTable.datasetAccess.colName.dataset", "editableTable.datasetAccess.colName.rights", "editableTable.datasetAccess.colName.providerUser"};
    
    public IDatasetAccessSelectionEditableTable(FormWidget form, SettingsUser user)
    {
        form_ = form;
        user_ = user;
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        currentUser_ = t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid());
        currentUserDatasetAccess = currentUser_.getDatasetAccesses();            
        t.commit();
    }
    
    public void addData(WWidget[] widgets)
    {
        TextField dsTF = (TextField)widgets[0];
        WComboBox priv = (WComboBox)widgets[1];
        Privileges privillege = ((DataComboMessage<Privileges>)priv.currentText()).getValue();
        
        for(DatasetAccess cuda : currentUserDatasetAccess)
        {
            if(cuda.getId().getDataset().getDescription().equals(dsTF.text()))
            {
                DatasetAccess dsa = new DatasetAccess(new DatasetAccessId(user_, cuda.getId().getDataset()),privillege.getValue(), ((TextField)widgets[2]).text());
                user_.getDatasetAccesses().add(dsa);
            }
        }
    }

    public void changeData(DatasetAccess da, WWidget[] widgets)
    {
        WComboBox priv = (WComboBox)widgets[1];
        Privileges privillege = ((DataComboMessage<Privileges>)priv.currentText()).getValue();
        da.setPermissions(privillege.getValue());
    }
    
    public void deleteData(DatasetAccess da)
    {
        user_.getDatasetAccesses().remove(da);
        if(da!=null)
        {
            transaction_.delete(da);
        }
    }
    
    public InteractionState getInteractionState()
    {
        return form_.getInteractionState();
    }

    public String[] getTableHeaders()
    {
        return headers_;
    }
    
    public WWidget[] getWidgets(DatasetAccess da)
    {
        WWidget[] widgets = new WWidget[3];
        
            //dataset
            TextField datasetTF = new TextField(InteractionState.Viewing, form_);
            datasetTF.setText(da.getId().getDataset().getDescription());
            widgets[0] = datasetTF;

            //priveleges
            DatasetAccess loggedInDatasetAccess = null;
            for(DatasetAccess dsa : currentUserDatasetAccess)
            {
                if(dsa.getId().getDataset().getDescription().equals(da.getId().getDataset().getDescription()))
                {
                    loggedInDatasetAccess = dsa;
                    break;
                }
            }
            boolean isProvider = false;
            if(da.getProvider().equalsIgnoreCase(currentUser_.getUid()))
            {
                isProvider = true;
            }
            Privileges daPermissions = Privileges.getPrivilege(da.getPermissions());

            if(getInteractionState()==InteractionState.Viewing)
            {
                TextField rightTF = new TextField(InteractionState.Viewing, form_);
                rightTF.setText(getPrivilegeString(daPermissions));
                widgets[1] = rightTF;
            }
            else
            {
                WComboBox comboRights = new WComboBox();
                widgets[1] = comboRights;
                
                DataComboMessage<Privileges> selected = new DataComboMessage<Privileges>(daPermissions, getPrivilegeString(daPermissions));
                comboRights.addItem(selected);
                
                if(loggedInDatasetAccess!=null)
                {
                    Privileges loggedInPermissions = Privileges.getPrivilege(loggedInDatasetAccess.getPermissions());
                    if(isProvider)
                    {
                        for(int i = 1; i<daPermissions.getValue()&&i<=loggedInPermissions.getValue(); i++)
                        {
                            Privileges p = Privileges.getPrivilege(i);
                            comboRights.insertItem(i-1, new DataComboMessage<Privileges>(p,getPrivilegeString(p)));
                        }
                    }
                    
                    for(int i = daPermissions.getValue()+1; i<Privileges.values().length+1&&i<=loggedInPermissions.getValue(); i++)
                    {
                        if(i<=loggedInDatasetAccess.getPermissions())
                        {
                            Privileges p = Privileges.getPrivilege(i);
                            comboRights.addItem(new DataComboMessage<Privileges>(p,getPrivilegeString(p)));
                        }
                    }
                }
                
                comboRights.setCurrentItem(selected);
            }
            
            //provider user
            TextField providerTF = new TextField(InteractionState.Viewing, form_);
            providerTF.setText(da.getProvider());
            widgets[2] = providerTF;
            
        return widgets;
    }
    
    private WMessage getPrivilegeMessage(Privileges p)
    {
        return WWidget.tr("privilege.status."+p.toString());
    }
    
    private String getPrivilegeString(Privileges p)
    {
        return WWidget.tr("privilege.status."+p.toString()).value();
    }
    
    public void setTransaction(Transaction transaction) 
    {
        this.transaction_ = transaction;
    }

    public WWidget[] addRow() 
    {
        WWidget[] widgets = new WWidget[3];
        
        final WComboBox datasetCombo = new WComboBox();
        final WComboBox privilegesCombo = new WComboBox();
        datasetCombo.changed.addListener(new SignalListener<WEmptyEvent>()
        {
            public void notify(WEmptyEvent a) 
            {
                setRights(datasetCombo, privilegesCombo);
            }
        });
        TextField providerTF = new TextField(InteractionState.Viewing, form_);
        
        for(DatasetAccess dsa : currentUserDatasetAccess)
        {
            Dataset ds = dsa.getId().getDataset();
            DataComboMessage<DatasetAccess> currentAccess = new DataComboMessage<DatasetAccess>(dsa, ds.getDescription());
            datasetCombo.addItem(currentAccess);
        }
        
        setRights(datasetCombo, privilegesCombo);
        
        providerTF.setText(currentUser_.getUid());
        
        widgets[0] = datasetCombo;
        widgets[1] = privilegesCombo;
        widgets[2] = providerTF;
        
        return widgets;
    }
    
    private void setRights(WComboBox datasetCombo, WComboBox privilegesCombo)
    {
        Privileges daPermissions = Privileges.getPrivilege(((DataComboMessage<DatasetAccess>)datasetCombo.currentText()).getValue().getPermissions());
        privilegesCombo.clear();
        for(int i = 0; i<daPermissions.getValue(); i++)
        {
            Privileges p = Privileges.getPrivilege(i+1);
            privilegesCombo.addItem(new DataComboMessage<Privileges>(p,getPrivilegeString(p)));
        }
    }

    public WWidget[] fixAddRow(WWidget[] widgets)
    {
        WComboBox ds = (WComboBox)widgets[0];
        DatasetAccess dsaFromAddRow = ((DataComboMessage<DatasetAccess>)ds.currentText()).getValue();
        WComboBox priv = (WComboBox)widgets[1];
        Privileges privillege = ((DataComboMessage<Privileges>)priv.currentText()).getValue();
    
        DatasetAccess dsa = new DatasetAccess(new DatasetAccessId(currentUser_, dsaFromAddRow.getId().getDataset()),privillege.getValue(), ((TextField)widgets[2]).text());
        
        return getWidgets(dsa);
    }
}
