package net.sf.regadb.io.db.mateibals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.xls.ExcelTable;

public class MateibalsUtils {
	public static Attribute nameA = new Attribute();
	public static Attribute countyA = new Attribute(StandardObjects.getNominalValueType(), StandardObjects.getDemographicsAttributeGroup(),"County", new HashSet<AttributeNominalValue>());
	public static Attribute residenceA = new Attribute(StandardObjects.getNominalValueType(), StandardObjects.getDemographicsAttributeGroup(),"Residence", new HashSet<AttributeNominalValue>());
	
	public static Test viCommentT;
	public static Test viSetNoT;
	public static Test viEpidT;
	
	static {
		nameA.setValueType(new ValueType("string"));
		nameA.setName("Name");
		nameA.setAttributeGroup(StandardObjects.getPersonalAttributeGroup());
		
		viCommentT = createViTest("Comment");
		viSetNoT = createViTest("Set no");
		viEpidT = createViTest("Epidem info");
	}
	
	private static Test createViTest(String name) {
		TestType type = 
			new TestType(StandardObjects.getViralIsolateAnalysisTestObject(), name);
		type.setValueType(StandardObjects.getStringValueType());
		return new Test(type, name);
	}
	
	public static Date createDate(SimpleDateFormat df, String date) {
		try {
			return df.parse(date);
		} catch (ParseException pe) {
			pe.printStackTrace();
			return null;
		}
	}
	
	public static Date parseDate(SimpleDateFormat df, String date, Date lower, int row, String colName) {
		String error = "Illegal date: row=\"" + row + "\" col=\"" + colName + "\" date=\""+date + "\""; 
		
		if (!date.equals("")) {
			try {
				Date birthDate = df.parse(date);
				
				if (birthDate.before(lower) || birthDate.after(new Date())) {
					System.err.println(error);
					return null;
				} else {
					return birthDate;
				}
			} catch (ParseException e) {
				System.err.println(error);
				return null;
			}
		} else {
			return null;
		}
	}
	
    public static String parseViralLoad(int row, String value_orig) {
        String value = value_orig;
        
        if(value==null || "".equals(value)) {
            return null;
        }
        
        String val = null;
        
        value = value.replace(",", "");
        value = value.replace(".", "");
        
        if(Character.isDigit(value.charAt(0)) || value.charAt(0) == '-') {
            value = "=" + value;
        }
        if(value.charAt(0)=='>' || value.charAt(0)=='<' || value.charAt(0)=='=') {
            try {
                Double.parseDouble(value.substring(1));
                val = value;
            } catch(NumberFormatException nfe) {
                System.err.println("Cannot parse Viral Load value at row=\"" + row + "\" value=\"" + value + "\"");
                return null;
            }
        }
        
        return val;
    }
    
	public static String getValue(ExcelTable table, int row, String columnName) {
		int col = -1;
		for (int c = 0; c < table.columnCount(); c++) {
			if (table.getCell(0, c).trim().equals(columnName)) {
				col = c;
				break;
			}
		}
		
		if (col == -1)
			System.err.println("No column named \"" + columnName + "\"");
		
		return table.getCell(row, col).trim();
	}
	
	public static void addTestResult(Patient p, Test t, String value, Date d) {
        TestResult tr = p.createTestResult(t);
        tr.setValue(value);
        tr.setTestDate(d);
	}
	
	public static void handleANV(Patient p, Attribute a, String value) {
		value = value.trim();
		if (value.equals(""))
			return;

		AttributeNominalValue selectedAnv = null;

		for (AttributeNominalValue anv : a.getAttributeNominalValues()) {
			if (anv.getValue().equals(value))
				selectedAnv = anv;
		}

		if (selectedAnv == null) {
			selectedAnv = new AttributeNominalValue(a, value);
			a.getAttributeNominalValues().add(selectedAnv);
		}
		
		boolean addANV = true;
		
		for (PatientAttributeValue pav : p.getPatientAttributeValues()) {
			if (pav.getAttribute().getName().equals(a.getName())) {
				addANV = false;
			}
		}
		
		if (addANV)
			p.createPatientAttributeValue(a).setAttributeNominalValue(selectedAnv);
	}
}
