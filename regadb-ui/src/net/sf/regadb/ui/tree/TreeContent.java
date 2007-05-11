package net.sf.regadb.ui.tree;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.datatable.attributeSettings.SelectAttributeForm;
import net.sf.regadb.ui.datatable.attributeSettings.SelectAttributeGroupForm;
import net.sf.regadb.ui.datatable.measurement.SelectMeasurementForm;
import net.sf.regadb.ui.datatable.settingsUser.SelectSettingsUserForm;
import net.sf.regadb.ui.datatable.testSettings.SelectTestForm;
import net.sf.regadb.ui.datatable.testSettings.SelectTestTypeForm;
import net.sf.regadb.ui.datatable.therapy.SelectTherapyForm;
import net.sf.regadb.ui.datatable.viralisolate.SelectViralIsolateForm;
import net.sf.regadb.ui.form.attributeSettings.AttributeForm;
import net.sf.regadb.ui.form.attributeSettings.AttributeGroupForm;
import net.sf.regadb.ui.form.singlePatient.MeasurementForm;
import net.sf.regadb.ui.form.singlePatient.SinglePatientForm;
import net.sf.regadb.ui.form.singlePatient.TherapyForm;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateForm;
import net.sf.regadb.ui.form.singlePatient.chart.PatientChartForm;
import net.sf.regadb.ui.form.testTestTypes.TestForm;
import net.sf.regadb.ui.form.testTestTypes.TestTypeForm;
import net.sf.regadb.ui.forms.account.AccountForm;
import net.sf.regadb.ui.forms.account.PasswordForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.administrator.AdministratorItem;
import net.sf.regadb.ui.tree.items.administrator.NotRegisteredUserSelectedItem;
import net.sf.regadb.ui.tree.items.administrator.RegisteredUserSelectedItem;
import net.sf.regadb.ui.tree.items.attributeSettings.AttributeGroupSelectedItem;
import net.sf.regadb.ui.tree.items.attributeSettings.AttributeSelectedItem;
import net.sf.regadb.ui.tree.items.myAccount.LoginItem;
import net.sf.regadb.ui.tree.items.myAccount.LogoutItem;
import net.sf.regadb.ui.tree.items.myAccount.MyAccountItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import net.sf.regadb.ui.tree.items.singlePatient.MeasurementSelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientAddItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientSelectItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientSelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.TherapySelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.ViralIsolateSelectedItem;
import net.sf.regadb.ui.tree.items.testSettings.TestSelectedItem;
import net.sf.regadb.ui.tree.items.testSettings.TestTypeSelectedItem;
import net.sf.witty.wt.WWidget;

public class TreeContent
{
    public PatientItem singlePatientMain;
    public PatientSelectItem patientSelect;
    public PatientAddItem patientAdd;
    public PatientSelectedItem patientSelected;
    public ActionItem viewPatient;
    public ActionItem editPatient;
    public ActionItem chart;
    public ActionItem measurements;
    public ActionItem measurementsSelect;
    public MeasurementSelectedItem measurementSelected;
    public ActionItem measurementView;
    public ActionItem measurementEdit;
    public ActionItem measurementsAdd;
    public ActionItem therapies;
    public ActionItem therapiesSelect;
    public ActionItem therapiesAdd;
    public TherapySelectedItem therapiesSelected;
    public ActionItem therapiesEdit;
    public ActionItem therapiesView;
    public ActionItem viralIsolates;
    public ActionItem viralIsolatesSelect;
    public ActionItem viralIsolatesAdd;
    public ViralIsolateSelectedItem viralIsolateSelected;
    public ActionItem viralIsolateView;
    public ActionItem viralIsolateEdit;
    
    public MyAccountItem myAccountMain;
    public LoginItem myAccountLogin;
    public LogoutItem myAccountLogout;
    public ActionItem myAccountView;
    public ActionItem myAccountEdit;
    public ActionItem myAccountCreate;
    public ActionItem myAccountEditPassword;
    
