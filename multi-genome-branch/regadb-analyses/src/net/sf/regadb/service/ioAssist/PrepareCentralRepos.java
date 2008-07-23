package net.sf.regadb.service.ioAssist;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.RegaDBWtsServer;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class PrepareCentralRepos
{
    private static TestType resistanceTestType = StandardObjects.getGssTestType();
    
    public static void main(String [] args)
    {
        String outputDir = args[0];
        
        ExportToXML export = new ExportToXML();
        
        Element attributes = new Element("attributes");
        Element events = new Element("events");        
        Element tests = new Element("tests");
        
        //Attributes
        export.writeTopAttribute(StandardObjects.getGenderAttribute(), attributes);
        export.writeTopAttribute(StandardObjects.getEthnicityAttribute(), attributes);
        export.writeTopAttribute(StandardObjects.getGeoGraphicOriginAttribute(), attributes);
        export.writeTopAttribute(StandardObjects.getTransmissionGroupAttribute(), attributes);
        export.writeTopAttribute(StandardObjects.getClinicalFileNumberAttribute(), attributes);
        Attribute country = createCountryOfOrigin();
        export.writeTopAttribute(country, attributes);
        
        File attributesFile = new File(outputDir +File.separatorChar+"attributes.xml");
        writeXMLFile(attributesFile, attributes);
        
        //Events
        export = new ExportToXML();
        export.writeTopEvent(StandardObjects.getAidsDefiningIllnessEvent(), events);
        File eventsFile = new File(outputDir +File.separatorChar+"events.xml");
        writeXMLFile(eventsFile, events);
        
        
        export = new ExportToXML();
        //Tests
        export.writeTopTest(StandardObjects.getGenericHiv1ViralLoadTest(), tests);
        export.writeTopTest(StandardObjects.getGenericHiv1ViralLoadLog10Test(), tests);
        export.writeTopTest(StandardObjects.getGenericCD4Test(), tests);
        export.writeTopTest(StandardObjects.getGenericCD4PercentageTest(), tests);
        export.writeTopTest(StandardObjects.getGenericCD8Test(), tests);
        export.writeTopTest(StandardObjects.getGenericCD8PercentageTest(), tests);
        export.writeTopTest(StandardObjects.getPregnancyTest(), tests);

        export.writeTopTest(StandardObjects.getSeroconversionTest(), tests);
        
        export.writeTopTest(StandardObjects.getFollowUpTest(), tests);
        
        export.writeTopTest(StandardObjects.getHiv1SeroconversionTest(), tests);
        export.writeTopTest(StandardObjects.getGenericHiv1SeroStatusTest(), tests);

        export.writeTopTest(StandardObjects.getGenericHBVViralLoadTest(), tests);
        export.writeTopTest(StandardObjects.getGenericHCVViralLoadTest(), tests);

        export.writeTopTest(StandardObjects.getGenericHCVAbTest(), tests);
        export.writeTopTest(StandardObjects.getGenericHBcAbTest(), tests);
        export.writeTopTest(StandardObjects.getGenericHBcAgTest(), tests);
        export.writeTopTest(StandardObjects.getGenericHBeAbTest(), tests);
        export.writeTopTest(StandardObjects.getGenericHBeAgTest(), tests);
        export.writeTopTest(StandardObjects.getGenericHBsAbTest(), tests);
        export.writeTopTest(StandardObjects.getGenericHBsAgTest(), tests);
        export.writeTopTest(StandardObjects.getGenericCD3Test(), tests);
        export.writeTopTest(StandardObjects.getGenericCD3PercentTest(), tests);
        export.writeTopTest(StandardObjects.getGenericCMVIgGTest(), tests);
        export.writeTopTest(StandardObjects.getGenericCMVIgMTest(), tests);
        export.writeTopTest(StandardObjects.getGenericToxoIgGTest(), tests);
        export.writeTopTest(StandardObjects.getGenericToxoIgMTest(), tests);
        export.writeTopTest(StandardObjects.getGenericHAVIgGTest(), tests);
        export.writeTopTest(StandardObjects.getGenericHAVIgMTest(), tests);
        
        //Resistance tests
        Test anrs_2006_07 = createResistanceTest("ANRSV2006.07.xml", "ANRS 2006.07");
        export.writeTopTest(anrs_2006_07, tests);
        Test hivdb_429 = createResistanceTest("HIVDBv4.2.9.xml", "HIVDB 4.2.9");
        export.writeTopTest(hivdb_429, tests);
        Test rega_641 = createResistanceTest("RegaV6.4.1.xml", "REGA v6.4.1");
        export.writeTopTest(rega_641, tests);
        Test rega_71 = createResistanceTest("RegaHIV1V7.1.xml", "REGA v7.1");
        export.writeTopTest(rega_71, tests);
        
        File testsFile = new File(outputDir +File.separatorChar+"tests-genomes.xml");
        writeXMLFile(testsFile, tests);
        
        //testing
        /*Login login=null;
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
        }*/
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
        Test resistanceTest = new Test(resistanceTestType, algorithm);
        
        Analysis analysis = new Analysis();
        analysis.setUrl(RegaDBWtsServer.getUrl());
        analysis.setAnalysisType(new AnalysisType("wts"));
        analysis.setAccount("public");
        analysis.setPassword("public");
        analysis.setBaseinputfile("viral_isolate");
        analysis.setBaseoutputfile("interpretation");
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
    
    
    private static Attribute createCountryOfOrigin()
    {
        Table countries = null;
        Attribute country = new Attribute("Country of origin");
        country.setAttributeGroup(StandardObjects.getRegaDBAttributeGroup());
        country.setValueType(StandardObjects.getNominalValueType());
        
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
