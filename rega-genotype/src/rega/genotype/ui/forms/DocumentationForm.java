package rega.genotype.ui.forms;

import net.sf.witty.wt.WBreak;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;

import org.jdom.Element;

import rega.genotype.ui.framework.GenotypeWindow;
import rega.genotype.ui.recombination.Table;
import rega.genotype.ui.util.GenotypeLib;

public class DocumentationForm extends IForm {
	public DocumentationForm(GenotypeWindow main, String title) {
		super(main, title);
	}

	protected void fillForm(String formName, String formContent) {
		String ruleNumber;
		String ruleName;
		int headerNr=0;
		
		Element text = getMain().getResourceManager().getOrganismElement(formName, formContent);
		for(Object o : text.getChildren()) {
			final Element e = (Element)o;
			if(e.getName().equals("header")) {
				WText header = new WText(lt((++headerNr) + ". " + getMain().getResourceManager().extractFormattedText(e) +":"), this);
				header.setStyleClass("decisionTreeHeader");
			} else if(e.getName().equals("rule")){
				ruleNumber = e.getAttributeValue("number");
				ruleName = e.getAttributeValue("name");
				new WText(lt(ruleNumber + ": " + ruleName + "<br></br>" + getMain().getResourceManager().extractFormattedText(e) + "<br></br>"), this);
			} else if(e.getName().equals("figure")) {
				WContainerWidget imgDiv = new WContainerWidget(this);
				imgDiv.setStyleClass("imgDiv");
				GenotypeLib.getWImageFromResource(getMain().getOrganismDefinition(),e.getTextTrim(), imgDiv);
			} else if(e.getName().equals("sequence")) {
				String sequence = ">" + e.getAttributeValue("name");
				sequence += "</br>";
				sequence += e.getTextTrim() + "</br>";
				new WText(lt(sequence), this);
			} else if(e.getName().equals("table")) {
				createTable(e.getTextTrim(), this);
			} if(e.getName().equals("text")) {
				new WText(lt(getMain().getResourceManager().extractFormattedText(e)), this);
			}
			new WBreak(this);
		}
	}
	
	private WTable createTable(String csvFile, WContainerWidget parent) {
		Table csvTable = new Table(
				getClass().getClassLoader().getResourceAsStream(
						getMain().getOrganismDefinition().getOrganismDirectory()+csvFile
						), false);
		WTable table = new WTable(parent);

		for(int i = 0; i<csvTable.numRows(); i++) {
			for(int j = 0; j<csvTable.numColumns(); j++) {
				table.putElementAt(i, j, new WText(lt(csvTable.valueAt(j, i))));
			}
		}
		return table;
	}
}