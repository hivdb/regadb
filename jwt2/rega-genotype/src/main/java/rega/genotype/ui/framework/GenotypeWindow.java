package rega.genotype.ui.framework;

import java.io.File;

import rega.genotype.ui.data.OrganismDefinition;
import rega.genotype.ui.forms.AbstractJobOverview;
import rega.genotype.ui.forms.ContactUsForm;
import rega.genotype.ui.forms.DecisionTreesForm;
import rega.genotype.ui.forms.DetailsForm;
import rega.genotype.ui.forms.ExampleSequencesForm;
import rega.genotype.ui.forms.HowToCiteForm;
import rega.genotype.ui.forms.IForm;
import rega.genotype.ui.forms.StartForm;
import rega.genotype.ui.forms.SubtypingProcessForm;
import rega.genotype.ui.forms.TutorialForm;
import rega.genotype.ui.i18n.resources.GenotypeResourceManager;
import rega.genotype.ui.util.GenotypeLib;
import rega.genotype.ui.util.Settings;
import rega.genotype.ui.util.StateLink;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WText;

public class GenotypeWindow extends WContainerWidget
{
	private IForm activeForm;
		
	private WContainerWidget content;
	
	private WImage header;
	private WText footer;
	
	private OrganismDefinition od;
	
	public OrganismDefinition getOrganismDefinition() {
		return od;
	}

	private GenotypeResourceManager resourceManager;
	
	private WText start;
	private StartForm startForm;
	private StateLink monitor;
	private AbstractJobOverview monitorForm;
	private DetailsForm detailsForm;
	private WText howToCite;
	private HowToCiteForm howToCiteForm;
	private WText tutorial;
	private TutorialForm tutorialForm;
	private WText decisionTrees;
	private DecisionTreesForm decisionTreesForm;
	private WText subtypingProcess;
	private SubtypingProcessForm subtypingProcessForm;
	private WText exampleSequences;
	private ExampleSequencesForm exampleSequencesForm;
	private WText contactUs;
	private ContactUsForm contactUsForm;
	
	public GenotypeWindow(OrganismDefinition od)
	{
		super();
		this.od = od;
	}

	public GenotypeResourceManager getResourceManager() {
		return resourceManager;
	}
	private void loadI18nResources()
	{
		resourceManager = new GenotypeResourceManager("/rega/genotype/ui/i18n/resources/common_resources.xml", od.getOrganismDirectory()+"resources.xml");
		WApplication.instance().setLocalizedStrings(resourceManager);
	}

	public void init() {
		loadI18nResources();

		setStyleClass("root");
		WApplication.instance().useStyleSheet("style/genotype.css");

		header = GenotypeLib.getWImageFromResource(od, "header.gif", this);
		header.setStyleClass("header");
		
		content = new WContainerWidget(this);
		content.setStyleClass("content");
		
		WContainerWidget navigation = new WContainerWidget(this);
		navigation.setStyleClass("navigation");
		
		footer = new WText(resourceManager.getOrganismValue("main-form", "footer"), this);
		footer.setStyleClass("footer");


		
		start = new WText(tr("main.navigation.start"), navigation);
		start.clicked.addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent a) {
				startForm();
			}
		});
		start.setStyleClass("link");
		monitor = new StateLink(tr("main.navigation.monitor"), navigation){
			public void clickAction(String value) {
				monitorForm(new File(Settings.getInstance().getJobDir().getAbsolutePath()+File.separatorChar+value));
			}
		};
		howToCite = new WText(tr("main.navigation.howToCite"), navigation);
		howToCite.setStyleClass("link");
		howToCite.clicked.addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent a) {
				if(howToCiteForm==null)
					howToCiteForm = new HowToCiteForm(GenotypeWindow.this);
				setForm(howToCiteForm);
			}
		});
		tutorial = new WText(tr("main.navigation.tutorial"), navigation);
		tutorial.setStyleClass("link");
		tutorial.clicked.addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent a) {
				if(tutorialForm==null)
					tutorialForm = new TutorialForm(GenotypeWindow.this);
				setForm(tutorialForm);
			}
		});
		decisionTrees = new WText(tr("main.navigation.decisionTrees"), navigation);
		decisionTrees.setStyleClass("link");
		decisionTrees.clicked.addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent a) {
				if(decisionTreesForm==null)
					decisionTreesForm = new DecisionTreesForm(GenotypeWindow.this);
				setForm(decisionTreesForm);
			}
		});
		subtypingProcess = new WText(tr("main.navigation.subtypingProcess"), navigation);
		subtypingProcess.setStyleClass("link");
		subtypingProcess.clicked.addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				if(subtypingProcessForm==null)
					subtypingProcessForm = new SubtypingProcessForm(GenotypeWindow.this);
				setForm(subtypingProcessForm);
			}
		});
		exampleSequences = new WText(tr("main.navigation.exampleSequences"), navigation);
		exampleSequences.setStyleClass("link");
		exampleSequences.clicked.addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				if(exampleSequencesForm==null)
					exampleSequencesForm = new ExampleSequencesForm(GenotypeWindow.this);
				setForm(exampleSequencesForm);
			}
		});
		contactUs = new WText(tr("main.navigation.contactUs"), navigation);
		contactUs.setStyleClass("link");
		contactUs.clicked.addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				if(contactUsForm==null) 
					contactUsForm = new ContactUsForm(GenotypeWindow.this);
				setForm(contactUsForm);
			}
		});
		
		startForm();
	}
	
	private void setForm(IForm form) {
		if(activeForm!=null)
			activeForm.hide();
		activeForm = form;
		activeForm.show();
		if (form.parent() == null){
			content.addWidget(form);
		}
	}
	
	public void startForm() {
		if(startForm==null)
			startForm = new StartForm(this);
		startForm.init();
		setForm(startForm);
	}
	
	public void monitorForm(File jobDir) {
		if(monitorForm==null)
			monitorForm = od.getJobOverview(this);
		monitorForm.init(jobDir);
		monitor.setVarValue(jobDir.getAbsolutePath().substring(jobDir.getAbsolutePath().lastIndexOf(File.separatorChar)+1));
		setForm(monitorForm);
	}
	
	public void detailsForm(File jobDir, int selectedSequenceIndex) {
		if(detailsForm==null)
			detailsForm = new DetailsForm(this);
		detailsForm.init(jobDir, selectedSequenceIndex);
		setForm(detailsForm);
	}
}
