/*
 * AWCEditorPanel.java
 *
 * Created on November 27, 2003, 8:46 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui;

import com.pharmadm.custom.rega.queryeditor.*;

/**
 *
 * @author  kristof
 */
public class AWCEditorPanel extends WLOEditorPanel {
    
    /** Creates a new instance of AWCEditorPanel */
    public AWCEditorPanel(AtomicWhereClauseEditor controller) {
        super(controller);
    }
    
    public AtomicWhereClause getClause() {
        return ((AtomicWhereClauseEditor)controller).getAtomicWhereClause();
    }
    
    
}
