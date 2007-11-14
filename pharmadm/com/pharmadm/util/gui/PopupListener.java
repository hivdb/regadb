/*
 * PopupListener.java
 *
 * Created on May 6, 2004, 4:26 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 * A listener that brings up a popup menu when the mouse enters a popup trigger.
 *
 * Example:
 * JTable table = new JTable();
 * JPopupMenu popupMenu = new JPopupMenu();
 * // [add some JMenuItems to popupMenu here]
 * table.getTableHeader().addMouseListener(new PopupListener(popupMenu));
 *
 * @author  kdg
 */
public class PopupListener extends MouseAdapter {
    
    private final JPopupMenu popup;
    private int x;
    private int y;
    
    public PopupListener(JPopupMenu popup) {
        this.popup = popup;
    }
    
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }
    
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }
    
    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            x = e.getX();
            y = e.getY();
            popup.show(e.getComponent(), x, y);
        }
    }
    
    /**
     * Gets the x coordinate at which the popup is being or has been shown most recently.
     * If the popup has not yet been shown, returns 0.
     */
    public int getX() {
        return x;
    }
    
    /**
     * Gets the y coordinate at which the popup is being or has been shown most recently.
     * If the popup has not yet been shown, returns 0.
     */
    public int getY() {
        return y;
    }
    
    /**
     * Gets the location at which the popup is being or has been shown most recently.
     */
    public Point getLocation() {
    	return new Point(x, y);
    }
}
