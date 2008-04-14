/*
 * RectangleSetting.java
 *
 * Created on May 27, 2005, 5:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.awt.Rectangle;

/**
 * A setting for storing a Rectangle.
 *
 * @author kdg
 */
public class RectangleSetting extends FixedComposedSetting {
    
    private static final String BOUNDS_X = "x";
    private static final String BOUNDS_Y = "y";
    private static final String BOUNDS_WIDTH = "width";
    private static final String BOUNDS_HEIGHT = "height";
    
    /** Creates a new instance of RectangleSetting */
    public RectangleSetting(String name) {
        setName(name);
        add(new IntegerSetting(null, BOUNDS_X, new Integer(30)));
        add(new IntegerSetting(null, BOUNDS_Y, new Integer(30)));
        add(new IntegerSetting(null, BOUNDS_WIDTH, new Integer(800)));
        add(new IntegerSetting(null, BOUNDS_HEIGHT, new Integer(800)));
    }
    
    /** Creates a new instance of RectangleSetting
     *
     * @param name the name of this setting
     * @param rect the default value
     */
    public RectangleSetting(String name, Rectangle rect) {
        setName(name);
        add(new IntegerSetting(null, BOUNDS_X, new Integer(30)));
        add(new IntegerSetting(null, BOUNDS_Y, new Integer(30)));
        add(new IntegerSetting(null, BOUNDS_WIDTH, new Integer(800)));
        add(new IntegerSetting(null, BOUNDS_HEIGHT, new Integer(800)));
    }
    
    public Rectangle getRectangle() {
        return new Rectangle(
                ((IntegerSetting)getChild(BOUNDS_X)).integerValue().intValue(),
                ((IntegerSetting)getChild(BOUNDS_Y)).integerValue().intValue(),
                ((IntegerSetting)getChild(BOUNDS_WIDTH)).integerValue().intValue(),
                ((IntegerSetting)getChild(BOUNDS_HEIGHT)).integerValue().intValue());
    }
    
    public void setRectangle(Rectangle rect) {
        ((IntegerSetting)getChild(BOUNDS_X)).setValue(rect.x);
        ((IntegerSetting)getChild(BOUNDS_Y)).setValue(rect.y);
        ((IntegerSetting)getChild(BOUNDS_WIDTH)).setValue(rect.width);
        ((IntegerSetting)getChild(BOUNDS_HEIGHT)).setValue(rect.height);
    }
}
