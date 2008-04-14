/*
 * SpreadsheetFormula.java
 *
 * Created on November 4, 2004, 3:00 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file.table;

/**
 * A marker interface that shows an entry in a table is a formula, not an actual value.
 * It would be better to create (or reuse) a proper spreadsheet object model, 
 * but time/deadline constraints do not allow for that now.
 *
 * @author  kdg
 */
public interface SpreadsheetFormula {
    public boolean isArrayFormula();
}
