package net.sf.regadb.service.wts.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Test;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.importXML.ImportGenomes;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Utils {
    public static List<Test> getResistanceTests() {
        ImportFromXML importXML = new ImportFromXML();
        importXML.loadDatabaseObjects(null);
        importXML.setGenomes(getGenomesMap());
        importXML.getAnalysisTypes().put("WTS", new AnalysisType("wts"));

        try 
        {
            File tests =  RegaDBWtsServer.getTests();
            List<Test> resistanceTests = importXML.readTests(new InputSource(new FileReader(tests)), null);
            //remove non-resistance tests
            ArrayList<Test> toRemove = new ArrayList<Test>();
            for(Test resTest : resistanceTests)
            {
                if(!resTest.getTestType().getDescription().equals(StandardObjects.getGssDescription()))
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

    public static Collection<Genome> getGenomes(){
        RegaDBSettings.getInstance().getProxyConfig().initProxySettings();
        
        File genomesFile = null;
        try {
            genomesFile = RegaDBWtsServer.getGenomes();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        final ImportGenomes imp = new ImportGenomes();
        return imp.importFromXml(genomesFile, true);
    }
    
    public static Map<String, Genome> getGenomesMap(){
        Collection<Genome> list = getGenomes();
        Map<String, Genome> map = new HashMap<String, Genome>();
        
        for(Genome g : list)
            map.put(g.getOrganismName(), g);
        
        return map;
    }
}
