/*
 * RelativeResourceURLfetcher.java
 *
 * Created on April 25, 2005, 4:50 PM
 *
 * Copyright 2005 PharmaDM. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * PharmaDM ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with PharmaDM.
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.work;

import java.net.URL;

/**
 * Contains a method for contructing an URL from the package name of a class and a string with the resource name
 * @author ldh
 */
public class RelativeResourceURLfetcher {
    
    /** Returns a URL contructed from a class and a
     * file name of a resource.
     * The assumption is that that the package of
     * the class contains a directory 'resources' where
     * the file name can be found.
     * @param c The class of which the package contains the 'resources' directory.
     * @param resourceString The file name of the resource.
     * @return The url that correponds to the package name of the class (with dots replaced by
     * file separators) plus "resources", plus file separator, plus
     * the resource string.
     */    
    public static URL fetch(Class c, String resourceString) {
        return c.getResource("resources/" + resourceString);
    }
    
}
