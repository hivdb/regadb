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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.DatasetAccessId;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.util.xml.XMLTools;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ImportFromXMLBase extends DefaultHandler{
    protected Patient patient = null;
    protected StringBuffer value = null;
    private DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    
    private Transaction transaction;

    private Map<String, DrugGeneric> genericDrugs;
    private Map<String, DrugCommercial> commercialDrugs;
    private Map<String, Protein> proteins;
    private Map<String, AnalysisType> analysisTypes;
    private Map<String, TherapyMotivation> therapyMotivations;
    private Map<String, Genome> genomes;
    private Map<String, Dataset> datasets = new HashMap<String, Dataset>();
    
    protected StringBuffer log = new StringBuffer();
    public enum SyncMode { Clean, CleanBase, Update, UpdateBase };
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (value == null)
            value = new StringBuffer();
        value.append(new String(ch, start, length));
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
    
    protected boolean parseboolean(String value){
        return value != null ? Boolean.parseBoolean(value) : null;
    }
    
    protected Long parseLong(String value){
        return value != null ? Long.parseLong(value) : null;
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
    
    protected Long nullValueLong(){
        return null;
    }
    
    protected boolean nullValueboolean(){
        return false;
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
    protected TherapyMotivation resolveTherapyMotivation(String value) throws SAXException {
        TherapyMotivation result = therapyMotivations.get(value.toUpperCase());
        if (result == null)
            throw new SAXException(new ImportException("Could not resolve therapy motivation: '" + value + "'"));
        else
            return result;
    }
    protected Genome resolveGenome(String value) throws SAXException {
        Genome result = genomes.get(value.toUpperCase());
        if (result == null)
            throw new SAXException(new ImportException("Could not resolve genome: '" + value + "'"));
        else
            return result;
    }
    protected Dataset resolveDataset(String value) throws SAXException {
        Dataset result = datasets.get(value.toUpperCase());
        
        if(result == null){
            result = transaction.getDataset(value);
            
            if(result == null){
                result = new Dataset(transaction.getSettingsUser(), value, new Date());
                result.setRevision(1);
                
                transaction.getSettingsUser().getDatasetAccesses().add( new DatasetAccess(
                                                                            new DatasetAccessId(transaction.getSettingsUser(), result),
                                                                            Privileges.READWRITE.getValue(),
                                                                            transaction.getSettingsUser().getUid())
                                                                        );
                transaction.save(result);
            }
            
            datasets.put(value, result);
        }
        return result;
    }
    
    protected void addDataset(Patient patient, Dataset dataset){
        patient.setSourceDataset(dataset,transaction);
    }

    public void loadDatabaseObjects(Transaction t) {
        transaction = t;
        
        genericDrugs = new TreeMap<String, DrugGeneric>();
        if (t!=null) {
            for (DrugGeneric d : t.getGenericDrugs()) {
                genericDrugs.put(d.getGenericId().toUpperCase(), d);
            }
        }

        commercialDrugs = new TreeMap<String, DrugCommercial>();
        if (t!=null) {
            for (DrugCommercial d : t.getCommercialDrugs()) {
                commercialDrugs.put(d.getName().toUpperCase(), d);
            }
        }
        
        proteins = new TreeMap<String, Protein>();
        if (t!=null) {
            for (Protein p : t.getProteins()) {
                proteins.put(p.getAbbreviation().toUpperCase(), p);
            }
        }

        analysisTypes = new TreeMap<String, AnalysisType>();
        if (t!=null) {
            for (AnalysisType a : t.getAnalysisTypes()) {
                analysisTypes.put(a.getType().toUpperCase(), a);
            }
        }

        therapyMotivations = new TreeMap<String, TherapyMotivation>();
        if (t!=null) {
            for (TherapyMotivation a : t.getTherapyMotivations()) {
                therapyMotivations.put(a.getValue().toUpperCase(), a);
            }
        }
        
        genomes = new TreeMap<String, Genome>();
        if(t!=null){
            for(Genome g : t.getGenomes()){
                genomes.put(g.getOrganismName(), g);
            }
        }
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
        return o1 == o2 || (o1 != null && o2 != null && o1.getAbbreviation().equals(o2.getAbbreviation()));
    }

    protected boolean equals(DrugCommercial o1, DrugCommercial o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.getName().equals(o2.getName()));
    }

    protected boolean equals(TherapyMotivation o1, TherapyMotivation o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.getValue().equals(o2.getValue()));
    }

    protected boolean equals(Double o1, Double o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }

    protected boolean equals(DrugGeneric o1, DrugGeneric o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.getGenericId().equals(o2.getGenericId()));
    }

    protected boolean equals(Boolean o1, Boolean o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }

    protected boolean equals(AnalysisType analysisType, AnalysisType analysisType2) {
        return analysisType.getType().equals(analysisType2.getType());
    }

    protected boolean equals(Integer a, Integer b) {
        return a == b || (a != null && a.equals(b));
    }
    
    protected boolean equals(byte[] data, byte[] data2) {
        return Arrays.equals(data, data2);
    }
    
    protected boolean equals(Long a, Long b){
        return a == b || (a != null && a.equals(b));
    }
    
    protected boolean equals(Genome a, Genome b){
        return (a == b) || (a != null && b != null && a.getOrganismName().equals(b.getOrganismName()));
    }

    public StringBuffer getLog() 
    {
        return log;
    }

    public Map<String, AnalysisType> getAnalysisTypes() {
        return analysisTypes;
    }
}
