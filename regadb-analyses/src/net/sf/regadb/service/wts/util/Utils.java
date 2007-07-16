package net.sf.regadb.service.wts.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Test;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.service.wts.FileProvider;

public class Utils {
    public static List<Test> getResistanceTests() {
        ImportFromXML importXML = new ImportFromXML();
        importXML.loadDatabaseObjects(null);
        importXML.getAnalysisTypes().put("WTS", new AnalysisType("wts"));

        try 
        {
            File tests =  File.createTempFile("tests_from_central_repos",".xml");
            FileProvider fp = new FileProvider();
            fp.getFile("regadb-tests", "tests.xml", tests);
            List<Test> resistanceTests = importXML.readTests(new InputSource(new FileReader(tests)), null);
            //remove non-resistance tests
            ArrayList<Test> toRemove = new ArrayList<Test>();
            for(Test resTest : resistanceTests)
            {
                if(!resTest.getTestType().getDescription().equals("Genotypic Susceptibility Score (GSS)"))
                {
                    toRemove.add(resTest);
                }
            }
            for(Test remove : toRemove)
            {
                resistanceTests.remove(remove);
            }
            
            return resistanceTests;
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
