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

/*
 * A remotely implemented viral isolate analysis using WSRF.
 */
public class RemoteViralIsolateAnalysis implements ViralIsolateAnalysis {

    public RemoteViralIsolateAnalysis(String serviceDescriptionXML, String serviceOptionsXML)
    {
        
    }
    
    public AnalysisResult getResult(AnalysisTicket ticket) {
        // TODO Auto-generated method stub
        return null;
    }

    public AnalysisTicket submit(Test test, List<ViralIsolate> isolates) {
        // TODO Auto-generated method stub
        return null;
    }

}
