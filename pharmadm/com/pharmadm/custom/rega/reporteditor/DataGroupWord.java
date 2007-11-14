/*
 * DataGroupWord.java
 *
 * Created on November 18, 2003, 6:34 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

/**
 *
 * @author  kristof
 */
public interface DataGroupWord extends com.pharmadm.custom.rega.queryeditor.ConfigurableWord {
    
    // this method required because some DataGroupWords (i.c. ObjectListVariables) can depend on a context
    public String getHumanStringValue(QueryOutputReportSeeder context);
}
