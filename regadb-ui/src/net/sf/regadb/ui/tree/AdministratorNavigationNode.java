package net.sf.regadb.ui.tree;

import java.io.File;
import java.util.EnumSet;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.datatable.attributeSettings.SelectAttributeForm;
import net.sf.regadb.ui.datatable.attributeSettings.SelectAttributeGroupForm;
import net.sf.regadb.ui.datatable.datasetSettings.SelectDatasetAccessUserForm;
import net.sf.regadb.ui.datatable.datasetSettings.SelectDatasetForm;
import net.sf.regadb.ui.datatable.importTool.SelectImportToolForm;
import net.sf.regadb.ui.datatable.log.SelectLogForm;
import net.sf.regadb.ui.datatable.settingsUser.SelectSettingsUserForm;
import net.sf.regadb.ui.datatable.testSettings.SelectResRepTemplateForm;
import net.sf.regadb.ui.datatable.testSettings.SelectTestForm;
import net.sf.regadb.ui.datatable.testSettings.SelectTestTypeForm;
import net.sf.regadb.ui.form.administrator.AboutForm;
import net.sf.regadb.ui.form.administrator.MergePatientsForm;
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
import net.sf.regadb.ui.form.importTool.ImportToolForm;
import net.sf.regadb.ui.form.importTool.data.ImportDefinition;
import net.sf.regadb.ui.form.log.LogForm;
import net.sf.regadb.ui.form.testTestTypes.ResistanceInterpretationTemplateForm;
import net.sf.regadb.ui.form.testTestTypes.TestForm;
import net.sf.regadb.ui.form.testTestTypes.TestTypeForm;
import net.sf.regadb.ui.forms.account.AccountForm;
import net.sf.regadb.ui.forms.account.PasswordForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.events.SelectEventForm;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WResource;
import eu.webtoolkit.jwt.WString;

public class AdministratorNavigationNode extends DefaultNavigationNode{
	
	private ObjectTreeNode<SettingsUser> settingsUser;
	
	private FormNavigationNode updateForm;
	private FormNavigationNode batchTestRunningForm;

