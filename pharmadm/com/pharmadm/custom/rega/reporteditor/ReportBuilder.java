/*
 * ReportBuilder.java
 *
 * Created on November 28, 2003, 5:28 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.List;

/**
 *
 * @author  kristof
 */
public class ReportBuilder {
    
    private Report report;
    private final ReportFormatEditor formatEditor;
    
    /** Creates a new instance of ReportBuilder */
    public ReportBuilder() {
        this.report = new Report();
        this.formatEditor = new ReportFormatEditor(report.getFormat());
    }
    
    public ReportFormatEditor getFormatEditor() {
        return formatEditor;
    }
    
    public Report getReport() {
        return report;
    }
    
    public void setReport(Report report) {
        this.report = report;
        formatEditor.setReportFormat(report.getFormat());
        // %$ KVB : fire GUI events
    }
        
    public void createNewReport() {
        setReport(new Report());
    }
    
    public void setReportFormat(ReportFormat format) {
        formatEditor.setReportFormat(format);
        // %$ KVB : fire GUI events
    }
    
    public void resetSeeds() {
        report.reset();
    }
    
    public void seedObjectList(List objectList, ObjectListVariable oLVar) {
        report.seedObjectList(objectList, oLVar);
        // %$ KVB : fire GUI events
    }
    
    public void calculateRows() {
        report.calculateRows();
        // %$ KVB : fire GUI events
    }
}
