package net.sf.regadb.ui.forms.login;

import java.util.ArrayList;

import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WImage;
import net.sf.witty.wt.widgets.WLineEditEchoMode;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.WText;
import net.sf.witty.wt.widgets.event.WMouseEvent;

public class LoginForm extends WGroupBox implements IForm
{	
	private ArrayList<IFormField> formFields_ = new ArrayList<IFormField>(); 
	
	private WImage warningImage_ = new WImage("pics/formWarning.gif");
	private WText warningText_ = new WText(tr("form.validationProblem.warning.mainText"));
	private WContainerWidget warningWidget_ = new WContainerWidget();
	
	//login group
	private WGroupBox loginGroup_ = new WGroupBox(tr("form.login.loginForm.login"));
	private Label uidL = new Label(tr("form.login.label.uid"));
	private TextField uidTF = new TextField(true, this);
	private Label passwordL = new Label(tr("form.login.label.password"));
	private TextField passwordTF = new TextField(true, this);
	
	//control
	private WPushButton _loginButton = new WPushButton(tr("form.login.button.login"));
	private WPushButton _helpButton = new WPushButton(tr("form.login.button.help"));
	
	public LoginForm()
	{
		super(tr("form.login.loginForm"));
		addWidget(warningWidget_);
		warningWidget_.addWidget(warningImage_);
		warningWidget_.addWidget(warningText_);
		warningWidget_.setHidden(true);
		
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
		
		//control
		addWidget(_loginButton);
		addWidget(_helpButton);
		_loginButton.clicked.addListener(new SignalListener<WMouseEvent>()
		{
			public void notify(WMouseEvent me)
			{
				if(validate())
				{
					if(validateLogin())
					{
						RegaDBMain.getApp().getTree().getRootTreeNode().refresh();
						RegaDBMain.getApp().getTree().getRootTreeNode().findDeepChild("menu.singlePatient.patientSelectItem").prograSelectNode();
					}
					else
					{
						warningWidget_.setHidden(false);
					}
				}
				else
				{
					warningWidget_.setHidden(false);
				}
			}
		});
	}
	
	private boolean validate()
	{
		boolean erroneousInput = false;
		
		for(IFormField ff : formFields_)
		{
			if(!ff.validate())
			{
				erroneousInput = true;
				ff.flagErroneous();
			}
			else
			{
				ff.flagValid();
			}
		}
		
		return !erroneousInput;
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
	}
	
	public WContainerWidget getWContainer()
	{
		return this;
	}

	public void addFormField(IFormField field)
	{
		formFields_.add(field);
	}
}
