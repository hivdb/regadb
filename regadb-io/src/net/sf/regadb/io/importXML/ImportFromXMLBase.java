/*
 * Created on May 11, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.importXML;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.util.xml.XMLTools;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ImportFromXMLBase extends DefaultHandler{
    protected Patient patient = null;
    protected String value = null;
    private DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private Map<String, DrugGeneric> genericDrugs;
    private Map<String, DrugCommercial> commercialDrugs;
    private Map<String, Protein> proteins;
    private Map<String, AnalysisType> analysisTypes;
    
    protected StringBuffer log = new StringBuffer();
    public enum SyncMode { Clean, Update };
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (value == null)
            value = new String(ch, start, length);
        else
            value += new String(ch, start, length);
    }

    protected Date parseDate(String value) throws SAXException {
        try {
            return dateFormatter.parse(value);
        } catch (ParseException e) {
            throw new SAXException(new ImportException("Cannot parse date: " + value));
        }
    }

    protected String parseString(String value) {
        return value != null ? value.trim() : null;
    }

    protected Boolean parseBoolean(String value) {
        return value != null ? Boolean.parseBoolean(value) : null;
    }

    protected Double parseDouble(String value) {
        return value != null ? Double.parseDouble(value) : null;
    }

    protected short parseshort(String value) {
        return value != null ? Short.parseShort(value) : 0;
    }

    protected Integer parseInteger(String value) {
        return value != null ? Integer.parseInt(value) : null;
    }

    protected byte[] parsebyteArray(String value) throws SAXException{
        try {
            return XMLTools.base64Decoding(value);
        } catch (IOException e) {
            throw new SAXException(new ImportException("Cannot parse byte []: " + value));
        }
    }
    
    protected Integer nullValueInteger() {
        return null;
    }

    protected Boolean nullValueBoolean() {
        return null;
    }

    protected Double nullValueDouble() {
        return null;
    }

    protected short nullValueshort() {
        return 0;
    }

    protected Date nullValueDate() {
        return null;
    }

    protected String nullValueString() {
        return null;
    }
    
    protected byte[] nullValuebyteArray() {
        return null;
    }

    protected DrugGeneric resolveDrugGeneric(String value) throws SAXException {
        DrugGeneric result = genericDrugs.get(value.toUpperCase());
        if (result == null)
            throw new SAXException(new ImportException("Could not resolve generic drug: '" + value + "'"));
        else
            return result;
    }

    protected Protein resolveProtein(String value) throws SAXException {
        Protein result = proteins.get(value.toUpperCase());
        if (result == null)
            throw new SAXException(new ImportException("Could not resolve protein: '" + value + "'"));
        else
            return result;
    }

    protected DrugCommercial resolveDrugCommercial(String value) throws SAXException {
        DrugCommercial result = commercialDrugs.get(value.toUpperCase());
        if (result == null)
            throw new SAXException(new ImportException("Could not resolve commercial drug: '" + value + "'"));
        else
            return result;
    }
    protected AnalysisType resolveAnalysisType(String value) throws SAXException {
        AnalysisType result = analysisTypes.get(value.toUpperCase());
        if (result == null)
            throw new SAXException(new ImportException("Could not resolve analysis type: '" + value + "'"));
        else
            return result;
    }

    public void loadDatabaseObjects(Transaction t) {
        genericDrugs = new TreeMap<String, DrugGeneric>();
        if(t!=null)
        {
        for (DrugGeneric d : t.getGenericDrugs()) {
            genericDrugs.put(d.getGenericId().toUpperCase(), d);
        }
        }

        commercialDrugs = new TreeMap<String, DrugCommercial>();
        if(t!=null)
        {
        for (DrugCommercial d : t.getCommercialDrugs()) {
            commercialDrugs.put(d.getName().toUpperCase(), d);
        }
        }
        

        proteins = new TreeMap<String, Protein>();
        if(t!=null)
        {
        for (Protein p : t.getProteins()) {
            proteins.put(p.getAbbreviation().toUpperCase(), p);
        }
        }

        analysisTypes = new TreeMap<String, AnalysisType>();
        if(t!=null)
        {
        for (AnalysisType a : t.getAnalysisTypes()) {
            analysisTypes.put(a.getType().toUpperCase(), a);
        }
        }
    }    

    protected Patient dbFindPatient(Transaction t, Patient o) {
        return t.getPatient(o.getSourceDataset(), o.getPatientId());
    }

    protected Attribute dbFindAttribute(Transaction t, Attribute o) {
        return t.getAttribute(o.getName());
    }

    protected Test dbFindTest(Transaction t, Test o) {
        return t.getTest(o.getDescription());
    }

    protected TestType dbFindTestType(Transaction t, TestType o) {
        return t.getTestType(o.getDescription());
    }

    protected DrugGeneric dbFindDrugGeneric(Transaction t, DrugGeneric o) {
        return t.getGenericDrug(o.getGenericId());
    }

    protected DrugCommercial dbFindDrugCommercial(Transaction t, DrugCommercial o) {
        return t.getCommercialDrug(o.getName());
    }

    protected boolean equals(Date o1, Date o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }

    protected boolean equals(String o1, String o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }

    protected boolean equals(short o1, short o2) {
        return o1 == o2;
    }

    protected boolean equals(Protein o1, Protein o2) {
        return o1 == o2;
    }

    protected boolean equals(DrugCommercial o1, DrugCommercial o2) {
        return o1 == o2;
    }

    protected boolean equals(Double o1, Double o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }

    protected boolean equals(DrugGeneric o1, DrugGeneric o2) {
        return o1 == o2;
    }

    protected boolean equals(Boolean o1, Boolean o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }

    protected boolean equals(AnalysisType analysisType, AnalysisType analysisType2) {
        return analysisType == analysisType2;
    }

    protected boolean equals(Integer revision, Integer revision2) {
        return revision.equals(revision2);
    }
    
    protected boolean equals(byte[] data, byte[] data2) {
        return Arrays.equals(data, data2);
    }

    protected void sync(Transaction t, DrugGeneric o, DrugGeneric dbo, boolean simulate) {
        // TODO Auto-generated method stub
        
    }

    protected void sync(Transaction t, DrugCommercial o, DrugCommercial dbo, boolean simulate) {
        // TODO Auto-generated method stub
        
    }

    public StringBuffer getLog() 
    {
        return log;
    }
}
