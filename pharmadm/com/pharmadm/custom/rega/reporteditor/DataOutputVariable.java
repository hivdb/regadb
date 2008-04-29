
/** Java class "OutputVariable.java" generated from Poseidon for UML and copy-pasted
 *  to this class "DataOutputVariable.java" by Kristof from Belleghem. 
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.*;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;

/**
 * <p>
 * Represents a value that gets calculated by a ValueSpecifier in a DataGroup,
 * and of which the result can be reused elsewhere in the ReportFormat. Reuse 
 * occurs by associating a DataOutputVariable with a compatible DataInputVariable.
 * </p>
 * <p>
 * A DataOutputVariable has two names. It has a non-unique, descriptive,
 * formal name. From this formal name, a ReportFormat-wide unique name is derived.
 * The unique name can be used by the user to distinguish the different
 * DataOutputVariables in a ReportFormat.
 * </p>
 * <p>
 * A DataOutputVariable also has a ValueSpecifier. A ValueSpecifier determines
 * a unique procedure for calculating the value of the DataOutputVariable in any
 * Report context.
 * </p>
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  formalName
 *  uniqueName
 *  specifier
 * </p> 
 */
public class DataOutputVariable extends com.pharmadm.custom.rega.queryeditor.Variable implements Valuable, DataGroupWord, Cloneable {

  // Clearly there is a strong connection between this class and com.pharmadm.custom.rega.queryeditor.OutputVariable.
  // (see also the similarities in gui.J(Data)OutputVariableConfigurer)
  // Eventually, they may become subclasses of a common GeneralizedOutputVariable class.
    
  ///////////////////////////////////////
  // attributes

    public DataOutputVariable(com.pharmadm.custom.rega.queryeditor.VariableType type, String formalName) {
        super(type);
        this.formalName = formalName;
    }

    /** For xml-encoding purposes only */
    public DataOutputVariable() {
    }
    
/**
 * <p>
 * Represents a non-unique name of the variable that has some meaning to
 * the user. For example, a DataOutputVariable of the tye 'Time' could be
 * referred to as variable 'T'. A formal name may not:<ul>
 * <li>be null
 * <li>have a zero length
 * <li>contain other characters than letters (small or caps), digits, dashes and underscores
 * <li>have a last character that is a digit or a dash
 * </ul>
 * </p>
 * <p>
 * The formal name is persistent.
 * </p>
 * 
 */
    private String formalName; 

/**
 * <p>
 * Represents a unique name of the variable that can is unique for an
 * entire report format. The uniqueName is derived from the formal name, 
 * but may be longer to allow for uniqueness.
 * </p>
 * <p>
 * For example, two DataOutputVariables in a report format, both of the type 
 * 'Time' and with formal name 'T', could have uniqueNames 'T1' and 'T2'.
 * </p>
 * <p>
 * The unique name is transient, for it is always possible to derive a
 * unique name from the formal name. Although re-generation would not
 * nescessarily always result in the same unique name, it can still be
 * used to distinguish variables from each other.
 * </p>
 * 
 */
    private transient String uniqueName; 
    
/**
 * <p>
 * 
 * </p>
 */
    private ValueSpecifier specifier;

    private List<Property> properties = null;
    
    public String getFormalName() {
        return formalName;
    }
    
    /** For xml-encoding purposes only */
    public void setFormalName(String formalName) {
        this.formalName = formalName;
    }
    
    public String getUniqueName() {
        return uniqueName;
    }
    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }
    
    
    
   ///////////////////////////////////////
   // associations



   ///////////////////////////////////////
   // access methods for associations

    public ValueSpecifier getSpecifier() {
        return specifier;
    }
    
    /* public for xml-encoding purposes only */
    public void setSpecifier(ValueSpecifier specifier) { 
        this.specifier = specifier;
        if (specifier.getValueType() != getValueType()) {
            System.err.println("Warning : specifier value type is incompatible with variable value type.");
        }
    }
    
    public Class getValueType() {
        return getVariableType().getValueType();
    }
    
    public boolean hasDomainClassType() {
        return getValueType().getName().startsWith("com.pharmadm.custom.rega.domainclasses");
    }
    
    public boolean consistsOfSingleObjectListVariable() {
        return (getSpecifier() != null && getSpecifier() instanceof ObjectListVariable);
    }
    
    public Property getProperty(String name) {
        Iterator<Property> iter = getProperties().iterator();
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            if (prop.getName().equals(name)) {
                return prop;
            }
        }
        return null;
    }
    
    public List<Property> getProperties() {
        if (hasDomainClassType()) {
            if (properties == null) {
                properties = Property.getDomainProperties(this);
            }
            return properties;
        } else {
            return new ArrayList<Property>();
        }
    }
    
    public String getHumanStringValue(OutputReportSeeder context) {
        if (consistsOfSingleObjectListVariable()) {
            // this is a hack for user-friendliness; it is not supposed to be
            // problematic/confusing as long as ObjectListVariables remain invisible 
            return ((ObjectListVariable)specifier).getHumanStringValue(context);
        } else if ((getUniqueName() == null) || (getUniqueName().equals(""))) {
            return formalName;
        } else {
            return getUniqueName();
        }
    } 
    
    public String getHumanStringValue() {
        if (consistsOfSingleObjectListVariable()) {
            // this is a hack for user-friendliness; it is not supposed to be
            // problematic/confusing as long as ObjectListVariables remain invisible 
            return ((ObjectListVariable)specifier).getHumanStringValue();
        } else if ((uniqueName == null) || (uniqueName.equals(""))) {
            return formalName;
        } else {
            return uniqueName;
        }
    }
 
    // for presentation in JComponents
    public String toString() {
        return getHumanStringValue();
    }

    public void calculateValue(DataRow row) {
        row.setValue(this, getSpecifier().getValue(row));
        //System.err.println("Set value for " + this + " (" + this.getClass() + "): " + getSpecifier().getValue(row));
    }

    public Object getStoredValue(DataRow row) {
        return row.getValue(this);
    }
    
    /* Implementing Valuable */
    public final Object getValue(DataRow row) {
        return getStoredValue(row);
    }
    
    protected DataOutputVariable cloneInContext(Map originalToCloneMap) throws CloneNotSupportedException {
        DataOutputVariable clone = (DataOutputVariable)super.clone();
        if (getSpecifier() != null) {
            clone.setSpecifier((ValueSpecifier)getSpecifier().cloneInContext(originalToCloneMap));
        }
        clone.properties = null; // just reset this, new properties will be made as needed
        return clone; 
    }
} // end DataOutputVariable
