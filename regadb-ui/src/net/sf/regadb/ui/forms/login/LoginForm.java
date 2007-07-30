package net.sf.regadb.ui.forms.login;

import java.util.ArrayList;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IConfirmForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.forms.validation.WFormValidation;
import net.sf.regadb.util.pair.Pair;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WBreak;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WLineEditEchoMode;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.i8n.WMessage;

public class LoginForm extends WGroupBox implements IForm, IConfirmForm
{	
	private ArrayList<IFormField> formFields_ = new ArrayList<IFormField>();
	
    private WFormValidation formValidation_ = new WFormValidation();
    
	//login group
	private WGroupBox loginGroup_ = new WGroupBox(tr("form.login.loginForm.login"));
	private Label uidL = new Label(tr("form.login.label.uid"));
	private TextField uidTF = new TextField(InteractionState.Editing, this);
	private Label passwordL = new Label(tr("form.login.label.password"));
	private TextField passwordTF = new TextField(InteractionState.Editing, this);
    private Label proxyL;
    private ComboBox<Pair<String, String>> proxyCB;
    private WText createAccountLink_ = new WText(tr("form.login.link.create"));
	
	//control
	private WPushButton _loginButton = new WPushButton(tr("form.login.button.login"));
	private WPushButton _helpButton = new WPushButton(tr("form.general.button.help"));
	
	public LoginForm()
	{
		super(tr("form.login.loginForm"));
        init();
	}
    
    public void init()
    {
        formValidation_.init(this);
        
        //login group
        addWidget(loginGroup_);
        WTable loginGroupTable = new WTable(loginGroup_);
        //user id
        uidTF.setMandatory(true);
        uidL.setBuddy(uidTF);
        loginGroupTable.putElementAt(0, 0, uidL);
        loginGroupTable.putElementAt(0, 1, uidTF);
        //user password
        passwordTF.setMandatory(true);
        passwordL.setBuddy(passwordTF);
        passwordTF.setEchomode(WLineEditEchoMode.Password);
        loginGroupTable.putElementAt(1, 0, passwordL);
        loginGroupTable.putElementAt(1, 1, passwordTF);
        if(RegaDBSettings.getInstance().getProxyList().size() > 1)
        {
            proxyL = new Label(tr("form.login.label.proxy"));
            proxyCB = new ComboBox<Pair<String, String>>(InteractionState.Editing, this);
            loginGroupTable.putElementAt(2, 0, proxyL);
            loginGroupTable.putElementAt(2, 1, proxyCB);
            for(Pair<String,String> proxy : RegaDBSettings.getInstance().getProxyList())
            {
                String proxyKey = "".equals(proxy.getValue())?"Empty proxy":proxy.getKey();
                proxyCB.addItem(new DataComboMessage<Pair<String, String>>(proxy, proxyKey));
            }
        }
        createAccountLink_.setStyleClass("general-clickable-text");
        createAccountLink_.clicked.addListener(new SignalListener<WMouseEvent>()
        {
            public void notify(WMouseEvent me)
            {
                RegaDBMain.getApp().getTree().getTreeContent().myAccountCreate.selectNode();
            }
        });
        
        //control
        WContainerWidget buttonContainer = new WContainerWidget(this);
        buttonContainer.addWidget(_loginButton);
        buttonContainer.addWidget(_helpButton);
        buttonContainer.setContentAlignment(WHorizontalAlignment.AlignRight);
        _loginButton.clicked.addListener(new SignalListener<WMouseEvent>()
        {
            public void notify(WMouseEvent me)
            {
                confirmAction();
            }
        });
        buttonContainer.addWidget(new WBreak());
        buttonContainer.addWidget(createAccountLink_);
    }
	
	private boolean validateLogin()
	{
		try
		{
			RegaDBMain.getApp().login(uidTF.text(), passwordTF.text());
			return true;
		}
		catch (WrongUidException e)
		{
			uidTF.flagErroneous();
			return false;
		}
		catch (WrongPasswordException e)
		{
			passwordTF.flagErroneous();
			return false;
		} 
        catch (DisabledUserException e) 
        {
            uidTF.flagErroneous();
            return false;
        }
	}
	
	public WContainerWidget getWContainer()
	{
		return this;
	}

	public void addFormField(IFormField field)
	{
		formFields_.add(field);
	}

    public void confirmAction() 
    {
        if(formValidation_.validate(formFields_))
        {
            if(validateLogin())
            {
                RegaDBMain.getApp().getTree().getRootTreeNode().refreshAllChildren();
                RegaDBMain.getApp().getTree().getTreeContent().patientSelect.prograSelectNode();
                
                if(proxyCB!=null)
                {
                    RegaDBSettings.getInstance().setProxySettings(proxyCB.currentValue());
                }
            }
            else
            {
                formValidation_.setHidden(false);
            }
        }
        else
        {
            formValidation_.setHidden(false);
        }
    }

    public WMessage leaveForm() {
        return tr("form.login.tree.warning");
    }
}
