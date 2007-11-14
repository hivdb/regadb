/*
 * MethodSpecifier.java
 *
 * Created on November 24, 2003, 2:22 PM
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
 * This class supports xml-encoding. The following properties are encoded :
 *  caller
 *  name
 *  parameters
 *
 */
public class MethodSpecifier implements ValueSpecifier, Cloneable {
    
    private String name;
    private DataInputVariable caller;
    private ValueSpecifier[] parameters;
    
    /** Creates a new instance of MethodSpecifier */
    public MethodSpecifier() {
    }
    
    public MethodSpecifier(DataInputVariable caller, String name, ValueSpecifier[] parameters) {
        this.name = name;
        this.caller = caller;
        this.parameters = parameters;
    }
    
    public DataInputVariable getCaller() {
        return caller;
    }
    
    public void setCaller(DataInputVariable caller) {
        this.caller = caller;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ValueSpecifier[] getParameters() { 
        return parameters;
    }
    
    public void setParameters(ValueSpecifier[] parameters) {
        this.parameters = parameters;
    }
    
    private Class[] getParameterTypes() {
        Class[] types = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            types[i] = parameters[i].getValueType();
        }
        return types;
    }
    
    private String showParameterTypes() {
        String res = "";
        Class[] types = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            types[i] = parameters[i].getValueType();
            res += types[i].toString() + " -- ";
        }
        return res;
    }
            
    private Object[] getParameterValues(DataRow dataRow) {
        Object[] values = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            values[i] = parameters[i].getValue(dataRow);
        }
        return values;
    }
    
    private String showParameterValueTypes(DataRow dataRow) {
        String res = "";
        Class[] values = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            values[i] = parameters[i].getValue(dataRow).getClass();
            res += values[i].toString() + " -- ";
        }
        return res;
    }
    
    private Method getMethod() {
        try {
            Class callerType = caller.getValueType();
            Method method = callerType.getMethod(name, getParameterTypes());
            return method;
        } catch (java.lang.NoSuchMethodException nsme) {
            nsme.printStackTrace();
            return null;
        }
    }
    
    /* Implementing ValueSpecifier */
    public Object getValue(DataRow dataRow) {
        Object callerValue = caller.getValue(dataRow);
        if (callerValue == null) {
            return null;
        } else {
            try {
                return getMethod().invoke(callerValue, getParameterValues(dataRow));
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(showParameterTypes());
                System.err.println(showParameterValueTypes(dataRow));
                return null;
            }
        }
    }
    
    /* Implementing ValueSpecifier */
    public Class getValueType() {
        return getMethod().getReturnType(); 
    }
    
    /* Implementing ValueSpecifier */
    public ValueSpecifier cloneInContext(java.util.Map originalToCloneMap) throws CloneNotSupportedException {
        MethodSpecifier clone = (MethodSpecifier)super.clone();
        clone.setCaller((DataInputVariable)getCaller().cloneInContext(originalToCloneMap));
        ValueSpecifier[] cloneParameters = new ValueSpecifier[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            cloneParameters[i] = parameters[i].cloneInContext(originalToCloneMap);
        }
        clone.setParameters(cloneParameters);
        return clone;
    }
    
    public Object clone() throws CloneNotSupportedException {
        MethodSpecifier clone = (MethodSpecifier)super.clone();
        clone.setCaller((DataInputVariable)getCaller().clone());
        ValueSpecifier[] cloneParameters = new ValueSpecifier[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            cloneParameters[i] = (ValueSpecifier)parameters[i].clone();
        }
        clone.setParameters(cloneParameters);
        return clone;
    }
    
}
