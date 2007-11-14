/*
 * StringSetting.java
 *
 * Created on February 12, 2001, 5:16 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

//import com.pharmadm.util.resource.MediaInterface;
import com.pharmadm.util.file.FileUtil;

/**
 * This Setting is for relative files only.  It creates files if they do not yet exist
 * when they are requested.
 * For directories, use RelDirSetting.
 *
 * @author  kdg, henkv
 * @version 1.0
 */

public class RelFileSetting extends FileSetting {
    
    private DirSetting prefix = null;
    
    
    public RelFileSetting(XMLSettings xs, String name, DirSetting prefix) {
        super(xs, name);
        this.prefix = prefix;
    }
    
     
    public RelFileSetting(XMLSettings xs, String name, File defaultValue, DirSetting prefix) {
        this(xs, name, prefix);
        setDefaultValue(defaultValue);
    }

    private String subtractPrefix(String S){
        String first = ((File)this.prefix.getValue()).getPath();
        String result;
        if (S.startsWith(first)) {
            result = S.substring(first.length()+1); /* plus 1 for File.separator */
        } else {
            result = S;
        }
        return(result);
    }
    
    public boolean setValue(Object o) {
        String val;
        if (o instanceof File) {
            val = subtractPrefix(((File)o).getPath());
        } else if (o instanceof String) {
            val = subtractPrefix((String)o);
        } else {
            return false;
        }
        super.setValue(new File(val));
        return true;
    }
    
    
    public boolean read(String s) {
        if (s.equals("null")) {
            setValue(null);
        } else {
            String val = subtractPrefix(s);
            setValue(val);
        }
        return true;
    }
    
    
    public boolean write(PrintStream writer) {
        writer.println(super.getValue());
        return true;
    }
    
    public Object getValue() {
        File file = (File) super.getValue();
        if (file == null) {
            return null;
        } else {
            if (file.isAbsolute()) {
                return file;
            } else {
                String first = ((File)this.prefix.getValue()).getPath();
                String result = first + File.separator + file.getPath();
                return (new File(result));
            }
        }
    }
    
}
