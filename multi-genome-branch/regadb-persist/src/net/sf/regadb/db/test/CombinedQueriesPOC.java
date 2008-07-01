package net.sf.regadb.db.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.session.HibernateUtil;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class CombinedQueriesPOC {
    public static void main(String [] args) {
        /*Login login = null;
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
        
        Transaction t = login.createTransaction();
        
        Query q = t.createQuery("from Attribute");
        for(Object o : q.list()) {
            System.err.println(((Attribute)o).getName());
        }*/
        
        Element hibernateMappingEl = new Element("hibernate-mapping");
        Element classEl = new Element("class");
        classEl.setAttribute("entity-name", "testit");
        classEl.setAttribute("table", "testit");
        classEl.setAttribute("schema", "public");
        hibernateMappingEl.addContent(classEl);
        
        Element id = new Element("id");
        classEl.addContent(id);
        id.setAttribute("name", "id");
        id.setAttribute("column", "id");
        id.setAttribute("type", "integer");
        
        Element a_ii = new Element("property");
        classEl.addContent(a_ii);
        a_ii.setAttribute("name", "a_ii");
        a_ii.setAttribute("column", "a_ii");
        a_ii.setAttribute("type", "integer");
        
        /*Element b_ii = new Element("property");
        classEl.addContent(b_ii);
        b_ii.setAttribute("name", "b_ii");
        b_ii.setAttribute("column", "b_ii");
        b_ii.setAttribute("type", "integer");*/
        
        try {
            File f = File.createTempFile("mapping", ".xml");
            System.err.println(f.getAbsolutePath());
            writeXMLFile(f, hibernateMappingEl);
            Session s = HibernateUtil.getEditedSession(f);
            //SQLQuery sqlq = s.createSQLQuery("create table testit (id integer, a_ii integer, b_ii integer)");

            Connection c = HibernateUtil.getJDBCConnection();
            try {
                c.createStatement().execute("create table testit (id integer, a_ii integer, b_ii integer)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            org.hibernate.Transaction t = s.beginTransaction();
            Query q = s.createQuery("from NtSequence");
            int counter = 0;
            for(Object o : q.list()) {
                Map testIt = new HashMap();
                testIt.put("id", counter);
                counter++;
                testIt.put("a_ii", ((NtSequence)o).getNtSequenceIi());
                s.save("testit", testIt);
            }
            t.commit();
            
            
            t = s.beginTransaction();
            q = s.createQuery("select ntseq from NtSequence as ntseq, testit as test where ntseq.ntSequenceIi = test.a_ii");
            for(Object o : q.list()) {
                System.err.println(((NtSequence)o).getNucleotides());
            }
            t.commit();
            
            //t = s.beginTransaction();
            
            //q = s.createQuery("from NtSequence as ntseq where ntseq");
            
            
            //q = s.createQuery("from testit");
            //System.err.println("q.list().size()" + q.list().size());
            /*for(Object o : ) {
                //System.err.println(((Attribute)o).getName());
            }*/
        } catch (IOException e) {
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
            StringBuffer output = new StringBuffer(outputter.outputString(n));
            int firstNewLine = output.indexOf("\n");
            output.insert(firstNewLine, "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\n");
            writer = new java.io.FileWriter(f);
            writer.write(output.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
