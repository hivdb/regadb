/*
 * DMaxMedia.java
 *
 * Created on July 18, 2002, 3:38 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.resource;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;

/**
 *
 * @author kdg
 */
public class DMaxMedia {
    
    private static DMaxMedia instance;
    private static final String RESOURCESLOCATION = "/resources/";
    private static final String IMAGESLOCATION    = "/resources/img/";
    private static final String JMOLIMAGESLOCATION    = "/org/openscience/jmol/viewer/images/";
    /** Creates new DMaxMedia */
    protected DMaxMedia() {
    }
    
    public static DMaxMedia getInstance() {
        if (instance == null)
            instance = new DMaxMedia();
        return instance;
    }
    
    public Image getJmolImage(final String name) {
        final URL url = DMaxMedia.class.getResource(JMOLIMAGESLOCATION + name);
        if (url == null) {
            System.err.println("Could not locate Jmol image: " + name);
        }
        return Toolkit.getDefaultToolkit().getImage(url);
    }
    
    public Image getImage(final String name) {
        final URL url = DMaxMedia.class.getResource(IMAGESLOCATION + name);
        if (url == null) {
            System.err.println("Could not locate image: " + name);
        }
        return Toolkit.getDefaultToolkit().getImage(url);
    }
    
    public BufferedImage getBufferedImage(final String name) {
        BufferedImage result = null;
        final URL url = DMaxMedia.class.getResource(IMAGESLOCATION + name);
        if (url == null) {
            System.err.println("Could not locate image: " + name);
        }
        try {
            result = javax.imageio.ImageIO.read(url);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result;
    }
    
    public InputStream getResource(String name) {
        return DMaxMedia.class.getResourceAsStream(RESOURCESLOCATION + name);
    }
    
    public String getResourceLocation() {
        String s =  DMaxMedia.class.getResource(RESOURCESLOCATION).toString();
        return s.substring(5,s.length()); //drop "file:
    }
    
    
}
