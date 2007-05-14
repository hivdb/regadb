package net.sf.regadb.io.exportXML;

import java.io.IOException;
import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class TestExportXml 
{
    public static void main(String[]args)
    {
        Login login = null;
        try
        {
            login = Login.authenticate("kdforc0", "Vitabis1");
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
        
        Transaction t = login.createTransaction();
        
        String [] constraints ={null,null,null,null};
        HibernateFilterConstraint hfc = new HibernateFilterConstraint();
        hfc.clause_ = " dataset.description='TEST' ";
        List<Patient> pList = t.getPatients(0, 20, "dataset.description", true, hfc);
        for(Patient p : pList)
        {
            System.err.println(((Dataset)p.getDatasets().toArray()[0]).getDescription());
        }
        
        ExportToXML l = new ExportToXML();
        Element root = new Element("Patients");
        l.writePatient(pList.get(0), root);
        
        Document n = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        try {
            outputter.output(n, System.out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        java.io.FileWriter writer;
        try {
            writer = new java.io.FileWriter("/home/kdforc0/patient.xml");
            outputter.output(n, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
