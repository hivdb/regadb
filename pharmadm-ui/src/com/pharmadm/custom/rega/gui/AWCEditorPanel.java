package com.pharmadm.custom.rega.gui;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.AtomicWhereClauseEditor;


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
