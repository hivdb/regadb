/*
 * TableSaver.java
 *
 * Created on October 1, 2003, 2:09 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file.table;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.*;
import com.pharmadm.util.file.FileUtil;
import com.pharmadm.util.file.BasicFileFilter;

/**
 * Default partial implementation of TableSaver for character-based files.
 * Concrete classes may subclass this class implement common TableSaver functionality.
 *
 * @author  kdg
 *
 * @invar altFileFilters is null (before init) or contains only BasicFileFilter instances.
 * @invar getAlternativeDescriptions == null || getAlternativeDescriptions().size() == getAlternativeFileFilters().size()
 * @invar getAlternativeDescriptions == null || getAlternativeExtensions == null || getAlternativeDescriptions().size() == getAlternativeExtensions().size()
 */
public abstract class CharacterAbstractTableSaver implements TableSaver {
    
    private FileFilter defaultFileFilter;
    private Collection altFileFilters;
    private Map columnIndexToRenderer = new HashMap();
    
    public CharacterAbstractTableSaver() {
    }
    
    public abstract String getDefaultExtension();
    
    public abstract Collection getAlternativeExtensions();
    
    /**
     * Gets the description associated with the default extension.
     * It is used to create the default file filter.
     * Must not return null.
     */
    protected abstract String getDefaultDescription();
    
    /**
     * Gets the descriptions associated with the alternative extensions.
     * They are used to create the alternative file filters.
     * May return null.
     */
    protected abstract Collection getAlternativeDescriptions();
    
    /**
     * This default implementation returns a filefilter based on the default extension and description.
     */
    public FileFilter getDefaultFileFilter() {
        if (altFileFilters == null) {
            initFileFilters();
        }
        return defaultFileFilter;
    }
    
    /**
     * This default implementation returns filefilters based on the alternative extensions and descriptions.
     */
    public Collection getAlternativeFileFilters() {
        if (altFileFilters == null) {
            initFileFilters();
        }
        return Collections.unmodifiableCollection(altFileFilters);
    }
    
    private void initFileFilters() {
        defaultFileFilter = new BasicFileFilter(getDefaultExtension(), getDefaultDescription());
        List ffList = new ArrayList();
        Collection extensionColl = getAlternativeExtensions();
        Collection descriptionColl = getAlternativeDescriptions();
        if ((extensionColl != null) && (descriptionColl != null)) {
            Iterator iterExts = extensionColl.iterator();
            Iterator iterDesc = descriptionColl.iterator();
            while (iterExts.hasNext()) {
                String ext = (String)iterExts.next();
                String desc = (String)iterDesc.next();
                ffList.add(new BasicFileFilter(ext, desc));
            }
        }
        altFileFilters = ffList;
    }
    
    public boolean save(TableModel tModel) {
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(getDefaultFileFilter());
        Iterator iterAltFF = getAlternativeFileFilters().iterator();
        while (iterAltFF.hasNext()) {
            fc.addChoosableFileFilter((FileFilter)iterAltFF.next());
        }
        fc.setFileFilter(getDefaultFileFilter());
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            FileFilter filter = fc.getFileFilter();
            boolean goodFilter = ((filter == getDefaultFileFilter()) || getAlternativeFileFilters().contains(filter));
            if (!filter.accept(file) || !goodFilter) {
                file = FileUtil.forceExtension(file, "." + getDefaultExtension());
            }
            return save(tModel, file);
        }
        return false;
    }
    
    public boolean save(TableModel tModel, File file) {
        if (file.exists()) {
            int option = JOptionPane.showConfirmDialog(null, "This file already exists. Do you want to overwrite the file?\nAny current contents of the file will be lost.", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.YES_OPTION) {
                return false;
            }
        }
        return saveUnconditional(tModel, file);
    }
    
    /**
     * @inheritDoc
     *
     * WARNING: This implementation uses an UNDOCUMENTED (in SDK 1.4.2) system property.
     * (But it seems to work nevertheless and we're not the only ones using it.)
     */
    public boolean saveUnconditional(TableModel tModel, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            try {
                String encoding = System.getProperty("file.encoding"); // UNDOCUMENTED feature in SDK 1.4.2
                if (encoding == null) {
                    encoding = fileWriter.getEncoding(); // alas, this does NOT return the canonical in many cases!
                }
                writeHeader(tModel, writer, encoding);
                writeRows(tModel, writer);
                writeFooter(tModel, writer);
                writer.flush();
                return true;
            } finally {
                writer.close();
            }
        } catch (IOException ioe) {
            showIOException(ioe);
            return false;
        }
    }
    
    /**
     * Writes the bulk table data section of the file.
     */
    private void writeRows(TableModel model, BufferedWriter writer) throws IOException {
        for (int row = 0; row < model.getRowCount(); row++) {
            writeRow(model, row, writer);
        }
    }
    
    /**
     * Writes the header section of the file to the given writer.
     */
    protected abstract void writeHeader(TableModel model, BufferedWriter writer, String encoding) throws IOException;
    
    /**
     * Writes one row of the table to the given writer.
     * A concrete subclass should use the getCellRenderer method while writing a cell value.
     */
    protected abstract void writeRow(TableModel model, int row, BufferedWriter writer) throws IOException;
    
    /**
     * Writes the footer section of the file (if there is one) to the given writer.
     */
    protected abstract void writeFooter(TableModel model, BufferedWriter writer) throws IOException;
    
    /**
     * Shows an I/O exception to the user.
     * Default behaviour is to show a popup dialog.
     */
    protected void showIOException(IOException ioe) {
        JOptionPane.showMessageDialog(null, "Saving the table to file failed because of:\n"+ioe.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Sets a new call renderer for a specific column.
     */
    public void setCellRenderer(int column, TableSaverCellRenderer renderer) {
        columnIndexToRenderer.put(new Integer(column), renderer);
    }
    
    /**
     * Gets the renderer for the given column, if there is one.
     * If no renderer is present, the toString of the value should be used.
     *
     * @return the cell renderer for the given column (may be null)
     */
    public TableSaverCellRenderer getCellRenderer(int column) {
        return (TableSaverCellRenderer)columnIndexToRenderer.get(new Integer(column));
    }
}
