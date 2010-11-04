package net.sf.regadb.ui.tree;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.forms.account.AccountForm;
import net.sf.regadb.ui.forms.account.PasswordForm;
import net.sf.regadb.ui.forms.login.LoginForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.StandardButton;
import eu.webtoolkit.jwt.WMessageBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class AccountNavigationNode extends DefaultNavigationNode{
	private TreeMenuNode accountLogin;
	private TreeMenuNode accountView;
	private TreeMenuNode accountAdd;

	@SuppressWarnings("unused")
	public AccountNavigationNode(TreeMenuNode parent) {
		super(WString.tr("menu.myAccount.myAccount"), parent);

        accountLogin = new FormNavigationNode(WString.tr("menu.myAccount.login"), this){
        	@Override
        	public IForm createForm(){
        		return new LoginForm();
        	}
        	
        	@Override
        	public boolean isDisabled(){
        		return RegaDBMain.getApp().getLogin() != null;
        	}
        };
        accountAdd = new FormNavigationNode(WString.tr("menu.myAccount.add"),this) {
			@Override
			public IForm createForm() {
				return new AccountForm(WWidget.tr("form.account.create"), InteractionState.Adding, null, new SettingsUser(), false){
					@Override
					public void redirectAfterSave(){
						accountLogin.selectNode();
					}
					@Override
					public void redirectAfterCancel(){
						accountLogin.selectNode();
					}
				};
			}
			
            @Override
            public boolean isDisabled()
            {
                return RegaDBMain.getApp().getLogin()!=null;
            }
		};
		accountView = new FormNavigationNode(WString.tr("menu.myAccount.view"),this){
			@Override
			public IForm createForm(){
				return new AccountForm(WWidget.tr("form.account.view"), InteractionState.Viewing, null, null, false);
			}
			
			@Override
            public boolean isDisabled()
            {
                return RegaDBMain.getApp().getLogin()==null;
            }
		};
		FormNavigationNode accountEdit = new FormNavigationNode(WString.tr("menu.myAccount.edit"),this){
			@Override
			public IForm createForm(){
				return new AccountForm(WWidget.tr("form.account.edit"), InteractionState.Editing, null, null, false){
					@Override
					public void redirectAfterSave(){
						accountView.selectNode();
					}
					@Override
					public void redirectAfterCancel(){
						accountView.selectNode();
					}
				};
			}
			
			@Override
            public boolean isDisabled()
            {
                return RegaDBMain.getApp().getLogin()==null;
            }
		};
		FormNavigationNode accountPass = new FormNavigationNode(WString.tr("menu.myAccount.passwordForm"),this){
			@Override
			public IForm createForm(){
				return new PasswordForm(WWidget.tr("form.account.edit.password"), InteractionState.Editing, accountView, false, null);
			}
			
			@Override
            public boolean isDisabled()
            {
                return RegaDBMain.getApp().getLogin()==null;
            }
		};
		TreeMenuNode accountLogout = new TreeMenuNode(WString.tr("menu.myAccount.logout"),this){
			public void doAction(){
				final WMessageBox cmb = UIUtils.createYesNoMessageBox(this, tr("menu.myAccount.logout.warning"));
                cmb.buttonClicked().addListener(this, new Signal1.Listener<StandardButton>(){
    				public void trigger(StandardButton sb) {
    					cmb.remove();
    					if(sb==StandardButton.Yes) {
                            RegaDBMain.getApp().getFormContainer().setForm(new LoginForm());
                            RegaDBMain.getApp().logout();
                            RegaDBMain.getApp().getWindow().newTree();
                            RegaDBMain.getApp().getTree().getRootTreeNode().refresh();
                            AccountNavigationNode.this.expand();
    					}
    				}
                });
                cmb.show();
			}
			
			@Override
            public boolean isDisabled()
            {
                return RegaDBMain.getApp().getLogin()==null;
            }
		};

	}
	
	public TreeMenuNode getViewNode(){
		return accountView;
	}
	public TreeMenuNode getLoginNode(){
		return accountLogin;
	}
	public TreeMenuNode getAddNode(){
		return accountAdd;
	}

}
