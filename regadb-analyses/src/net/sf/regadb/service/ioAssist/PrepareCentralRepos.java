package net.sf.regadb.service.ioAssist;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.InstituteConfig;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class PrepareCentralRepos
{
    public static void main(String [] args)
    {
    	Arguments as = new Arguments();
    	PositionalArgument output = as.addPositionalArgument("output-dir", true);
    	ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
    	
    	if(!as.handle(args))
    		return;
    	
    	if(confDir.isSet())
    		RegaDBSettings.createInstance(confDir.getValue());
    	else
    		RegaDBSettings.createInstance();
    	
    	String outputDir = new File(output.getValue()).getAbsolutePath();
    	
        ExportToXML export = new ExportToXML();
        
        Element attributes = new Element("attributes");
        Element events = new Element("events");        
        Element tests = new Element("tests");
        
        //Attributes
        export.writeTopAttribute(StandardObjects.getFirstNameAttribute(), attributes);
        export.writeTopAttribute(StandardObjects.getLastNameAttribute(), attributes);
        export.writeTopAttribute(StandardObjects.getBirthDateAttribute(), attributes);
        export.writeTopAttribute(StandardObjects.getDeathDateAttribute(), attributes);
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
        export.writeTopEvent(StandardObjects.getPregnancyEvent(), events);
        File eventsFile = new File(outputDir +File.separatorChar+"events.xml");
        writeXMLFile(eventsFile, events);
        
        export = new ExportToXML();
        //Tests
        
        //export all standard genome-related tests for every genome
        for(Map.Entry<String, Map<String, Test>> me : StandardObjects.getStandardGenomeTests().entrySet()){
            for(Map.Entry<String, Test> meme : me.getValue().entrySet()){
                System.out.println(meme.getValue().getDescription());
                export.writeTopTest(meme.getValue(), tests);
            }
        }
        
        export.writeTopTest(StandardObjects.getGenericCD4Test(), tests);
        export.writeTopTest(StandardObjects.getGenericCD4PercentageTest(), tests);
        export.writeTopTest(StandardObjects.getGenericCD8Test(), tests);
        export.writeTopTest(StandardObjects.getGenericCD8PercentageTest(), tests);

        export.writeTopTest(StandardObjects.getFollowUpTest(), tests);
        
        export.writeTopTest(StandardObjects.getGenericHBVViralLoadTest(), tests);

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
        
        export.writeTopTest(StandardObjects.getContaminationTest(), tests);
        export.writeTopTest(StandardObjects.getContaminationClusterFactorTest(), tests);
        
        //Resistance tests
        Test resTest;
        resTest = createResistanceTest("ANRS-HIV1-2011.10.xml", "ANRS 2011.10", StandardObjects.getHiv1Genome());
        export.writeTopTest(resTest, tests);
        resTest = createResistanceTest("HIVDB-HIV1-6.0.12F.xml", "HIVDB 6.0.12F", StandardObjects.getHiv1Genome());
        export.writeTopTest(resTest, tests);
        resTest = createResistanceTest("Rega-HIV1-6.4.1.xml", "REGA v6.4.1", StandardObjects.getHiv1Genome());
        export.writeTopTest(resTest, tests);
        resTest = createResistanceTest("Rega-HIV1-7.1.1.xml", "REGA v7.1.1", StandardObjects.getHiv1Genome());
        export.writeTopTest(resTest, tests);
        resTest = createResistanceTest("Rega-HIV1-8.0.2.xml", "REGA v8.0.2", StandardObjects.getHiv1Genome());
        export.writeTopTest(resTest, tests);
        
        resTest = createTransmittedResistanceTest("TDR-WHO-2009.xml", "WHO 2009", StandardObjects.getHiv1Genome());
        export.writeTopTest(resTest, tests);
        
        resTest = createResistanceTest("Rega-HIV2-7.1.1.xml", "REGA v7.1.1", StandardObjects.getHiv2AGenome());
        export.writeTopTest(resTest, tests);
        
        resTest = createResistanceTest("Rega-HIV2-8.0.1.xml", "REGA v8.0.1", StandardObjects.getHiv2AGenome());
        export.writeTopTest(resTest, tests);
        
        export.writeTopTest(RegaDBWtsServer.getSubtypeTest(), tests);
        
        File testsFile = new File(outputDir +File.separatorChar+"tests-genomes.xml");
        writeXMLFile(testsFile, tests);
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
    
    private static Test createResistanceTest(String baseFileName, String algorithm, Genome genome)
    {
    	return createResistanceTest(StandardObjects.getGssTestType(genome), baseFileName, algorithm, genome);
    }
    
    private static Test createTransmittedResistanceTest(String baseFileName, String algorithm, Genome genome)
    {
    	return createResistanceTest(StandardObjects.getTDRTestType(genome), baseFileName, algorithm, genome);
    }
    
    private static Test createResistanceTest(TestType testType, String baseFileName, String algorithm, Genome genome)
    {
        Test resistanceTest = new Test(testType, algorithm);
        
        Analysis analysis = new Analysis();
        analysis.setUrl(InstituteConfig.WTS_URL);
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
        country.setAttributeGroup(StandardObjects.getDemographicsAttributeGroup());
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
            if(typeList.get(i).equals("Independent State")
            		|| typeList.get(i).equals("Proto Dependency"))
            {
                AttributeNominalValue anv = new AttributeNominalValue(country, countryList.get(i));
                country.getAttributeNominalValues().add(anv);
            }
        }
        
        return country;
    }
}
