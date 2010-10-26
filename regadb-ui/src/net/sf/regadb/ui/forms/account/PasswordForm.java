package net.sf.regadb.ui.forms.account;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.encrypt.Encrypt;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WString;

public class PasswordForm extends FormWidget
{
    private SettingsUser su_;
    
    private boolean administrator_;
    
    private TreeMenuNode selectNode_, expandNode_;
    
    private WGroupBox passwordGroup_;
    private FormTable passwordGroupTable;
    private Label passwordL;
    private TextField passwordTF;
    private Label newPasswordL;
    private TextField newPasswordTF;
    private Label retypePasswordL;
    private TextField retypePasswordTF;
    
    public PasswordForm(WString formName, InteractionState interactionState, TreeMenuNode selectNode, TreeMenuNode expandNode, boolean admin, SettingsUser settingsUser)
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
        passwordGroupTable = new FormTable(passwordGroup_);
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
        passwordTF.setEchomode(WLineEdit.EchoMode.Password);
        passwordGroupTable.addLineToTable(passwordL, passwordTF);
        newPasswordL = new Label(tr("form.settings.user.label.password.new"));
        newPasswordTF = new TextField(getInteractionState(), this);
        newPasswordTF.setMandatory(true);
        newPasswordTF.setEchomode(WLineEdit.EchoMode.Password);
        passwordGroupTable.addLineToTable(newPasswordL, newPasswordTF);
        retypePasswordL = new Label(tr("form.settings.user.label.password.retype.new"));
        retypePasswordTF = new TextField(getInteractionState(), this);
        retypePasswordTF.setMandatory(true);
        retypePasswordTF.setEchomode(WLineEdit.EchoMode.Password);
        passwordGroupTable.addLineToTable(retypePasswordL, retypePasswordTF);
        
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
            
            UIUtils.showWarningMessageBox(this, tr("form.settings.user.password.message"));
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
