package net.sf.regadb.ui.forms.account;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

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
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.encrypt.Encrypt;
import net.sf.regadb.util.mail.MailUtils;
import net.sf.regadb.util.settings.EmailConfig;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.settings.Role;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WString;

public class AccountForm extends FormWidget
{
    private SettingsUser su_;
    
    private TreeMenuNode selectNode_, expandNode_;
    
    private Login login = null;

    //Account fields
    private WGroupBox accountGroup_;
    private FormTable loginGroupTable;
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
    private Label roleL;
    private ComboBox<Role> roleCB;
    
    //Attribute fields
    private WGroupBox attributeGroup_;
    private FormTable attributeGroupTable;
    private Label chartWidthL;
    private TextField chartWidthTF;
    private Label chartHeightL;
    private TextField chartHeightTF;
    private Label chartMutationL;
    private ComboBox<Test> chartMutationCB;
    
    private boolean admin;
    
    public AccountForm(WString formName, InteractionState interactionState, TreeMenuNode selectNode, TreeMenuNode expandNode, boolean admin, SettingsUser settingsUser)
    {
        super(formName, interactionState);
        selectNode_ = selectNode;
        expandNode_ = expandNode;
        su_ = settingsUser;
        this.admin = admin;
        init();
        authenticateLogin();
        fillData();
    }
    
