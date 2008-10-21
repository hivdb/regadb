package rega.genotype.ui.forms;

import rega.genotype.ui.framework.GenotypeWindow;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WText;

public abstract class IForm extends WContainerWidget {
	private WText title;
	private GenotypeWindow main;

	public IForm(GenotypeWindow main, String title) {
		this(main, title, getCssClass(title));
	}
	
	public IForm(GenotypeWindow main, String title, String cssClass) {
		this.main = main;
		this.title = new WText(main.getResourceManager().getOrganismValue(title, "title"), this);
		this.title.setStyleClass("header-mainTitle");
		this.setStyleClass(cssClass);
		new WBreak(this);
	}
	
	public GenotypeWindow getMain() {
		return main;
	}
	
	private static String getCssClass(String title){
		return title.trim().replace(' ', '_').replace('.','-');
	}
}
