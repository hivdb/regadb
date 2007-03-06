package net.sf.regadb.ui.tree;

import net.sf.regadb.ui.datatable.test.SelectTestForm;
import net.sf.regadb.ui.datatable.therapy.SelectTherapyForm;
import net.sf.regadb.ui.form.singlePatient.SinglePatientForm;
import net.sf.regadb.ui.form.singlePatient.TestResultForm;
import net.sf.regadb.ui.form.singlePatient.TherapyForm;
import net.sf.regadb.ui.form.singlePatient.chart.PatientChartForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.myAccount.LoginItem;
import net.sf.regadb.ui.tree.items.myAccount.MyAccountItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionAddItem;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientAddItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientSelectItem;
import net.sf.regadb.ui.tree.items.singlePatient.PatientSelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.TestResultSelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.TherapySelectedItem;
import net.sf.regadb.ui.tree.items.singlePatient.ViralIsolateSelectedItem;
import net.sf.witty.wt.widgets.WWidget;

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
    public TestResultSelectedItem measurementSelected;
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
    public ViralIsolateSelectedItem viralIsolateSelected;

    public MyAccountItem myAccountMain;
    public LoginItem myAccountLogin;
    
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
                        	RegaDBMain.getApp().getFormContainer().setForm(new SelectTestForm());
                        }
                    });
    				measurementsAdd = new ActionItem(rootItem.tr("menu.singlePatient.measurements.add"), measurements, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new TestResultForm(InteractionState.Adding, WWidget.tr("form.measurement.add"), null));
						}
    				});
    				measurementSelected = new TestResultSelectedItem(measurements);
    				measurementView = new ActionItem(rootItem.tr("menu.singlePatient.measurement.view"), measurementSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new TestResultForm(InteractionState.Viewing, WWidget.tr("form.measurement.view"), measurementSelected.getSelectedTestResult()));
						}
    				});
    				measurementEdit = new ActionItem(rootItem.tr("menu.singlePatient.measurement.edit"), measurementSelected, new ITreeAction()
    				{
						public void performAction(TreeMenuNode node)
						{
							RegaDBMain.getApp().getFormContainer().setForm(new TestResultForm(InteractionState.Editing, WWidget.tr("form.measurement.edit"), measurementSelected.getSelectedTestResult()));
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
