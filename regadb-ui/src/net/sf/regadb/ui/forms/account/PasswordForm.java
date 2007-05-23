package net.sf.regadb.ui.forms.account;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.regadb.util.encrypt.Encrypt;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WLineEditEchoMode;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class PasswordForm extends FormWidget
{
    private SettingsUser su_;
    
    private boolean administrator_;
    
    private TreeMenuNode selectNode_, expandNode_;
    
    private WGroupBox passwordGroup_;
    private WTable passwordGroupTable;
    private Label passwordL;
    private TextField passwordTF;
    private Label newPasswordL;
    private TextField newPasswordTF;
    private Label retypePasswordL;
    private TextField retypePasswordTF;
    
    public PasswordForm(WMessage formName, InteractionState interactionState, TreeMenuNode selectNode, TreeMenuNode expandNode, boolean admin, SettingsUser settingsUser)
    {
        super(formName, interactionState);
        selectNode_ = selectNode;
        expandNode_ = expandNode;
        administrator_ = admin;
        
        Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        
        if(administrator_)
        {
        	su_ = settingsUser;
        }
        else
        {
        	su_ = t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid());
        }
        
        t.commit();
        
        init();
    }
    
    public void init()
    {
        passwordGroup_ = new WGroupBox(tr("form.account.editView.general"));
        passwordGroupTable = new WTable(passwordGroup_);
        if(!administrator_)
        {
            passwordL = new Label(tr("form.settings.user.label.password.old"));
        }
        else
        {
            passwordL = new Label(tr("form.settings.user.label.password.administrator"));
        }
        passwordTF = new TextField(getInteractionState(), this);
        passwordTF.setMandatory(true);
        passwordTF.setEchomode(WLineEditEchoMode.Password);
        addLineToTable(passwordGroupTable, passwordL, passwordTF);
        newPasswordL = new Label(tr("form.settings.user.label.password.new"));
        newPasswordTF = new TextField(getInteractionState(), this);
        newPasswordTF.setMandatory(true);
        newPasswordTF.setEchomode(WLineEditEchoMode.Password);
        addLineToTable(passwordGroupTable, newPasswordL, newPasswordTF);
        retypePasswordL = new Label(tr("form.settings.user.label.password.retype.new"));
        retypePasswordTF = new TextField(getInteractionState(), this);
        retypePasswordTF.setMandatory(true);
        retypePasswordTF.setEchomode(WLineEditEchoMode.Password);
        addLineToTable(passwordGroupTable, retypePasswordL, retypePasswordTF);
        
        addWidget(passwordGroup_);
        addControlButtons();
    }
    
    private boolean validatePasswordFields()
    {
    	Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
    	
        boolean valid = true;
        
        if(Encrypt.encryptMD5(passwordTF.getFormText()).equals(t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid()).getPassword()))
        {
            passwordTF.flagValid();
        }
        else
        {
            passwordTF.flagErroneous();
            valid = false;
        }
        
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
        
        t.commit();
        
        return valid;
    }
    
    @Override
    public void saveData()
    {        
        if(validatePasswordFields())
        {
            Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
            
            su_.setPassword(Encrypt.encryptMD5(newPasswordTF.getFormText()));
            
            update(su_, t);
            t.commit();
            
            MessageBox.showWarningMessage(tr("form.settings.user.password.message"));
            
            redirectToView(expandNode_, selectNode_);
        }
    }
    
    @Override
    public void cancel()
    {
        redirectToView(expandNode_, selectNode_);
    }
}
