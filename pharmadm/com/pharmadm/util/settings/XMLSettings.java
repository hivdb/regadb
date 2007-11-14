/*
 * XMLSettings.java
 *
 * Created on July 17, 2001, 4:33 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.*;
import javax.swing.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;

//import com.pharmadm.util.resource.MediaInterface;

/**
 * Modified version of XMLSettings
 *
 * @author  toms
 *
 * Changed 21 Feb 2002 by kdg
 *      Removed old format support to improve maintainability.
 */
public abstract class XMLSettings implements Cloneable {
    
    //protected Vector settings = new Vector(0, 4);
    
    protected static XMLSettings instance;
    
    protected static int FILE_NOT_SAVED_YET =  1;
    protected static int FILE_SAVED         =  2;
    
    protected int xmlFileStatus = FILE_NOT_SAVED_YET;
    
    public ComposedSetting rootSetting;
    
    // By default, don't be quiet.
    // Being quiet will shut up most stats msgs and some error msgs.
    public boolean isQuiet() {
        return false;
    }
    
    /**
     * Load this settings from its location.
     */
    public void load() {
        XMLSettings backupCopy = (XMLSettings) clone();
        boolean succeeded = false;
        String location = getLocation();
        try {
            
            if (!isQuiet()) {
                System.out.println("Loading settings from " + location);
            }
            
            InputStream input = new FileInputStream(location);
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(input);
                succeeded = loadDocument(document);
            } catch (SAXException sxe) {
                // Error generated during parsing)
                Exception  x = sxe;
                if (sxe.getException() != null)
                    x = sxe.getException();
                //x.printStackTrace();
                if (!isQuiet()) {
                    System.out.print(" [XML: " + x.getMessage() + "] ");
                }
                
            } catch (ParserConfigurationException pce) {
                // Parser with specified options can't be built
                //pce.printStackTrace();
                if (!isQuiet()) {
                    System.out.print(" [Parser Config: " + pce.getMessage() + "] ");
                }
                
            } catch (IOException ioe) {
                // I/O error
                //ioe.printStackTrace();
                if (!isQuiet()) {
                    System.out.print(" [I/O: " + ioe.getMessage() + "] ");
                }
            }
            
            input.close();
            if (!isQuiet()) {
                System.out.println("done.");
            }
        } catch (IOException e) {
            
            succeeded = false;
        }
        // FIXME
        if ((!succeeded) && (!isQuiet())) {
            instance = backupCopy;
            System.out.println();
            System.out.println("Error reading settings from ["+location+"].  Settings were not loaded (rollback).");
            //JOptionPane.showMessageDialog(null,"Error reading settings from [" + location + "].\nMake sure the settings file has been converted to xml format.", "Error reading settings", JOptionPane.ERROR_MESSAGE);
        }
        
