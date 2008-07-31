package net.sf.regadb.ui.tree;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.datatable.attributeSettings.SelectAttributeForm;
import net.sf.regadb.ui.datatable.attributeSettings.SelectAttributeGroupForm;
import net.sf.regadb.ui.datatable.datasetSettings.SelectDatasetAccessUserForm;
import net.sf.regadb.ui.datatable.datasetSettings.SelectDatasetForm;
import net.sf.regadb.ui.datatable.log.SelectLogForm;
import net.sf.regadb.ui.datatable.measurement.SelectMeasurementForm;
import net.sf.regadb.ui.datatable.query.SelectQueryToolQueryForm;
import net.sf.regadb.ui.datatable.query.SelectQueryDefinitionForm;
import net.sf.regadb.ui.datatable.query.SelectQueryDefinitionRunForm;
import net.sf.regadb.ui.datatable.settingsUser.SelectSettingsUserForm;
import net.sf.regadb.ui.datatable.testSettings.SelectResRepTemplateForm;
import net.sf.regadb.ui.datatable.testSettings.SelectTestForm;
import net.sf.regadb.ui.datatable.testSettings.SelectTestTypeForm;
import net.sf.regadb.ui.datatable.therapy.SelectTherapyForm;
import net.sf.regadb.ui.datatable.viralisolate.SelectViralIsolateForm;
import net.sf.regadb.ui.form.administrator.UpdateForm;
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
import net.sf.regadb.ui.form.log.LogForm;
import net.sf.regadb.ui.form.log.LogSelectedItem;
import net.sf.regadb.ui.form.query.QueryDefinitionForm;
import net.sf.regadb.ui.form.query.QueryDefinitionRunForm;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.form.query.wiv.WivArcCd4Form;
import net.sf.regadb.ui.form.query.wiv.WivArcDeathsForm;
import net.sf.regadb.ui.form.query.wiv.WivArcLastContactForm;
import net.sf.regadb.ui.form.query.wiv.WivArcTherapyAtcForm;
import net.sf.regadb.ui.form.query.wiv.WivArlCd4Form;
import net.sf.regadb.ui.form.query.wiv.WivArlConfirmedHivForm;
import net.sf.regadb.ui.form.query.wiv.WivArlEpidemiologyForm;
import net.sf.regadb.ui.form.query.wiv.WivArlViralLoadForm;
import net.sf.regadb.ui.form.singlePatient.MeasurementForm;
import net.sf.regadb.ui.form.singlePatient.PatientEventForm;
import net.sf.regadb.ui.form.singlePatient.SinglePatientForm;
import net.sf.regadb.ui.form.singlePatient.TherapyForm;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateCumulatedResistance;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateForm;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateMutationEvolution;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateResistanceEvolutionForm;
import net.sf.regadb.ui.form.singlePatient.chart.PatientChartForm;
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
import net.sf.regadb.ui.tree.items.administrator.NotRegisteredUserSelectedItem;
import net.sf.regadb.ui.tree.items.administrator.RegisteredUserSelectedItem;
import net.sf.regadb.ui.tree.items.attributeSettings.AttributeGroupSelectedItem;
import net.sf.regadb.ui.tree.items.attributeSettings.AttributeSelectedItem;
import net.sf.regadb.ui.tree.items.custom.ContactItem;
import net.sf.regadb.ui.tree.items.datasetSettings.DatasetAccessSelectedItem;
import net.sf.regadb.ui.tree.items.datasetSettings.DatasetSelectedItem;
import net.sf.regadb.ui.tree.items.events.EventSelectedItem;
import net.sf.regadb.ui.tree.items.events.SelectEventForm;
import net.sf.regadb.ui.tree.items.myAccount.LoginItem;
import net.sf.regadb.ui.tree.items.myAccount.LogoutItem;
import net.sf.regadb.ui.tree.items.myAccount.MyAccountItem;
import net.sf.regadb.ui.tree.items.query.QueryDefinitionItem;
import net.sf.regadb.ui.tree.items.query.QueryDefinitionRunItem;
import net.sf.regadb.ui.tree.items.query.QueryDefinitionRunSelectedItem;
import net.sf.regadb.ui.tree.items.query.QueryDefinitionSelectedItem;
import net.sf.regadb.ui.tree.items.query.QueryItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import net.sf.regadb.ui.tree.items.singlePatient.MeasurementSelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientAddItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientEventAdd;
import net.sf.regadb.ui.tree.items.singlePatient.PatientEventSelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientSelectItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientSelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.SelectPatientEvent;
import net.sf.regadb.ui.tree.items.singlePatient.TherapySelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.ViralIsolateSelectedItem;
import net.sf.regadb.ui.tree.items.testSettings.ResRepTemplateSelectedItem;
import net.sf.regadb.ui.tree.items.testSettings.TestSelectedItem;
import net.sf.regadb.ui.tree.items.testSettings.TestTypeSelectedItem;
import net.sf.witty.wt.WResource;
import net.sf.witty.wt.WWidget;

