package com.pharmadm.custom.rega.gui.awceditor;

import java.util.List;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;

/**
 * Panel containing one or more {@link AWCEditorPanel}s and/or other AWCSelectorPanels
 * providing the means to select one of the {@link AWCEditorPanel}s
 * @author fromba0
 *
 */
public abstract class AWCSelectorPanel extends JPanel {
    public AWCSelectorPanel() {}
    
    /**
     * get a list of all the radio buttons in this selector panel
     * @return
     */
    public abstract List<JRadioButton> getRadioButtons();    
    
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
    public abstract boolean addSelectorPanel(AWCSelectorPanel panel);
    
    /**
     * returns the editor of the currently selected clause
     * @return
     */
    public abstract AWCEditorPanel getSelectedClause();
}
