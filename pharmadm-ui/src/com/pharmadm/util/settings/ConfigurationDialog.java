/*
 * ConfigurationDialog.java
 *
 * Created on July 18, 2001, 9:13 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import javax.swing.border.EmptyBorder;

import javax.swing.event.CellEditorListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author  toms
 */
public class ConfigurationDialog extends JDialog {
    
    private JTable table;
    private ComposedSetting setting;
    
    private boolean isRoot = false;
    
    private List list;
    
    private int columnWidth;
    
    private Component owner;
    
    private void init(ComposedSetting setting) {
        
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        columnWidth = (screen.width - 600)/2;
        
        if (columnWidth<0) {
            columnWidth = 200;
        }
        
        this.setting = setting;
        list = new ArrayList();
        Iterator iter = setting.getChildren();
        while(iter.hasNext()) {
            list.add(iter.next());
        }
        initComponents();
        initTable();
   
        /**
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                ;
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                ;
            }
        });
        */
        
    }
    
    public ConfigurationDialog(Dialog owner, ComposedSetting setting) {
        super(owner, false);
        init(setting); 
        this.owner = owner;
    }
    
    public ConfigurationDialog(Frame owner, ComposedSetting setting) {
        super(owner, false);
        isRoot = true;
        init(setting); 
        this.owner = owner;
    }

    public void initComponents() {
        
        getContentPane().setLayout(new BorderLayout());
        
        setTitle(setting.getName() + " Configuration");
        
        table = new JTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(3,3,3,3));
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (isRoot) setting.getConfigurationController().commit();
                // FIX ME: recursively no commit is allowed, only root may commit
                setVisible(false);
                if (isRoot) dispose();
                // FIX ME: only root is allowed to dispose
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
                if (isRoot) dispose();
                //FIX ME: only is root is allowed to dispose
            }
        });
        
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setting.getConfigurationController().reset();
            }
        });
        
        panel.add(new Box.Filler(new Dimension(0,0), new Dimension(columnWidth,0), new Dimension(columnWidth,0)));
        panel.add(okButton);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(cancelButton);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(resetButton);
        panel.add(new Box.Filler(new Dimension(0,0), new Dimension(columnWidth,0), new Dimension(columnWidth,0)));        
        
        getContentPane().add(panel, BorderLayout.SOUTH);
    }
    
    private void initTable() {
        table.setModel(new ConfigurationTableModel());
        table.setRowHeight(20);
        table.getColumnModel().getColumn(1).setCellEditor(new Editor());
        
        table.getColumnModel().getColumn(1).setCellRenderer(
        
            new TableCellRenderer() {
                
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    return ((AbstractSetting) value).getConfigurationControl(ConfigurationDialog.this);
                }
                
            }        
        );
        
    }
    
    private class ConfigurationTableModel extends AbstractTableModel {
                 
        public Class getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return AbstractSetting.class;
            }
        }
             
        public int getColumnCount() {
            return 2;
        }
                   
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return "Setting name";
            } else {
                return "Setting value";
            }
                
        }
                  
        public int getRowCount() {
            return list.size();
        }
                   
        public Object getValueAt(int rowIndex, int columnIndex) {
            AbstractSetting as = (AbstractSetting) list.get(rowIndex);
            if (columnIndex == 0) {
                return as.getName();
            } else {
                return as;
            }
        }
             
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1;
        }
        
    }
    
    private class Editor implements TableCellEditor {
        
        public void addCellEditorListener(CellEditorListener l) {}
                   
        public void cancelCellEditing() {}
                   
        public Object getCellEditorValue() {
            return null;
        }
                   
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }
                   
        public void removeCellEditorListener(CellEditorListener l) {}
                   
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }
         
        public boolean stopCellEditing() {
            return true;
        }                   
        
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return ((AbstractSetting) value).getConfigurationControl(ConfigurationDialog.this);
        }
        
    }
    
    public void formComponentShown(java.awt.event.ComponentEvent evt) {

        //SwingUtilities.invokeLater(new Runnable() {
            
        //    public void run() {
                
                int w = getWidth() + 5;
                int h = getHeight() + 5;
        
                //System.out.println("SHOWN owner:"+owner+", w:"+w+", h:"+h);
        
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        
                int x = (screen.width - w) / 2;
                int y = (screen.height - h) / 2;
        
                Rectangle r = owner.getBounds();
        
                if (Math.abs(x-r.x)<20) {
                    x = x + 20;
                }
        
                if (Math.abs(y-r.y)<20) {
                    y = y + 20;
                }
        
                setBounds(x, y, w, h);
                
        //    }
            
        //});

    }
    
}
