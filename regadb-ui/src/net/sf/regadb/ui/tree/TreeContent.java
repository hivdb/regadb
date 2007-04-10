package net.sf.regadb.ui.tree;

import net.sf.regadb.ui.datatable.attributeSettings.SelectAttributeForm;
import net.sf.regadb.ui.datatable.attributeSettings.SelectAttributeGroupForm;
import net.sf.regadb.ui.datatable.measurement.SelectMeasurementForm;
import net.sf.regadb.ui.datatable.therapy.SelectTherapyForm;
import net.sf.regadb.ui.datatable.viralisolate.SelectViralIsolateForm;
import net.sf.regadb.ui.form.attributeSettings.AttributeForm;
import net.sf.regadb.ui.form.attributeSettings.AttributeGroupForm;
import net.sf.regadb.ui.form.singlePatient.SinglePatientForm;
import net.sf.regadb.ui.form.singlePatient.MeasurementForm;
import net.sf.regadb.ui.form.singlePatient.TherapyForm;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateForm;
import net.sf.regadb.ui.form.singlePatient.chart.PatientChartForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.attributeSettings.AttributeGroupSelectedItem;
import net.sf.regadb.ui.tree.items.attributeSettings.AttributeSelectedItem;
import net.sf.regadb.ui.tree.items.myAccount.LoginItem;
import net.sf.regadb.ui.tree.items.myAccount.MyAccountItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientAddItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientSelectItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientSelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.MeasurementSelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.TherapySelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.ViralIsolateSelectedItem;
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
                        RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Viewing, WWidget.tr("form.singlePatient.view"), patientSelected.getSelectedPatient()));
                    }
                });
                editPatient = new ActionItem(rootItem.tr("menu.singlePatient.edit"), patientSelected, new ITreeAction()
                {
                    public void performAction(TreeMenuNode node) 
                    {
                        RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Editing, WWidget.tr("form.singlePatient.edit"), patientSelected.getSelectedPatient()));
                    }
                });
				chart = new ActionItem(rootItem.tr("menu.singlePatient.chart"), patientSelected, new ITreeAction()
                {
                    public void performAction(TreeMenuNode node) 
                    {
                        RegaDBMain.getApp().getFormContainer().setForm(new PatientChartForm(patientSelected.getSelectedPatient()));
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
							RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Viewing, WWidget.tr("form.measurement.view"), measurementSelected.getSelectedTestResult()));
						}
    				});
    				measurementEdit = new ActionItem(rootItem.tr("menu.singlePatient.measurement.edit"), measurementSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Editing, WWidget.tr("form.measurement.edit"), measurementSelected.getSelectedTestResult()));
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
							RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Viewing, WWidget.tr("form.therapy.view"), therapiesSelected.getSelectedTherapy()));
						}
    				});
    				therapiesEdit = new ActionItem(rootItem.tr("menu.singlePatient.therapies.edit"), therapiesSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new TherapyForm(InteractionState.Editing, WWidget.tr("form.therapy.edit"), therapiesSelected.getSelectedTherapy()));
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
							RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Viewing, WWidget.tr("form.viralIsolate.view"), viralIsolateSelected.getSelectedViralIsolate()));
						}
    				});
                    viralIsolateEdit = new ActionItem(rootItem.tr("menu.singlePatient.viralIsolates.edit"), viralIsolateSelected, new ITreeAction()
                    {
                        public void performAction(TreeMenuNode node)
                        {
                            RegaDBMain.getApp().getFormContainer().setForm(new ViralIsolateForm(InteractionState.Editing, WWidget.tr("form.viralIsolate.edit"), viralIsolateSelected.getSelectedViralIsolate()));
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
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Viewing, WWidget.tr("form.attributeSettings.attribute.view"), attributesSelected.getSelectedAttribute()));
               }
           });
           attributesEdit = new ActionItem(rootItem.tr("menu.attributeSettings.attributes.edit"), attributesSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeForm(InteractionState.Editing, WWidget.tr("form.attributeSettings.attribute.edit"), attributesSelected.getSelectedAttribute()));
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
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Viewing, WWidget.tr("form.attributeSettings.attributeGroups.view"), attributeGroupsSelected.getSelectedAttributeGroup()));
               }
           });
           attributeGroupsEdit = new ActionItem(rootItem.tr("menu.attributeSettings.attributeGroups.edit"), attributeGroupsSelected, new ITreeAction()
           {
               public void performAction(TreeMenuNode node)
               {
                   RegaDBMain.getApp().getFormContainer().setForm(new AttributeGroupForm(InteractionState.Editing, WWidget.tr("form.attributeSettings.attributeGroups.edit"), attributeGroupsSelected.getSelectedAttributeGroup()));
               }
           });

        myAccountMain = new MyAccountItem(rootItem);
            myAccountLogin = new LoginItem(myAccountMain);
			
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