	public AdministratorNavigationNode(TreeMenuNode parent) {
		super(WString.tr("menu.administrator.administrator"), parent);
		
		DefaultNavigationNode attributeSettings = new DefaultNavigationNode(WString.tr("menu.attributeSettings.attributeSettings"),this);
		new ObjectTreeNode<Attribute>("attributeSettings.attribute", attributeSettings){

			@Override
			protected ObjectForm<Attribute> createForm(WString name, InteractionState interactionState, Attribute selectedObject) {
				return new AttributeForm(name, interactionState, this, selectedObject);
			}

			@Override
			protected IForm createSelectionForm() {
				return new SelectAttributeForm(this);
			}

			@Override
			public String getArgument(Attribute type) {
				return type.getName();
			}

			@Override
			protected String getObjectId(Attribute object) {
				return object.getAttributeIi() +"";
			}

			@Override
			protected Attribute getObjectById(Transaction t, String id) {
				return t.getAttribute(Integer.parseInt(id));
			}
		};
		
		new ObjectTreeNode<AttributeGroup>("attributeSettings.attributeGroup", attributeSettings){

			@Override
			protected ObjectForm<AttributeGroup> createForm(WString name, InteractionState interactionState, AttributeGroup selectedObject) {
				return new AttributeGroupForm(name, interactionState, this, selectedObject);
			}

			@Override
			protected IForm createSelectionForm() {
				return new SelectAttributeGroupForm(this);
			}

			@Override
			public String getArgument(AttributeGroup type) {
				return type.getGroupName();
			}

			@Override
			protected String getObjectId(AttributeGroup object) {
				return object.getAttributeGroupIi() +"";
			}

			@Override
			protected AttributeGroup getObjectById(Transaction t, String id) {
				return t.getAttributeGroup(Integer.parseInt(id));
			}
			
		};
		
		DefaultNavigationNode testSettings = new DefaultNavigationNode(WString.tr("menu.testSettings.testSettings"), this);
		
		new ObjectTreeNode<TestType>("testSettings.testType", testSettings){
			@Override
			protected ObjectForm<TestType> createForm(WString name, InteractionState interactionState, TestType selectedObject) {
				return new TestTypeForm(name, interactionState, this, selectedObject);
			}

			@Override
			protected IForm createSelectionForm() {
				return new SelectTestTypeForm(this);
			}

			@Override
			public String getArgument(TestType type) {
				return type.getDescription();
			}

			@Override
			protected String getObjectId(TestType object) {
				return object.getTestTypeIi() +"";
			}

			@Override
			protected TestType getObjectById(Transaction t, String id) {
				return t.getTestType(Integer.parseInt(id));
			}
		};
		
		new ObjectTreeNode<Test>("testSettings.test", testSettings){
			@Override
			protected ObjectForm<Test> createForm(WString name, InteractionState interactionState, Test selectedObject) {
				return new TestForm(name,interactionState,this,selectedObject);
			}

			@Override
			protected IForm createSelectionForm() {
				return new SelectTestForm(this);
			}

			@Override
			public String getArgument(Test type) {
				return type.getDescription();
			}

			@Override
			protected String getObjectId(Test object) {
				return object.getTestIi() +"";
			}

			@Override
			protected Test getObjectById(Transaction t, String id) {
				return t.getTest(Integer.parseInt(id));
			}
		};
		
		new ObjectTreeNode<ResistanceInterpretationTemplate>("resistance.report.template", testSettings){
			@Override
			protected ObjectForm<ResistanceInterpretationTemplate> createForm(
					WString name, InteractionState interactionState, ResistanceInterpretationTemplate selectedObject) {
				return new ResistanceInterpretationTemplateForm(name, interactionState, this, selectedObject);
			}

			@Override
			protected IForm createSelectionForm() {
				return new SelectResRepTemplateForm(this);
			}

			@Override
			public String getArgument(ResistanceInterpretationTemplate type) {
				return type.getName();
			}

			@Override
			protected String getObjectId(ResistanceInterpretationTemplate object) {
				return object.getTemplateIi() +"";
			}

			@Override
			protected ResistanceInterpretationTemplate getObjectById(
					Transaction t, String id) {
				return t.getResistanceInterpretationTemplate(Integer.parseInt(id));
			}
		};
		
		new ObjectTreeNode<Event>("event", this){
			@Override
			protected ObjectForm<Event> createForm(WString name, InteractionState interactionState, Event selectedObject) {
				return new EventForm(name, interactionState, this, selectedObject);
			}

			@Override
			protected IForm createSelectionForm() {
				return new SelectEventForm(this);
			}

			@Override
			public String getArgument(Event type) {
				return type.getName();
			}

			@Override
			protected String getObjectId(Event object) {
				return object.getEventIi() +"";
			}

			@Override
			protected Event getObjectById(Transaction t, String id) {
				return t.getEvent(Integer.parseInt(id));
			}
		};
		
		DefaultNavigationNode datasetSettings = new DefaultNavigationNode(WString.tr("menu.datasetSettings"), this);
		
		new ObjectTreeNode<Dataset>("dataset",datasetSettings){
			@Override
			protected ObjectForm<Dataset> createForm(WString name, InteractionState interactionState, Dataset selectedObject) {
				return new DatasetForm(name, interactionState, this, selectedObject);
			}

			@Override
			protected IForm createSelectionForm() {
				return new SelectDatasetForm(this);
			}

			@Override
			public String getArgument(Dataset type) {
				return type.getDescription();
			}

			@Override
			protected String getObjectId(Dataset object) {
				return object.getDescription();
			}

			@Override
			protected Dataset getObjectById(Transaction t, String id) {
				return t.getDataset(id);
			}
		};
		
		new ObjectTreeNode<SettingsUser>("dataset.access", datasetSettings, EnumSet.of(InteractionState.Viewing, InteractionState.Editing)){
			@Override
			protected ObjectForm<SettingsUser> createForm(WString name,
					InteractionState interactionState,
					SettingsUser selectedObject) {
				return new DatasetAccessForm(name,interactionState,this,selectedObject);
			}

			@Override
			protected IForm createSelectionForm() {
				return new SelectDatasetAccessUserForm(this);
			}

			@Override
			public String getArgument(SettingsUser type) {
				return type.getUid();
			}

			@Override
			protected String getObjectId(SettingsUser object) {
				return object.getUid();
			}

			@Override
			protected SettingsUser getObjectById(Transaction t, String id) {
				return t.getSettingsUser(id);
			}
		};
        
        settingsUser = new ObjectTreeNode<SettingsUser>("administrator.user", this){

			@Override
			protected ObjectForm<SettingsUser> createForm(WString name, InteractionState interactionState, SettingsUser selectedObject) {
				return new AccountForm(name,interactionState,this,selectedObject,true);
			}

			@Override
			protected IForm createSelectionForm() {
				return new SelectSettingsUserForm(this);
			}

			@Override
			public String getArgument(SettingsUser type) {
				return type.getUid() == null ? type.getFirstName() : type.getUid();
			}

			@Override
			protected String getObjectId(SettingsUser object) {
				return object.getUid();
			}

			@Override
			protected SettingsUser getObjectById(Transaction t, String id) {
				return t.getSettingsUser(id);
			}
        	
        };
        new FormNavigationNode(WString.tr("form.settings.user.password"), settingsUser.getSelectedItemNavigationNode(), true){

			@Override
			public IForm createForm() {
				return new PasswordForm(WString.tr("menu.myAccount.passwordForm"),
						InteractionState.Editing,
						settingsUser.getSelectedItemNavigationNode(),
						true,
						settingsUser.getSelectedItem());
			}
        };
        
        
        DefaultNavigationNode update = new DefaultNavigationNode(WString.tr("menu.administrator.updateFromCentralRepos"), this);
        updateForm = new FormNavigationNode(WString.tr("menu.administrator.updateFromCentralRepos.update.view"), update, true)
        {
            public IForm createForm()
            {
                return new UpdateForm(WString.tr("form.update_central_server.view"), InteractionState.Viewing){

					@Override
					public void redirectAfterSave() {
					}

					@Override
					public void redirectAfterCancel() {
					}
                	
                };
            }
        };
        new FormNavigationNode(WString.tr("menu.administrator.updateFromCentralRepos.update"), update, true)
        {
            public IForm createForm()
            {
                return new UpdateForm(WString.tr("form.update_central_server"),InteractionState.Editing){
					@Override
					public void redirectAfterSave() {
						updateForm.selectNode();
					}

					@Override
					public void redirectAfterCancel() {
						updateForm.selectNode();
					}
                };
            }
        };
        
        new ObjectTreeNode<ImportDefinition>("importTool", this){
			@Override
			protected ObjectForm<ImportDefinition> createForm(
					WString name, InteractionState interactionState, ImportDefinition selectedObject) {
				return new ImportToolForm(name, interactionState, this, selectedObject);
			}

			@Override
			protected IForm createSelectionForm() {
				return new SelectImportToolForm(this);
			}

			@Override
			public String getArgument(ImportDefinition type) {
				return type.getDescription();
			}

			@Override
			protected String getObjectId(ImportDefinition object) {
				return object.getXmlFile().getName();
			}

			@Override
			protected ImportDefinition getObjectById(Transaction t, String id) {
				File f = new File(
						RegaDBSettings.getInstance().getInstituteConfig().getImportToolDir().getAbsolutePath()
						+ File.separatorChar + id);
				return ImportDefinition.getImportDefinition(f);
			}
        };
        
        DefaultNavigationNode importXML = new DefaultNavigationNode(WString.tr("menu.impex.import"), this);
        final FormNavigationNode importRunning = new FormNavigationNode(WString.tr("menu.impex.import.run"), importXML, true)
        {
            public IForm createForm() 
            {
                return new ImportFormRunning(WString.tr("form.impex.import.title"), InteractionState.Viewing);
            }
        };
        new FormNavigationNode(WString.tr("menu.impex.import.add"), importXML, true)
        {
            public IForm createForm() 
            {
                return new ImportFormAdd(WString.tr("form.impex.import.title"), InteractionState.Adding){
					@Override
					public void redirectAfterSave() {
						importRunning.selectNode();
					}
					@Override
					public void redirectAfterCancel() {
						importRunning.selectNode();
					}
                	
                };
            }
        };
        
        new FormNavigationNode(WString.tr("menu.impex.export"), this, true)
        {
            public IForm createForm() 
            {
                return new ExportForm(WResource.tr("form.impex.export.title"), InteractionState.Viewing);
            }
        };
        
        DefaultNavigationNode batchTest = new DefaultNavigationNode(WString.tr("menu.batchtest"), this);
        
        batchTestRunningForm = new FormNavigationNode(WString.tr("menu.batchtest.running"), batchTest, true) {
            public IForm createForm() 
            {
                return new BatchTestRunningForm(WString.tr("form.batchtest.title"), InteractionState.Viewing);
            }
        };

        new FormNavigationNode(WString.tr("menu.batchtest.add"), batchTest, true) {
            public IForm createForm() 
            {
                return new BatchTestAddForm(WString.tr("form.batchtest.title"), InteractionState.Adding){

					@Override
					public void redirectAfterSave() {
						batchTestRunningForm.selectNode();
					}

					@Override
					public void redirectAfterCancel() {
						batchTestRunningForm.selectNode();
					}
                	
                };
            }
        };
        
        new FormNavigationNode(WString.tr("menu.mergePatients"), this, true)
        {
        	public IForm createForm()
        	{
        		return new MergePatientsForm();
        	}
        };
        
        new ObjectTreeNode<File>("log",this,EnumSet.of(InteractionState.Viewing,InteractionState.Deleting)){

			@Override
			protected ObjectForm<File> createForm(WString name, InteractionState interactionState, File selectedObject) {
				return new LogForm(name,interactionState,this,selectedObject);
			}

			@Override
			protected IForm createSelectionForm() {
				return new SelectLogForm(this);
			}

			@Override
			public String getArgument(File type) {
				return type.getName();
			}

			@Override
			protected String getObjectId(File object) {
				return object.getName();
			}

			@Override
			protected File getObjectById(Transaction t, String id) {
				return new File(RegaDBSettings.getInstance().getInstituteConfig().getLogDir().getAbsolutePath()
						+ File.separatorChar + id);
			}
        	
        };
        
        new FormNavigationNode(WString.tr("menu.about"),this, true)
        {
            public IForm createForm() 
            {
                return new AboutForm();
            }
        };
	}

    @Override
    public boolean isDisabled()
    {
        return RegaDBMain.getApp().getLogin()==null
            || !RegaDBMain.getApp().getRole().isAdmin();
    }
}
