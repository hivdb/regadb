package net.sf.regadb.ui.form.importTool.imp;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.IAnalysis;
import net.sf.regadb.service.wts.FullAnalysis;
import net.sf.regadb.service.wts.ServiceException;

public class NonThreadedFullAnalysis extends FullAnalysis{
	public NonThreadedFullAnalysis(ViralIsolate viralIsolate, Genome genome) {
		super(viralIsolate, genome);
	}
	
	protected void launchAnalysis(IAnalysis analysis, Login login) {
		Login copiedLogin = login.copyLogin();
        try {
			analysis.launch(copiedLogin);
		} catch (ServiceException e) {
			e.printStackTrace();
		} finally {
			copiedLogin.closeSession();
		}
	}
}
