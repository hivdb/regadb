package net.sf.regadb.io.db.uzbrussel;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestType;
import net.sf.regadb.io.util.StandardObjects;

public class Items {
    private static TestType hivTherapyAdherence;
    private static Test generichivTherapyAdherence;
    
    private static Attribute patCodeAttribute = new Attribute("PatCode");
    
    private static AttributeGroup regadbAttributeGroup = new AttributeGroup("RegaDB");

    
    static {
        hivTherapyAdherence = new TestType(StandardObjects.getPatientTestObject(), "HIV Therapy Adherence");
        hivTherapyAdherence.setValueType(StandardObjects.getNominalValueType());
        hivTherapyAdherence.getTestNominalValues().add(new TestNominalValue(hivTherapyAdherence, "Good"));
        hivTherapyAdherence.getTestNominalValues().add(new TestNominalValue(hivTherapyAdherence, "Moderate"));
        hivTherapyAdherence.getTestNominalValues().add(new TestNominalValue(hivTherapyAdherence, "Bad"));
        generichivTherapyAdherence = new Test(hivTherapyAdherence, "HIV Therapy Adherence (generic)");
        
        patCodeAttribute.setAttributeGroup(regadbAttributeGroup);
        patCodeAttribute.setValueType(StandardObjects.getStringValueType());
    }

    public static TestType getHivTherapyAdherence() {
        return hivTherapyAdherence;
    }

    public static Test getGenerichivTherapyAdherence() {
        return generichivTherapyAdherence;
    }

	public static Attribute getPatCodeAttribute() {
		return patCodeAttribute;
	}

	public static AttributeGroup getRegadbAttributeGroup() {
		return regadbAttributeGroup;
	}
}