    public AdministratorItem administratorMain;
    public ActionItem enabledUsers;
    public ActionItem registeredUsersSelect;
    public RegisteredUserSelectedItem registeredUserSelected;
    public ActionItem registeredUsersView;
    public ActionItem registeredUsersEdit;
    public ActionItem registeredUsersChangePassword;
    public ActionItem disabledUsers;
    public ActionItem notRegisteredUsersSelect;
    public NotRegisteredUserSelectedItem notRegisteredUserSelected;
    public ActionItem notRegisteredUsersView;
    public ActionItem notRegisteredUsersEdit;
    public ActionItem notRegisteredUsersChangePassword;

    public ActionItem attributesSettings;
    public ActionItem attributes;
    public ActionItem attributesSelect;
    public ActionItem attributesAdd;
    public AttributeSelectedItem attributesSelected;
    public ActionItem attributesEdit;
    public ActionItem attributesView;
    public ActionItem attributeGroups;
    public ActionItem attributeGroupsSelect;
    public ActionItem attributeGroupsAdd;
    public AttributeGroupSelectedItem attributeGroupsSelected;
    public ActionItem attributeGroupsView;
    public ActionItem attributeGroupsEdit;
    
    public ActionItem testSettings;
    public ActionItem testTypes;
    public ActionItem testTypesSelect;
    public ActionItem testTypesAdd;
    public ActionItem testTypesEdit;
    public ActionItem testTypesView;
    public TestTypeSelectedItem testTypeSelected;
    
    public ActionItem test;
    public ActionItem testSelect;
    public ActionItem testAdd;
    public ActionItem testEdit;
    public ActionItem testView;
    public TestSelectedItem testSelected;
    
