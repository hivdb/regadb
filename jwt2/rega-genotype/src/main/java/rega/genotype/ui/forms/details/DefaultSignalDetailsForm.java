package rega.genotype.ui.forms.details;

import java.io.File;

import rega.genotype.ui.data.OrganismDefinition;
import rega.genotype.ui.data.SaxParser;
import rega.genotype.ui.forms.IDetailsForm;
import rega.genotype.ui.framework.widgets.WListContainerWidget;
import rega.genotype.ui.util.GenotypeLib;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public class DefaultSignalDetailsForm extends IDetailsForm {

	@Override
	public void fillForm(SaxParser p, OrganismDefinition od, File jobDir) {
		WListContainerWidget ul = new WListContainerWidget(this);
		WContainerWidget li;
		li = ul.addItem(new WText(tr("defaultSignalAnalysis.signalValue")));
		li.addWidget(new WText(lt(p.getValue("genotype_result.sequence.result['pure-puzzle'].signal"))));
		li = ul.addItem(new WText(tr("defaultSignalAnalysis.signalComment")));
		
		addWidget(GenotypeLib.getWImageFromFile(GenotypeLib.getSignalPNG(GenotypeLib.getFile(jobDir, p.getValue("genotype_result.sequence.result['pure-puzzle'].puzzle")))));
	}
	
	@Override
	public WString getComment() {
		return tr("defaultSignalAnalysis.comment");
	}

	@Override
	public WString getTitle() {
		return tr("defaultSignalAnalysis.title");
	}

	@Override
	public WString getExtraComment() {
		return tr("defaultSignalAnalysis.extraComment");
	}
}
