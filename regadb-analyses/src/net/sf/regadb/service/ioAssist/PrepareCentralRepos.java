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
import net.sf.regadb.db.ValueType;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.importXML.ImportFromXML;
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
    public static void main(String [] args)
    {
        ExportToXML export = new ExportToXML();
        
        Element attributes = new Element("Attributes");
        
        Element tests = new Element("Tests");
        
        //Attributes
        Attribute country = createCountryOfOrigin();
        export.writeAttribute(country, attributes);
        Attribute ethnicity = createEthnicity();
        export.writeAttribute(ethnicity, attributes);
        Attribute geographicEthnicity = createGeographicOrigin();
        export.writeAttribute(geographicEthnicity, attributes);
        Attribute transmissionGroup = createTransmissionGroup();
        export.writeAttribute(transmissionGroup, attributes);
        
        //Tests
        Test vl = createGenericViralLoad();
        export.writeTest(vl, tests);
        Test cd4 = createGenericCD4();
        export.writeTest(cd4, tests);
        
        //Resistance tests
        Test anrs_2006_07 = createResistanceTest("ANRSV2006.07.xml", "ANRS 2006.07");
        export.writeTest(anrs_2006_07, tests);
        Test hivdb_429 = createResistanceTest("HIVDBv4.2.9.xml", "HIVDB 4.2.9");
        export.writeTest(hivdb_429, tests);
        Test rega_641 = createResistanceTest("RegaV6.4.1.xml", "REGA v6.4.1");
        export.writeTest(rega_641, tests);
        Test rega_71 = createResistanceTest("RegaHIV1V7.1.xml", "REGA v7.1");
        export.writeTest(rega_71, tests);
        
        //write files
        File attributesFile = new File(args[0]+File.separatorChar+"attributes.xml");
        writeXMLFile(attributesFile, attributes);
        File testsFile = new File(args[0]+File.separatorChar+"tests.xml");
        writeXMLFile(testsFile, tests);
        
        //testing
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
            List<Test> testsList = imp.readTests(new InputSource(r), null);
            for(Test test : testsList)
            {
                if(test.getAnalysis()!=null)
                {
                    for(AnalysisData ad : test.getAnalysis().getAnalysisDatas())
                    {
                        FileUtils.writeByteArrayToFile(new File(args[0]+File.separatorChar+test.getDescription()), ad.getData());
                    }
                }
            }
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
        TestType resistanceTestType = new TestType(new TestObject("Resistance test", 3), "Genotypic Susceptibility Score (GSS)");
        resistanceTestType.setValueType(new ValueType("number"));
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
        cd4Type.setValueType(new ValueType("number"));
        Test cd4Test = new Test(cd4Type, "CD4 Count (generic)");
        
        return cd4Test;
    }

    private static Attribute createTransmissionGroup()
    {
        Attribute transmissionGroup = new Attribute("Transmission group");
        transmissionGroup.setAttributeGroup(new AttributeGroup("RegaDB"));
        transmissionGroup.setValueType(new ValueType("nominal value"));
        
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
        geographicOrigin.setAttributeGroup(new AttributeGroup("RegaDB"));
        geographicOrigin.setValueType(new ValueType("nominal value"));
        
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
        ethnicity.setAttributeGroup(new AttributeGroup("RegaDB"));
        ethnicity.setValueType(new ValueType("nominal value"));
        
        ethnicity.getAttributeNominalValues().add(new AttributeNominalValue(ethnicity, "african"));
        ethnicity.getAttributeNominalValues().add(new AttributeNominalValue(ethnicity, "asian"));
        ethnicity.getAttributeNominalValues().add(new AttributeNominalValue(ethnicity, "caucasian"));
        
        return ethnicity;
    }
    
    private static Attribute createCountryOfOrigin()
    {
        Table countries = null;
        Attribute country = new Attribute("Country of origin");
        country.setAttributeGroup(new AttributeGroup("RegaDB"));
        country.setValueType(new ValueType("nominal value"));
        
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
