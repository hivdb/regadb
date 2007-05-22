package net.sf.regadb.install.initdb;

import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.util.encrypt.Encrypt;

import org.hibernate.Session;

public class InitRegaDB 
{
    public static void main(String [] args)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        
        addAdminUser(session);
        initTestObjects(session);
        initValueTypes(session);
        initProteins(session);
        initAnalysisTypes(session);
        
        session.getTransaction().commit();
        session.close();
    }
    
    private static void addAdminUser(Session session)
    {
        SettingsUser admin = new SettingsUser("admin", 0, 0);
        admin.setFirstName("install-admin");
        admin.setLastName("install-admin");
        admin.setAdmin(true);
        admin.setEnabled(true);
        admin.setPassword(Encrypt.encryptMD5("admin"));
        admin.setEmail("regadb-admin@uz.kuleuven.ac.be");
        session.save(admin);
    }
    
    private static void initTestObjects(Session session)
    {
        TestObject patientTest = new TestObject("Patient test", 0);
        TestObject seqAnalysis = new TestObject("Sequence analysis", 1);
        TestObject genericDrugTest = new TestObject("Generic drug test", 2);
        TestObject resistanceTest = new TestObject("Resistance test", 3);
        TestObject viAnalysis = new TestObject("Viral Isolate analysis", 4);
        
        session.save(patientTest);
        session.save(seqAnalysis);
        session.save(genericDrugTest);
        session.save(resistanceTest);
        session.save(viAnalysis);
    }
    
    private static void initValueTypes(Session session)
    {
        ValueType number = new ValueType("number");
        ValueType limitedNumber = new ValueType("limited number (<,=,>)");
        ValueType string = new ValueType("string");
        ValueType nominalValue = new ValueType("nominal value");
        
        session.save(number);
        session.save(limitedNumber);
        session.save(string);
        session.save(nominalValue);
    }
    
    private static void initProteins(Session session)
    {
        Protein p6 = new Protein("p6", "Transframe peptide (partially)");
        Protein pro = new Protein("PRO", "Protease");
        Protein rt = new Protein("RT", "Reverse Transcriptase");
        Protein in = new Protein("IN", "Integrase");

        Protein sig = new Protein("sig", "Signal peptide");
        Protein gp120 = new Protein("gp120", "Envelope surface glycoprotein gp120");
        Protein gp41 = new Protein("gp41", "Envelope transmembrane domain");
        
        session.save(p6);
        session.save(pro);
        session.save(rt);
        session.save(in);
        
        session.save(sig);
        session.save(gp120);
        session.save(gp41);
    }
    
    private static void initAnalysisTypes(Session session)
    {
        AnalysisType wts = new AnalysisType("wts");
        
        session.save(wts);
    }
}
