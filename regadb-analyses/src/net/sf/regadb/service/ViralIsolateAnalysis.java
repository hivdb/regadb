/*
 * Created on Jan 11, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.service;

import java.util.List;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.ViralIsolate;

public interface ViralIsolateAnalysis extends Analysis {

    public AnalysisTicket submit(Test test, List<ViralIsolate> isolates);
    public AnalysisResult getResult(AnalysisTicket ticket);
    
}