public class TreeContent
{
    public PatientItem singlePatientMain;
    public PatientSelectItem patientSelect;
    public PatientAddItem patientAdd;
    public PatientSelectedItem patientSelected;
    public ActionItem viewPatient;
    public ActionItem editPatient;
    public ActionItem deletePatient;
    public ActionItem chart;
    public ActionItem measurements;
    public ActionItem measurementsSelect;
    public MeasurementSelectedItem measurementSelected;
    public ActionItem measurementView;
    public ActionItem measurementEdit;
    public ActionItem measurementsAdd;
    public ActionItem measurementsDelete;
    public ActionItem therapies;
    public ActionItem therapiesSelect;
    public ActionItem therapiesAdd;
    public ActionItem therapiesCopyLast;
    public ActionItem therapiesDelete;
    public TherapySelectedItem therapiesSelected;
    public ActionItem therapiesEdit;
    public ActionItem therapiesView;
    public ActionItem viralIsolates;
    public ActionItem viralIsolatesSelect;
    public ActionItem viralIsolatesAdd;
    public ActionItem viralIsolatesDelete;
    public ViralIsolateSelectedItem viralIsolateSelected;
    public ActionItem viralIsolateView;
    public ActionItem viralIsolateEdit;
    public ActionItem viralIsolateEvolution;
    public ActionItem viralIsolateMutationEvolution;
    public ActionItem viralIsolateResistanceEvolution;
    public ActionItem viralIsolateCumulatedResistance;
    
    public ActionItem custom;
    public ContactItem contact;
    
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
    public ActionItem enabledUsers;
    public ActionItem registeredUsersSelect;
    public RegisteredUserSelectedItem registeredUserSelected;
    public ActionItem registeredUserDelete;
    public ActionItem registeredUsersView;
    public ActionItem registeredUsersEdit;
    public ActionItem registeredUsersDelete;
    public ActionItem registeredUsersChangePassword;
    public ActionItem disabledUsers;
    public ActionItem notRegisteredUsersSelect;
    public NotRegisteredUserSelectedItem notRegisteredUserSelected;
    public ActionItem notRegisteredUsersView;
    public ActionItem notRegisteredUsersEdit;
    public ActionItem notRegisteredUsersDelete;
    public ActionItem notRegisteredUsersChangePassword;
    public ActionItem updateFromCentralServer;
    public ActionItem updateFromCentralServerUpdate;
    public ActionItem updateFromCentralServerUpdateView;
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
    
    public ActionItem patientEvent;
    public ActionItem patientEventSelect;
    public ActionItem patientEventAdd;
    public PatientEventSelectedItem patientEventSelected;
    public ActionItem patientEventView;
    public ActionItem patientEventEdit;
    public ActionItem patientEventDelete;
    
    public ActionItem log;
    public ActionItem logSelect;
    public ActionItem logView;
    public ActionItem logDelete;
    public LogSelectedItem logSelectedItem;
	public QueryDefinitionSelectedItem queryToolSelected;
	public ActionItem queryToolSelectedView;
	public ActionItem queryToolSelectedEdit;
	public ActionItem queryToolSelectedDelete;
    
