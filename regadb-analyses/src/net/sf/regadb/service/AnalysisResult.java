/*
 * Created on Jan 11, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.service;

import java.util.List;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ViralIsolate;

public class AnalysisResult {

    boolean done;
    private AnalysisTicket ticket;
    private List<TestResult> results;

    public AnalysisResult(AnalysisTicket ticket, boolean done, List<TestResult> results) {
        this.done = done;
        this.ticket = ticket;
        this.results = results;
    }
}
