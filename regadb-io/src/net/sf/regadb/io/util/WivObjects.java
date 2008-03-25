package net.sf.regadb.io.util;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.ValueType;

public class WivObjects {
    private static Map<String,Attribute> attributes_ = new HashMap<String,Attribute>();
    private static Map<String,AttributeNominalValue> nominalValues_ = new HashMap<String,AttributeNominalValue>();
    private static AttributeGroup wivAttributeGroup_;
    
    static{
        initWivAttributes();
    }

    private static void initWivAttributes(){
        wivAttributeGroup_ = new AttributeGroup("WIV");
        
        ValueType num = StandardObjects.getNumberValueType();
        ValueType lim = StandardObjects.getLimitedNumberValueType();
        ValueType str = StandardObjects.getStringValueType();
        ValueType nom = StandardObjects.getNominalValueType();
        ValueType dat = StandardObjects.getDateValueType();
        
        //createAttribute(ag,"WIV: SEX",nom,new String[]{"M","F","?"},session);
        //createAttribute(ag,"WIV: HIVTYPE",nom,new String[]{"1","2"},session);
        createAttribute("WIV: REF_LABO",str,null);
        createAttribute("WIV: NATION",nom,null);
        createAttribute("WIV: COUNTRY",nom,null);
        //createAttribute("WIV: ORIGIN",nom,null);
        createAttribute("WIV: RESID_B",num,null);
        createAttribute("WIV: ARRIVAL_B",dat,null);
        createAttribute("WIV: SEXCONTACT",nom,new String[]{"A: Other sex","B: Same sex","C: Both","D: None"});
        createAttribute("WIV: SEXPARTNER",nom,new String[]{"A: HIV+", "B: IVDU", "C: Nationality", "D: Prostitution", "E: Unknown by patient"});
        createAttribute("WIV: NATPARTNER",nom,null);
        createAttribute("WIV: BLOODBORNE",nom,new String[]{"A: IVDU", "B: Hemophilia", "C: Transfusion", "D: None"});
        createAttribute("WIV: YEARTRANSF",dat,null);
        createAttribute("WIV: TRANCOUNTR",nom,null);
        createAttribute("WIV: CHILD",nom,new String[]{"A: Yes","B: No"});
        createAttribute("WIV: PROFRISK",nom,new String[]{"M: Medical","P: Sexual","O: Other"});
        createAttribute("WIV: PROBYEAR",dat,null);
        createAttribute("WIV: PROBCOUNTR",nom,null);
        createAttribute("WIV: STAD_CLIN",nom,new String[]{"A: Recent infection (< 6 months)","B: Asymptomatic carrier", "C: Symptomatic carrier"});
        createAttribute("WIV: REASONTEST",nom,new String[]{"A: Patient's request","B: Clinical reasons","C: Preoperative","D: Prenatal","E: Administrative reasons","F: Other"});
        createAttribute("WIV: FORM_OUT",dat,null);
        createAttribute("WIV: FORM_IN",dat,null);
        //createAttribute(ag,"WIV: LABO",nom,new String[]{"HSP","ITG","KUL","RUG","UCL","ULB","VUB"});
    }

    private static void createAttribute(String name, ValueType vt, String[] anvs){
        Attribute a = new Attribute();
        a.setAttributeGroup(wivAttributeGroup_);
        a.setName(name);
        a.setValueType(vt);
        
        attributes_.put(name, a);
    
        if(anvs != null){
            for(String value : anvs){
                createAttributeNominalValue(a,value);
            }
            createAttributeNominalValue(a,"U: Unknown");
        }
    }
    
    private static void createAttributeNominalValue(Attribute a, String value){
        AttributeNominalValue anv = new AttributeNominalValue();
        anv.setAttribute(a);
        anv.setValue(value);
        nominalValues_.put(getAttributeNominalValueKey(a, value), anv);
    }
    
    private static String getAttributeNominalValueKey(Attribute a, String nominalValue){
        return a.getName() +";"+ nominalValue;
    }
    
    private static AttributeNominalValue getANVFromAbbrev(Attribute attribute, char abbrev) {
        for(AttributeNominalValue anv : attribute.getAttributeNominalValues()) {
            if(anv.getValue().startsWith(abbrev+":")) {
                return anv;
            }
        }
        return null;
    }
    
    public static AttributeGroup getWivAttributeGroup(){
        return wivAttributeGroup_;
    }
    
    public static Attribute getAttribute(String s){
        return attributes_.get(s);
    }
    
    public static AttributeNominalValue getAttributeNominalValue(Attribute a, String nominalValue){
        return nominalValues_.get(getAttributeNominalValueKey(a, nominalValue));
    }
    
    public static PatientAttributeValue createPatientAttributeNominalValue(String attributeName, char nominalAbbrev, Patient p){
        Attribute attribute = attributes_.get(attributeName);
        if(attribute==null)
            return null;
        
        AttributeNominalValue anv = getANVFromAbbrev(attribute, nominalAbbrev);
        if(anv==null) {
            return null;
        }
        
        PatientAttributeValue pav = p.createPatientAttributeValue(attribute);
        pav.setAttributeNominalValue(anv);
        
        return pav;
    }
    
    public static PatientAttributeValue createPatientAttributeValue(String attributeName, String value, Patient p){
        Attribute attribute = attributes_.get(attributeName);
        if(attribute==null)
            return null;
        
        PatientAttributeValue pav = p.createPatientAttributeValue(attribute);
        pav.setValue(value);
        
        return pav;
    }
}