package net.sf.regadb.ui.forms.account;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.CheckBox;
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
    private Label administratorL;
    private CheckBox administratorCB;
    private Label registeredL;
    private CheckBox registeredCB;
    
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
        accountGroup_ = new WGroupBox(tr("form.account.editView.general"));
        loginGroupTable = new WTable(accountGroup_);
        
        //UserId
        if(getInteractionState()!=InteractionState.Adding)
        {
            uidL = new Label(tr("form.settings.user.label.uid"));
            uidTF = new TextField(InteractionState.Viewing, this);
            addLineToTable(loginGroupTable, uidL, uidTF);
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
        emailTF = new TextField(getInteractionState(), this);
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
        
        addWidget(accountGroup_);
        addControlButtons();
    }
    
    private void fillData()
    {
        Transaction t = login.createTransaction();
            
        if(getInteractionState()!=InteractionState.Adding)
        {
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
            }
            
            firstNameTF.setText(su_.getFirstName());
            lastNameTF.setText(su_.getLastName());
            emailTF.setText(su_.getEmail());
            uidTF.setText(su_.getUid());
        }
        
        t.commit();
    }
    
    //If nobody is logged in, set default login
    //!! solve this problem see wiki chicken/egg
    private void authenticateLogin()
    {
        if(RegaDBMain.getApp().getLogin()==null)
        {
            try
            {
                login = Login.authenticate("paashaas", "paashaas");
            }
            catch (WrongUidException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (WrongPasswordException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            login = RegaDBMain.getApp().getLogin();
        }
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
    
    @Override
    public void saveData()
    {
        if(validatePasswordFields())
        {
            Transaction t = login.createTransaction();
            
            if(getInteractionState()==InteractionState.Adding)
            {
                //Generate new userId
                int indexOfArobase = emailTF.getFormText().lastIndexOf('@');
                String uid = emailTF.getFormText().substring(0, indexOfArobase);
                
                su_.setUid(uid);
                su_.setAdmin(false);
                su_.setEnabled(null);
            }
            
            su_.setFirstName(firstNameTF.getFormText());
            su_.setLastName(lastNameTF.getFormText());
            su_.setEmail(emailTF.getFormText());
            
            if(newPasswordTF!=null)
            {
                su_.setPassword(Encrypt.encryptMD5(newPasswordTF.getFormText()));
            }
                
            if(administrator_)
            {
                su_.setAdmin(administratorCB.isChecked());
                su_.setEnabled(registeredCB.isChecked());
            }
            
            update(su_, t);
            t.commit();
            
            if(getInteractionState()==InteractionState.Adding)
            {
                MessageBox.showWarningMessage(tr("form.account.create.warning"));
            }
            
            expandNode_.expand();
            expandNode_.refreshAllChildren();
            selectNode_.selectNode();
        }
    }
}
