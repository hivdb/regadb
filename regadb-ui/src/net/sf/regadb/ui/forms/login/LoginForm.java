package net.sf.regadb.ui.forms.login;

import java.util.ArrayList;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormListener;
import net.sf.regadb.ui.framework.forms.IConfirmForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.forms.validation.WFormValidation;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.ProxyConfig.ProxyServer;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

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
    private ComboBox<ProxyServer> proxyCB;
    private WText createAccountLink_ = new WText(tr("form.login.link.create"));
	
	//control
	private WPushButton _loginButton = new WPushButton(tr("form.login.button.login"));
	private WPushButton _helpButton = new WPushButton(tr("form.general.button.help"));
	
	private FormListener listener = null;
	
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
        FormTable loginGroupTable = new FormTable(loginGroup_);
        //user id
        uidTF.setMandatory(true);
        uidL.setBuddy(uidTF);
        loginGroupTable.addLineToTable(uidL, uidTF);
        //user password
        passwordTF.setMandatory(true);
        passwordL.setBuddy(passwordTF);
        passwordTF.setEchomode(WLineEdit.EchoMode.Password);
        loginGroupTable.addLineToTable(passwordL, passwordTF);
        if(RegaDBSettings.getInstance().getProxyConfig().getProxyList().size() > 1)
        {
            proxyL = new Label(tr("form.login.label.proxy"));
            proxyCB = new ComboBox<ProxyServer>(InteractionState.Editing, this);
            loginGroupTable.addLineToTable(proxyL, proxyCB);
            for(ProxyServer proxy : RegaDBSettings.getInstance().getProxyConfig().getProxyList())
            {
                String proxyKey = "".equals(proxy.getPort())?"Empty proxy":proxy.getHost();
                proxyCB.addItem(new DataComboMessage<ProxyServer>(proxy, proxyKey));
            }
            proxyCB.sort();
        }
        WContainerWidget createAccountContainer = new WContainerWidget(loginGroup_);
        createAccountContainer.setStyleClass("create-account");
        createAccountLink_.setStyleClass("general-clickable-text");
        createAccountLink_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
        {
            public void trigger(WMouseEvent me)
            {
//                RegaDBMain.getApp().getTree().getTreeContent().myAccountCreate.selectNode();
            }
        });
        createAccountContainer.addWidget(createAccountLink_);
        
        //control
        WContainerWidget buttonContainer = new WContainerWidget(this);
        buttonContainer.setStyleClass("control-buttons");
        buttonContainer.addWidget(_loginButton);
        buttonContainer.addWidget(_helpButton);
        
        _loginButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
        {
            public void trigger(WMouseEvent me)
            {
                confirmAction();
            }
        });
    }
	
	private boolean validateLogin()
	{
		try
		{
			RegaDBMain.getApp().login(uidTF.text(), passwordTF.text());
			
			if(RegaDBMain.getApp().getRole() == null) {
				UIUtils.showWarningMessageBox(this, tr("form.login.error.noSuchRole"));
				return false;
			}
			
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
            	RegaDBMain.getApp().getTree().getRootTreeNode().refresh();
                RegaDBMain.getApp().getTree().getRootTreeNode().selectNode();
                RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.selectNode();
                
                if(proxyCB!=null)
                {
                    RegaDBSettings.getInstance().getProxyConfig().setProxySettings(proxyCB.currentValue());
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

    public WString leaveForm() {
        return tr("form.login.tree.warning");
    }

	public void removeFormField(IFormField field) {
		
	}

	@Override
	public void setListener(FormListener listener) {
		this.listener = listener;
	}
}
