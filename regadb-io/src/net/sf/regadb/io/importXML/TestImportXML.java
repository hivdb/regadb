/*
 * Created on May 11, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.importXML;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TestImportXML {

    /**
     * @param args
     * @throws SAXException 
     * @throws IOException 
     */
    public static void main(String[] args) throws SAXException, IOException {
        ImportFromXML instance = new ImportFromXML();
        
        Login login = null;
        try
        {
            login = Login.authenticate("jvsant1", "Kangoer1");
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

        instance.loadDatabaseObjects(t);
        
        t.commit();
        
        FileReader r = new FileReader(new File(args[0]));

        List<Attribute> attributes = instance.readAttributes(new InputSource(r));
        
        System.err.println("Read: " + attributes.size() + " attributes");
    }

}
