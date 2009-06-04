package rega.genotype.ui.viruses.htlv;

import rega.genotype.ui.framework.GenotypeApplication;
import rega.genotype.ui.framework.GenotypeMain;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WEnvironment;

@SuppressWarnings("serial")
public class HtlvMain extends GenotypeMain {


	public WApplication createApplication(WEnvironment env) {
		GenotypeApplication app = new GenotypeApplication(env, this.getServletContext(), new HtlvDefinition());
		return app;
	}

}
