package org.sf.hivgensim.queries.input;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.exportXML.ExportToXML;

import org.hibernate.ScrollableResults;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class CreateXml {
	
	/*
	 * @param args 	args[0] outputfile
	 * 				args[1]	login
	 * 				args[2] passwd
	 */
	public static void main(String [] args) {
		Login login = null;
		try
		{
			login = Login.authenticate(args[1], args[2]);
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
        exportPatientsToXml(t, new File(args[0]));
	}
	
	public static void exportPatientsToXml(Transaction t, File exportXmlFile) {
		ExportToXML l = new ExportToXML();
        Element root = new Element("patients");
        
        ScrollableResults patients = t.getPatientsScrollable();
        
        int i=0;       
        while ( i < 5000) { //smaller test set
            Element patient = new Element("patients-el");
            root.addContent(patient);
            if(patients.next()) {
	            Object [] os = patients.get();
	            l.writePatient((Patient)os[0], patient);
            } else {
            	break;
            }
            System.err.println(i++);
        }
        
        System.err.println("done");
		Document doc = new Document(root);
	        XMLOutputter outputter = new XMLOutputter();
	        outputter.setFormat(Format.getPrettyFormat());

	        try {
	        	FileWriter writer = new FileWriter(exportXmlFile);
	        	outputter.output(doc, writer);
	 	        writer.flush();
	 	        writer.close();
	 	       System.err.println("done-writing");
	        } catch ( IOException e ) {
	        	e.printStackTrace();
	        }
	}	
}
