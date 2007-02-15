package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WTable;

public class SinglePatientForm extends WContainerWidget implements IForm
{
    private ArrayList<IFormField> formFields_ = new ArrayList<IFormField>();
    
    //general group
    private WGroupBox generalGroup_;
    private WTable generalGroupTable_;
    private Label sourceDatasetL;
    private TextField sourceDatasetTF;
    private Label idL;
    private TextField idTF;
    private Label firstNameL;
    private TextField firstNameTF;
    private Label lastNameL;
    private TextField lastNameTF;
    private Label birthDateL;
    private TextField birthDateTF;
    private Label deathDateL;
    private TextField deathDateTF;
    
    private boolean editable_;
    
    public SinglePatientForm(boolean editable)
	{
        super(null);
        editable_ = editable;
	}
    
    public void init()
    {       
        //general group
        generalGroup_ = new WGroupBox(tr("form.singlePatient.editView.general"), this);
        generalGroupTable_ = new WTable(generalGroup_);
        sourceDatasetL = new Label(tr("form.singlePatient.editView.sourceDataset"));
        sourceDatasetTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, sourceDatasetL, sourceDatasetTF);
        idL = new Label(tr("form.singlePatient.editView.patientId"));
        idTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, idL, idTF);
        firstNameL = new Label(tr("form.singlePatient.editView.firstName"));
        firstNameTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, firstNameL, firstNameTF);
        lastNameL = new Label(tr("form.singlePatient.editView.lastName"));
        lastNameTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, lastNameL, lastNameTF);
        birthDateL = new Label(tr("form.singlePatient.editView.birthDate"));
        birthDateTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, birthDateL, birthDateTF);
        deathDateL = new Label(tr("form.singlePatient.editView.deathDate"));
        deathDateTF = new TextField(editable_, this);
        addLineToTable(generalGroupTable_, deathDateL, deathDateTF);
        
        fillData(RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedPatient());
    }
    
    public void addLineToTable(WTable table, Label label, IFormField field)
    {
        int numRows = table.numRows();
        table.putElementAt(numRows, 0, label);
        table.putElementAt(numRows, 1, field.getWidget());
        label.setBuddy(field);
    }
    
    public void fillData(Patient patient)
    {
        sourceDatasetTF.setText("IMPLEMENT");
        idTF.setText(patient.getPatientId());
        firstNameTF.setText(patient.getFirstName());
        lastNameTF.setText(patient.getLastName());
        if(patient.getBirthDate()!=null)
        birthDateTF.setText(patient.getBirthDate().toString());
        if(patient.getDeathDate()!=null)
        deathDateTF.setText(patient.getDeathDate().toString());
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
