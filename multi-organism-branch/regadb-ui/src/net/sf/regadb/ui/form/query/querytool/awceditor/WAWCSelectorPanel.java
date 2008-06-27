package net.sf.regadb.ui.form.query.querytool.awceditor;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;

import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WRadioButton;

public abstract class WAWCSelectorPanel extends WContainerWidget {
    /**
     * get a list of all the radio buttons in this selector panel
     * @return
     */
    public abstract List<WRadioButton> getRadioButtons();    
    
    /**
     * return true if this selector panel or one of its children
     * is selected
     * @return
     */
    public abstract boolean isSelected();
    
    /**
     * adds the given atomic where clause to this selector panel
     * @param clause
     * @return true on success
     *         false on failure
     */
    public abstract boolean addAtomicWhereClause(AtomicWhereClause clause);
    
    /**
     * add the given panel to this panel
     * @param panel
     * @return true on success
     *         false on failure
     */
    public abstract boolean addSelectorPanel(WAWCSelectorPanel panel);
    
    /**
     * returns the editor of the currently selected clause
     * @return
     */
    public abstract WAWCEditorPanel getSelectedClause();
    
    /**
     * return true if all clauses in this panel are useless
     * @return
     */
    public abstract boolean isUseless();
}
