package net.sf.regadb.io.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public class WivObjects {
    private static Map<String,Attribute> attributes_ = new HashMap<String,Attribute>();
    private static Map<String,AttributeNominalValue> nominalValues_ = new HashMap<String,AttributeNominalValue>();
    private static AttributeGroup wivAttributeGroup_;
    
    private static Test genericwivConfirmation_;
    
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

        List<String> countryCodes = getCountryCodes();
        Attribute a;
        
        createAttribute("REF_LABO",str);
        
        createAttribute("DATE_TEST",dat);
        
        a = createAttribute("NATION",nom);
        createAttributeNominalValues(a, countryCodes);

        a = createAttribute("COUNTRY",nom);
        createAttributeNominalValues(a, countryCodes);
        
        a = createAttribute("ORIGIN",nom);
        createAttributeNominalValues(a, countryCodes);
        
        createAttribute("RESID_B",num);
        createAttribute("ARRIVAL_B",dat);
        createAttribute("SEXCONTACT",nom,new String[]{"A: Other sex","B: Same sex","C: Both","D: None"});
        createAttribute("SEXPARTNER",nom,new String[]{"A: HIV+", "B: IVDU", "C: Nationality", "D: Prostitution", "E: Unknown by patient"});
        
        a = createAttribute("NATPARTNER",nom);
        createAttributeNominalValues(a, countryCodes);
        
        createAttribute("BLOODBORNE",nom,new String[]{"A: IVDU", "B: Hemophilia", "C: Transfusion", "D: None"});
        createAttribute("YEARTRANSF",dat);
        
        a = createAttribute("TRANCOUNTR",nom);
        createAttributeNominalValues(a, countryCodes);
        
        createAttribute("CHILD",nom,new String[]{"A: Yes","B: No"});
        createAttribute("PROFRISK",nom,new String[]{"M: Medical","P: Sexual","O: Other", "N: No risk"});
        createAttribute("PROBYEAR",dat);
        
        a = createAttribute("PROBCOUNTR",nom);
        createAttributeNominalValues(a, countryCodes);
        
        createAttribute("STAD_CLIN",nom,new String[]{"A: Recent infection (< 6 months)","B: Asymptomatic carrier", "C: Symptomatic carrier"});
        createAttribute("REASONTEST",nom,new String[]{"A: Patient's request","B: Clinical reasons","C: Preoperative","D: Prenatal","E: Administrative reasons","F: Other"});
        createAttribute("FORM_OUT",dat);
        createAttribute("FORM_IN",dat);
        //createAttribute(ag,"LABO",nom,new String[]{"HSP","ITG","KUL","RUG","UCL","ULB","VUB"});
        createAttribute("FOLLOW-UP",nom,new String[]{
        		"1: ARC of the same institution as ARL",
        		"2: ARC of another institution",
        		"3: Outside of ARC"}, false);
        
        TestType wivConfirmation = new TestType(StandardObjects.getPatientObject(), "WIV HIV Confirmation");
        wivConfirmation.setValueType(StandardObjects.getNominalValueType());
        wivConfirmation.getTestNominalValues().add(new TestNominalValue(wivConfirmation, "HIV 1"));
        wivConfirmation.getTestNominalValues().add(new TestNominalValue(wivConfirmation, "HIV 2"));
        wivConfirmation.getTestNominalValues().add(new TestNominalValue(wivConfirmation, "HIV 1/2 Coinfection"));
        wivConfirmation.getTestNominalValues().add(new TestNominalValue(wivConfirmation, "HIV Undetermined"));
        wivConfirmation.getTestNominalValues().add(new TestNominalValue(wivConfirmation, "Not performed"));
        genericwivConfirmation_ = new Test(wivConfirmation, "WIV HIV Confirmation (generic)");
    }

    
    private static Attribute createAttribute(String name, ValueType vt){
        Attribute a = new Attribute();
        a.setAttributeGroup(wivAttributeGroup_);
        a.setName(name);
        a.setValueType(vt);
        
        attributes_.put(name, a);
        return a;
    }
    
    private static Attribute createAttribute(String name, ValueType vt, String[] anvs, boolean unknown){
        Attribute a = createAttribute(name, vt);
    
        if(anvs != null){
            for(String value : anvs){
                createAttributeNominalValue(a,value);
            }
            if(unknown)
            	createAttributeNominalValue(a,"U: Unknown");
        }
        
        return a;
    }
    
    private static Attribute createAttribute(String name, ValueType vt, String[] anvs) {
    	return createAttribute(name, vt, anvs, true);
    }
    
    private static void createAttributeNominalValue(Attribute a, String value){
        AttributeNominalValue anv = new AttributeNominalValue();
        anv.setAttribute(a);
        anv.setValue(value);
        a.getAttributeNominalValues().add(anv);
        nominalValues_.put(getAttributeNominalValueKey(a, value), anv);
    }
    
    private static String getAttributeNominalValueKey(Attribute a, String nominalValue){
        return a.getName() +";"+ nominalValue;
    }
    
    public static AttributeNominalValue getANVFromAbbrev(Attribute attribute, String abbrev) {
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
        
        AttributeNominalValue anv = getANVFromAbbrev(attribute, nominalAbbrev+"");
        if(anv==null) {
            return null;
        }
        
        PatientAttributeValue pav = p.createPatientAttributeValue(attribute);
        pav.setAttributeNominalValue(anv);
        
        return pav;
    }
    
    public static PatientAttributeValue createCountryPANV(String attributeName, String abbrev, Patient p){
    	if(abbrev.equals("?")) {
    		abbrev = "999";
    	}
        Attribute attribute = attributes_.get(attributeName);
        if(attribute==null)
            return null;
        
        AttributeNominalValue anv = getANVFromAbbrev(attribute, abbrev);
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
    
    private static void createAttributeNominalValues(Attribute a, List<String> values){
        for(String value : values){
            createAttributeNominalValue(a, value);
        }
    }
    
    private static List<String> getCountryCodes(){
//        PackageUtils.getDirectoryPath(packageName, projectName);
//        File countries = new File("");
//        
//        List<String> l = new ArrayList<String>();
//        try{
//            BufferedReader fr = new BufferedReader(new FileReader(countries));
//            
//            String line;
//            int comma;
//            line = fr.readLine();   //skip header
//            while((line = fr.readLine()) != null){
//                comma = line.indexOf(',');
//                if(comma != -1){
//                    String code = line.substring(0,comma);
//                    String country = line.substring(comma+1,line.length()).replace("\"", "");
//                    
//                    l.add(code +": "+ country);
//                }
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        
        List<String> l = new ArrayList<String>();
        
        l.add(getFormattedCountryCode("212","Afghanistan"));
        l.add(getFormattedCountryCode("125","Albania"));
        l.add(getFormattedCountryCode("352","Algeria"));
        l.add(getFormattedCountryCode("130","Andorra"));
        l.add(getFormattedCountryCode("395","Angola"));
        l.add(getFormattedCountryCode("441","Antigua and Barbuda"));
        l.add(getFormattedCountryCode("415","Argentina"));
        l.add(getFormattedCountryCode("252","Armenia"));
        l.add(getFormattedCountryCode("501","Australia"));
        l.add(getFormattedCountryCode("110","Austria"));
        l.add(getFormattedCountryCode("253","Azerbaijan"));
        l.add(getFormattedCountryCode("436","Bahamas, The"));
        l.add(getFormattedCountryCode("249","Bahrain"));
        l.add(getFormattedCountryCode("246","Bangladesh"));
        l.add(getFormattedCountryCode("434","Barbados"));
        l.add(getFormattedCountryCode("148","Belarus"));
        l.add(getFormattedCountryCode("131","Belgium"));
        l.add(getFormattedCountryCode("429","Belize"));
        l.add(getFormattedCountryCode("327","Benin"));
        l.add(getFormattedCountryCode("214","Bhutan"));
        l.add(getFormattedCountryCode("418","Bolivia"));
        l.add(getFormattedCountryCode("118","Bosnia and Herzegovina"));
        l.add(getFormattedCountryCode("347","Botswana"));
        l.add(getFormattedCountryCode("416","Brazil"));
        l.add(getFormattedCountryCode("308","British Indian Ocean Territory"));
        l.add(getFormattedCountryCode("225","Brunei"));
        l.add(getFormattedCountryCode("111","Bulgaria"));
        l.add(getFormattedCountryCode("331","Burkina Faso"));
        l.add(getFormattedCountryCode("321","Burundi"));
        l.add(getFormattedCountryCode("234","Cambodia"));
        l.add(getFormattedCountryCode("322","Cameroon"));
        l.add(getFormattedCountryCode("401","Canada"));
        l.add(getFormattedCountryCode("396","Cape Verde"));
        l.add(getFormattedCountryCode("323","Central African Republic"));
        l.add(getFormattedCountryCode("344","Chad"));
        l.add(getFormattedCountryCode("417","Chile"));
        l.add(getFormattedCountryCode("216","China, People's Republic of"));
        l.add(getFormattedCountryCode("236","China, Republic of (Taiwan)"));
        l.add(getFormattedCountryCode("419","Colombia"));
        l.add(getFormattedCountryCode("397","Comoros"));
        l.add(getFormattedCountryCode("324","Congo, Democratic Republic of the (Congo - Kinshasa)"));
        l.add(getFormattedCountryCode("312","Congo, Democratic Republic of the (Congo - Kinshasa)"));
        l.add(getFormattedCountryCode("406","Costa Rica"));
        l.add(getFormattedCountryCode("326","Cote d'Ivoire (Ivory Coast)"));
        l.add(getFormattedCountryCode("119","Croatia"));
        l.add(getFormattedCountryCode("407","Cuba"));
        l.add(getFormattedCountryCode("254","Cyprus"));
        l.add(getFormattedCountryCode("116","Czech Republic"));
        l.add(getFormattedCountryCode("101","Denmark"));
        l.add(getFormattedCountryCode("399","Djibouti"));
        l.add(getFormattedCountryCode("438","Dominica"));
        l.add(getFormattedCountryCode("408","Dominican Republic"));
        l.add(getFormattedCountryCode("420","Ecuador"));
        l.add(getFormattedCountryCode("301","Egypt"));
        l.add(getFormattedCountryCode("414","El Salvador"));
        l.add(getFormattedCountryCode("314","Equatorial Guinea"));
        l.add(getFormattedCountryCode("317","Eritrea"));
        l.add(getFormattedCountryCode("106","Estonia"));
        l.add(getFormattedCountryCode("315","Ethiopia"));
        l.add(getFormattedCountryCode("427","Falkland Islands (Islas Malvinas)"));
        l.add(getFormattedCountryCode("508","Fiji"));
        l.add(getFormattedCountryCode("105","Finland"));
        l.add(getFormattedCountryCode("150","France"));
        l.add(getFormattedCountryCode("445","French Guiana"));
        l.add(getFormattedCountryCode("519","French Polynesia"));
        l.add(getFormattedCountryCode("521","French Southern and Antarctic Lands"));
        l.add(getFormattedCountryCode("328","Gabon"));
        l.add(getFormattedCountryCode("304","Gambia, The"));
        l.add(getFormattedCountryCode("255","Georgia"));
        l.add(getFormattedCountryCode("109","Germany"));
        l.add(getFormattedCountryCode("329","Ghana"));
        l.add(getFormattedCountryCode("133","Gibraltar"));
        l.add(getFormattedCountryCode("126","Greece"));
        l.add(getFormattedCountryCode("430","Greenland"));
        l.add(getFormattedCountryCode("435","Grenada"));
        l.add(getFormattedCountryCode("443","Guadeloupe"));
        l.add(getFormattedCountryCode("409","Guatemala"));
        l.add(getFormattedCountryCode("330","Guinea"));
        l.add(getFormattedCountryCode("392","Guinea-Bissau"));
        l.add(getFormattedCountryCode("428","Guyana"));
        l.add(getFormattedCountryCode("410","Haiti"));
        l.add(getFormattedCountryCode("411","Honduras"));
        l.add(getFormattedCountryCode("230","Hong Kong"));
        l.add(getFormattedCountryCode("112","Hungary"));
        l.add(getFormattedCountryCode("102","Iceland"));
        l.add(getFormattedCountryCode("223","India"));
        l.add(getFormattedCountryCode("231","Indonesia"));
        l.add(getFormattedCountryCode("204","Iran"));
        l.add(getFormattedCountryCode("203","Iraq"));
        l.add(getFormattedCountryCode("136","Ireland"));
        l.add(getFormattedCountryCode("207","Israel"));
        l.add(getFormattedCountryCode("127","Italy"));
        l.add(getFormattedCountryCode("426","Jamaica"));
        l.add(getFormattedCountryCode("217","Japan"));
        l.add(getFormattedCountryCode("222","Jordan"));
        l.add(getFormattedCountryCode("256","Kazakhstan"));
        l.add(getFormattedCountryCode("332","Kenya"));
        l.add(getFormattedCountryCode("513","Kiribati"));
        l.add(getFormattedCountryCode("238","Korea, Democratic People's Republic of (North Korea)"));
        l.add(getFormattedCountryCode("239","Korea, Republic of  (South Korea)"));
        l.add(getFormattedCountryCode("240","Kuwait"));
        l.add(getFormattedCountryCode("257","Kyrgyzstan"));
        l.add(getFormattedCountryCode("241","Laos"));
        l.add(getFormattedCountryCode("107","Latvia"));
        l.add(getFormattedCountryCode("205","Lebanon"));
        l.add(getFormattedCountryCode("348","Lesotho"));
        l.add(getFormattedCountryCode("302","Liberia"));
        l.add(getFormattedCountryCode("316","Libya"));
        l.add(getFormattedCountryCode("113","Liechtenstein"));
        l.add(getFormattedCountryCode("108","Lithuania"));
        l.add(getFormattedCountryCode("137","Luxembourg"));
        l.add(getFormattedCountryCode("232","Macau"));
        l.add(getFormattedCountryCode("156","Macedonia"));
        l.add(getFormattedCountryCode("333","Madagascar"));
        l.add(getFormattedCountryCode("334","Malawi"));
        l.add(getFormattedCountryCode("227","Malaysia"));
        l.add(getFormattedCountryCode("229","Maldives"));
        l.add(getFormattedCountryCode("335","Mali"));
        l.add(getFormattedCountryCode("144","Malta"));
        l.add(getFormattedCountryCode("515","Marshall Islands"));
        l.add(getFormattedCountryCode("444","Martinique"));
        l.add(getFormattedCountryCode("336","Mauritania"));
        l.add(getFormattedCountryCode("390","Mauritius"));
        l.add(getFormattedCountryCode("362","Mayotte"));
        l.add(getFormattedCountryCode("405","Mexico"));
        l.add(getFormattedCountryCode("516","Micronesia"));
        l.add(getFormattedCountryCode("151","Moldova"));
        l.add(getFormattedCountryCode("138","Monaco"));
        l.add(getFormattedCountryCode("242","Mongolia"));
        l.add(getFormattedCountryCode("350","Morocco"));
        l.add(getFormattedCountryCode("393","Mozambique"));
        l.add(getFormattedCountryCode("224","Myanmar (Burma)"));
        l.add(getFormattedCountryCode("311","Namibia"));
        l.add(getFormattedCountryCode("507","Nauru"));
        l.add(getFormattedCountryCode("215","Nepal"));
        l.add(getFormattedCountryCode("135","Netherlands"));
        l.add(getFormattedCountryCode("518","New Caledonia"));
        l.add(getFormattedCountryCode("502","New Zealand"));
        l.add(getFormattedCountryCode("412","Nicaragua"));
        l.add(getFormattedCountryCode("337","Niger"));
        l.add(getFormattedCountryCode("338","Nigeria"));
        l.add(getFormattedCountryCode("103","Norway"));
        l.add(getFormattedCountryCode("250","Oman"));
        l.add(getFormattedCountryCode("213","Pakistan"));
        l.add(getFormattedCountryCode("517","Palau"));
        l.add(getFormattedCountryCode("261","Palestinian Territories (Gaza Strip and West Bank)"));
        l.add(getFormattedCountryCode("413","Panama"));
        l.add(getFormattedCountryCode("510","Papua New Guinea"));
        l.add(getFormattedCountryCode("421","Paraguay"));
        l.add(getFormattedCountryCode("422","Peru"));
        l.add(getFormattedCountryCode("220","Philippines"));
        l.add(getFormattedCountryCode("503","Pitcairn Islands"));
        l.add(getFormattedCountryCode("122","Poland"));
        l.add(getFormattedCountryCode("139","Portugal"));
        l.add(getFormattedCountryCode("248","Qatar"));
        l.add(getFormattedCountryCode("361","Reunion"));
        l.add(getFormattedCountryCode("114","Romania"));
        l.add(getFormattedCountryCode("123","Russia"));
        l.add(getFormattedCountryCode("340","Rwanda"));
        l.add(getFormattedCountryCode("306","Saint Helena"));
        l.add(getFormattedCountryCode("442","Saint Kitts and Nevis"));
        l.add(getFormattedCountryCode("439","Saint Lucia"));
        l.add(getFormattedCountryCode("446","Saint Pierre and Miquelon"));
        l.add(getFormattedCountryCode("440","Saint Vincent and the Grenadines"));
        l.add(getFormattedCountryCode("506","Samoa"));
        l.add(getFormattedCountryCode("128","San Marino"));
        l.add(getFormattedCountryCode("394","Sao Tome and Principe"));
        l.add(getFormattedCountryCode("201","Saudi Arabia"));
        l.add(getFormattedCountryCode("341","Senegal"));
        l.add(getFormattedCountryCode("121","Serbia"));
        l.add(getFormattedCountryCode("398","Seychelles"));
        l.add(getFormattedCountryCode("342","Sierra Leone"));
        l.add(getFormattedCountryCode("226","Singapore"));
        l.add(getFormattedCountryCode("117","Slovakia"));
        l.add(getFormattedCountryCode("145","Slovenia"));
        l.add(getFormattedCountryCode("512","Solomon Islands"));
        l.add(getFormattedCountryCode("318","Somalia"));
        l.add(getFormattedCountryCode("303","South Africa"));
        l.add(getFormattedCountryCode("134","Spain"));
        l.add(getFormattedCountryCode("235","Sri Lanka"));
        l.add(getFormattedCountryCode("343","Sudan"));
        l.add(getFormattedCountryCode("437","Suriname"));
        l.add(getFormattedCountryCode("391","Swaziland"));
        l.add(getFormattedCountryCode("104","Sweden"));
        l.add(getFormattedCountryCode("140","Switzerland"));
        l.add(getFormattedCountryCode("206","Syria"));
        l.add(getFormattedCountryCode("259","Tajikistan"));
        l.add(getFormattedCountryCode("309","Tanzania"));
        l.add(getFormattedCountryCode("219","Thailand"));
        l.add(getFormattedCountryCode("345","Togo"));
        l.add(getFormattedCountryCode("509","Tonga"));
        l.add(getFormattedCountryCode("433","Trinidad and Tobago"));
        l.add(getFormattedCountryCode("351","Tunisia"));
        l.add(getFormattedCountryCode("208","Turkey"));
        l.add(getFormattedCountryCode("260","Turkmenistan"));
        l.add(getFormattedCountryCode("511","Tuvalu"));
        l.add(getFormattedCountryCode("339","Uganda"));
        l.add(getFormattedCountryCode("155","Ukraine"));
        l.add(getFormattedCountryCode("247","United Arab Emirates"));
        l.add(getFormattedCountryCode("132","United Kingdom"));
        l.add(getFormattedCountryCode("404","United States"));
        l.add(getFormattedCountryCode("423","Uruguay"));
        l.add(getFormattedCountryCode("258","Uzbekistan"));
        l.add(getFormattedCountryCode("514","Vanuatu"));
        l.add(getFormattedCountryCode("424","Venezuela"));
        l.add(getFormattedCountryCode("243","Vietnam"));
        l.add(getFormattedCountryCode("520","Wallis and Futuna"));
        l.add(getFormattedCountryCode("353","Western Sahara"));
        l.add(getFormattedCountryCode("251","Yemen"));
        l.add(getFormattedCountryCode("346","Zambia"));
        l.add(getFormattedCountryCode("310","Zimbabwe"));
        l.add(getFormattedCountryCode("300","Africa (continent)"));
        l.add(getFormattedCountryCode("400","America (continent)"));
        l.add(getFormattedCountryCode("200","Asia (continent)"));
        l.add(getFormattedCountryCode("319","Azores, Madeira"));
        l.add(getFormattedCountryCode("425","British Territories in the Antilles"));
        l.add(getFormattedCountryCode("313","Canary Islands"));
        l.add(getFormattedCountryCode("431","Dutch Antilles"));
        l.add(getFormattedCountryCode("100","Europe (continent)"));
        l.add(getFormattedCountryCode("504","Hawai"));
        l.add(getFormattedCountryCode("505","US Territories in Oceania"));
        l.add(getFormattedCountryCode("432","US Territories in the Antilles"));
        //unofficial
        l.add(getFormattedCountryCode("360","North Africa"));
        l.add(getFormattedCountryCode("370","Subsaharan Africa"));
        l.add(getFormattedCountryCode("460","North America"));
        l.add(getFormattedCountryCode("470","Latin America"));
        l.add(getFormattedCountryCode("999","Unknown"));

        return l;
    }
    
    private static String getFormattedCountryCode(String code, String country){
        return code +": "+ country;
    }


    public static Test getGenericwivConfirmation() {
        return genericwivConfirmation_;
    }
}