    public void init()
    {
        //Account fields
        accountGroup_ = new WGroupBox(tr("form.account.editView.general"));
        loginGroupTable = new FormTable(accountGroup_);
        
        //UserId
        if(getInteractionState()!=InteractionState.Adding)
        {
            uidL = new Label(tr("form.settings.user.label.uid"));
            uidTF = new TextField(  su_!=null&&
                                    su_.getRole()==null&&
                                    getInteractionState()!=InteractionState.Viewing
                                    ?InteractionState.Editing:InteractionState.Viewing, this);
            uidTF.setMandatory(true);
            loginGroupTable.addLineToTable(uidL, uidTF);
            
            datasetL = new Label(tr("form.settings.user.label.dataset"));
            datasetCB= new ComboBox<Dataset>(su_!=null&&
            						su_.getRole()==null&&
                                    getInteractionState()!=InteractionState.Viewing
                                    ?InteractionState.Editing:InteractionState.Viewing, this);
            datasetCB.setMandatory(true);
            loginGroupTable.addLineToTable(datasetL, datasetCB);
        }
        
        //First name
        firstNameL = new Label(tr("form.settings.user.label.firstname"));
        firstNameTF = new TextField(getInteractionState(), this);
        firstNameTF.setMandatory(true);
        loginGroupTable.addLineToTable(firstNameL, firstNameTF);
        
        //Last name
        lastNameL = new Label(tr("form.settings.user.label.lastname"));
        lastNameTF = new TextField(getInteractionState(), this);
        lastNameTF.setMandatory(true);
        loginGroupTable.addLineToTable(lastNameL, lastNameTF);
        
        //E-mail address
        emailL = new Label(tr("form.settings.user.label.email"));
        emailTF = new TextField(getInteractionState(), this, FieldType.EMAIL);
        emailTF.setMandatory(true);
        loginGroupTable.addLineToTable(emailL, emailTF);
        
        //New password & retype password
        if(getInteractionState()==InteractionState.Adding)
        {
            newPasswordL = new Label(tr("form.settings.user.label.password"));
            newPasswordTF = new TextField(getInteractionState(), this);
            newPasswordTF.setMandatory(true);
            newPasswordTF.setEchomode(WLineEdit.EchoMode.Password);
            loginGroupTable.addLineToTable(newPasswordL, newPasswordTF);
            retypePasswordL = new Label(tr("form.settings.user.label.password.retype"));
            retypePasswordTF = new TextField(getInteractionState(), this);
            retypePasswordTF.setMandatory(true);
            retypePasswordTF.setEchomode(WLineEdit.EchoMode.Password);
            loginGroupTable.addLineToTable(retypePasswordL, retypePasswordTF);
        }
        
        if(getInteractionState()!=InteractionState.Adding)
        {
	        roleL = new Label(tr("form.settings.user.label.role"));
	        roleCB = new ComboBox<Role>(admin?getInteractionState():InteractionState.Viewing, this);
	        roleCB.setMandatory(true);
	        Map<String, Role> roles = RegaDBSettings.getInstance().getAccessPolicyConfig().getRoles();
	        roleCB.addNoSelectionItem();
	        for(Role r : roles.values()) {
	        	roleCB.addItem(new DataComboMessage<Role>(r,r.getName()));
	        }
	        loginGroupTable.addLineToTable(roleL, roleCB);
            

            //Attribute fields
            attributeGroup_ = new WGroupBox(tr("form.account.editView.attributes"));
            attributeGroupTable = new FormTable(attributeGroup_);
            chartWidthL = new Label(tr("form.settings.user.label.chartWidth"));
            chartWidthTF = new TextField(getInteractionState(), this, FieldType.INTEGER);
            attributeGroupTable.addLineToTable(chartWidthL, chartWidthTF);
            chartHeightL = new Label(tr("form.settings.user.label.chartHeight"));
            chartHeightTF = new TextField(getInteractionState(), this, FieldType.INTEGER);
            attributeGroupTable.addLineToTable(chartHeightL, chartHeightTF);
            chartMutationL = new Label(tr("form.settings.user.label.chartMutation"));
            chartMutationCB = new ComboBox<Test>(getInteractionState(), this);
            attributeGroupTable.addLineToTable(chartMutationL, chartMutationCB);
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
        	
            if(!admin)
            {
                su_ = t.getSettingsUser(login.getUid());
            }
            else
            {
                if(su_.getRole()==null) {
                	for(Dataset ds : t.getDatasets())
                    {
                        datasetCB.addItem(new DataComboMessage<Dataset>(ds, ds.getDescription()));
                    }
                    datasetCB.sort();
                }
            }
            
        	if(su_.getRole()==null) {
                roleCB.selectIndex(0);
        	} else {
                roleCB.selectItem(su_.getRole());
        	}
            
            if(su_.getDataset()!=null)
            {
                datasetCB.selectItem(su_.getDataset().getDescription());
            }
            firstNameTF.setText(su_.getFirstName());
            lastNameTF.setText(su_.getLastName());
            emailTF.setText(su_.getEmail());
            uidTF.setText(su_.getUid());
            if(su_.getRole()==null)
            {
                uidTF.setText("");
            }
            
            if(attributeGroup_!=null)
            {
                chartWidthTF.setText("" + su_.getChartWidth());
                chartHeightTF.setText("" + su_.getChartHeight());
                
                for(Test test : t.getTests())
                {
                    if(StandardObjects.getGssDescription().equals(test.getTestType().getDescription()))
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
        boolean wasNotEnabled = su_.getRole()==null;
        
        Transaction t;
        if(admin && wasNotEnabled)
        {
            t = login.createTransaction();
            boolean nonExistingName = t.getSettingsUser(uidTF.text())==null;
            if(!nonExistingName)
            {
            	UIUtils.showWarningMessageBox(this, tr("form.administrator.user.edit.uid.warning"));
                return;
            }
        }
        
        if(validatePasswordFields())
        {
            if(getInteractionState()==InteractionState.Adding)
            {
                su_.setUid(emailTF.text());
                su_.setRole(null);
            }
            
            su_.setFirstName(firstNameTF.text());
            su_.setLastName(lastNameTF.text());
            su_.setEmail(emailTF.text());
            
            if(newPasswordTF!=null)
            {
                su_.setPassword(Encrypt.encryptMD5(newPasswordTF.getFormText()));
            }
            
            if(admin)
            {
                su_.setRole(roleCB.currentValue().getName());
            }
                
            if(getInteractionState()==InteractionState.Adding)
            {
                Login.createNewAccount(su_);
                UIUtils.showWarningMessageBox(this, tr("form.account.create.warning"));
                
                EmailConfig ec = RegaDBSettings.getInstance().getInstituteConfig().getEmailConfig();
                if (ec != null) {
	                try {
						MailUtils.sendMail(ec.getHost(), ec.getFrom(), ec.getTo(), 
									tr("form.account.create.email.subject"), 
									tr("form.account.create.email.message")
										.arg(su_.getFirstName())
										.arg(su_.getLastName())
										.arg(su_.getEmail()));
					} catch (AddressException e) {
						e.printStackTrace();
					} catch (MessagingException e) {
						e.printStackTrace();
					}
                }
            }
            else
            {                
                t = login.createTransaction();
                update(su_, t);
                t.commit(); 
                
                if(admin && wasNotEnabled)
                {
                    t = login.createTransaction();
                    su_.setDataset(t.getDataset(datasetCB.currentItem().getValue()));
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
            
            
            if(attributeGroup_!=null)
            {
                t = login.createTransaction();
                su_.setChartWidth( Integer.parseInt( chartWidthTF.getFormText() ) );
                su_.setChartHeight( Integer.parseInt( chartHeightTF.getFormText() ) );
                update(su_, t);
                t.commit();

            	
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
        
        t.delete(su_);
        
        t.commit();
        
        return null;
    }

    @Override
    public void redirectAfterDelete() 
    {
    }

	@Override
	public void redirectAfterSave() {
	}

	@Override
	public void redirectAfterCancel() {
	}
}
