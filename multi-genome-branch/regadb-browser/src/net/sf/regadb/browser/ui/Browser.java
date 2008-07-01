package net.sf.regadb.browser.ui;


/*
 * Copyright (C) 2004 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */ 

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import org.jdesktop.jdic.browser.WebBrowser;
import org.jdesktop.jdic.browser.WebBrowserEvent;
import org.jdesktop.jdic.browser.WebBrowserListener;


/**
 * JDIC API demo main class.
 * <p>
 * <code>Browser</code> is a GUI application demonstrating the usage of the JDIC API package 
 * <code>org.jdesktop.jdic.browser</code> (Browser component).
 */

public class Browser extends JPanel {
    private BorderLayout borderLayout1 = new BorderLayout();

    private MyStatusBar statusBar = new MyStatusBar();
    private JPanel jBrowserPanel = new JPanel();

    private WebBrowser webBrowser;
    
    private JLabel pleaseWait = new JLabel();

    public Browser() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        this.setPreferredSize(new Dimension(screenSize.width * 9 / 10,
                screenSize.height * 8 / 10));

        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        statusBar.lblDesc.setText("RegaDB HIV Data and Analysis Management Software");


        jBrowserPanel.setLayout(new BorderLayout());
        jBrowserPanel.add(pleaseWait, BorderLayout.CENTER);
        pleaseWait.setHorizontalTextPosition(JLabel.CENTER);
        pleaseWait.setVerticalTextPosition(JLabel.CENTER);
        pleaseWait.setText("<html>Loading RegaDB <br> Please Wait...</html>");
        pleaseWait.setFont(new Font(pleaseWait.getFont().getFontName(), Font.BOLD, 50));
        
        this.add(statusBar, BorderLayout.SOUTH);
        this.add(jBrowserPanel, BorderLayout.CENTER);
    }

    public void initWebBrowser() {
        try {
            webBrowser = new WebBrowser(new URL("http://localhost:8080/regadb/RegaDB"));
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
            return;
        }

        webBrowser.addWebBrowserListener(new WebBrowserListener() {
            public void downloadStarted(WebBrowserEvent event) {
                updateStatusInfo("Loading started.");
            }

            public void downloadCompleted(WebBrowserEvent event) {

                updateStatusInfo("Loading completed.");
            }

            public void downloadProgress(WebBrowserEvent event) {
                // updateStatusInfo("Loading in progress...");
            }

            public void downloadError(WebBrowserEvent event) {
                updateStatusInfo("Loading error.");
            }

            public void documentCompleted(WebBrowserEvent event) {
                updateStatusInfo("Document loading completed.");
            }

            public void titleChange(WebBrowserEvent event) {
                updateStatusInfo("Title of the browser window changed.");
            }  

            public void statusTextChange(WebBrowserEvent event) {
                // updateStatusInfo("Status text changed.");
            }  
        });
        
        jBrowserPanel.remove(pleaseWait);
        jBrowserPanel.add(webBrowser, BorderLayout.CENTER);
    }
    
    void updateStatusInfo(String statusMessage) {
        statusBar.lblStatus.setText(statusMessage);
    }
}