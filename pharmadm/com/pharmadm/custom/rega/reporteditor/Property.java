/*
 * Property.java
 *
 * Created on November 28, 2003, 5:50 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.*;
import java.lang.reflect.Method;

/**
 *
 * @author  kristof
 */
/* 
 * It is not required for this class to support xml-encoding. 
 */
public class Property implements Valuable, Cloneable {
    
    // implementing cloneable is essential (used in DataSelectionList)
    
    private DataOutputVariable outputVariable;
    private String name;
    private MethodSpecifier specifier;
    
    /** Creates a new instance of Property */
    public Property(String name, DataOutputVariable outputVariable) {
        this.name = name;
        this.outputVariable = outputVariable;
    }
    
    public boolean isId() {
        return name.equalsIgnoreCase("id");
    }
    
    public String getName() {
        return name;
    }
    
    public DataOutputVariable getOutputVariable() {
        return outputVariable;
    }
    
    public void switchOutputVariable(DataOutputVariable outputVariable) {
        this.outputVariable = outputVariable;
        getSpecifier().getCaller().setOutputVariable(outputVariable);
    }
    
    public MethodSpecifier getSpecifier() {
        return specifier;
    }
    
    private void setSpecifier(MethodSpecifier specifier) {
        this.specifier = specifier;
    }
    
    /* Implementing Valuable */
    public Object getValue(DataRow dataRow) {
        return specifier.getValue(dataRow);
    }
    
    public String getColumnHeader(QueryOutputReportSeeder context) {
        return getOutputVariable().getHumanStringValue(context) + "." + getName();
    }
    
    public Object clone() {
        try {
            Property cloneProp = (Property)super.clone();
            cloneProp.setSpecifier((MethodSpecifier)specifier.clone());  
            return cloneProp;
        } catch (CloneNotSupportedException cnse) {
            cnse.printStackTrace();
            return null;
        }
    }
    
    /* 
     * The domain properties associated with a DataOutputVariable are all properties
     * of the Class (value type) of that variable : these are each specified by
     * a public method with no arguments which has a name starting with "getProperty" 
     */
    public static List<Property> getDomainProperties(DataOutputVariable ovar) {
        Class domainClass = ovar.getValueType();
        ArrayList<Property> res = new ArrayList<Property>();
        try {
            Method[] methods = domainClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                String name = methods[i].getName();
                if (name.startsWith("getProperty")) {
                    Property prop = new Property(name.substring(11), ovar);
                    MethodSpecifier mspec = new MethodSpecifier();
                    DataInputVariable dummyInput = new DataInputVariable(ovar.getVariableType());
                    dummyInput.setOutputVariable(ovar);
                    mspec.setCaller(dummyInput);
                    mspec.setName(name);
                    mspec.setParameters(new ValueSpecifier[0]);
                    prop.setSpecifier(mspec);
                    res.add(prop);
                }
            }
        } catch (SecurityException se) {
            se.printStackTrace();
        }
        return res;
    }
    
}