        if (succeeded) {
            
            setXmlFileStatus(FILE_SAVED);
            
        }
        
    }
    
    /*
     * Retry to load the settings. If the first read has failed,
     * that might have been due to an old data format. The will
     * have been overwritten with an xml format then.
     */
    private void reload() {
        System.out.println("Reloading...");
        XMLSettings backupCopy = (XMLSettings) clone();
        boolean succeeded = false;
        String location = getLocation();
        if (!isQuiet()) System.out.println("Location :" + location);
        try {
            
            if (!isQuiet()) {
                System.out.println("Loading settings from " + location + " ... ");
            }
            
            InputStream input = new FileInputStream(location);
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(input);
                succeeded = loadDocument(document);
            } catch (SAXException sxe) {
                // Error generated during parsing)
                Exception  x = sxe;
                if (sxe.getException() != null)
                    x = sxe.getException();
                //x.printStackTrace();
                
            } catch (ParserConfigurationException pce) {
                // Parser with specified options can't be built
                //pce.printStackTrace();
                
            } catch (IOException ioe) {
                // I/O error
                //ioe.printStackTrace();
            }
            
            input.close();
            if (!isQuiet()) {
                System.out.println("done.");
            }
        } catch (IOException e) {
            succeeded = false;
        }
        if ((!succeeded) && (!isQuiet())) {
            instance = backupCopy;
            System.out.println();
            System.out.println("Error reading settings from ["+location+"].  Settings were not reloaded (rollback).");
        }
    }
    
    /**
     * Save this settings to its location.
     */
    public void save() {
        boolean succeeded = false;
        String location = getLocation();
        if (!isQuiet()) System.out.println("Saving to location:" + location);
        try {
            OutputStream output = new FileOutputStream(location);
            PrintStream writer = new PrintStream(output);
            succeeded = doSave(writer);
            writer.close();
        } catch (IOException e) {
            succeeded = false;
        }
        if ((!succeeded) && (!isQuiet())) {
            System.out.println("Error writing options: ["+location+"]");
        }
        
        if (succeeded) {
            
            setXmlFileStatus(FILE_SAVED);
            
        }
        
    }
    
    private boolean loadDocument(Document document){
        Node node = document.getDocumentElement();
        
        //NodeList children = node.getChildNodes();
        NodeList children = document.getChildNodes();
        
        //Node child = node.getFirstChild();
        Node child = children.item(0);
        /*
        if (child!=null) {
            NamedNodeMap map = child.getAttributes();
         
            if (map!=null) {
         
                Node verNode = map.getNamedItem("version");
         
                if (verNode!=null) {
         
                    String ver = verNode.getNodeValue();
         
                    if ((this instanceof com.pharmadm.dmax.example.FileBasicStatistics) && (!ver.equals("2.0"))) {
                        return false;
                    }
                }
            }
        }
         */
        boolean ret = getRootSetting().readXML(node);
        return ret;
    }
    
    public ComposedSetting getRootSetting() {
        if (rootSetting == null) {
            rootSetting = new FixedComposedSetting();
            rootSetting.setName("Settings");
        }
        return rootSetting;
    }
    
    protected void setRootSetting(ComposedSetting newRootSetting) {
        this.rootSetting = newRootSetting;
    }
    
    /**
     * Gets a child of the root setting.  Convenience method.
     *
     * @return one of the children of the rootsetting with the specified name, or null if there is no such child.
     */
    protected AbstractSetting getChild(String name) {
        return getRootSetting().getChild(name);
    }
    
    /**
     * This should be overruled by object of subclasses to return an object of their own class.
     */
    public Object clone() {
        try {
            XMLSettings clone = (XMLSettings)super.clone();
            if (rootSetting != null) {
                clone.rootSetting = (ComposedSetting)rootSetting.clone();
            }
            return clone;
        } catch (CloneNotSupportedException cnse) {
            cnse.printStackTrace();
            return null;
        }
    }
    
    protected boolean doSave(PrintStream writer) {
        writer.println("<?xml version='1.0' encoding='utf-8'?>");
        return getRootSetting().writeXML(writer);
        
    }
    
    protected String getDataDir() {
        String home = "Security-Error";
        try {
            String sep = File.separator;
            home = System.getProperty("user.home");
            int idx = home.lastIndexOf(sep);
            if (idx != -1) {
                String lastname = home.substring(idx+1).toUpperCase();
                if (!(System.getProperty("os.name").equals("Windows 2000")) && lastname.equals("WINDOWS")) {
                    home = "C:\\";
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return home;
    }
    
    /**
     * This returns a directory (for use in Windows9x).
     * Should provide Windows-9x compatibility.
     */
    protected abstract String getHome();
    
    /**
     * Returns the default relative pathname of the settings file.
     */
    protected abstract String getLocation();
    
    /*
     * Add an abstractsetting.
     */
    protected void add(AbstractSetting s) {
        getRootSetting().add(s);
    }
    
    /**
     * Returns a cOnfiguration diaglog to edit the settings.
     */
    public JDialog getConfigurationDialog(JFrame owner) {
        
        final ComposedSetting setting = getRootSetting();
        
        JDialog dialog = setting.getConfigurationDialog(owner);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                // Disposing of all buffered configuration controls.
                setting.clearConfigurationControl();
            }
        });
        setting.getConfigurationController().addCommitSubscriber(new CommitSubscriber() {
            public void commited() {
                save();
            }
        });
        return dialog;
    }
    
    public int getXmlFileStatus() {
        return this.xmlFileStatus;
    }
    
    public void setXmlFileStatus(int status) {
        this.xmlFileStatus = status;
    }
}
