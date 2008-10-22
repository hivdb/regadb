package rega.genotype.ui.framework;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.NavigationFilter;

import rega.genotype.ui.data.OrganismDefinition;
import rega.genotype.ui.forms.AbstractJobOverview;
import rega.genotype.ui.forms.ContactUsForm;
import rega.genotype.ui.forms.DecisionTreesForm;
import rega.genotype.ui.forms.DetailsForm;
import rega.genotype.ui.forms.DocumentationForm;
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
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public class GenotypeWindow extends WContainerWidget
{
	private static final String START_URL = "/";
	private static final String EXAMPLES_URL = "/examples";
	private static final String METHOD_URL = "/method";
	private static final String DECISIONTREES_URL = "/decisiontrees";
	private static final String TUTORIAL_URL = "/tutorial";
	private static final String CITE_URL = "/cite";
	private static final String JOB_URL = "/job";
	private static final String CONTACT_URL = "/contact";

	private Map<String, IForm> forms = new HashMap<String, IForm>();
	
	private IForm activeForm;
		
	private WContainerWidget content;
	
	private WImage header;
	private WText footer;
	
	private OrganismDefinition od;
	
	public OrganismDefinition getOrganismDefinition() {
		return od;
	}

	private GenotypeResourceManager resourceManager;
	
	private StartForm startForm;
	private StateLink monitor;
	private AbstractJobOverview monitorForm;
	private DetailsForm detailsForm;

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
		WApplication.instance().useStyleSheet("/style/genotype.css");

		header = GenotypeLib.getWImageFromResource(od, "header.gif", this);
		header.setStyleClass("header");

		content = new WContainerWidget(this);
		content.setStyleClass("content");

		WContainerWidget navigation = new WContainerWidget(this);
		navigation.setStyleClass("navigation");

		footer = new WText(resourceManager.getOrganismValue("main-form", "footer"), this);
		footer.setStyleClass("footer");

		addLink(navigation, tr("main.navigation.start"), START_URL, startForm = new StartForm(this));

		monitor = new StateLink(tr("main.navigation.monitor"), JOB_URL, navigation);

		addLink(navigation, tr("main.navigation.howToCite"), CITE_URL, new HowToCiteForm(this));
		addLink(navigation, tr("main.navigation.tutorial"), TUTORIAL_URL, new TutorialForm(this));
		addLink(navigation, tr("main.navigation.decisionTrees"), DECISIONTREES_URL, new DecisionTreesForm(this));
		addLink(navigation, tr("main.navigation.subtypingProcess"), METHOD_URL, new SubtypingProcessForm(this));
		addLink(navigation, tr("main.navigation.exampleSequences"), EXAMPLES_URL, new ExampleSequencesForm(this));
		addLink(navigation, tr("main.navigation.contactUs"), CONTACT_URL, new ContactUsForm(this));

		GenotypeMain.getApp().internalPathChanged.addListener(this, new Signal1.Listener<String>() {

			public void trigger(String basePath) {
				if (basePath.equals("/")) {
					String newPath = "/" + GenotypeMain.getApp().internalPathNextPart(basePath);

					IForm f = forms.get(newPath);

					if (f != null)
						setForm(f);
				} else if (basePath.equals("/job/")) {
					String jobId = GenotypeMain.getApp().internalPathNextPart(basePath);
					monitorForm(new File(Settings.getInstance().getJobDir().getAbsolutePath()+File.separatorChar+jobId));
				}
			} });

		startForm.init();
		setForm(startForm);
	}

	private void addLink(WContainerWidget parent, WString text, String url, IForm form) {
		WAnchor a = new WAnchor("", text, parent);
		a.setRefInternalPath(url);
		a.setStyleClass("link");
		forms.put(url, form);
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
	
	public void monitorForm(File jobDir) {
		if (monitorForm==null)
			monitorForm = od.getJobOverview(this);
		monitorForm.init(jobDir);
		monitor.setVarValue(jobId(jobDir));		
		WApplication.instance().setInternalPath(monitor.ref());

		setForm(monitorForm);
	}

	public void detailsForm(File jobDir, int selectedSequenceIndex) {
		if (detailsForm==null)
			detailsForm = new DetailsForm(this);

		monitor.setVarValue(jobId(jobDir));
		detailsForm.init(jobDir, selectedSequenceIndex);

		setForm(detailsForm);
	}

	public static String reportPath(File jobDir, int sequenceIndex) {
		return jobPath(jobDir) + '/' + String.valueOf(sequenceIndex);
	}

	public static String jobPath(File jobDir) {
		return JOB_URL + '/' + jobId(jobDir);
	}
	
	public static String jobId(File jobDir) {
		return jobDir.getAbsolutePath().substring(jobDir.getAbsolutePath().lastIndexOf(File.separatorChar)+1);
	}	
}
