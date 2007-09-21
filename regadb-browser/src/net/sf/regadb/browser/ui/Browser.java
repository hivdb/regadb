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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

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
    BorderLayout borderLayout1 = new BorderLayout();

    MyStatusBar statusBar = new MyStatusBar();
    JPanel jBrowserPanel = new JPanel();

    WebBrowser webBrowser;

    public Browser() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        JFrame frame = new JFrame("JDIC API Demo - Browser");

        Container contentPane = frame.getContentPane();

        contentPane.setLayout(new GridLayout(1, 1));
        contentPane.add(new Browser());

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        this.setPreferredSize(new Dimension(screenSize.width * 9 / 10,
                screenSize.height * 8 / 10));

        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        statusBar.lblDesc.setText("JDIC API Demo - Browser");

        try {
            webBrowser = new WebBrowser(new URL("http://java.net"));
            // Print out debug messages in the command line.
            //webBrowser.setDebug(true);
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

        jBrowserPanel.setLayout(new BorderLayout());
        jBrowserPanel.add(webBrowser, BorderLayout.CENTER);

        this.add(statusBar, BorderLayout.SOUTH);
        this.add(jBrowserPanel, BorderLayout.CENTER);
    }

    void updateStatusInfo(String statusMessage) {
        statusBar.lblStatus.setText(statusMessage);
    }

    /**
     * Check the current input URL string in the address text field, load it,
     * and update the status info and toolbar info.
     */
    void loadURL() {
       
        try {
                            
                webBrowser.setURL(new URL("http://www.google.be"));

            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            }                
    }

    void jGoButton_actionPerformed(ActionEvent e) {
        loadURL();
    }

    void jAddressTextField_actionPerformed(ActionEvent e) {
        loadURL();
    }

    void jBackButton_actionPerformed(ActionEvent e) {
        webBrowser.back();
    }

    void jForwardButton_actionPerformed(ActionEvent e) {
        webBrowser.forward();
    }

    void jRefreshButton_actionPerformed(ActionEvent e) {
        webBrowser.refresh();
    }

    void jStopButton_actionPerformed(ActionEvent e) {
        webBrowser.stop();
    }
}