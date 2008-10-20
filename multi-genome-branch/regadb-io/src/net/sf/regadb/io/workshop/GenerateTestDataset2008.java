package net.sf.regadb.io.workshop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientImplHelper;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.exportXML.ExportToXML;

public class GenerateTestDataset2008 {
	public static void main(String [] args) {
        Login login = null;
        try
        {
            login = Login.authenticate("admin", "admin");
        }
        catch (WrongUidException e)
        {
            e.printStackTrace();
        }
        catch (WrongPasswordException e)
        {
            e.printStackTrace();
        } 
        catch (DisabledUserException e) 
        {
            e.printStackTrace();
        }
        
        Transaction t = login.createTransaction();
        List l = t.createQuery("from PatientImpl").list();
        
        List<Dataset> datasets = t.getCurrentUsersDatasets();
        Set<Dataset> datasetsS = new HashSet<Dataset>();
        datasetsS.addAll(datasets);
        
        List<Patient> pList = new ArrayList<Patient>();
        
        for(Object o: l) {
        	Patient p = PatientImplHelper.castPatientImplToPatient(o, datasetsS);
            int therapies = p.getTherapies().size();
            if(therapies>3 && p.getViralIsolates().size()==1) {
            	pList.add(p);
            }
        }
        
        ExportToXML export = new ExportToXML();
        Element root = new Element("patients");
        for(Patient p : pList) {
            Element patientEl = new Element("patients-el");
            root.addContent(patientEl);
            export.writePatient(p, patientEl);
        }
        
        GenerateTestDataset.writeXMLFile(root, new File("/home/plibin0/docs/presentations/workshop2008/regadb/dataset.xml"));
        
        System.err.println(pList.size());
	}

    private static void writeXMLFile(Element root, File xmlFile) {
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
}
