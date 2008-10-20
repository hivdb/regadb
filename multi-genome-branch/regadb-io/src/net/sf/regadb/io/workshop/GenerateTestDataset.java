package net.sf.regadb.io.workshop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.importXML.ImportHandler;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class GenerateTestDataset {
    public static void main(String [] args) {
        Login login = null;
        try
        {
            login = Login.authenticate("admin", "admin");
        }
        catch (WrongUidException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (WrongPasswordException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (DisabledUserException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
        
        ImportFromXML imp = new ImportFromXML();
        
        Transaction t = login.createTransaction();
        
        imp.loadDatabaseObjects(t);
        
        final List<Patient> dataset = new ArrayList<Patient>();
        final List<ViralIsolate> vis = new ArrayList<ViralIsolate>();
        
        try {
            imp.readPatients(new InputSource(new FileReader(new File(args[0]))), new ImportHandler<Patient>() {
                public void importObject(Patient object) {
                    int therapies = object.getTherapies().size();
                    if(listContainsTherapyCount(dataset, therapies)<4) {
                        dataset.add(object);
                        for(ViralIsolate vi : object.getViralIsolates()) {
                            vis.add(vi);
                        }
                    }
                }
            });
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        for(Patient p : dataset) {
            System.err.println(p.getTherapies().size());
        }
        t.commit();
        System.err.println("size:"+dataset.size());
        
        ExportToXML export = new ExportToXML();
        Element root = new Element("patients");
        for(Patient p : dataset) {
            Element patientEl = new Element("patients-el");
            root.addContent(patientEl);
            export.writePatient(p, patientEl);
        }
        
        writeXMLFile(root, new File("/home/plibin0/workshop2007/regadb/dataset.xml"));
        
        ExportToXML exportvi = new ExportToXML();
        Element rootvi = new Element("viralIsolates");
        for(ViralIsolate vi : vis) {
            //Element viEl = new Element("viralIsolates-el");
            //rootvi.addContent(viEl);
            exportvi.writeTopViralIsolate(vi, rootvi);
        }
        
        writeXMLFile(rootvi, new File("/home/plibin0/workshop2007/regadb/dataset_vi_tobeprocessed.xml"));
        
        imp = new ImportFromXML();
        
        t = login.createTransaction();
        
        imp.loadDatabaseObjects(t);
        
        /*try {
            imp.readPatients(new InputSource(new FileReader(new File("/home/plibin0/workshop2007/regadb/dataset.xml"))) , new ImportHandler<Patient>() {

                public void importObject(Patient object) {
                    // TODO Auto-generated method stub
                    System.err.println(object.getPatientIi());
                    
                }});
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }
    
    static void writeXMLFile(Element root, File xmlFile) {
        Document n = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        
        java.io.FileWriter writer;
        try {
            writer = new java.io.FileWriter(xmlFile);
            outputter.output(n, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static int listContainsTherapyCount(List<Patient> dataset, int therapyCount) {
        int count = 0;
        for(Patient p : dataset) {
            if(p.getTherapies().size()==therapyCount)
                count++;
        }
        return count;
    }
}
