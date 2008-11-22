/*
 * Copyright (C) 2008 Rega Institute for Medical Research, KULeuven
 * 
 * See the LICENSE file for terms of use.
 */
package rega.genotype.ui.forms;

import java.io.File;

import rega.genotype.ui.data.SaxParser;
import rega.genotype.ui.framework.GenotypeWindow;
import rega.genotype.ui.framework.widgets.WListContainerWidget;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WText;

/**
 * The DetailsForm widget groups together the different detail modules defined in the 
 * OrganismDefinition implementation.
 */
public class DetailsForm extends AbstractForm {
	private WContainerWidget mainTable;
	private IDetailsForm mainDetails;
	
	private SaxParser p;
	
	public DetailsForm(GenotypeWindow main) {
		super(main, "details-form");
		mainTable = new WContainerWidget(this);
		mainTable.setStyleClass("detailsForm");
	}
	
	public void init(File jobDir, final int selectedSequenceIndex) {
		p = SaxParser.parseFile(jobDir, selectedSequenceIndex);
		
		if (p == null) {
			getMain().monitorForm(jobDir, true);
			return;
		}
		
		mainTable.clear();

		mainDetails = getMain().getOrganismDefinition().getMainDetailsForm();
		addDetailsForm(mainDetails, jobDir);

		WListContainerWidget ul = null;
		WContainerWidget details = null;
		WContainerWidget li;

		if (getMain().getOrganismDefinition().haveDetailsNavigationForm()) {
			WContainerWidget title = new WContainerWidget(mainTable);
			title.addWidget(new WText(tr("details.analysisDetails")));
			details = new WContainerWidget(mainTable);
			details.setStyleClass("details");
			ul = new WListContainerWidget(details);
		}

		for (IDetailsForm df : getMain().getOrganismDefinition().getSupportingDetailsforms(p)) {
			if (ul != null) {
				String detailTitle = df.getTitle().value();
				WText titleText = new WText(lt("<a href=\"#" + detailTitle.replace(" ", "")
						.toLowerCase() + "\">"+detailTitle+"</a>"));
				titleText.setStyleClass("link");
				li = ul.addItem(titleText);
				li.addWidget(new WBreak());
				li.addWidget(new WText(df.getComment()));
				li.addWidget(new WBreak());
			
				if (df.getExtraComment()!=null) {
					WText extraComment = new WText(df.getExtraComment());
					details.addWidget(extraComment);
					extraComment.setStyleClass("details-extraComments");
				}
			}
			
			addDetailsForm(df, jobDir);
		}
	}
	
	void addDetailsForm(IDetailsForm df, File jobDir){
		WContainerWidget cwTitle = new WContainerWidget(mainTable);
		String detailTitle = df.getTitle().value();
		WText titleText = new WText(lt("<h2><a name=\"" + detailTitle.replace(" ", "").toLowerCase() + "\"></a>"
				+ detailTitle + "</h2>"));

		cwTitle.addWidget(titleText);

		WContainerWidget cwDetails = new WContainerWidget(mainTable);
		cwDetails.setStyleClass("details");

		cwDetails.addWidget(df);

		df.fillForm(p, getMain().getOrganismDefinition(), jobDir);
	}
	
	public String getSequenceName() {
		return p.getEscapedValue("genotype_result.sequence[name]");
	}
}
