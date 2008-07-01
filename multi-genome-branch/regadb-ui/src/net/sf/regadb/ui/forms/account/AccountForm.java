package net.sf.regadb.ui.forms.account;

import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.DatasetAccessId;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.UserAttribute;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.DescribeMutations;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.CheckBox;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.regadb.util.encrypt.Encrypt;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WLineEditEchoMode;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class AccountForm extends FormWidget
{
    private SettingsUser su_;
    
    private boolean administrator_;
    
    private TreeMenuNode selectNode_, expandNode_;
    
    private Login login = null;

    //Account fields
    private WGroupBox accountGroup_;
    private WTable loginGroupTable;
    private Label uidL;
    private TextField uidTF;
    private Label firstNameL;
    private TextField firstNameTF;
    private Label lastNameL;
    private TextField lastNameTF;
    private Label emailL;
    private TextField emailTF;
    private Label newPasswordL;
    private TextField newPasswordTF;
    private Label retypePasswordL;
    private TextField retypePasswordTF;
    private Label datasetL;
    private ComboBox<Dataset> datasetCB;
    private Label administratorL;
    private CheckBox administratorCB;
    private Label registeredL;
    private CheckBox registeredCB;
    
    //Attribute fields
    private WGroupBox attributeGroup_;
    private WTable attributeGroupTable;
    private Label chartWidthL;
    private TextField chartWidthTF;
    private Label chartHeightL;
    private TextField chartHeightTF;
    private Label chartMutationL;
    private ComboBox<Test> chartMutationCB;
    
    public AccountForm(WMessage formName, InteractionState interactionState, TreeMenuNode selectNode, TreeMenuNode expandNode, boolean admin, SettingsUser settingsUser)
    {
        super(formName, interactionState);
        administrator_ = admin;
        selectNode_ = selectNode;
        expandNode_ = expandNode;
        su_ = settingsUser;
        init();
        authenticateLogin();
        fillData();
    }
    
    public void init()
    {
        //Account fields
        accountGroup_ = new WGroupBox(tr("form.account.editView.general"));
        loginGroupTable = new WTable(accountGroup_);
        
        //UserId
        if(getInteractionState()!=InteractionState.Adding)
        {
            uidL = new Label(tr("form.settings.user.label.uid"));
            uidTF = new TextField(  su_!=null&&
                                    su_.getEnabled()==null&&
                                    getInteractionState()!=InteractionState.Viewing
                                    ?InteractionState.Editing:InteractionState.Viewing, this);
            uidTF.setMandatory(true);
            addLineToTable(loginGroupTable, uidL, uidTF);
            
            datasetL = new Label(tr("form.settings.user.label.dataset"));
            datasetCB= new ComboBox<Dataset>(su_!=null&&
                                    su_.getEnabled()==null&&
                                    getInteractionState()!=InteractionState.Viewing
                                    ?InteractionState.Editing:InteractionState.Viewing, this);
            datasetCB.setMandatory(true);
            addLineToTable(loginGroupTable,datasetL, datasetCB);
        }
        
        //First name
        firstNameL = new Label(tr("form.settings.user.label.firstname"));
        firstNameTF = new TextField(getInteractionState(), this);
        firstNameTF.setMandatory(true);
        addLineToTable(loginGroupTable, firstNameL, firstNameTF);
        
        //Last name
        lastNameL = new Label(tr("form.settings.user.label.lastname"));
        lastNameTF = new TextField(getInteractionState(), this);
        lastNameTF.setMandatory(true);
        addLineToTable(loginGroupTable, lastNameL, lastNameTF);
        
        //E-mail address
        emailL = new Label(tr("form.settings.user.label.email"));
        emailTF = new TextField(getInteractionState(), this, FieldType.EMAIL);
        emailTF.setMandatory(true);
        addLineToTable(loginGroupTable, emailL, emailTF);
        
        //New password & retype password
        if(getInteractionState()==InteractionState.Adding)
        {
            newPasswordL = new Label(tr("form.settings.user.label.password"));
            newPasswordTF = new TextField(getInteractionState(), this);
            newPasswordTF.setMandatory(true);
            newPasswordTF.setEchomode(WLineEditEchoMode.Password);
            addLineToTable(loginGroupTable, newPasswordL, newPasswordTF);
            retypePasswordL = new Label(tr("form.settings.user.label.password.retype"));
            retypePasswordTF = new TextField(getInteractionState(), this);
            retypePasswordTF.setMandatory(true);
            retypePasswordTF.setEchomode(WLineEditEchoMode.Password);
            addLineToTable(loginGroupTable, retypePasswordL, retypePasswordTF);
        }
        
        //Administrator & enabled
        if(administrator_)
        {
            administratorL = new Label(tr("form.settings.user.label.administrator"));
            administratorCB = new CheckBox(getInteractionState(), this);
            addLineToTable(loginGroupTable, administratorL, administratorCB);
            registeredL = new Label(tr("form.settings.user.label.enabled"));
            registeredCB = new CheckBox(getInteractionState(), this);
            addLineToTable(loginGroupTable, registeredL, registeredCB);
        }
        
        if(getInteractionState()!=InteractionState.Adding)
        {
            //Attribute fields
            attributeGroup_ = new WGroupBox(tr("form.account.editView.attributes"));
            attributeGroupTable = new WTable(attributeGroup_);
            chartWidthL = new Label(tr("form.settings.user.label.chartWidth"));
            chartWidthTF = new TextField(getInteractionState(), this, FieldType.INTEGER);
            addLineToTable(attributeGroupTable, chartWidthL, chartWidthTF);
            chartHeightL = new Label(tr("form.settings.user.label.chartHeight"));
            chartHeightTF = new TextField(getInteractionState(), this, FieldType.INTEGER);
            addLineToTable(attributeGroupTable, chartHeightL, chartHeightTF);
            chartMutationL = new Label(tr("form.settings.user.label.chartMutation"));
            chartMutationCB = new ComboBox<Test>(getInteractionState(), this);
            addLineToTable(attributeGroupTable, chartMutationL, chartMutationCB);
        }
        
        addWidget(accountGroup_);
        if(attributeGroup_!=null)
            addWidget(attributeGroup_);
        
        addControlButtons();
    }
    
    private void fillData()
    {
        if(getInteractionState()!=InteractionState.Adding)
        {
            Transaction t = login.createTransaction();
            
            if(!administrator_)
            {
                su_ = t.getSettingsUser(login.getUid());
            }
            else
            {
                administratorCB.setChecked(su_.getAdmin());
                if(su_.getEnabled()!=null)
                {
                    registeredCB.setChecked(su_.getEnabled());
                }
                else
                {
                    for(Dataset ds : t.getDatasets())
                    {
                        datasetCB.addItem(new DataComboMessage<Dataset>(ds, ds.getDescription()));
                    }
                    datasetCB.sort();
                }
            }
            
            if(su_.getDataset()!=null)
            {
                datasetCB.selectItem(su_.getDataset().getDescription());
            }
            firstNameTF.setText(su_.getFirstName());
            lastNameTF.setText(su_.getLastName());
            emailTF.setText(su_.getEmail());
            uidTF.setText(su_.getUid());
            if(su_.getEnabled()==null)
            {
                uidTF.setText("");
            }
            
            if(attributeGroup_!=null)
            {
                chartWidthTF.setText("" + su_.getChartWidth());
                chartHeightTF.setText("" + su_.getChartHeight());
                
                for(Test test : t.getTests())
                {
                    if(StandardObjects.getGssId().equals(test.getTestType().getDescription()))
                    {
                        chartMutationCB.addItem(new DataComboMessage<Test>(test, test.getDescription()));
                    }
                }
                chartMutationCB.sort();
                
                //do this after the sort
                chartMutationCB.addNoSelectionItem();
                
                String value = getAttributeValue("chart.mutation", t);
                chartMutationCB.selectIndex(0);
                chartMutationCB.selectItem(value);
            }

            t.commit();
        }
    }
    
    private String getAttributeValue(String attributeName, Transaction t)
    {
        UserAttribute ua = t.getUserAttribute(su_, attributeName);
        if(ua!=null)
            return ua.getValue();
        else 
            return null;
    }
    
    private void authenticateLogin()
    {
        login = RegaDBMain.getApp().getLogin();
    }
    
    private boolean validatePasswordFields()
    {
        boolean valid = true;
        
        if(newPasswordTF != null)
        {
            if(newPasswordTF.getFormText().equals(retypePasswordTF.getFormText()))
            {
                newPasswordTF.flagValid();
                retypePasswordTF.flagValid();
            }
            else
            {
                newPasswordTF.flagErroneous();
                retypePasswordTF.flagErroneous();
                valid = false;
            }
        }
        
        return valid;
    }
    
    private void saveUserAttribute(String valueType, String attributeName, String attributeText, byte[] data, Transaction t)
    {
        UserAttribute ua = t.getUserAttribute(su_, attributeName);
        
        if(ua==null)
        {
            ua  = new UserAttribute(t.getValueType(valueType), su_, attributeName, attributeText, data);
            t.save(ua);
        }
        else
        {
            ua.setValue("".equals(attributeText)?null:attributeText);
            ua.setData(data);
            t.update(ua);
        }
    }
    
    @Override
    public void saveData()
    {
        boolean wasNotEnabled = su_.getEnabled()==null;
        
        Transaction t;
        if(administrator_ && wasNotEnabled)
        {
            t = login.createTransaction();
            boolean nonExistingName = t.getSettingsUser(uidTF.text())==null;
            if(!nonExistingName)
            {
                MessageBox.showWarningMessage(tr("form.administrator.notRegisteredUser.edit.uid.warning"));
                return;
            }
        }
        
        if(validatePasswordFields())
        {
            if(getInteractionState()==InteractionState.Adding)
            {
                su_.setUid(emailTF.text());
                su_.setAdmin(false);
                su_.setEnabled(null);
            }
            
            su_.setFirstName(firstNameTF.text());
            su_.setLastName(lastNameTF.text());
            su_.setEmail(emailTF.text());
            
            if(newPasswordTF!=null)
            {
                su_.setPassword(Encrypt.encryptMD5(newPasswordTF.getFormText()));
            }
            
            if(administrator_)
            {
                su_.setAdmin(administratorCB.isChecked());
                su_.setEnabled(registeredCB.isChecked());
            }
                
            if(getInteractionState()==InteractionState.Adding)
            {
                Login.createNewAccount(su_);
                MessageBox.showWarningMessage(tr("form.account.create.warning"));
            }
            else
            {                
                t = login.createTransaction();
                update(su_, t);
                t.commit(); 
                
                if(administrator_ && wasNotEnabled)
                {
                    t = login.createTransaction();
                    su_.setDataset(t.getDataset(datasetCB.currentItem().value()));
                    su_ = t.changeUid(su_, uidTF.text());
                    update(su_, t);
                    t.commit();
                    
                    t = login.createTransaction();
                    DatasetAccess da = new DatasetAccess(new DatasetAccessId(su_, su_.getDataset()),Privileges.READWRITE.getValue(), login.getUid());
                    t.save(da);
                    t.commit();
                    
                    t = login.createTransaction();
                    t.refresh(su_);
                    t.commit();
                }
            }
            
            su_.setChartWidth( Integer.parseInt( chartWidthTF.getFormText() ) );
            su_.setChartHeight( Integer.parseInt( chartHeightTF.getFormText() ) );
            
            if(attributeGroup_!=null)
            {
                t = login.createTransaction();
                Test chartMutation = chartMutationCB.currentValue();
                byte [] mutationDescription = null;
                String value = null;
                if(chartMutation!=null) {
                    mutationDescription = DescribeMutations.describeMutations(((AnalysisData)chartMutation.getAnalysis().getAnalysisDatas().toArray()[0]).getData());
                    value = chartMutation.getDescription();
                }
                saveUserAttribute("string", "chart.mutation", value, mutationDescription, t);
                t.commit();
            }
            
            redirectToView(expandNode_, selectNode_);
        }
    }
    
    @Override
    public void cancel()
    {
        redirectToView(expandNode_, selectNode_);
    }

    @Override
    public WMessage deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        t.delete(su_);
        
        t.commit();
        
        return null;
    }

    @Override
    public void redirectAfterDelete() 
    {
        RegaDBMain.getApp().getTree().getTreeContent().registeredUsersSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().registeredUserSelected.setSelectedItem(null);
    }
}
