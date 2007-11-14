/*
 * original file: TigenixPrintExampleTable.java
 *
 * Created on July 17, 2001, 5:34 PM
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.gui.table;

import java.awt.print.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.table.*;
import javax.print.*;
import javax.print.event.*;
import javax.print.attribute.*;
/**
 *
 * @author  kdg
 * @version  2.0
 *
 * History
 *  1.0 initial release
 *  2.0 Incorporated some modifications distilled from Michael's version
 *        * fix for a bug that splitted cells between pages.
 *        * Now uses SDK 1.4 printing subsystem
 */
public class TablePrinter implements Printable {
    
    private JTable tableView;
    
    private TablePrinter(JTable table) {
        this.tableView = table;
    }
    
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        
        Graphics2D  g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        int fontHeight=g2.getFontMetrics().getHeight();
        int fontDesent=g2.getFontMetrics().getDescent();
        //leave room for page number
        double pageHeight = pageFormat.getImageableHeight() - fontHeight;
        double pageWidth = pageFormat.getImageableWidth();
        double tableWidth = (double) tableView.getColumnModel().getTotalColumnWidth();
        double scale = 1;
        
        if (tableWidth >= pageWidth) { // rescale to make sure the table fits horizontally on a page.
            scale =  pageWidth / (tableWidth + 0.0001); // sub-1-pixel roundoff margin
        }
        
        { // rescale to make sure cells fit vertically on a page
            final double preliminaryHeaderHeigthOnPage = tableView.getTableHeader().getHeight()*scale;
            final double preliminaryAvailablePageHeigth = pageHeight-preliminaryHeaderHeigthOnPage;
            final double preliminaryRowHeight = (tableView.getRowHeight())*scale;
            
            if (preliminaryRowHeight > preliminaryAvailablePageHeigth) {
                scale = scale * (preliminaryAvailablePageHeigth / (preliminaryRowHeight + 0.0001)); // sub-1-pixel roundoff margin
            }
        }
        
        double headerHeightOnPage = tableView.getTableHeader().getHeight()*scale;
        double tableWidthOnPage = tableWidth*scale;
        
        double rowHeight = (tableView.getRowHeight())*scale;
        int rowsPerPage = (int)((pageHeight-headerHeightOnPage)/rowHeight);
        double pageHeightForTable = rowHeight*rowsPerPage;
        // Number of pages might be zero!
        int totalNumPages = (int)Math.ceil(tableView.getRowCount() / (1.0 * rowsPerPage));
        
        if (pageIndex >= totalNumPages) {
            return NO_SUCH_PAGE;
        }
        
        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2.drawString("Page: " + (pageIndex+1), (int)pageWidth/2-35, (int)(pageHeight+fontHeight-fontDesent));//bottom center
        
        g2.translate(0f,headerHeightOnPage);
        g2.translate(0f,-pageIndex*pageHeightForTable);
        //TODO this next line treats the last page as a full page
        g2.setClip(0, (int)(pageHeightForTable*pageIndex), (int)Math.ceil(tableWidthOnPage), (int)Math.ceil(pageHeightForTable));
        
        g2.scale(scale, scale);
        tableView.print(g2);
        
        g2.scale(1/scale, 1/scale);
        g2.translate(0f,pageIndex*pageHeightForTable);
        g2.translate(0f, -headerHeightOnPage);
        g2.setClip(0, 0, (int)Math.ceil(tableWidthOnPage), (int)Math.ceil(headerHeightOnPage));
        g2.scale(scale, scale);
        
        //paint header at top
        tableView.getTableHeader().print(g2);
        
        return Printable.PAGE_EXISTS;
    }
    
    public static void printTable(JTable table) {
        TablePrinter printIt = new TablePrinter(table);
        //specifying the attributes for the printer
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        //specifying the output format of the document-> service-oriented
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        //returns the set of printers that support printing a specific
        //document type with a specific set of attributes
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        
        PrintService service = ServiceUI.printDialog(null, 200, 200, printService, defaultService, flavor, pras);
        if (service != null) {
            DocPrintJob pj = service.createPrintJob();
            PrintJobListener pjListener = new PrintJobAdapter() {
                public void printDataTransferCompleted(PrintJobEvent e) {
                    System.out.println("Finished printing.");
                }
            };
            pj.addPrintJobListener(pjListener);
            
            DocAttributeSet das = new HashDocAttributeSet();
            Doc doc = new SimpleDoc(printIt, flavor, das);
            try {
                pj.print(doc, pras);
            } catch (PrintException pe) {
                pe.printStackTrace();
            }
            //Now we might want to ensure some way the the data does not change
            //until the printDataTransferCompleted event (see above) was published,
            // Currently, this is not yet implemented.
        }
    }
}