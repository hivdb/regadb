package net.sf.regadb.ui.tree;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.datatable.attributeSettings.SelectAttributeForm;
import net.sf.regadb.ui.datatable.attributeSettings.SelectAttributeGroupForm;
import net.sf.regadb.ui.datatable.datasetSettings.SelectDatasetAccessUserForm;
import net.sf.regadb.ui.datatable.datasetSettings.SelectDatasetForm;
import net.sf.regadb.ui.datatable.importTool.SelectImportToolForm;
import net.sf.regadb.ui.datatable.log.SelectLogForm;
import net.sf.regadb.ui.datatable.query.SelectQueryDefinitionForm;
import net.sf.regadb.ui.datatable.query.SelectQueryDefinitionRunForm;
import net.sf.regadb.ui.datatable.query.SelectQueryToolQueryForm;
import net.sf.regadb.ui.datatable.settingsUser.SelectSettingsUserForm;
import net.sf.regadb.ui.datatable.testSettings.SelectResRepTemplateForm;
import net.sf.regadb.ui.datatable.testSettings.SelectTestForm;
import net.sf.regadb.ui.datatable.testSettings.SelectTestTypeForm;
import net.sf.regadb.ui.form.administrator.ContaminationOverview;
import net.sf.regadb.ui.form.administrator.SampleDistancesForm;
import net.sf.regadb.ui.form.administrator.UpdateForm;
import net.sf.regadb.ui.form.administrator.VersionForm;
import net.sf.regadb.ui.form.attributeSettings.AttributeForm;
import net.sf.regadb.ui.form.attributeSettings.AttributeGroupForm;
import net.sf.regadb.ui.form.batchtest.BatchTestAddForm;
import net.sf.regadb.ui.form.batchtest.BatchTestRunningForm;
import net.sf.regadb.ui.form.datasetSettings.DatasetAccessForm;
import net.sf.regadb.ui.form.datasetSettings.DatasetForm;
import net.sf.regadb.ui.form.event.EventForm;
import net.sf.regadb.ui.form.impex.ExportForm;
import net.sf.regadb.ui.form.impex.ImportFormAdd;
import net.sf.regadb.ui.form.impex.ImportFormRunning;
import net.sf.regadb.ui.form.importTool.ImportToolForm;
import net.sf.regadb.ui.form.log.LogForm;
import net.sf.regadb.ui.form.log.LogSelectedItem;
import net.sf.regadb.ui.form.query.QueryDefinitionForm;
import net.sf.regadb.ui.form.query.QueryDefinitionRunForm;
import net.sf.regadb.ui.form.query.custom.NadirQuery;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.form.query.wiv.WivArcCd4Form;
import net.sf.regadb.ui.form.query.wiv.WivArcDeathsForm;
import net.sf.regadb.ui.form.query.wiv.WivArcLastContactForm;
import net.sf.regadb.ui.form.query.wiv.WivArcTherapyAtcForm;
import net.sf.regadb.ui.form.query.wiv.WivArcViralLoadForm;
import net.sf.regadb.ui.form.query.wiv.WivArlCd4Form;
import net.sf.regadb.ui.form.query.wiv.WivArlConfirmedHivForm;
import net.sf.regadb.ui.form.query.wiv.WivArlEpidemiologyForm;
import net.sf.regadb.ui.form.query.wiv.WivArlViralLoadForm;
import net.sf.regadb.ui.form.testTestTypes.ResistanceInterpretationTemplateForm;
import net.sf.regadb.ui.form.testTestTypes.TestForm;
import net.sf.regadb.ui.form.testTestTypes.TestTypeForm;
import net.sf.regadb.ui.forms.account.AccountForm;
import net.sf.regadb.ui.forms.account.PasswordForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.administrator.AdministratorItem;
import net.sf.regadb.ui.tree.items.administrator.UserSelectedItem;
import net.sf.regadb.ui.tree.items.attributeSettings.AttributeGroupSelectedItem;
import net.sf.regadb.ui.tree.items.attributeSettings.AttributeSelectedItem;
import net.sf.regadb.ui.tree.items.datasetSettings.DatasetAccessSelectedItem;
import net.sf.regadb.ui.tree.items.datasetSettings.DatasetSelectedItem;
import net.sf.regadb.ui.tree.items.events.EventSelectedItem;
import net.sf.regadb.ui.tree.items.events.SelectEventForm;
import net.sf.regadb.ui.tree.items.importTool.ImportToolSelectedItem;
import net.sf.regadb.ui.tree.items.myAccount.LoginItem;
import net.sf.regadb.ui.tree.items.myAccount.LogoutItem;
import net.sf.regadb.ui.tree.items.myAccount.MyAccountItem;
import net.sf.regadb.ui.tree.items.query.QueryDefinitionItem;
import net.sf.regadb.ui.tree.items.query.QueryDefinitionRunItem;
import net.sf.regadb.ui.tree.items.query.QueryDefinitionRunSelectedItem;
import net.sf.regadb.ui.tree.items.query.QueryDefinitionSelectedItem;
import net.sf.regadb.ui.tree.items.query.QueryItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientTreeNode;
import net.sf.regadb.ui.tree.items.testSettings.ResRepTemplateSelectedItem;
import net.sf.regadb.ui.tree.items.testSettings.TestSelectedItem;
import net.sf.regadb.ui.tree.items.testSettings.TestTypeSelectedItem;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WResource;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class TreeContent
{
	public PatientTreeNode patientTreeNode;
    
    public MyAccountItem myAccountMain;
    public LoginItem myAccountLogin;
    public ActionItem myAccountView;
    public ActionItem myAccountEdit;
    public ActionItem myAccountCreate;
    public ActionItem myAccountEditPassword;
    //public ActionItem datasetAccess;
    public LogoutItem myAccountLogout;
    
    public ActionItem datasetSettings;
    public ActionItem datasets;
    public ActionItem datasetSelect;
    public ActionItem datasetAdd;
    public ActionItem datasetEdit;
    public ActionItem datasetView;
    public ActionItem datasetDelete;
    public DatasetSelectedItem datasetSelected;
    
    public ActionItem datasetAccess;
    public ActionItem datasetAccessSelect;
    public DatasetAccessSelectedItem datasetAccessSelected;
    public ActionItem datasetAccessView;
    public ActionItem datasetAccessEdit;
    
    public QueryItem queryMain;

    public QueryItem queryToolMain;
    public ActionItem queryToolSelect;
    public ActionItem queryToolAdd;
    public ActionItem queryToolView;
    
    public QueryItem queryWiv;
    public ActionItem queryWivArlConfirmedHiv;
    public ActionItem queryWivArlEpidemiology;
    public ActionItem queryWivArlViralLoad;
    public ActionItem queryWivArlCd4;
    public ActionItem queryWivArcCd4;
    public ActionItem queryWivArcViralLoad;
    public ActionItem queryWivArcTherapyAtc;
    public ActionItem queryWivArcLastContact;
    public ActionItem queryWivArcDeaths;
    
    public QueryDefinitionItem queryDefinitionMain;
    public QueryDefinitionSelectedItem queryDefinitionSelected;
    public ActionItem queryDefinitionSelect;
    public ActionItem queryDefinitionAdd;
    public ActionItem queryDefinitionSelectedView;
    public ActionItem queryDefinitionSelectedEdit;
    public ActionItem queryDefinitionSelectedDelete;
    public ActionItem queryDefinitionSelectedRun;
    public QueryDefinitionRunItem queryDefinitionRunMain;
    public QueryDefinitionRunSelectedItem queryDefinitionRunSelected;
    public ActionItem queryDefinitionRunSelect;
    public ActionItem queryDefinitionRunSelectedView;
    public ActionItem queryDefinitionRunSelectedDelete;
    
    public AdministratorItem administratorMain;
    public ActionItem users;
    public ActionItem usersSelect;
    public UserSelectedItem userSelected;
    public ActionItem userDelete;
    public ActionItem usersView;
    public ActionItem usersEdit;
    public ActionItem usersDelete;
    public ActionItem usersChangePassword;
    public ActionItem updateFromCentralServer;
    public ActionItem updateFromCentralServerUpdate;
    public ActionItem updateFromCentralServerUpdateView;
    
    public ActionItem importTool;
    public ActionItem importToolSelect;
    public ActionItem importToolAdd;
    public ImportToolSelectedItem importToolSelected;
    public ActionItem importToolSelectedView;
    public ActionItem importToolSelectedEdit;
    public ActionItem importToolSelectedDelete;
    
    public ActionItem importXML;
    public ActionItem importXMLadd;
    public ActionItem importXMLrun;
    public ActionItem exportXML;
    public ActionItem batchTest;
    public ActionItem batchTestAdd;
    public ActionItem batchTestRunning;
    
    public ActionItem attributesSettings;
    public ActionItem attributes;
    public ActionItem attributesSelect;
    public ActionItem attributesAdd;
    public AttributeSelectedItem attributesSelected;
    public ActionItem attributesView;
    public ActionItem attributesEdit;
    public ActionItem attributesDelete;
    public ActionItem attributeGroups;
    public ActionItem attributeGroupsSelect;
    public ActionItem attributeGroupsAdd;
    public AttributeGroupSelectedItem attributeGroupsSelected;
    public ActionItem attributeGroupsView;
    public ActionItem attributeGroupsEdit;
    public ActionItem attributeGroupsDelete;
    
    public ActionItem testSettings;
    public ActionItem testTypes;
    public ActionItem testTypesSelect;
    public ActionItem testTypesAdd;
    public ActionItem testTypesView;
    public ActionItem testTypesEdit;
    public ActionItem testTypesDelete;
    public TestTypeSelectedItem testTypeSelected;
    
    public ActionItem test;
    public ActionItem testSelect;
    public ActionItem testAdd;
    public ActionItem testView;
    public ActionItem testEdit;
    public ActionItem testDelete;
    public TestSelectedItem testSelected;
    
    public ActionItem resRepTemplate;
    public ActionItem resRepTemplateSelect;
    public ActionItem resRepTemplateAdd;
    public ActionItem resRepTemplateView;
    public ActionItem resRepTemplateEdit;
    public ActionItem resRepTemplateDelete;
    public ResRepTemplateSelectedItem resRepTemplateSelected;
    
    public ActionItem event;
    public ActionItem eventSelect;
    public ActionItem eventAdd;
    public EventSelectedItem eventSelected;
    public ActionItem eventSelectedView;
    public ActionItem eventSelectedEdit;
    public ActionItem eventSelectedDelete;
    
    public ActionItem log;
    public ActionItem logSelect;
    public ActionItem logView;
    public ActionItem logDelete;
    public LogSelectedItem logSelectedItem;
    
    public ActionItem version;
    
	public QueryDefinitionSelectedItem queryToolSelected;
	public ActionItem queryToolSelectedView;
	public ActionItem queryToolSelectedEdit;
	public ActionItem queryToolSelectedDelete;
    
	public TreeMenuNode setContent(RootItem rootItem)
	{
		patientTreeNode = new PatientTreeNode(rootItem);
        
        queryMain = new QueryItem(rootItem);
        
        queryToolMain = new QueryItem(WResource.tr("menu.query.querytool"), queryMain);

          queryToolSelect = new ActionItem(WResource.tr("menu.query.querytool.select"), queryToolMain, new ITreeAction() {
  			public void performAction(TreeMenuNode node) {
  				RegaDBMain.getApp().getFormContainer().setForm(new SelectQueryToolQueryForm());
  			}
          });
          
            queryToolAdd = new ActionItem(WResource.tr("menu.query.querytool.add"), queryToolMain, new ITreeAction() {
                public void performAction(TreeMenuNode node) {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryToolForm(WResource.tr("form.query.querytool.add"), InteractionState.Adding ));
                }
            }); 
            
            queryToolSelected = new QueryDefinitionSelectedItem("menu.query.querytool.selectedItem", queryToolMain);
            queryToolSelectedView = new ActionItem(WResource.tr("menu.query.querytool.selected.view"), queryToolSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryToolForm(WWidget.tr("form.query.querytool.selected.view"), InteractionState.Viewing, queryToolSelected.getSelectedItem()));
                }
            });
            queryToolSelectedEdit = new ActionItem(WResource.tr("menu.query.querytool.selected.edit"), queryToolSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                	RegaDBMain.getApp().getFormContainer().setForm(new QueryToolForm(WWidget.tr("form.query.querytool.selected.edit"), InteractionState.Editing, queryToolSelected.getSelectedItem()));
                }
            })
            {
                @Override
                public boolean isDisabled()
                {
                	if((RegaDBMain.getApp().getLogin() != null) && (queryToolSelected.getSelectedItem() != null))
                	{
                		return !((RegaDBMain.getApp().getLogin().getUid()).equals(queryToolSelected.getQueryDefinitionCreator(queryToolSelected.getSelectedItem())));
                	}
                	else
                	{
                		return true;
                	}
                	
                }
            };
            queryToolSelectedDelete = new ActionItem(WResource.tr("menu.query.querytool.selected.delete"), queryToolSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryToolForm(WWidget.tr("form.query.querytool.selected.delete"), InteractionState.Deleting, queryToolSelected.getSelectedItem()));
                }
            })
            {
                @Override
                public boolean isDisabled()
                {
                	if((RegaDBMain.getApp().getLogin() != null) && (queryToolSelected.getSelectedItem() != null))
                	{
                		return !((RegaDBMain.getApp().getLogin().getUid()).equals(queryToolSelected.getQueryDefinitionCreator(queryToolSelected.getSelectedItem())));
                	}
                	else
                	{
                		return true;
                	}
                }
            };

        queryDefinitionMain = new QueryDefinitionItem(queryMain);
            queryDefinitionSelect = new ActionItem(WResource.tr("menu.query.definition.select"), queryDefinitionMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectQueryDefinitionForm());
                }
            });
            queryDefinitionAdd = new ActionItem(WResource.tr("menu.query.definition.add"), queryDefinitionMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionForm(WWidget.tr("form.query.definition.add"), InteractionState.Adding, new QueryDefinition(StandardObjects.getHqlQueryQueryType())));
                }
            });
            queryDefinitionSelected = new QueryDefinitionSelectedItem("menu.query.definition.selectedItem", queryDefinitionMain);
            queryDefinitionSelectedView = new ActionItem(WResource.tr("menu.query.definition.selected.view"), queryDefinitionSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionForm(WWidget.tr("form.query.definition.selected.view"), InteractionState.Viewing, queryDefinitionSelected.getSelectedItem()));
                }
            });
            queryDefinitionSelectedEdit = new ActionItem(WResource.tr("menu.query.definition.selected.edit"), queryDefinitionSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                	RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionForm(WWidget.tr("form.query.definition.selected.edit"), InteractionState.Editing, queryDefinitionSelected.getSelectedItem()));
                }
            })
            {
                @Override
                public boolean isDisabled()
                {
                	if((RegaDBMain.getApp().getLogin() != null) && (queryDefinitionSelected.getSelectedItem() != null))
                	{
                		return !((RegaDBMain.getApp().getLogin().getUid()).equals(queryDefinitionSelected.getQueryDefinitionCreator(queryDefinitionSelected.getSelectedItem())));
                	}
                	else
                	{
                		return true;
                	}
                	
                }
            };
            queryDefinitionSelectedDelete = new ActionItem(WResource.tr("menu.query.definition.selected.delete"), queryDefinitionSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionForm(WWidget.tr("form.query.definition.selected.delete"), InteractionState.Deleting, queryDefinitionSelected.getSelectedItem()));
                }
            })
            {
                @Override
                public boolean isDisabled()
                {
                	if((RegaDBMain.getApp().getLogin() != null) && (queryDefinitionSelected.getSelectedItem() != null))
                	{
                		return !((RegaDBMain.getApp().getLogin().getUid()).equals(queryDefinitionSelected.getQueryDefinitionCreator(queryDefinitionSelected.getSelectedItem())));
                	}
                	else
                	{
                		return true;
                	}
                }
            };
            queryDefinitionSelectedRun = new ActionItem(WResource.tr("menu.query.definition.selected.run"), queryDefinitionSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionRunForm(WWidget.tr("form.query.definition.run.add"), InteractionState.Adding, new QueryDefinitionRun()));
                }
            });
            queryDefinitionRunMain = new QueryDefinitionRunItem(queryMain);
            queryDefinitionRunSelect = new ActionItem(WResource.tr("menu.query.definition.run.select"), queryDefinitionRunMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectQueryDefinitionRunForm());
                }
            });
            queryDefinitionRunSelected = new QueryDefinitionRunSelectedItem(queryDefinitionRunMain);
            queryDefinitionRunSelectedView = new ActionItem(WResource.tr("menu.query.definition.run.selected.view"), queryDefinitionRunSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionRunForm(WWidget.tr("form.query.definition.run.selected.view"), InteractionState.Viewing, queryDefinitionRunSelected.getSelectedItem()));
                }
            });
            queryDefinitionRunSelectedDelete = new ActionItem(WResource.tr("menu.query.definition.run.selected.delete"), queryDefinitionRunSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionRunForm(WWidget.tr("form.query.definition.selected.delete"), InteractionState.Deleting, queryDefinitionRunSelected.getSelectedItem()));
                }
            });
        

        if(RegaDBSettings.getInstance().getInstituteConfig().getWivConfig() != null){
	        queryWiv = new QueryItem(WResource.tr("menu.query.wiv"),queryMain);
	
	            queryWivArlConfirmedHiv = new ActionItem(WResource.tr("menu.query.wiv.arl.confirmedHiv"), queryWiv, new ITreeAction()
	            {
	                public void performAction(TreeMenuNode node) 
	                {
	                    RegaDBMain.getApp().getFormContainer().setForm(new WivArlConfirmedHivForm());
	                }
	            });
	            
	            queryWivArlEpidemiology = new ActionItem(WResource.tr("menu.query.wiv.arl.epidemiology"), queryWiv, new ITreeAction()
	            {
	                public void performAction(TreeMenuNode node) 
	                {
	                    RegaDBMain.getApp().getFormContainer().setForm(new WivArlEpidemiologyForm());
	                }
	            });
	            queryWivArlCd4 = new ActionItem(WResource.tr("menu.query.wiv.arl.cd4"), queryWiv, new ITreeAction()
	            {
	                public void performAction(TreeMenuNode node) 
	                {
	                    RegaDBMain.getApp().getFormContainer().setForm(new WivArlCd4Form());
	                }
	            });
	            queryWivArlViralLoad = new ActionItem(WResource.tr("menu.query.wiv.arl.viralLoad"), queryWiv, new ITreeAction()
	            {
	                public void performAction(TreeMenuNode node) 
	                {
	                    RegaDBMain.getApp().getFormContainer().setForm(new WivArlViralLoadForm());
	                }
	            });
	            queryWivArcCd4 = new ActionItem(WResource.tr("menu.query.wiv.arc.cd4"), queryWiv, new ITreeAction()
	            {
	                public void performAction(TreeMenuNode node) 
	                {
	                    RegaDBMain.getApp().getFormContainer().setForm(new WivArcCd4Form());
	                }
	            });
	            queryWivArcViralLoad = new ActionItem(WResource.tr("menu.query.wiv.arc.viralLoad"), queryWiv, new ITreeAction()
	            {
	                public void performAction(TreeMenuNode node) 
	                {
	                    RegaDBMain.getApp().getFormContainer().setForm(new WivArcViralLoadForm());
	                }
	            });
	            queryWivArcTherapyAtc = new ActionItem(WResource.tr("menu.query.wiv.arc.therapyAtc"), queryWiv, new ITreeAction()
	            {
	                public void performAction(TreeMenuNode node) 
	                {
	                    RegaDBMain.getApp().getFormContainer().setForm(new WivArcTherapyAtcForm());
	                }
	            });
	            queryWivArcLastContact = new ActionItem(WResource.tr("menu.query.wiv.arc.lastContact"), queryWiv, new ITreeAction()
	            {
	                public void performAction(TreeMenuNode node) 
	                {
	                    RegaDBMain.getApp().getFormContainer().setForm(new WivArcLastContactForm());
	                }
	            });
	            queryWivArcDeaths = new ActionItem(WResource.tr("menu.query.wiv.arc.deaths"), queryWiv, new ITreeAction()
	            {
	                public void performAction(TreeMenuNode node) 
	                {
	                    RegaDBMain.getApp().getFormContainer().setForm(new WivArcDeathsForm());
	                }
	            });
	            
        }
        
        QueryItem queryCustom = new QueryItem(WResource.tr("menu.query.custom"),queryMain);
    	
        ActionItem queryNadir = new ActionItem(WResource.tr("form.query.custom.nadir.name"), queryCustom, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                RegaDBMain.getApp().getFormContainer().setForm(new NadirQuery());
            }
        });

           
        myAccountMain = new MyAccountItem(rootItem);
            myAccountLogin = new LoginItem(myAccountMain);
            myAccountCreate = new ActionItem(WResource.tr("menu.myAccount.add"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("form.account.create"), InteractionState.Adding, myAccountLogin, myAccountMain, false, new SettingsUser()));
                }
            })
            {
                @Override
                public boolean isDisabled()
                {
                    return RegaDBMain.getApp().getLogin()!=null;
                }
            };
            myAccountView = new ActionItem(WResource.tr("menu.myAccount.view"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("form.account.view"), InteractionState.Viewing, myAccountView, myAccountMain, false, null));
                }
            })
            {
                @Override
                public boolean isDisabled()
                {
                    return RegaDBMain.getApp().getLogin()==null;
                }
            };
            myAccountEdit = new ActionItem(WResource.tr("menu.myAccount.edit"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("form.account.edit"), InteractionState.Editing, myAccountView, myAccountMain, false, null));
                }
            })
            {
                @Override
                public boolean isDisabled()
                {
                    return RegaDBMain.getApp().getLogin()==null;
                }
            };
            myAccountEditPassword = new ActionItem(WResource.tr("menu.myAccount.passwordForm"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new PasswordForm(WWidget.tr("form.account.edit.password"), InteractionState.Editing, myAccountView, myAccountMain, false, null));
                }
            })
            {
                @Override
                public boolean isDisabled()
                {
                    return RegaDBMain.getApp().getLogin()==null;
                }
            };
            myAccountLogout = new LogoutItem(myAccountMain);
        
        administratorMain = new AdministratorItem(rootItem);
        
        attributesSettings = new ActionItem(WResource.tr("menu.attributeSettings.attributeSettings"), administratorMain)
        {
             @Override
             public boolean isDisabled()
             {
                 return RegaDBMain.getApp().getLogin()==null;
             }
        };
            attributes = new ActionItem(WResource.tr("menu.attributeSettings.attributes"), attributesSettings);
            attributesSelect  = new ActionItem(WResource.tr("menu.attributeSettings.attributes.select"), attributes, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectAttributeForm());
                }
            });
            attributesAdd = new ActionItem(WResource.tr("menu.attributeSettings.attributes.add"), attributes, new ITreeAction()
            {
                 public void performAction(TreeMenuNode node)
                 {
                 	TreeContent.this.attributesSelected.setSelectedItem(null);
                     RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Adding, WWidget.tr("form.attributeSettings.attribute.add"), null));
                 }
             });
            attributesSelected = new AttributeSelectedItem(attributes);
            attributesView = new ActionItem(WResource.tr("menu.attributeSettings.attributes.view"), attributesSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Viewing, WWidget.tr("form.attributeSettings.attribute.view"), attributesSelected.getSelectedItem()));
                }
            });
            attributesEdit = new ActionItem(WResource.tr("menu.attributeSettings.attributes.edit"), attributesSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Editing, WWidget.tr("form.attributeSettings.attribute.edit"), attributesSelected.getSelectedItem()));
                }
            });
            attributesDelete = new ActionItem(WResource.tr("menu.attributeSettings.attributes.delete"), attributesSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Deleting, WWidget.tr("form.attributeSettings.attribute.delete"), attributesSelected.getSelectedItem()));
                }
            });
            
            attributeGroups = new ActionItem(WResource.tr("menu.attributeSettings.attributeGroups"), attributesSettings);
            attributeGroupsSelect  = new ActionItem(WResource.tr("menu.attributeSettings.attributeGroups.select"), attributeGroups, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectAttributeGroupForm());
                }
            });
            attributeGroupsAdd = new ActionItem(WResource.tr("menu.attributeSettings.attributeGroups.add"), attributeGroups, new ITreeAction()
            {
                 public void performAction(TreeMenuNode node)
                 {
                 	TreeContent.this.attributeGroupsSelected.setSelectedItem(null);
                     RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Adding, WWidget.tr("form.attributeSettings.attributeGroups.add"), null));
                 }
             });
            attributeGroupsSelected = new AttributeGroupSelectedItem(attributeGroups);
            attributeGroupsView = new ActionItem(WResource.tr("menu.attributeSettings.attributeGroups.view"), attributeGroupsSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Viewing, WWidget.tr("form.attributeSettings.attributeGroups.view"), attributeGroupsSelected.getSelectedItem()));
                }
            });
            attributeGroupsEdit = new ActionItem(WResource.tr("menu.attributeSettings.attributeGroups.edit"), attributeGroupsSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Editing, WWidget.tr("form.attributeSettings.attributeGroups.edit"), attributeGroupsSelected.getSelectedItem()));
                }
            });
            attributeGroupsDelete = new ActionItem(WResource.tr("menu.attributeSettings.attributeGroups.delete"), attributeGroupsSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Deleting, WWidget.tr("form.attributeSettings.attributeGroups.delete"), attributeGroupsSelected.getSelectedItem()));
                }
            });
            
           testSettings = new ActionItem(WResource.tr("menu.testSettings.testSettings"), administratorMain)
            {
                 @Override
                 public boolean isDisabled()
                 {
                     return RegaDBMain.getApp().getLogin()==null;
                 }
            };
           
           testTypes = new ActionItem(WResource.tr("menu.testSettings.testTypes"), testSettings);
           testTypesSelect  = new ActionItem(WResource.tr("menu.testSettings.testTypes.select"), testTypes, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectTestTypeForm());
                }
            });
            testTypesAdd = new ActionItem(WResource.tr("menu.testSettings.testTypes.add"), testTypes, new ITreeAction()
            {
                 public void performAction(TreeMenuNode node)
                 {
                 	TreeContent.this.testTypeSelected.setSelectedItem(null);
                     RegaDBMain.getApp().getFormContainer().setForm(new TestTypeForm(InteractionState.Adding, WWidget.tr("form.testSettings.testType.add"),null));
                 }
             });
            testTypeSelected = new TestTypeSelectedItem(testTypes);
            testTypesView = new ActionItem(WResource.tr("menu.testSettings.testTypes.view"), testTypeSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new TestTypeForm(InteractionState.Viewing, WWidget.tr("form.testSettings.testType.view"),testTypeSelected.getSelectedItem()));
                }
            });
            testTypesEdit = new ActionItem(WResource.tr("menu.testSettings.testTypes.edit"), testTypeSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new TestTypeForm(InteractionState.Editing, WWidget.tr("form.testSettings.testType.edit"),testTypeSelected.getSelectedItem()));
                }
            });
            testTypesDelete = new ActionItem(WResource.tr("menu.testSettings.testTypes.delete"), testTypeSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new TestTypeForm(InteractionState.Deleting, WWidget.tr("form.testSettings.testType.delete"),testTypeSelected.getSelectedItem()));
                }
            });
            
            //test
            test = new ActionItem(WResource.tr("menu.testSettings.tests"), testSettings);        
            testSelect  = new ActionItem(WResource.tr("menu.testSettings.tests.select"), test, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectTestForm());
                }
            });
            testAdd = new ActionItem(WResource.tr("menu.testSettings.tests.add"), test, new ITreeAction()
            {
                 public void performAction(TreeMenuNode node)
                 {
                 	TreeContent.this.testSelected.setSelectedItem(null);
                     RegaDBMain.getApp().getFormContainer().setForm(new TestForm(InteractionState.Adding, WWidget.tr("form.testSettings.test.add"),null));
                 }
             });
            testSelected = new TestSelectedItem(test);
            testView = new ActionItem(WResource.tr("menu.testSettings.tests.view"), testSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new TestForm(InteractionState.Viewing, WWidget.tr("form.testSettings.testType.view"),testSelected.getSelectedItem()));
                }
            });
            testEdit = new ActionItem(WResource.tr("menu.testSettings.tests.edit"), testSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new TestForm(InteractionState.Editing, WWidget.tr("form.testSettings.test.edit"),testSelected.getSelectedItem()));
                }
            });
            testDelete = new ActionItem(WResource.tr("menu.testSettings.tests.delete"), testSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new TestForm(InteractionState.Deleting, WWidget.tr("form.testSettings.test.delete"),testSelected.getSelectedItem()));
                }
            });
            
            //resistance report template
            resRepTemplate = new ActionItem(WResource.tr("menu.resistance.report.template"), testSettings);        
            resRepTemplateSelect  = new ActionItem(WResource.tr("menu.resistance.report.template.select"), resRepTemplate, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectResRepTemplateForm());
                }
            });
            resRepTemplateAdd = new ActionItem(WResource.tr("menu.resistance.report.template.add"), resRepTemplate, new ITreeAction()
            {
                 public void performAction(TreeMenuNode node)
                 {
                     RegaDBMain.getApp().getFormContainer().setForm(new ResistanceInterpretationTemplateForm(InteractionState.Adding, WWidget.tr("form.resistance.report.template.add"), null));
                     
                 }
             });
            resRepTemplateSelected = new ResRepTemplateSelectedItem(resRepTemplate);
            resRepTemplateView = new ActionItem(WResource.tr("menu.resistance.report.template.view"), resRepTemplateSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new ResistanceInterpretationTemplateForm(InteractionState.Viewing, WWidget.tr("form.resistance.report.template.view"), resRepTemplateSelected.getSelectedItem()));
                }
            });
            resRepTemplateEdit = new ActionItem(WResource.tr("menu.resistance.report.template.edit"), resRepTemplateSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new ResistanceInterpretationTemplateForm(InteractionState.Editing, WWidget.tr("form.resistance.report.template.edit"), resRepTemplateSelected.getSelectedItem()));
                }
            });
            resRepTemplateDelete = new ActionItem(WResource.tr("menu.resistance.report.template.delete"), resRepTemplateSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new ResistanceInterpretationTemplateForm(InteractionState.Deleting, WWidget.tr("form.resistance.report.template.delete"), resRepTemplateSelected.getSelectedItem()));
                }
            });
            
            event = new ActionItem(RootItem.tr("menu.event"), administratorMain)
            {
                @Override
                public boolean isDisabled()
                {
                    return RegaDBMain.getApp().getLogin()==null;
                }
            };
            
            eventSelect = new ActionItem(WResource.tr("menu.event.select"), event, new ITreeAction()
            {
            	public void performAction(TreeMenuNode node)
            	{
            		RegaDBMain.getApp().getFormContainer().setForm(new SelectEventForm());
            	}
            });
            
            eventAdd = new ActionItem(WResource.tr("menu.event.add"), event, new ITreeAction()
            {
            	public void performAction(TreeMenuNode node)
            	{
            		TreeContent.this.eventSelected.setSelectedItem(null);
            		RegaDBMain.getApp().getFormContainer().setForm(new EventForm(InteractionState.Adding, WWidget.tr("menu.event.add"), null));
            	}
            });
            
            eventSelected = new EventSelectedItem(event);
            
    	        eventSelectedView = new ActionItem(WResource.tr("menu.event.selected.view"), eventSelected, new ITreeAction()
    	        {
    	        	public void performAction(TreeMenuNode node)
    	        	{
    	        		RegaDBMain.getApp().getFormContainer().setForm(new EventForm(InteractionState.Viewing, WWidget.tr("menu.event.selected.view"), eventSelected.getSelectedItem()));
    	        	}
    	        });
    	        
    	        eventSelectedEdit = new ActionItem(WResource.tr("menu.event.selected.edit"), eventSelected, new ITreeAction()
    	        {
    	        	public void performAction(TreeMenuNode node)
    	        	{
    	        		RegaDBMain.getApp().getFormContainer().setForm(new EventForm(InteractionState.Editing, WWidget.tr("menu.event.selected.edit"), eventSelected.getSelectedItem()));
    	        	}
    	        });
    	        
    	        eventSelectedDelete = new ActionItem(WResource.tr("menu.event.selected.delete"), eventSelected, new ITreeAction()
    	        {
    	        	public void performAction(TreeMenuNode node)
    	        	{
    	        		RegaDBMain.getApp().getFormContainer().setForm(new EventForm(InteractionState.Deleting, WWidget.tr("menu.event.selected.delete"), eventSelected.getSelectedItem()));
    	        	}
    	        });
    	        
    	        datasetSettings = new ActionItem(WResource.tr("menu.datasetSettings.datasetSettings"), administratorMain)
    	        {
    	             @Override
    	             public boolean isDisabled()
    	             {
    	                 return RegaDBMain.getApp().getLogin()==null;
    	             }
    	        }; 
    	        
    	        datasets = new ActionItem(WResource.tr("menu.datasetSettings.dataset"), datasetSettings); 
    	        datasetSelect  = new ActionItem(WResource.tr("menu.datasetSettings.dataset.select"), datasets, new ITreeAction()
    	        {
    	            public void performAction(TreeMenuNode node) 
    	            {
    	                RegaDBMain.getApp().getFormContainer().setForm(new SelectDatasetForm());
    	            }
    	        });
    	        datasetAdd = new ActionItem(WResource.tr("menu.datasetSettings.dataset.add"), datasets, new ITreeAction()
    	        {
    	             public void performAction(TreeMenuNode node)
    	             {
    	            	 TreeContent.this.datasetSelected.setSelectedItem(null);
    	                 RegaDBMain.getApp().getFormContainer().setForm(new DatasetForm(InteractionState.Adding, WWidget.tr("form.datasetSettings.dataset.add"),null));
    	             }
    	         });
    	        datasetSelected = new DatasetSelectedItem(datasets);
    	       datasetView = new ActionItem(WResource.tr("menu.datasetSettings.dataset.view"), datasetSelected, new ITreeAction()
    	        {
    	            public void performAction(TreeMenuNode node)
    	            {
    	                RegaDBMain.getApp().getFormContainer().setForm(new DatasetForm(InteractionState.Viewing, WWidget.tr("form.datasetSettings.dataset.view"),datasetSelected.getSelectedItem()));
    	            }
    	        });
    	        datasetEdit = new ActionItem(WResource.tr("menu.datasetSettings.dataset.edit"), datasetSelected, new ITreeAction()
    	        {
    	            public void performAction(TreeMenuNode node)
    	            {
    	                RegaDBMain.getApp().getFormContainer().setForm(new DatasetForm(InteractionState.Editing, WWidget.tr("form.datasetSettings.dataset.edit"),datasetSelected.getSelectedItem()));
    	            }
    	        })
    	        {
    	            @Override
    	            public boolean isDisabled()
    	            {
    	                return !setDatasetSensitivity();
    	            }
    	        };
    	        datasetDelete = new ActionItem(WResource.tr("menu.datasetSettings.dataset.delete"), datasetSelected, new ITreeAction()
    	        {
    	            public void performAction(TreeMenuNode node)
    	            {
    	                RegaDBMain.getApp().getFormContainer().setForm(new DatasetForm(InteractionState.Deleting, WWidget.tr("form.datasetSettings.dataset.delete"), datasetSelected.getSelectedItem()));
    	            }
    	        })
    	        {
    	            @Override
    	            public boolean isDisabled()
    	            {
    	                return !setDatasetSensitivity();
    	            }
    	        };
    	        datasetAccess = new ActionItem(WResource.tr("menu.dataset.access"), datasetSettings)
    	        {
    	            @Override
    	            public boolean isDisabled()
    	            {
    	                return RegaDBMain.getApp().getLogin()==null;
    	            }
    	        };
    	        datasetAccessSelect = new ActionItem(WResource.tr("menu.dataset.access.select"), datasetAccess, new ITreeAction()
    	        {
    	            public void performAction(TreeMenuNode node) 
    	            {
    	                RegaDBMain.getApp().getFormContainer().setForm(new SelectDatasetAccessUserForm());
    	            }
    	        });
    	        datasetAccessSelected = new DatasetAccessSelectedItem(datasetAccess);
    	        datasetAccessView = new ActionItem(WResource.tr("menu.dataset.access.view"), datasetAccessSelected, new ITreeAction()
    	        {
    	            public void performAction(TreeMenuNode node)
    	            {
    	                RegaDBMain.getApp().getFormContainer().setForm(new DatasetAccessForm(InteractionState.Viewing, WWidget.tr("form.dataset.access.view"),datasetAccessSelected.getSelectedItem()));
    	            }
    	        });
    	        datasetAccessEdit = new ActionItem(WResource.tr("menu.dataset.access.edit"), datasetAccessSelected, new ITreeAction()
    	        {
    	            public void performAction(TreeMenuNode node)
    	            {
    	                RegaDBMain.getApp().getFormContainer().setForm(new DatasetAccessForm(InteractionState.Editing, WWidget.tr("form.dataset.access.edit"),datasetAccessSelected.getSelectedItem()));
    	            }
    	        });



        usersSelect = new ActionItem(WResource.tr("menu.administrator.users"), administratorMain);

            usersSelect = new ActionItem(WResource.tr("menu.administrator.users.select"), usersSelect, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectSettingsUserForm());
                }
            });
            userSelected = new UserSelectedItem(usersSelect);
            usersView = new ActionItem(WResource.tr("menu.administrator.users.view"), userSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("form.administrator.user.view"), InteractionState.Viewing, usersSelect, userSelected, true, userSelected.getSelectedItem()));
                }
            });
            usersEdit = new ActionItem(WResource.tr("menu.administrator.users.edit"), userSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("form.administrator.user.edit"), InteractionState.Editing, usersSelect, userSelected, true, userSelected.getSelectedItem()));
                }
            });
            usersDelete = new ActionItem(WResource.tr("form.settings.user.delete"), userSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("form.administrator.user.delete"), InteractionState.Deleting, usersSelect, userSelected, true, userSelected.getSelectedItem()));
                }
            });
            usersChangePassword = new ActionItem(WResource.tr("form.settings.user.password"), userSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new PasswordForm(WWidget.tr("menu.myAccount.passwordForm"), InteractionState.Editing, usersView, userSelected, true, userSelected.getSelectedItem()));
                }
            });
            
            updateFromCentralServer = new ActionItem(WResource.tr("menu.administrator.updateFromCentralRepos"), administratorMain);
            updateFromCentralServerUpdateView = new ActionItem(WResource.tr("menu.administrator.updateFromCentralRepos.update.view"), updateFromCentralServer, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new UpdateForm(WWidget.tr("form.update_central_server.view"), InteractionState.Viewing));
                }
            });
            updateFromCentralServerUpdate = new ActionItem(WResource.tr("menu.administrator.updateFromCentralRepos.update"), updateFromCentralServer, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new UpdateForm(WWidget.tr("form.update_central_server"),InteractionState.Editing));
                }
            });
            
            importTool = new ActionItem(RootItem.tr("menu.importTool"), administratorMain)
            {
                @Override
                public boolean isDisabled()
                {
                    return RegaDBMain.getApp().getLogin()==null;
                }
            };
            
            importToolSelect = new ActionItem(WResource.tr("menu.importTool.select"), importTool, new ITreeAction()
            {
            	public void performAction(TreeMenuNode node)
            	{
            		RegaDBMain.getApp().getFormContainer().setForm(new SelectImportToolForm());
            	}
            });
            
            importToolAdd = new ActionItem(WResource.tr("menu.importTool.add"), importTool, new ITreeAction()
            {
            	public void performAction(TreeMenuNode node)
            	{
            		TreeContent.this.importToolSelected.setSelectedItem(null);
            		RegaDBMain.getApp().getFormContainer().setForm(new ImportToolForm(InteractionState.Adding, WWidget.tr("menu.importTool.add"), null));
            	}
            });
            
            importToolSelected = new ImportToolSelectedItem(importTool);
            
    	        importToolSelectedView = new ActionItem(WResource.tr("menu.importTool.selected.view"), importToolSelected, new ITreeAction()
    	        {
    	        	public void performAction(TreeMenuNode node)
    	        	{
    	        		RegaDBMain.getApp().getFormContainer().setForm(new ImportToolForm(InteractionState.Viewing, WWidget.tr("menu.importTool.selected.view"), importToolSelected.getSelectedItem()));
    	        	}
    	        });
    	        
    	        importToolSelectedEdit = new ActionItem(WResource.tr("menu.importTool.selected.edit"), importToolSelected, new ITreeAction()
    	        {
    	        	public void performAction(TreeMenuNode node)
    	        	{
    	        		RegaDBMain.getApp().getFormContainer().setForm(new ImportToolForm(InteractionState.Editing, WWidget.tr("menu.importTool.selected.edit"), importToolSelected.getSelectedItem()));
    	        	}
    	        });
    	        
    	        importToolSelectedDelete = new ActionItem(WResource.tr("menu.importTool.selected.delete"), importToolSelected, new ITreeAction()
    	        {
    	        	public void performAction(TreeMenuNode node)
    	        	{
    	        		RegaDBMain.getApp().getFormContainer().setForm(new ImportToolForm(InteractionState.Deleting, WWidget.tr("menu.importTool.selected.delete"), importToolSelected.getSelectedItem()));
    	        	}
    	        });
            
            importXML = new ActionItem(WResource.tr("menu.impex.import"), administratorMain);
            importXMLrun = new ActionItem(WResource.tr("menu.impex.import.run"), importXML, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new ImportFormRunning(WResource.tr("form.impex.import.title"), InteractionState.Viewing));
                }
            });
            importXMLadd = new ActionItem(WResource.tr("menu.impex.import.add"), importXML, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new ImportFormAdd(WResource.tr("form.impex.import.title"), InteractionState.Adding));
                }
            });
            
            exportXML = new ActionItem(WResource.tr("menu.impex.export"), administratorMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new ExportForm(WResource.tr("form.impex.export.title"), InteractionState.Viewing));
                }
            });
            
            batchTest = new ActionItem(WResource.tr("menu.batchtest"), administratorMain);
            
            batchTestRunning = new ActionItem(WResource.tr("menu.batchtest.running"), batchTest, new ITreeAction() {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new BatchTestRunningForm(WResource.tr("form.batchtest.title"), InteractionState.Viewing));
                }
            });

            batchTestAdd = new ActionItem(WResource.tr("menu.batchtest.add"), batchTest, new ITreeAction() {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new BatchTestAddForm(WResource.tr("form.batchtest.title"), InteractionState.Adding));
                }
            });
            
            
            log = new ActionItem(WResource.tr("menu.log"),administratorMain);
            logSelect = new ActionItem(WResource.tr("menu.log.select"), log, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectLogForm());
                }
            });
            logSelectedItem = new LogSelectedItem(log);
            logView = new ActionItem(WResource.tr("menu.log.view"), logSelectedItem, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new LogForm(WWidget.tr("form.log.view"), InteractionState.Viewing, logSelectedItem.getSelectedItem()));
                }
            });
            logDelete = new ActionItem(WResource.tr("menu.log.delete"), logSelectedItem, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new LogForm(WWidget.tr("form.log.view"), InteractionState.Deleting, logSelectedItem.getSelectedItem()));
                }
            });
            
            if(RegaDBSettings.getInstance().getContaminationConfig().isConfigured()){
	            new ActionItem(WString.tr("menu.contamination"),administratorMain, new ITreeAction()
	            {
	                public void performAction(TreeMenuNode node) 
	                {
	                    RegaDBMain.getApp().getFormContainer().setForm(new ContaminationOverview());
	                }
	            });
	            new ActionItem(WString.tr("menu.sample-distances"), administratorMain, new ITreeAction(){
	            	public void performAction(TreeMenuNode node)
	            	{
	            		RegaDBMain.getApp().getFormContainer().setForm(new SampleDistancesForm());
	            	}
	            });
            }
            
            version = new ActionItem(WResource.tr("menu.version"),administratorMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new VersionForm());
                }
            });
            
            
            rootItem.refresh();
            
		if(patientTreeNode.isEnabled())
		{
			return patientTreeNode;
		}
		else
		{
			return myAccountLogin;
		}
	}
    
    private boolean setDatasetSensitivity()
    {
        Login login = RegaDBMain.getApp().getLogin();
        if(login!=null)
        {
            Dataset ds = datasetSelected.getSelectedItem();
            if(ds!=null)
            {
                return ds.getSettingsUser().getUid().equals(login.getUid());
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
