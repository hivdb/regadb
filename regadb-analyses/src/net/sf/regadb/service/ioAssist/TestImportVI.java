package net.sf.regadb.service.ioAssist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.importXML.ImportHandler;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TestImportVI 
{
    public static void main(String [] args)
    {
        Login login = null;
        try
        {
            login = Login.authenticate("test", "test");
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
        
        try 
        {
            imp.readViralIsolates(new InputSource(new FileReader(new File("/home/plibin0/processed_seqs.xml"))), new ImportHandler<ViralIsolate>()
            {
                public void importObject(ViralIsolate object) 
                {
                    System.err.println("viral isolate-----");
                    for(NtSequence ntseq : object.getNtSequences())
                    {
                        System.err.println("ntseq-----------");
                        for(AaSequence aaseq : ntseq.getAaSequences())
                        {
                            String toPrint = aaseq.getProtein().getAbbreviation();
                            for(AaMutation mut : aaseq.getAaMutations())
                            {
                                toPrint += " " + mut.getNtMutationCodon() + " *";
                            }
                            System.err.println(toPrint);
                        }
                        for(TestResult tr : ntseq.getTestResults())
                        {
                            System.err.println(tr.getValue());
                        }
                    }
                    String toPrint = "";
                    for(TestResult tr : object.getTestResults())
                    {
                        toPrint += tr.getValue() +"/"+tr.getDrugGeneric().getGenericId() + " - ";
                    }
                    System.err.println(toPrint);
                }
            });
        } 
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        t.commit();
    }
}