	public TreeMenuNode setContent(RootItem rootItem)
	{
		singlePatientMain = new PatientItem(rootItem);
		    patientSelect = new PatientSelectItem(singlePatientMain);
			patientAdd = new PatientAddItem(singlePatientMain);
            patientSelected = new PatientSelectedItem(singlePatientMain);
                viewPatient = new ActionItem(WResource.tr("general.view"), patientSelected, new ITreeAction()
                {
                    public void performAction(TreeMenuNode node) 
                    {
                        RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Viewing, WWidget.tr("patient.form"), false, patientSelected.getSelectedItem()));
                    }
                });
                editPatient = new ActionItem(WResource.tr("general.edit"), patientSelected, new ITreeAction()
                {
                    public void performAction(TreeMenuNode node) 
                    {
                        RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Editing, WWidget.tr("patient.form"), false, patientSelected.getSelectedItem()));
                    }
                });
                deletePatient = new ActionItem(WResource.tr("general.delete"), patientSelected, new ITreeAction()
                {
                    public void performAction(TreeMenuNode node) 
                    {
                        RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Deleting, WWidget.tr("patient.form"), false, patientSelected.getSelectedItem()));
                    }
                });
				chart = new ActionItem(WResource.tr("chart.chart"), patientSelected, new ITreeAction()
                {
                    public void performAction(TreeMenuNode node) 
                    {
                        RegaDBMain.getApp().getFormContainer().setForm(new PatientChartForm(patientSelected.getSelectedItem()));
                    }
                });
    			measurements = new ActionItem(WResource.tr("measurement.plural"), patientSelected);
    				measurementsSelect = new ActionItem(WResource.tr("general.select"), measurements, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node) 
                        {
                        	RegaDBMain.getApp().getFormContainer().setForm(new SelectMeasurementForm());
                        }
                    });
    				measurementsAdd = new ActionItem(WResource.tr("general.add"), measurements, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getTree().getTreeContent().measurementSelected.setSelectedItem(null);
							RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Adding, WWidget.tr("measurement.form"), false, null));
						}
    				});
    				measurementSelected = new MeasurementSelectedItem(measurements);
    				measurementView = new ActionItem(WResource.tr("general.view"), measurementSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Viewing, WWidget.tr("measurement.form"), false, measurementSelected.getSelectedItem()));
						}
    				});
    				measurementEdit = new ActionItem(WResource.tr("general.edit"), measurementSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Editing, WWidget.tr("measurement.form"), false, measurementSelected.getSelectedItem()));
						}
    				});
                    measurementsDelete = new ActionItem(WResource.tr("general.delete"), measurementSelected, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                            RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Deleting, WWidget.tr("measurement.form"), false, measurementSelected.getSelectedItem()));
                        }
                    });

    			therapies = new ActionItem(WResource.tr("therapy.plural"), patientSelected);
    				therapiesSelect = new ActionItem(WResource.tr("general.select"), therapies, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node) 
                        {
                        	RegaDBMain.getApp().getFormContainer().setForm(new SelectTherapyForm());
                        }
                    });
    				therapiesAdd = new ActionItem(WResource.tr("general.add"), therapies, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getTree().getTreeContent().therapiesSelected.setSelectedItem(null);
							RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Adding, WWidget.tr("therapy.form"), false, null));
						}
    				});
                    therapiesCopyLast = new ActionItem(WResource.tr("menu.therapy.copyLast"), therapies, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                            Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
                            Therapy lastTherapy = null;
                            for(Therapy therapy : p.getTherapies()){
                                if(lastTherapy == null || lastTherapy.getStartDate().before(therapy.getStartDate()))
                                    lastTherapy = therapy;
                            }
                            RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Adding, WWidget.tr("therapy.form"), false, lastTherapy));
                        }
                    });

    				therapiesSelected = new TherapySelectedItem(therapies);
    				therapiesView = new ActionItem(WResource.tr("general.view"), therapiesSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Viewing, WWidget.tr("therapy.form"), false, therapiesSelected.getSelectedItem()));
						}
    				});
    				therapiesEdit = new ActionItem(WResource.tr("general.edit"), therapiesSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Editing, WWidget.tr("therapy.form"), false, therapiesSelected.getSelectedItem()));
						}
    				});
                    therapiesDelete = new ActionItem(WResource.tr("general.delete"), therapiesSelected, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                            RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Deleting, WWidget.tr("therapy.form"), false, therapiesSelected.getSelectedItem()));
                        }
                    });
    				
    				viralIsolates = new ActionItem(WResource.tr("viralIsolate.plural"), patientSelected);
    				viralIsolatesSelect = new ActionItem(WResource.tr("general.select"), viralIsolates, new ITreeAction()
                    {
    					public void performAction(TreeMenuNode node) 
                        {
                        	RegaDBMain.getApp().getFormContainer().setForm(new SelectViralIsolateForm());
                        }
                    });
                    viralIsolatesAdd = new ActionItem(WResource.tr("general.add"), viralIsolates, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                        	RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.setSelectedItem(null);
                            RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Adding, WWidget.tr("viralIsolate.form"), false, null));
                        }
                    });
    				viralIsolateSelected = new ViralIsolateSelectedItem(viralIsolates);
    				viralIsolateView = new ActionItem(WResource.tr("general.view"), viralIsolateSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Viewing, WWidget.tr("viralIsolate.form"), false, viralIsolateSelected.getSelectedItem()));
						}
    				});
                    viralIsolateEdit = new ActionItem(WResource.tr("general.edit"), viralIsolateSelected, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                            RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Editing, WWidget.tr("viralIsolate.form"), false, viralIsolateSelected.getSelectedItem()));
                        }
                    });
                    viralIsolatesDelete = new ActionItem(WResource.tr("general.delete"), viralIsolateSelected, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                            RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Deleting, WWidget.tr("viralIsolate.form"), false, viralIsolateSelected.getSelectedItem()));
                        }
                    });
                    viralIsolateEvolution = new ActionItem(RootItem.tr("menu.patient.evolution"), viralIsolates) {
						@Override
						public boolean isEnabled() {
							if(!super.isEnabled()) {
								return false;
							} else {
								return RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem().getViralIsolates().size()>1;
							}
						}
                    };
                    viralIsolateMutationEvolution = new ActionItem(WResource.tr("viralIsolate.resistance.evolution"), viralIsolateEvolution, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                            RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateMutationEvolution(WWidget.tr("viralIsolate.mutation.evolution"), true,
                                    RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem()));
                        }
                    });
                    viralIsolateResistanceEvolution = new ActionItem(WResource.tr("viralIsolate.resistance.evolution"), viralIsolateEvolution, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                            RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateResistanceEvolutionForm(WWidget.tr("viralIsolate.resistance.evolution"), true,
                                    RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem()));
                        }
                    });
                    viralIsolateCumulatedResistance = new ActionItem(WResource.tr("viralIsolate.resistance.cumulated"), viralIsolates, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                            RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateCumulatedResistance(WWidget.tr("viralIsolate.resistance.cumulated"),
                                    RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem(), true));
                        }
                    }){
                        @Override
                        public boolean isEnabled() {
							if(!super.isEnabled()) {
								return false;
							} else {
								return RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem().getViralIsolates().size()>1;
							}
						}
                    };
    		
                    patientEvent = new ActionItem(WResource.tr("event.plural"), patientSelected);
                    
    	                patientEventSelect = new ActionItem(WResource.tr("general.select"), patientEvent, new ITreeAction()
    			        {
    			        	public void performAction(TreeMenuNode node)
    			        	{
    			        		RegaDBMain.getApp().getFormContainer().setForm(new SelectPatientEvent());
    			        	}
    			        });
    	                
    	                PatientEventAdd patientEventAdd2 = new PatientEventAdd(patientEvent);
    	                
    	                patientEventSelected = new PatientEventSelectedItem(patientEvent);
    	                
    		                patientEventView = new ActionItem(WResource.tr("general.view"), patientEventSelected, new ITreeAction()
    		                {
    							public void performAction(TreeMenuNode node) {
    								RegaDBMain.getApp().getFormContainer().setForm(new PatientEventForm(InteractionState.Viewing, WWidget.tr("event.form"), false, patientEventSelected.getSelectedItem()));
    							}
    		                });
    		                
    		                patientEventEdit = new ActionItem(WResource.tr("general.edit"), patientEventSelected, new ITreeAction()
    		                {
    							public void performAction(TreeMenuNode node) {
    								RegaDBMain.getApp().getFormContainer().setForm(new PatientEventForm(InteractionState.Editing, WWidget.tr("event.form"), false, patientEventSelected.getSelectedItem()));
    							}
    		                });
    		                
    		                patientEventDelete = new ActionItem(WResource.tr("general.delete"), patientEventSelected, new ITreeAction()
    		                {
    							public void performAction(TreeMenuNode node) {
    								RegaDBMain.getApp().getFormContainer().setForm(new PatientEventForm(InteractionState.Deleting, WWidget.tr("event.form"), false, patientEventSelected.getSelectedItem()));
    							}
    		                });
    		                
    		                custom = new ActionItem(WResource.tr("menu.custom"), patientSelected);
    		                contact = new ContactItem(custom);
    		                
       attributesSettings = new ActionItem(WResource.tr("menu.attributeSettings"), rootItem)
       {
            @Override
            public boolean isEnabled()
            {
                return RegaDBMain.getApp().getLogin()!=null;
            }
       };
           attributes = new ActionItem(WResource.tr("attribute.plural"), attributesSettings);
           attributesSelect  = new ActionItem(WResource.tr("general.select"), attributes, new ITreeAction()
           {
               public void performAction(TreeMenuNode node) 
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new SelectAttributeForm());
               }
           });
           attributesAdd = new ActionItem(WResource.tr("general.add"), attributes, new ITreeAction()
           {
                public void performAction(TreeMenuNode node)
                {
                	RegaDBMain.getApp().getTree().getTreeContent().attributesSelected.setSelectedItem(null);
                    RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Adding, WWidget.tr("attribute.form"), false, null));
                }
            });
           attributesSelected = new AttributeSelectedItem(attributes);
           attributesView = new ActionItem(WResource.tr("general.view"), attributesSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Viewing, WWidget.tr("attribute.form"), false, attributesSelected.getSelectedItem()));
               }
           });
           attributesEdit = new ActionItem(WResource.tr("general.edit"), attributesSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Editing, WWidget.tr("attribute.form"), false, attributesSelected.getSelectedItem()));
               }
           });
           attributesDelete = new ActionItem(WResource.tr("general.delete"), attributesSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Deleting, WWidget.tr("attribute.form"), false, attributesSelected.getSelectedItem()));
               }
           });
           
           attributeGroups = new ActionItem(WResource.tr("attributeGroups.plural"), attributesSettings);
           attributeGroupsSelect  = new ActionItem(WResource.tr("general.select"), attributeGroups, new ITreeAction()
           {
               public void performAction(TreeMenuNode node) 
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new SelectAttributeGroupForm());
               }
           });
           attributeGroupsAdd = new ActionItem(WResource.tr("general.add"), attributeGroups, new ITreeAction()
           {
                public void performAction(TreeMenuNode node)
                {
                	RegaDBMain.getApp().getTree().getTreeContent().attributeGroupsSelected.setSelectedItem(null);
                    RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Adding, WWidget.tr("attributeGroups.form"), false, null));
                }
            });
           attributeGroupsSelected = new AttributeGroupSelectedItem(attributeGroups);
           attributeGroupsView = new ActionItem(WResource.tr("general.view"), attributeGroupsSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Viewing, WWidget.tr("attributeGroups.form"),false, attributeGroupsSelected.getSelectedItem()));
               }
           });
           attributeGroupsEdit = new ActionItem(WResource.tr("general.edit"), attributeGroupsSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Editing, WWidget.tr("attributeGroups.form"),false, attributeGroupsSelected.getSelectedItem()));
               }
           });
           attributeGroupsDelete = new ActionItem(WResource.tr("general.delete"), attributeGroupsSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Deleting, WWidget.tr("attributeGroups.form"),false, attributeGroupsSelected.getSelectedItem()));
               }
           });
           
          testSettings = new ActionItem(WResource.tr("menu.testSettings"), rootItem)
           {
                @Override
                public boolean isEnabled()
                {
                    return RegaDBMain.getApp().getLogin()!=null;
                }
           };
          
          testTypes = new ActionItem(WResource.tr("testType.plural"), testSettings);
          testTypesSelect  = new ActionItem(WResource.tr("general.select"), testTypes, new ITreeAction()
           {
               public void performAction(TreeMenuNode node) 
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new SelectTestTypeForm());
               }
           });
           testTypesAdd = new ActionItem(WResource.tr("general.add"), testTypes, new ITreeAction()
           {
                public void performAction(TreeMenuNode node)
                {
                	RegaDBMain.getApp().getTree().getTreeContent().testTypeSelected.setSelectedItem(null);
                    RegaDBMain.getApp().getFormContainer().setForm(new TestTypeForm(InteractionState.Adding, WWidget.tr("testType.form"),false,null));
                }
            });
           testTypeSelected = new TestTypeSelectedItem(testTypes);
           testTypesView = new ActionItem(WResource.tr("general.view"), testTypeSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new TestTypeForm(InteractionState.Viewing, WWidget.tr("testType.form"),false,testTypeSelected.getSelectedItem()));
               }
           });
           testTypesEdit = new ActionItem(WResource.tr("general.edit"), testTypeSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new TestTypeForm(InteractionState.Editing, WWidget.tr("testType.form"),false,testTypeSelected.getSelectedItem()));
               }
           });
           testTypesDelete = new ActionItem(WResource.tr("general.delete"), testTypeSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new TestTypeForm(InteractionState.Deleting, WWidget.tr("testType.form"),false,testTypeSelected.getSelectedItem()));
               }
           });
           
           //test
           test = new ActionItem(WResource.tr("test.plural"), testSettings);        
           testSelect  = new ActionItem(WResource.tr("general.select"), test, new ITreeAction()
           {
               public void performAction(TreeMenuNode node) 
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new SelectTestForm());
               }
           });
           testAdd = new ActionItem(WResource.tr("general.add"), test, new ITreeAction()
           {
                public void performAction(TreeMenuNode node)
                {
                	RegaDBMain.getApp().getTree().getTreeContent().testSelected.setSelectedItem(null);
                    RegaDBMain.getApp().getFormContainer().setForm(new TestForm(InteractionState.Adding, WWidget.tr("test.form"),false,null));
                }
            });
           testSelected = new TestSelectedItem(test);
           testView = new ActionItem(WResource.tr("general.view"), testSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new TestForm(InteractionState.Viewing, WWidget.tr("test.form"),false,testSelected.getSelectedItem()));
               }
           });
           testEdit = new ActionItem(WResource.tr("general.edit"), testSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new TestForm(InteractionState.Editing, WWidget.tr("test.form"),false,testSelected.getSelectedItem()));
               }
           });
           testDelete = new ActionItem(WResource.tr("general.delete"), testSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new TestForm(InteractionState.Deleting, WWidget.tr("test.form"),false,testSelected.getSelectedItem()));
               }
           });
           
           //resistance report template
           resRepTemplate = new ActionItem(WResource.tr("report.plural"), testSettings);        
           resRepTemplateSelect  = new ActionItem(WResource.tr("general.select"), resRepTemplate, new ITreeAction()
           {
               public void performAction(TreeMenuNode node) 
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new SelectResRepTemplateForm());
               }
           });
           resRepTemplateAdd = new ActionItem(WResource.tr("general.add"), resRepTemplate, new ITreeAction()
           {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new ResistanceInterpretationTemplateForm(InteractionState.Adding, WWidget.tr("report.form"), false, null));
                    
                }
            });
           resRepTemplateSelected = new ResRepTemplateSelectedItem(resRepTemplate);
           resRepTemplateView = new ActionItem(WResource.tr("general.view"), resRepTemplateSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new ResistanceInterpretationTemplateForm(InteractionState.Viewing, WWidget.tr("report.form"), false, resRepTemplateSelected.getSelectedItem()));
               }
           });
           resRepTemplateEdit = new ActionItem(WResource.tr("general.edit"), resRepTemplateSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new ResistanceInterpretationTemplateForm(InteractionState.Editing, WWidget.tr("report.form"), false, resRepTemplateSelected.getSelectedItem()));
               }
           });
           resRepTemplateDelete = new ActionItem(WResource.tr("general.delete"), resRepTemplateSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new ResistanceInterpretationTemplateForm(InteractionState.Deleting, WWidget.tr("report.form"), false, resRepTemplateSelected.getSelectedItem()));
               }
           });
        
        
        //DatasetSettings
        datasetSettings = new ActionItem(WResource.tr("menu.datasetSettings"), rootItem)
        {
             @Override
             public boolean isEnabled()
             {
                 return RegaDBMain.getApp().getLogin()!=null;
             }
        }; 
        
        datasets = new ActionItem(WResource.tr("dataset.plural"), datasetSettings); 
        datasetSelect  = new ActionItem(WResource.tr("general.select"), datasets, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                RegaDBMain.getApp().getFormContainer().setForm(new SelectDatasetForm());
            }
        });
        datasetAdd = new ActionItem(WResource.tr("general.add"), datasets, new ITreeAction()
        {
             public void performAction(TreeMenuNode node)
             {
            	 RegaDBMain.getApp().getTree().getTreeContent().datasetSelected.setSelectedItem(null);
                 RegaDBMain.getApp().getFormContainer().setForm(new DatasetForm(InteractionState.Adding, WWidget.tr("dataset.form"), false,null));
             }
         });
        datasetSelected = new DatasetSelectedItem(datasets);
       datasetView = new ActionItem(WResource.tr("general.view"), datasetSelected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new DatasetForm(InteractionState.Viewing, WWidget.tr("dataset.form"),false,datasetSelected.getSelectedItem()));
            }
        });
        datasetEdit = new ActionItem(WResource.tr("general.edit"), datasetSelected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new DatasetForm(InteractionState.Editing, WWidget.tr("dataset.form"),false,datasetSelected.getSelectedItem()));
            }
        })
        {
            @Override
            public boolean isEnabled()
            {
                return setDatasetSensitivity();
            }
        };
        datasetDelete = new ActionItem(WResource.tr("general.delete"), datasetSelected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new DatasetForm(InteractionState.Deleting, WWidget.tr("dataset.form"), false, datasetSelected.getSelectedItem()));
            }
        })
        {
            @Override
            public boolean isEnabled()
            {
                return setDatasetSensitivity();
            }
        };
        datasetAccess = new ActionItem(WResource.tr("datasetaccess.form"), datasetSettings)
        {
            @Override
            public boolean isEnabled()
            {
                return RegaDBMain.getApp().getLogin()!=null;
            }
        };
        datasetAccessSelect = new ActionItem(WResource.tr("general.select"), datasetAccess, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                RegaDBMain.getApp().getFormContainer().setForm(new SelectDatasetAccessUserForm());
            }
        });
        datasetAccessSelected = new DatasetAccessSelectedItem(datasetAccess);
        datasetAccessView = new ActionItem(WResource.tr("general.view"), datasetAccessSelected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new DatasetAccessForm(InteractionState.Viewing, WWidget.tr("datasetaccess.form"), false, datasetAccessSelected.getSelectedItem()));
            }
        });
        datasetAccessEdit = new ActionItem(WResource.tr("general.edit"), datasetAccessSelected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new DatasetAccessForm(InteractionState.Editing, WWidget.tr("datasetaccess.form"),false, datasetAccessSelected.getSelectedItem()));
            }
        });
        
        event = new ActionItem(RootItem.tr("menu.event"), rootItem)
    	// Ticket #62
        {
            @Override
            public boolean isEnabled()
            {
                return RegaDBMain.getApp().getLogin()!=null;
            }
        };
        
        eventSelect = new ActionItem(WResource.tr("general.select"), event, new ITreeAction()
        {
        	public void performAction(TreeMenuNode node)
        	{
        		RegaDBMain.getApp().getFormContainer().setForm(new SelectEventForm());
        	}
        });
        
        eventAdd = new ActionItem(WResource.tr("general.add"), event, new ITreeAction()
        {
        	public void performAction(TreeMenuNode node)
        	{
        		RegaDBMain.getApp().getTree().getTreeContent().eventSelected.setSelectedItem(null);
        		RegaDBMain.getApp().getFormContainer().setForm(new EventForm(InteractionState.Adding, WWidget.tr("event.form"), false, null));
        	}
        });
        
        eventSelected = new EventSelectedItem(event);
        
	        eventSelectedView = new ActionItem(WResource.tr("general.view"), eventSelected, new ITreeAction()
	        {
	        	public void performAction(TreeMenuNode node)
	        	{
	        		RegaDBMain.getApp().getFormContainer().setForm(new EventForm(InteractionState.Viewing, WWidget.tr("event.form"), false, eventSelected.getSelectedItem()));
	        	}
	        });
	        
	        eventSelectedEdit = new ActionItem(WResource.tr("general.edit"), eventSelected, new ITreeAction()
	        {
	        	public void performAction(TreeMenuNode node)
	        	{
	        		RegaDBMain.getApp().getFormContainer().setForm(new EventForm(InteractionState.Editing, WWidget.tr("event.form"), false, eventSelected.getSelectedItem()));
	        	}
	        });
	        
	        eventSelectedDelete = new ActionItem(WResource.tr("general.delete"), eventSelected, new ITreeAction()
	        {
	        	public void performAction(TreeMenuNode node)
	        	{
	        		RegaDBMain.getApp().getFormContainer().setForm(new EventForm(InteractionState.Deleting, WWidget.tr("event.form"), false, eventSelected.getSelectedItem()));
	        	}
	        });
        
        queryMain = new QueryItem(rootItem);
        
        queryWiv = new QueryItem(WResource.tr("query.wiv"),queryMain);

            queryWivArlConfirmedHiv = new ActionItem(WResource.tr("query.wiv.arl.confirmedHiv"), queryWiv, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new WivArlConfirmedHivForm());
                }
            });
            
            queryWivArlEpidemiology = new ActionItem(WResource.tr("query.wiv.arl.epidemiology"), queryWiv, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new WivArlEpidemiologyForm());
                }
            });
            queryWivArlViralLoad = new ActionItem(WResource.tr("query.wiv.arl.viralLoad"), queryWiv, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new WivArlViralLoadForm());
                }
            });
            queryWivArlCd4 = new ActionItem(WResource.tr("query.wiv.arl.cd4"), queryWiv, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new WivArlCd4Form());
                }
            });
            queryWivArcCd4 = new ActionItem(WResource.tr("query.wiv.arc.cd4"), queryWiv, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new WivArcCd4Form());
                }
            });
            queryWivArcTherapyAtc = new ActionItem(WResource.tr("query.wiv.arc.therapyAtc"), queryWiv, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new WivArcTherapyAtcForm());
                }
            });
            queryWivArcLastContact = new ActionItem(WResource.tr("query.wiv.arc.lastContact"), queryWiv, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new WivArcLastContactForm());
                }
            });
            queryWivArcDeaths = new ActionItem(WResource.tr("query.wiv.arc.deaths"), queryWiv, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new WivArcDeathsForm());
                }
            });
            
            queryToolMain = new QueryItem(WResource.tr("query.querytool"), queryMain);

          queryToolSelect = new ActionItem(WResource.tr("general.select"), queryToolMain, new ITreeAction() {
  			public void performAction(TreeMenuNode node) {
  				RegaDBMain.getApp().getFormContainer().setForm(new SelectQueryToolQueryForm());
  			}
          });
          
            queryToolAdd = new ActionItem(WResource.tr("general.add"), queryToolMain, new ITreeAction() {
                public void performAction(TreeMenuNode node) {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryToolForm(WResource.tr("query.form"), InteractionState.Adding , false));
                }
            }); 
            
            queryToolSelected = new QueryDefinitionSelectedItem("query.form", queryToolMain);
            queryToolSelectedView = new ActionItem(WResource.tr("general.view"), queryToolSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryToolForm(WWidget.tr("query.form"), InteractionState.Viewing, false, queryToolSelected.getSelectedItem()));
                }
            });
            queryToolSelectedEdit = new ActionItem(WResource.tr("general.edit"), queryToolSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                	RegaDBMain.getApp().getFormContainer().setForm(new QueryToolForm(WWidget.tr("query.form"), InteractionState.Editing, false, queryToolSelected.getSelectedItem()));
                }
            })
            {
                @Override
                public boolean isEnabled()
                {
                	if((RegaDBMain.getApp().getLogin() != null) && (queryToolSelected.getSelectedItem() != null))
                	{
                		return ((RegaDBMain.getApp().getLogin().getUid()).equals(queryToolSelected.getQueryDefinitionCreator(queryToolSelected.getSelectedItem())));
                	}
                	else
                	{
                		return false;
                	}
                	
                }
            };
            queryToolSelectedDelete = new ActionItem(WResource.tr("general.delete"), queryToolSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new QueryToolForm(WWidget.tr("query.form"), InteractionState.Deleting, false, queryToolSelected.getSelectedItem()));
                }
            })
            {
                @Override
                public boolean isEnabled()
                {
                	if((RegaDBMain.getApp().getLogin() != null) && (queryToolSelected.getSelectedItem() != null))
                	{
                		return ((RegaDBMain.getApp().getLogin().getUid()).equals(queryToolSelected.getQueryDefinitionCreator(queryToolSelected.getSelectedItem())));
                	}
                	else
                	{
                		return false;
                	}
                }
            };

        queryDefinitionMain = new QueryDefinitionItem(queryMain);
        queryDefinitionSelect = new ActionItem(WResource.tr("general.select"), queryDefinitionMain, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                RegaDBMain.getApp().getFormContainer().setForm(new SelectQueryDefinitionForm());
            }
        });
        queryDefinitionAdd = new ActionItem(WResource.tr("general.add"), queryDefinitionMain, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionForm(WWidget.tr("query.definition.form"), InteractionState.Adding, false, new QueryDefinition(StandardObjects.getHqlQueryQueryType())));
            }
        });
        queryDefinitionSelected = new QueryDefinitionSelectedItem("query.definition.form", queryDefinitionMain);
        queryDefinitionSelectedView = new ActionItem(WResource.tr("general.view"), queryDefinitionSelected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionForm(WWidget.tr("query.definition.form"), InteractionState.Viewing, false, queryDefinitionSelected.getSelectedItem()));
            }
        });
        queryDefinitionSelectedEdit = new ActionItem(WResource.tr("general.edit"), queryDefinitionSelected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
            	RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionForm(WWidget.tr("query.definition.form"), InteractionState.Editing, false, queryDefinitionSelected.getSelectedItem()));
            }
        })
        {
            @Override
            public boolean isEnabled()
            {
            	if((RegaDBMain.getApp().getLogin() != null) && (queryDefinitionSelected.getSelectedItem() != null))
            	{
            		return ((RegaDBMain.getApp().getLogin().getUid()).equals(queryDefinitionSelected.getQueryDefinitionCreator(queryDefinitionSelected.getSelectedItem())));
            	}
            	else
            	{
            		return false;
            	}
            	
            }
        };
        queryDefinitionSelectedDelete = new ActionItem(WResource.tr("general.delete"), queryDefinitionSelected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionForm(WWidget.tr("query.definition.form"), InteractionState.Deleting, false, queryDefinitionSelected.getSelectedItem()));
            }
        })
        {
            @Override
            public boolean isEnabled()
            {
            	if((RegaDBMain.getApp().getLogin() != null) && (queryDefinitionSelected.getSelectedItem() != null))
            	{
            		return ((RegaDBMain.getApp().getLogin().getUid()).equals(queryDefinitionSelected.getQueryDefinitionCreator(queryDefinitionSelected.getSelectedItem())));
            	}
            	else
            	{
            		return false;
            	}
            }
        };
        queryDefinitionSelectedRun = new ActionItem(WResource.tr("import.run"), queryDefinitionSelected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionRunForm(WWidget.tr("query.definition.run.form"), InteractionState.Adding, false, new QueryDefinitionRun()));
            }
        });
        queryDefinitionRunMain = new QueryDefinitionRunItem(queryMain);
        queryDefinitionRunSelect = new ActionItem(WResource.tr("general.select"), queryDefinitionRunMain, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                RegaDBMain.getApp().getFormContainer().setForm(new SelectQueryDefinitionRunForm());
            }
        });
        queryDefinitionRunSelected = new QueryDefinitionRunSelectedItem(queryDefinitionRunMain);
        queryDefinitionRunSelectedView = new ActionItem(WResource.tr("general.view"), queryDefinitionRunSelected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionRunForm(WWidget.tr("query.definition.run.form"), InteractionState.Viewing, false, queryDefinitionRunSelected.getSelectedItem()));
            }
        });
        queryDefinitionRunSelectedDelete = new ActionItem(WResource.tr("general.delete"), queryDefinitionRunSelected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node)
            {
                RegaDBMain.getApp().getFormContainer().setForm(new QueryDefinitionRunForm(WWidget.tr("query.definition.run.form"), InteractionState.Deleting, false, queryDefinitionRunSelected.getSelectedItem()));
            }
        });
           
        myAccountMain = new MyAccountItem(rootItem);
            myAccountLogin = new LoginItem(myAccountMain);
            myAccountCreate = new ActionItem(WResource.tr("account.create"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("account.form"), InteractionState.Adding, myAccountLogin, myAccountMain, false, new SettingsUser(), false));
                }
            })
            {
                @Override
                public boolean isEnabled()
                {
                    return RegaDBMain.getApp().getLogin()==null;
                }
            };
            myAccountView = new ActionItem(WResource.tr("account.view"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("account.form"), InteractionState.Viewing, myAccountView, myAccountMain, false, null, false));
                }
            })
            {
                @Override
                public boolean isEnabled()
                {
                    return RegaDBMain.getApp().getLogin()!=null;
                }
            };
            myAccountEdit = new ActionItem(WResource.tr("account.edit"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("account.form"), InteractionState.Editing, myAccountView, myAccountMain, false, null, false));
                }
            })
            {
                @Override
                public boolean isEnabled()
                {
                    return RegaDBMain.getApp().getLogin()!=null;
                }
            };
            myAccountEditPassword = new ActionItem(WResource.tr("account.password.change"), myAccountMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new PasswordForm(WWidget.tr("account.password.change"), InteractionState.Editing, true, myAccountView, myAccountMain, false, null));
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
            enabledUsers = new ActionItem(WResource.tr("account.select.registered.plural"), administratorMain);
            registeredUsersSelect = new ActionItem(WResource.tr("general.select"), enabledUsers, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectSettingsUserForm(true));
                }
            });
            registeredUserSelected = new RegisteredUserSelectedItem(enabledUsers);
            registeredUsersView = new ActionItem(WResource.tr("general.view"), registeredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("account.form"), InteractionState.Viewing, registeredUsersSelect, registeredUserSelected, true, registeredUserSelected.getSelectedItem(), false));
                }
            });
            registeredUsersEdit = new ActionItem(WResource.tr("general.edit"), registeredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("account.form"), InteractionState.Editing, registeredUsersSelect, registeredUserSelected, true, registeredUserSelected.getSelectedItem(), false));
                }
            });
            registeredUsersDelete = new ActionItem(WResource.tr("general.delete"), registeredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("account.form"), InteractionState.Deleting, registeredUsersSelect, registeredUserSelected, true, registeredUserSelected.getSelectedItem(), false));
                }
            });
            registeredUsersChangePassword = new ActionItem(WResource.tr("account.password.change"), registeredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new PasswordForm(WWidget.tr("account.password.change"), InteractionState.Editing, true, registeredUsersView, registeredUserSelected, true, registeredUserSelected.getSelectedItem()));
                }
            });
            
            disabledUsers = new ActionItem(WResource.tr("account.select.unregistered.plural"), administratorMain);
            notRegisteredUsersSelect = new ActionItem(WResource.tr("general.select"), disabledUsers, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectSettingsUserForm(false));
                }
            });
            notRegisteredUserSelected = new NotRegisteredUserSelectedItem(disabledUsers);
            notRegisteredUsersView = new ActionItem(WResource.tr("general.view"), notRegisteredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("account.form"), InteractionState.Viewing, notRegisteredUsersSelect, notRegisteredUserSelected, true, notRegisteredUserSelected.getSelectedItem(), false));
                }
            });
            notRegisteredUsersEdit = new ActionItem(WResource.tr("general.edit"), notRegisteredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("account.form"), InteractionState.Editing, notRegisteredUsersSelect, notRegisteredUserSelected, true, notRegisteredUserSelected.getSelectedItem(), false));
                }
            });
            notRegisteredUsersDelete = new ActionItem(WResource.tr("general.delete"), notRegisteredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new AccountForm(WWidget.tr("account.form"), InteractionState.Deleting, notRegisteredUsersSelect, notRegisteredUserSelected, true, notRegisteredUserSelected.getSelectedItem(), false));
                }
            });
            notRegisteredUsersChangePassword = new ActionItem(WResource.tr("account.password.change"), notRegisteredUserSelected, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new PasswordForm(WWidget.tr("account.password.change"), InteractionState.Editing, true, notRegisteredUsersView, notRegisteredUserSelected, true, notRegisteredUserSelected.getSelectedItem()));
                }
            });
            
            updateFromCentralServer = new ActionItem(WResource.tr("update.form"), administratorMain);
            updateFromCentralServerUpdateView = new ActionItem(WResource.tr("general.view"), updateFromCentralServer, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new UpdateForm(WWidget.tr("update.installed"), InteractionState.Viewing, true));
                }
            });
            updateFromCentralServerUpdate = new ActionItem(WResource.tr("update.update"), updateFromCentralServer, new ITreeAction()
            {
                public void performAction(TreeMenuNode node)
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new UpdateForm(WWidget.tr("update.form"),InteractionState.Editing, true));
                }
            });
            
            importXML = new ActionItem(WResource.tr("import.form"), administratorMain);
            importXMLrun = new ActionItem(WResource.tr("import.running"), importXML, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new ImportFormRunning(WResource.tr("import.form"), InteractionState.Viewing, true));
                }
            });
            importXMLadd = new ActionItem(WResource.tr("general.add"), importXML, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new ImportFormAdd(WResource.tr("import.form"), InteractionState.Adding, true));
                }
            });
            
            exportXML = new ActionItem(WResource.tr("export.form"), administratorMain, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new ExportForm(WResource.tr("export.form"), InteractionState.Viewing, true));
                }
            });
            
            batchTest = new ActionItem(WResource.tr("batch.form"), administratorMain);
            
            batchTestRunning = new ActionItem(WResource.tr("import.running"), batchTest, new ITreeAction() {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new BatchTestRunningForm(WResource.tr("batch.form"), InteractionState.Viewing, true));
                }
            });

            batchTestAdd = new ActionItem(WResource.tr("general.add"), batchTest, new ITreeAction() {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new BatchTestAddForm(WResource.tr("batch.form"), InteractionState.Adding, true));
                }
            });
            
            
            log = new ActionItem(WResource.tr("log.plural"),administratorMain);
            logSelect = new ActionItem(WResource.tr("general.select"), log, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new SelectLogForm());
                }
            });
            logSelectedItem = new LogSelectedItem(log);
            logView = new ActionItem(WResource.tr("general.view"), logSelectedItem, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new LogForm(WWidget.tr("log.form"), InteractionState.Viewing, false, logSelectedItem.getSelectedItem()));
                }
            });
            logDelete = new ActionItem(WResource.tr("general.delete"), logSelectedItem, new ITreeAction()
            {
                public void performAction(TreeMenuNode node) 
                {
                    RegaDBMain.getApp().getFormContainer().setForm(new LogForm(WWidget.tr("log.form"), InteractionState.Deleting, false, logSelectedItem.getSelectedItem()));
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
