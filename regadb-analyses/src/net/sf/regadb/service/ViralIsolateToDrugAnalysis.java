/*
 * Created on Jan 11, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.service;

import java.util.List;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.ViralIsolate;

/*
 * Requirements:
 *   - asynchronous handling of analyses
 *   - submitting of a batch
 *   - partial results -- later
 *   - rendering of a result -- later
 *   - persistence of the ticket -- later
 */
public interface ViralIsolateToDrugAnalysis extends Analysis {

    public AnalysisTicket submit(Test test, List<ViralIsolate> isolates, List<DrugGeneric> drugs);
    public AnalysisResult getResult(AnalysisTicket ticket);

}

