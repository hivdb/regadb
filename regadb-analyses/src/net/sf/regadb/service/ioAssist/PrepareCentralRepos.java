package net.sf.regadb.service.ioAssist;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.importXML.ImportException;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.importXML.ImportFromXMLBase.SyncMode;
import net.sf.regadb.service.wts.RegaDBWtsServer;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PrepareCentralRepos
{
    private static ValueType nominalValue = new ValueType("nominal value");
    private static ValueType number = new ValueType("number");
    private static AttributeGroup regadb = new AttributeGroup("RegaDB");
    private static TestType resistanceTestType = new TestType(new TestObject("Resistance test", 3), "Genotypic Susceptibility Score (GSS)");
    
    public static void main(String [] args)
    {
        ExportToXML export = new ExportToXML();
        
        Element attributes = new Element("attributes");
        
        Element tests = new Element("tests");
        
        //Attributes
        Attribute gender = createGender();
        export.writeTopAttribute(gender, attributes);
        Attribute country = createCountryOfOrigin();
        export.writeTopAttribute(country, attributes);
        Attribute ethnicity = createEthnicity();
        export.writeTopAttribute(ethnicity, attributes);
        Attribute geographicEthnicity = createGeographicOrigin();
        export.writeTopAttribute(geographicEthnicity, attributes);
        Attribute transmissionGroup = createTransmissionGroup();
        export.writeTopAttribute(transmissionGroup, attributes);
        
        //Tests
        Test vl = createGenericViralLoad();
        export.writeTopTest(vl, tests);
        Test cd4 = createGenericCD4();
        export.writeTopTest(cd4, tests);
        
        //Resistance tests
        Test anrs_2006_07 = createResistanceTest("ANRSV2006.07.xml", "ANRS 2006.07");
        export.writeTopTest(anrs_2006_07, tests);
        Test hivdb_429 = createResistanceTest("HIVDBv4.2.9.xml", "HIVDB 4.2.9");
        export.writeTopTest(hivdb_429, tests);
        Test rega_641 = createResistanceTest("RegaV6.4.1.xml", "REGA v6.4.1");
        export.writeTopTest(rega_641, tests);
        Test rega_71 = createResistanceTest("RegaHIV1V7.1.xml", "REGA v7.1");
        export.writeTopTest(rega_71, tests);
        
        //write files
        File attributesFile = new File(args[0]+File.separatorChar+"attributes.xml");
        writeXMLFile(attributesFile, attributes);
        File testsFile = new File(args[0]+File.separatorChar+"tests.xml");
        writeXMLFile(testsFile, tests);
        
        //testing
        Login login=null;
        try {
            login = Login.authenticate("test", "test");
        } catch (WrongUidException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (WrongPasswordException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (DisabledUserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        ImportFromXML imp = new ImportFromXML();
        //synch method
        FileReader r = null;
        try {
            r = new FileReader(testsFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Transaction t = login.createTransaction();
            imp.loadDatabaseObjects(t);
            
            List<Test> testsList = imp.readTests(new InputSource(r), null);
            for(Test test : testsList)
            {
                try 
                {
                    imp.sync(t, test, SyncMode.Update, true);
                } 
                catch (ImportException e)
                {
                    e.printStackTrace();
                }
                if(test.getAnalysis()!=null)
                {
                    for(AnalysisData ad : test.getAnalysis().getAnalysisDatas())
                    {
                        FileUtils.writeByteArrayToFile(new File(args[0]+File.separatorChar+test.getDescription()), ad.getData());
                    }
                }
            }
            t.commit();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static void writeXMLFile(File f, Element root)
    {
        Document n = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        
        java.io.FileWriter writer;
        try {
            writer = new java.io.FileWriter(f);
            outputter.output(n, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static Test createResistanceTest(String baseFileName, String algorithm)
    {
        resistanceTestType.setValueType(number);
        Test resistanceTest = new Test(resistanceTestType, algorithm);
        
        Analysis analysis = new Analysis();
        analysis.setUrl(RegaDBWtsServer.url_);
        analysis.setAnalysisType(new AnalysisType("wts"));
        analysis.setAccount("public");
        analysis.setPassword("public");
        analysis.setBaseinputfile("viral_isolate");
        analysis.setBaseoutputfile("asi_rules");
        analysis.setServiceName("regadb-hiv-resist");
        
        byte[] algo = null;
        try 
        {
            algo = FileUtils.readFileToByteArray(new File("io-assist-files"+File.separatorChar+baseFileName));
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
        analysis.getAnalysisDatas().add(new AnalysisData(analysis, "asi_rules", algo, "application/xml"));

        resistanceTest.setAnalysis(analysis);
        
        return resistanceTest;
    }
    
    private static Test createGenericViralLoad()
    {
        TestType vlType = new TestType(new TestObject("Patient test", 0), "Viral Load (copies/ml)");
        vlType.setValueType(new ValueType("limited number (<,=,>)"));
        Test vlTest = new Test(vlType, "Viral Load (generic)");
        
        return vlTest;
    }
 
    private static Test createGenericCD4()
    {
        TestType cd4Type = new TestType(new TestObject("Patient test", 0), "CD4 Count (cells/ul)");
        cd4Type.setValueType(number);
        Test cd4Test = new Test(cd4Type, "CD4 Count (generic)");
        
        return cd4Test;
    }

    private static Attribute createGender()
    {
        Attribute transmissionGroup = new Attribute("Gender");
        transmissionGroup.setAttributeGroup(regadb);
        transmissionGroup.setValueType(nominalValue);
        
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "male"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "female"));
        
        return transmissionGroup;
    }
    
    private static Attribute createTransmissionGroup()
    {
        Attribute transmissionGroup = new Attribute("Transmission group");
        transmissionGroup.setAttributeGroup(regadb);
        transmissionGroup.setValueType(nominalValue);
        
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "bisexual"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "heterosexual"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "homosexual"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "IVDU"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "other"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "vertical"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "transfusion"));
        
        return transmissionGroup;
    }
    
    private static Attribute createGeographicOrigin()
    {
        Attribute geographicOrigin = new Attribute("Geographic origin");
        geographicOrigin.setAttributeGroup(regadb);
        geographicOrigin.setValueType(nominalValue);
        
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "Africa"));
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "Asia"));
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "North America"));
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "South America"));
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "Europe"));
        
        return geographicOrigin;
    }
    
    private static Attribute createEthnicity()
    {
        Attribute ethnicity = new Attribute("Ethnicity");
        ethnicity.setAttributeGroup(regadb);
        ethnicity.setValueType(nominalValue);
        
        ethnicity.getAttributeNominalValues().add(new AttributeNominalValue(ethnicity, "african"));
        ethnicity.getAttributeNominalValues().add(new AttributeNominalValue(ethnicity, "asian"));
        ethnicity.getAttributeNominalValues().add(new AttributeNominalValue(ethnicity, "caucasian"));
        
        return ethnicity;
    }
    
    private static Attribute createCountryOfOrigin()
    {
        Table countries = null;
        Attribute country = new Attribute("Country of origin");
        country.setAttributeGroup(regadb);
        country.setValueType(nominalValue);
        
        try 
        {
            countries = new Table(new BufferedInputStream(new FileInputStream("io-assist-files"+File.separatorChar+"countrylist.csv")), false);
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        }
        
        ArrayList<String> countryList = countries.getColumn(1);
        ArrayList<String> typeList = countries.getColumn(3);
        for(int i = 1; i < countryList.size(); i++)
        {
            if(typeList.get(i).equals("Independent State"))
            {
                AttributeNominalValue anv = new AttributeNominalValue(country, countryList.get(i));
                country.getAttributeNominalValues().add(anv);
            }
        }
        
        return country;
    }
}