	public TreeMenuNode setContent(RootItem rootItem)
	{
		singlePatientMain = new PatientItem(rootItem);
		    patientSelect = new PatientSelectItem(singlePatientMain);
			patientAdd = new PatientAddItem(singlePatientMain);
            patientSelected = new PatientSelectedItem(singlePatientMain);
                viewPatient = new ActionItem(rootItem.tr("menu.singlePatient.view"), patientSelected, new ITreeAction()
                {
                    public void performAction(TreeMenuNode node) 
                    {
                        RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Viewing, WWidget.tr("form.singlePatient.view"), patientSelected.getSelectedItem()));
                    }
                });
                editPatient = new ActionItem(rootItem.tr("menu.singlePatient.edit"), patientSelected, new ITreeAction()
                {
                    public void performAction(TreeMenuNode node) 
                    {
                        RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Editing, WWidget.tr("form.singlePatient.edit"), patientSelected.getSelectedItem()));
                    }
                });
				chart = new ActionItem(rootItem.tr("menu.singlePatient.chart"), patientSelected, new ITreeAction()
                {
                    public void performAction(TreeMenuNode node) 
                    {
                        RegaDBMain.getApp().getFormContainer().setForm(new PatientChartForm(patientSelected.getSelectedItem()));
                    }
                });
    			measurements = new ActionItem(rootItem.tr("menu.singlePatient.measurements"), patientSelected);
    				measurementsSelect = new ActionItem(rootItem.tr("menu.singlePatient.measurements.select"), measurements, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node) 
                        {
                        	RegaDBMain.getApp().getFormContainer().setForm(new SelectMeasurementForm());
                        }
                    });
    				measurementsAdd = new ActionItem(rootItem.tr("menu.singlePatient.measurements.add"), measurements, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Adding, WWidget.tr("form.measurement.add"), null));
						}
    				});
    				measurementSelected = new MeasurementSelectedItem(measurements);
    				measurementView = new ActionItem(rootItem.tr("menu.singlePatient.measurement.view"), measurementSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Viewing, WWidget.tr("form.measurement.view"), measurementSelected.getSelectedItem()));
						}
    				});
    				measurementEdit = new ActionItem(rootItem.tr("menu.singlePatient.measurement.edit"), measurementSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Editing, WWidget.tr("form.measurement.edit"), measurementSelected.getSelectedItem()));
						}
    				});

    			therapies = new ActionItem(rootItem.tr("menu.singlePatient.therapies"), patientSelected);
    				therapiesSelect = new ActionItem(rootItem.tr("menu.singlePatient.therapies.select"), therapies, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node) 
                        {
                        	RegaDBMain.getApp().getFormContainer().setForm(new SelectTherapyForm());
                        }
                    });
    				therapiesAdd = new ActionItem(rootItem.tr("menu.singlePatient.therapies.add"), therapies, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Adding, WWidget.tr("form.therapy.add"), null));
						}
    				});
    				therapiesSelected = new TherapySelectedItem(therapies);
    				therapiesView = new ActionItem(rootItem.tr("menu.singlePatient.therapies.view"), therapiesSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Viewing, WWidget.tr("form.therapy.view"), therapiesSelected.getSelectedItem()));
						}
    				});
    				therapiesEdit = new ActionItem(rootItem.tr("menu.singlePatient.therapies.edit"), therapiesSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Editing, WWidget.tr("form.therapy.edit"), therapiesSelected.getSelectedItem()));
						}
    				});
    				
    				viralIsolates = new ActionItem(rootItem.tr("menu.singlePatient.viralIsolates"), patientSelected);
    				viralIsolatesSelect = new ActionItem(rootItem.tr("menu.singlePatient.viralIsolates.select"), viralIsolates, new ITreeAction()
                    {
    					public void performAction(TreeMenuNode node) 
                        {
                        	RegaDBMain.getApp().getFormContainer().setForm(new SelectViralIsolateForm());
                        }
                    });
                    viralIsolatesAdd = new ActionItem(rootItem.tr("menu.singlePatient.viralIsolates.add"), viralIsolates, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                            RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Adding, WWidget.tr("form.viralIsolate.add"), null));
                        }
                    });
    				viralIsolateSelected = new ViralIsolateSelectedItem(viralIsolates);
    				viralIsolateView = new ActionItem(rootItem.tr("menu.singlePatient.viralIsolates.view"), viralIsolateSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Viewing, WWidget.tr("form.viralIsolate.view"), viralIsolateSelected.getSelectedItem()));
						}
    				});
                    viralIsolateEdit = new ActionItem(rootItem.tr("menu.singlePatient.viralIsolates.edit"), viralIsolateSelected, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                            RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Editing, WWidget.tr("form.viralIsolate.edit"), viralIsolateSelected.getSelectedItem()));
                        }
                    });
    		
       attributesSettings = new ActionItem(rootItem.tr("menu.attributeSettings.attributeSettings"), rootItem)
       {
            @Override
            public boolean isEnabled()
            {
                return RegaDBMain.getApp().getLogin()!=null;
            }
       };
           attributes = new ActionItem(rootItem.tr("menu.attributeSettings.attributes"), attributesSettings);
           attributesSelect  = new ActionItem(rootItem.tr("menu.attributeSettings.attributes.select"), attributes, new ITreeAction()
           {
               public void performAction(TreeMenuNode node) 
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new SelectAttributeForm());
               }
           });
           attributesAdd = new ActionItem(rootItem.tr("menu.attributeSettings.attributes.add"), attributes, new ITreeAction()
           {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Adding, WWidget.tr("form.attributeSettings.attribute.add"), null));
                }
            });
           attributesSelected = new AttributeSelectedItem(attributes);
           attributesView = new ActionItem(rootItem.tr("menu.attributeSettings.attributes.view"), attributesSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Viewing, WWidget.tr("form.attributeSettings.attribute.view"), attributesSelected.getSelectedItem()));
               }
           });
           attributesEdit = new ActionItem(rootItem.tr("menu.attributeSettings.attributes.edit"), attributesSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Editing, WWidget.tr("form.attributeSettings.attribute.edit"), attributesSelected.getSelectedItem()));
               }
           });
           
           attributeGroups = new ActionItem(rootItem.tr("menu.attributeSettings.attributeGroups"), attributesSettings);
           attributeGroupsSelect  = new ActionItem(rootItem.tr("menu.attributeSettings.attributeGroups.select"), attributeGroups, new ITreeAction()
           {
               public void performAction(TreeMenuNode node) 
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new SelectAttributeGroupForm());
               }
           });
           attributeGroupsAdd = new ActionItem(rootItem.tr("menu.attributeSettings.attributeGroups.add"), attributeGroups, new ITreeAction()
           {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Adding, WWidget.tr("form.attributeSettings.attributeGroups.add"), null));
                }
            });
           attributeGroupsSelected = new AttributeGroupSelectedItem(attributeGroups);
           attributeGroupsView = new ActionItem(rootItem.tr("menu.attributeSettings.attributeGroups.view"), attributeGroupsSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Viewing, WWidget.tr("form.attributeSettings.attributeGroups.view"), attributeGroupsSelected.getSelectedItem()));
               }
           });
           attributeGroupsEdit = new ActionItem(rootItem.tr("menu.attributeSettings.attributeGroups.edit"), attributeGroupsSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Editing, WWidget.tr("form.attributeSettings.attributeGroups.edit"), attributeGroupsSelected.getSelectedItem()));
               }
           });
           
           
          testSettings = new ActionItem(rootItem.tr("menu.testSettings.testSettings"), rootItem)
           {
                @Override
                public boolean isEnabled()
                {
                    return RegaDBMain.getApp().getLogin()!=null;
                }
           };
          
          testTypes = new ActionItem(rootItem.tr("menu.testSettings.testTypes"), testSettings);
          testTypesSelect  = new ActionItem(rootItem.tr("menu.testSettings.testTypes.select"), testTypes, new ITreeAction()
           {
               public void performAction(TreeMenuNode node) 
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new SelectTestTypeForm());
               }
           });
           testTypesAdd = new ActionItem(rootItem.tr("menu.testSettings.testTypes.add"), testTypes, new ITreeAction()
           {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new TestTypeForm(InteractionState.Adding, WWidget.tr("form.testSettings.testType.add"),null));
                    
                }
            });
           testTypeSelected = new TestTypeSelectedItem(testTypes);
           testTypesView = new ActionItem(rootItem.tr("menu.testSettings.testTypes.view"), testTypeSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new TestTypeForm(InteractionState.Viewing, WWidget.tr("form.testSettings.testType.view"),testTypeSelected.getSelectedItem()));
               }
           });
           testTypesEdit = new ActionItem(rootItem.tr("menu.testSettings.testTypes.edit"), testTypeSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new TestTypeForm(InteractionState.Editing, WWidget.tr("form.testSettings.testType.edit"),testTypeSelected.getSelectedItem()));
               }
           });
           
           
           //test
           test = new ActionItem(rootItem.tr("menu.testSettings.tests"), testSettings);        
           testSelect  = new ActionItem(rootItem.tr("menu.testSettings.tests.select"), test, new ITreeAction()
           {
               public void performAction(TreeMenuNode node) 
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new SelectTestForm());
               }
           });
           testAdd = new ActionItem(rootItem.tr("menu.testSettings.tests.add"), test, new ITreeAction()
           {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new TestForm(InteractionState.Adding, WWidget.tr("form.testSettings.test.add"),null));
                    
                }
            });
           testSelected = new TestSelectedItem(test);
           testView = new ActionItem(rootItem.tr("menu.testSettings.tests.view"), testSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new TestForm(InteractionState.Viewing, WWidget.tr("form.testSettings.testType.view"),testSelected.getSelectedItem()));
               }
           });
           testEdit = new ActionItem(rootItem.tr("menu.testSettings.tests.edit"), testSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new TestForm(InteractionState.Editing, WWidget.tr("form.testSettings.test.edit"),testSelected.getSelectedItem()));
               }
           });

        myAccountMain = new MyAccountItem(rootItem);
            myAccountLogin = new LoginItem(myAccountMain);
            myAccountView = new ActionItem(rootItem.tr("form.account.view"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("menu.myAccount.accountForm"), InteractionState.Viewing, myAccountView, myAccountMain, false, null));
                }
            })
            {
                @Override
                public boolean isEnabled()
                {
                    return RegaDBMain.getApp().getLogin()!=null;
                }
            };
            myAccountCreate = new ActionItem(rootItem.tr("form.account.create"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("menu.myAccount.accountForm"), InteractionState.Adding, myAccountLogin, myAccountMain, false, new SettingsUser()));
                }
            })
            {
                @Override
                public boolean isEnabled()
                {
                    return RegaDBMain.getApp().getLogin()==null;
                }
            };
            myAccountEdit = new ActionItem(rootItem.tr("form.account.edit"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("menu.myAccount.accountForm"), InteractionState.Editing, myAccountView, myAccountMain, false, null));
                }
            })
            {
                @Override
                public boolean isEnabled()
                {
                    return RegaDBMain.getApp().getLogin()!=null;
                }
            };
            myAccountEditPassword = new ActionItem(rootItem.tr("form.account.edit.password"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new PasswordForm(WWidget.tr("menu.myAccount.passwordForm"), InteractionState.Editing, myAccountView, myAccountMain, false, null));
                }
            })
            {
                @Override
                public boolean isEnabled()
                {
                    return RegaDBMain.getApp().getLogin()!=null;
                }
            };
            myAccountLogout = new LogoutItem(myAccountMain);
            
        administratorMain = new AdministratorItem(rootItem);
            enabledUsers = new ActionItem(rootItem.tr("menu.administrator.registered.users"), administratorMain);
            registeredUsersSelect = new ActionItem(rootItem.tr("menu.administrator.users.select"), enabledUsers, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectSettingsUserForm(true));
                }
            });
            registeredUserSelected = new RegisteredUserSelectedItem(enabledUsers);
            registeredUsersView = new ActionItem(rootItem.tr("menu.administrator.users.view"), registeredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("form.administrator.registeredUser.view"), InteractionState.Viewing, registeredUsersSelect, registeredUserSelected, true, registeredUserSelected.getSelectedItem()));
                }
            });
            registeredUsersEdit = new ActionItem(rootItem.tr("menu.administrator.users.edit"), registeredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("form.administrator.registeredUser.edit"), InteractionState.Editing, registeredUsersSelect, registeredUserSelected, true, registeredUserSelected.getSelectedItem()));
                }
            });
            registeredUsersChangePassword = new ActionItem(rootItem.tr("form.settings.user.password"), registeredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new PasswordForm(WWidget.tr("menu.myAccount.passwordForm"), InteractionState.Editing, registeredUsersView, registeredUserSelected, true, registeredUserSelected.getSelectedItem()));
                }
            });
            
            disabledUsers = new ActionItem(rootItem.tr("menu.administrator.notregistered.users"), administratorMain);
            notRegisteredUsersSelect = new ActionItem(rootItem.tr("menu.administrator.users.select"), disabledUsers, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectSettingsUserForm(false));
                }
            });
            notRegisteredUserSelected = new NotRegisteredUserSelectedItem(disabledUsers);
            notRegisteredUsersView = new ActionItem(rootItem.tr("menu.administrator.users.view"), notRegisteredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("form.administrator.notRegisteredUser.view"), InteractionState.Viewing, notRegisteredUsersSelect, notRegisteredUserSelected, true, notRegisteredUserSelected.getSelectedItem()));
                }
            });
            notRegisteredUsersEdit = new ActionItem(rootItem.tr("menu.administrator.users.edit"), notRegisteredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("form.administrator.notRegisteredUser.edit"), InteractionState.Editing, notRegisteredUsersSelect, notRegisteredUserSelected, true, notRegisteredUserSelected.getSelectedItem()));
                }
            });
            notRegisteredUsersChangePassword = new ActionItem(rootItem.tr("form.settings.user.password"), notRegisteredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new PasswordForm(WWidget.tr("menu.myAccount.passwordForm"), InteractionState.Editing, registeredUsersView, registeredUserSelected, true, registeredUserSelected.getSelectedItem()));
                }
            });
			
		if(singlePatientMain.isEnabled())
		{
			return singlePatientMain;
		}
		else
		{
			return myAccountLogin;
		}
	}
}
