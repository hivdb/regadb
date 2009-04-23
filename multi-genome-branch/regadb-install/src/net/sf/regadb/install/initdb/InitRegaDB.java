package net.sf.regadb.install.initdb;

import java.util.ArrayList;

import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.QueryDefinitionParameterType;
import net.sf.regadb.db.QueryDefinitionParameterTypes;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.util.encrypt.Encrypt;
import net.sf.regadb.util.pair.Pair;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class InitRegaDB 
{
	private SettingsUser su_;
	
    public static void main(String [] args)
    {
        InitRegaDB init = new InitRegaDB();
        init.run();
    }
    
    public void run() {
        run(null);
    }
    
    protected boolean isUninitialized(Session session){
        Query q = session.createQuery("select count(*) from ValueType");
        return (Long)q.uniqueResult() == 0;
    }
    
    public void run(ArrayList<Pair<String, String>> configurations) {
        Session session = null;
        if(configurations==null)
            session = HibernateUtil.getSessionFactory().openSession();
        else 
            session = createSession(configurations);
        
        session.beginTransaction();
        
        if(isUninitialized(session)){
            System.err.println("Initializing database.");
            
            addAdminUser(session);
            ArrayList<TestObject> tos = initTestObjects(session);
            
            TestObject seqAnalysisTestObject = null;
            for(TestObject to : tos)
            {
                if(to.getDescription().equals("Sequence analysis"))
                {
                    seqAnalysisTestObject = to;
                }
            }
            
            ArrayList<ValueType> valueTypes = initValueTypes(session);
            initTherapyChangeMotivations(session);
            AnalysisType wts = initAnalysisTypes(session);
            initQueryDefinitionParameterTypes(session);
            initSubTypeTests(seqAnalysisTestObject, wts, valueTypes, session);
        }
        session.getTransaction().commit();
        session.close();
    }
    
	private Session createSession(ArrayList<Pair<String, String>> configurations) {
        try {
            Configuration conf = new Configuration().configure();
            for(Pair<String, String> p : configurations) {
                conf.setProperty(p.getKey(), p.getValue());
            }
            SessionFactory sessionFactory = conf.buildSessionFactory();
            return sessionFactory.openSession();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private void initTherapyChangeMotivations(Session session) {
        session.save(new TherapyMotivation("Toxicity"));
        session.save(new TherapyMotivation("Treatment failure, resistance"));
        session.save(new TherapyMotivation("Treatment failure, other"));
        session.save(new TherapyMotivation("Patient's choice"));
        session.save(new TherapyMotivation("Therapy change"));
        session.save(new TherapyMotivation("Adherence"));
        session.save(new TherapyMotivation("Pregnancy"));
        session.save(new TherapyMotivation("Interaction with other drugs"));
        session.save(new TherapyMotivation("Other"));
        session.save(new TherapyMotivation("Unknown"));
    }

    private void addAdminUser(Session session)
    {
    	if(su_==null) {
        SettingsUser admin = new SettingsUser("admin", 0, 0);
        admin.setFirstName("install-admin");
        admin.setLastName("install-admin");
        admin.setRole("admin");
        admin.setPassword(Encrypt.encryptMD5("admin"));
        admin.setEmail("regadb-admin@uz.kuleuven.ac.be");
        su_=admin;
    	}
        
        /*SettingsUser test = new SettingsUser("test", 0, 0);
        test.setFirstName("test");
        test.setLastName("test");
        test.setAdmin(true);
        test.setEnabled(true);
        test.setPassword(Encrypt.encryptMD5("test"));
        test.setEmail("test@uz.kuleuven.ac.be");*/
        
        session.save(su_);
        //session.save(test);
    }
    
    private ArrayList<TestObject> initTestObjects(Session session)
    {
        ArrayList<TestObject> tos = new ArrayList<TestObject>();
        
        TestObject patientTest      = StandardObjects.getPatientTestObject();
        TestObject seqAnalysis      = StandardObjects.getSequenceAnalysisTestObject();
        TestObject genericDrugTest  = StandardObjects.getGenericDrugTestObject();
        TestObject resistanceTest   = StandardObjects.getResistanceTestObject();
        TestObject viAnalysis       = StandardObjects.getViralIsolateAnalysisTestObject();
        
        tos.add(patientTest);
        tos.add(seqAnalysis);
        tos.add(genericDrugTest);
        tos.add(resistanceTest);
        tos.add(viAnalysis);
        
        session.save(patientTest);
        session.save(seqAnalysis);
        session.save(genericDrugTest);
        session.save(resistanceTest);
        session.save(viAnalysis);
        
        return tos;
    }
    
    private ArrayList<ValueType> initValueTypes(Session session)
    {
        ArrayList<ValueType> valueTypes = new ArrayList<ValueType>();
        
        ValueType number        = StandardObjects.getNumberValueType();
        ValueType limitedNumber = StandardObjects.getLimitedNumberValueType();
        ValueType string        = StandardObjects.getStringValueType();
        ValueType nominalValue  = StandardObjects.getNominalValueType();
        ValueType dateValue     = StandardObjects.getDateValueType();
        
        valueTypes.add(number);
        valueTypes.add(limitedNumber);
        valueTypes.add(string);
        valueTypes.add(nominalValue);
        valueTypes.add(dateValue);
        
        session.save(number);
        session.save(limitedNumber);
        session.save(string);
        session.save(nominalValue);
        session.save(dateValue);
        
        return valueTypes;
    }
    
    private AnalysisType initAnalysisTypes(Session session)
    {
        AnalysisType wts = new AnalysisType("wts");
        
        session.save(wts);
        
        return wts;
    }
    
    private void initQueryDefinitionParameterTypes(Session session) 
    {
		QueryDefinitionParameterType string = new QueryDefinitionParameterType("String", QueryDefinitionParameterTypes.STRING.getValue());
		QueryDefinitionParameterType integer = new QueryDefinitionParameterType("Integer", QueryDefinitionParameterTypes.INTEGER.getValue());
		QueryDefinitionParameterType doubleType = new QueryDefinitionParameterType("Double", QueryDefinitionParameterTypes.DOUBLE.getValue());
		QueryDefinitionParameterType date = new QueryDefinitionParameterType("Date", QueryDefinitionParameterTypes.DATE.getValue());
		QueryDefinitionParameterType genericDrug = new QueryDefinitionParameterType("GenericDrug", QueryDefinitionParameterTypes.GENERICDRUG.getValue());
		QueryDefinitionParameterType commercialDrug = new QueryDefinitionParameterType("CommercialDrug", QueryDefinitionParameterTypes.COMMERCIALDRUG.getValue());
		QueryDefinitionParameterType test = new QueryDefinitionParameterType("Test", QueryDefinitionParameterTypes.TEST.getValue());
		QueryDefinitionParameterType testType = new QueryDefinitionParameterType("TestType", QueryDefinitionParameterTypes.TESTTYPE.getValue());
		QueryDefinitionParameterType protein = new QueryDefinitionParameterType("Protein", QueryDefinitionParameterTypes.PROTEIN.getValue());
		QueryDefinitionParameterType attribute = new QueryDefinitionParameterType("Attribute", QueryDefinitionParameterTypes.ATTRIBUTE.getValue());
		QueryDefinitionParameterType attributeGroup = new QueryDefinitionParameterType("Attribute Group", QueryDefinitionParameterTypes.ATTRIBUTEGROUP.getValue());

		session.save(string);
		session.save(integer);
		session.save(doubleType);
		session.save(date);
		session.save(genericDrug);
		session.save(commercialDrug);
		session.save(test);
		session.save(testType);
		session.save(protein);
		session.save(attribute);
		session.save(attributeGroup);
	}
    
    private void initSubTypeTests(TestObject seqAnalysis, AnalysisType wts, ArrayList<ValueType> valueTypes, Session session)
    {
        ValueType stringVT = null;
        for(ValueType vt : valueTypes)
        {
            if(vt.getDescription().equals("string"))
            {
                stringVT = vt;
            }
        }
        
        Test subType = RegaDBWtsServer.getSubtypeTest(seqAnalysis, wts, stringVT);
        
        session.save(subType.getTestType());
        session.save(subType.getAnalysis());
        session.save(subType);
    }

	public SettingsUser getSu_() {
		return su_;
	}

	public void setSu_(SettingsUser su_) {
		this.su_ = su_;
	}
}
