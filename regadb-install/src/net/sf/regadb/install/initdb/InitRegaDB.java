package net.sf.regadb.install.initdb;

import java.util.ArrayList;

import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.QueryDefinitionParameterType;
import net.sf.regadb.db.QueryDefinitionParameterTypes;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.util.encrypt.Encrypt;

import org.hibernate.Session;

public class InitRegaDB 
{
    public static void main(String [] args)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        
        addAdminUser(session);
        ArrayList<TestObject> tos = initTestObjects(session);
        
        TestObject seqAnalysisTestObject = null;
        TestObject resistanceTestObject = null;
        for(TestObject to : tos)
        {
            if(to.getDescription().equals("Sequence analysis"))
            {
                seqAnalysisTestObject = to;
            }
            else if(to.getDescription().equals("Resistance test"))
            {
                resistanceTestObject = to;
            }
        }
        
        ArrayList<ValueType> valueTypes = initValueTypes(session);
        initProteins(session);
        AnalysisType wts = initAnalysisTypes(session);
        initQueryDefinitionParameterTypes(session);
        initSubTypeTests(seqAnalysisTestObject, wts, valueTypes, session);
        initGssTestType(resistanceTestObject, valueTypes, session);
        
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
    
    private static ArrayList<TestObject> initTestObjects(Session session)
    {
        ArrayList<TestObject> tos = new ArrayList<TestObject>();
        
        TestObject patientTest = new TestObject("Patient test", 0);
        TestObject seqAnalysis = new TestObject("Sequence analysis", 1);
        TestObject genericDrugTest = new TestObject("Generic drug test", 2);
        TestObject resistanceTest = new TestObject("Resistance test", 3);
        TestObject viAnalysis = new TestObject("Viral Isolate analysis", 4);
        
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
    
    private static ArrayList<ValueType> initValueTypes(Session session)
    {
        ArrayList<ValueType> valueTypes = new ArrayList<ValueType>();
        
        ValueType number = new ValueType("number");
        ValueType limitedNumber = new ValueType("limited number (<,=,>)");
        ValueType string = new ValueType("string");
        ValueType nominalValue = new ValueType("nominal value");
        
        valueTypes.add(number);
        valueTypes.add(limitedNumber);
        valueTypes.add(string);
        valueTypes.add(nominalValue);
        
        session.save(number);
        session.save(limitedNumber);
        session.save(string);
        session.save(nominalValue);
        
        return valueTypes;
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
    
    private static AnalysisType initAnalysisTypes(Session session)
    {
        AnalysisType wts = new AnalysisType("wts");
        
        session.save(wts);
        
        return wts;
    }
    
    private static void initQueryDefinitionParameterTypes(Session session) 
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
    
    private static void initSubTypeTests(TestObject seqAnalysis, AnalysisType wts, ArrayList<ValueType> valueTypes, Session session)
    {
        ValueType stringVT = null;
        for(ValueType vt : valueTypes)
        {
            if(vt.getDescription().equals("string"))
            {
                stringVT = vt;
            }
        }
        
        Test subType = RegaDBWtsServer.getHIV1SubTypeTest(seqAnalysis, wts, stringVT);
        Test type = RegaDBWtsServer.getHIVTypeTest(seqAnalysis, wts, stringVT);
        
        session.save(subType.getTestType());
        session.save(subType.getAnalysis());
        session.save(subType);
        session.save(type.getTestType());
        session.save(type.getAnalysis());
        session.save(type);
    }
    
    private static void initGssTestType(TestObject to, ArrayList<ValueType> valueTypes, Session session)
    {
        TestType gss = new TestType(to, "Genotypic Susceptibility Score (GSS)");
        
        ValueType numberVT = null;
        for(ValueType vt : valueTypes)
        {
            if(vt.getDescription().equals("number"))
            {
                numberVT = vt;
            }
        }
        
        gss.setValueType(numberVT);
        
        session.save(gss);
    }
}
