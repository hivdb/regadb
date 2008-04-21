/*
 * Created on Dec 14, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.pair.Pair;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Represents a short-lived transaction to the database.
 * 
 * The transaction must be either committed or rolled back. You should make
 * your transaction short-lived, since it consumes database resources.
 * 
 * You may fetch persistent objects in one transaction, modify them at will,
 * and then save them in a new transaction.
 */
public class Transaction {

    private Login login;
    private Session session;
    private final Query getTestQuery;
    private final Query getResistanceInterpretationTemplateQuery;
    private final Query getTestObjectQuery;
    private final Query getAttributeGroupQuery;
    private final Query getValueTypeQuery;
    private final Query getAttributeNominalValueQuery;
    private final Query getTestNominalValueQuery;
    private final Query getTestTypeQuery;
    private final Query getAttributeQuery;
    private final Query getPatientQuery;
    private final Query getDrugGenericQuery;
    private final Query getCommercialDrugQuery;
    private final Query getViralIsolateQuery;
    private final Query getEventQuery;
    private final Query getEventNominalValueQuery;

    public Transaction(Login login, Session session) {
        this.login = login;
        this.session = session;
        begin();
        getTestQuery = session.createQuery("from Test test where test.description = :description");
        getResistanceInterpretationTemplateQuery = session.createQuery("from ResistanceInterpretationTemplate resistanceInterpretationTemplate where resistanceInterpretationTemplate.name = :name");
        getTestObjectQuery = session.createQuery("from TestObject as testobject where description = :description");
        getAttributeGroupQuery = session.createQuery("from AttributeGroup as attributegroup where groupName = :groupName");
        getValueTypeQuery = session.createQuery("from ValueType as valuetype where description = :description");
        getAttributeNominalValueQuery = session.createQuery("from AttributeNominalValue as anv where attribute = :attribute and value = :value");
        getTestNominalValueQuery = session.createQuery("from TestNominalValue as anv where testType = :type and value = :value");
        getTestTypeQuery = session.createQuery("from TestType as testType where testType.description = :description");
        getAttributeQuery = session.createQuery("from Attribute attribute where attribute.name = :name and attribute.attributeGroup.groupName = :groupName");
        getPatientQuery = session.createQuery(
                        "select new net.sf.regadb.db.Patient(patient, max(access.permissions))" +
                        "from PatientImpl as patient " +
                        "join patient.patientDatasets as patient_dataset " +
                        "join patient_dataset.id.dataset as dataset " +
                        "join dataset.datasetAccesses access " +
                        "where dataset = :dataset " +
                        "and access.permissions >= 1 " +
                        "and access.id.settingsUser.uid = :uid " +
                        "and patient.patientId = :patientId " +
                        "group by patient");
        getDrugGenericQuery = session.createQuery("from DrugGeneric as drug where drug.genericId = :genericId");
        getCommercialDrugQuery = session.createQuery("from DrugCommercial as drug where drug.name = :name");
        getViralIsolateQuery = session.createQuery("select vi from ViralIsolate as vi "
                        + "join vi.patient as p "
                        + "join p.patientDatasets as patient_dataset "
                        + "join patient_dataset.id.dataset as dataset "
                        + "where dataset.closedDate = null "
                        + "and vi.sampleId = :sampleId");
        
        getEventQuery = session.createQuery("from Event event where event.name = :name");
        getEventNominalValueQuery = session.createQuery("from EventNominalValue as anv where event = :event and value = :value"); 
    }
    
    private void begin() {
        session.beginTransaction();
    }
    
    public void commit() {
        session.getTransaction().commit();
    }

    public void rollback() {
        session.getTransaction().rollback();
    }

    public void clearCache() {
        session.clear();
    }

    public Query createQuery(String query)
    {
    	Query q = session.createQuery(query);
    	
    	return q;
    }

    public NtSequence getSequence(int id)
    {
        Query q = session.createQuery("from NtSequence where id = :id");
        
        q.setParameter("id", id);
        
        return (NtSequence)q.uniqueResult();
    }

    public ViralIsolate getViralIsolate(Dataset dataset, String sampleId) {
        // TODO: check dataset access permissions before doing the query

        getViralIsolateQuery.setParameter("sampleId", sampleId);

        Object o = getViralIsolateQuery.uniqueResult();
        return (ViralIsolate) o;
    }
    
    public Patient getPatient(int id)
    {
        Query q = session.createQuery(
                "select new net.sf.regadb.db.Patient(patient, max(access.permissions)) " +
                getPatientsQuery() + 
                "and patient.id = :id "+
                "group by patient");
        
        q.setParameter("uid", login.getUid());
        q.setParameter("id", id);

        return (Patient)q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Patient> getPatients(String from, HibernateFilterConstraint filter)
    {
        Query q = session.createQuery("select new net.sf.regadb.db.Patient(patient, max(access.permissions)) from PatientImpl as patient " +
        		"join patient.patientDatasets as patient_dataset " +
                "join patient_dataset.id.dataset as dataset " +
                "join dataset.datasetAccesses access " +
                from +" "+
                "where ( access.permissions >= 1 " +
                "and access.id.settingsUser.uid = :uid ) and ( "+ filter.clause_ +" ) group by patient");
        
        for(Pair<String, Object> arg : filter.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        q.setParameter("uid", login.getUid());

        return (List<Patient>) q.list();
    }
    
    //simple get by id
    /*
     * Lists of attributes, tests, test types, etc...
     */
    @SuppressWarnings("unchecked")
    public List<Attribute> getAttributes() {
        Query q = session.createQuery("from Attribute");
        
        return q.list();
    }

    public Attribute getAttribute(String name, String groupName) {
        getAttributeQuery.setParameter("name", name);
        getAttributeQuery.setParameter("groupName", groupName);
        
        return (Attribute) getAttributeQuery.uniqueResult();
    }
    
    public Event getEvent(String name){
        getEventQuery.setParameter("name", name);
        return (Event) getEventQuery.uniqueResult();
    }
    
    public EventNominalValue getEventNominalValue(Event event, String value) {
        getEventNominalValueQuery.setParameter("event", event);
        getEventNominalValueQuery.setParameter("value", value);
        
        try {
            return (EventNominalValue)getEventNominalValueQuery.uniqueResult();        
        } catch (RuntimeException e) {
            System.err.println("Exception for event value : " + event.getName() + " " + value);
            throw e;
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Test> getTests() {
        Query q = session.createQuery("from Test test");
        
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Test> getTests(TestType testType) 
    {
        Query q = session.createQuery("from Test test where test.testType.testTypeIi = :testTypeIdParam");

        q.setParameter("testTypeIdParam", testType.getTestTypeIi());
        
        return q.list();
    }
    
    public TestResult getNewestTestResult(Test test, Patient p) {
        Query q = session.createQuery("from TestResult tr where tr.test.description = :testDescriptionParam and tr.patient.id = :patientIdParam order by tr.testDate desc");

        q.setParameter("testDescriptionParam", test.getDescription());
        q.setParameter("patientIdParam", p.getPatientIi());
        q.setMaxResults(1);
        
        if(q.list().size()>0)
            return (TestResult)q.list().get(0);
        else
            return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<TestType> getTestTypes() 
    {
        Query q = session.createQuery("from TestType");
        
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<AnalysisType> getAnalysisTypes() {
        Query q = session.createQuery("from AnalysisType");
        
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<TherapyMotivation> getTherapyMotivations() {
        Query q = session.createQuery("from TherapyMotivation");
        
        return q.list();
    }

    public boolean hasTests(TestType testType)
    {
        Query q = session.createQuery("select count(test) from Test test where test.testType.id = :testTypeIdParam");
        
        q.setParameter("testTypeIdParam", testType.getTestTypeIi());
        
        return ((Long)q.uniqueResult())>0;
    }

    public ResistanceInterpretationTemplate getResRepTemplate(String name) {
        getResistanceInterpretationTemplateQuery.setParameter("name", name);
        
        return (ResistanceInterpretationTemplate) getResistanceInterpretationTemplateQuery.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<ResistanceInterpretationTemplate> getResRepTemplates() {
        Query q = session.createQuery("from ResistanceInterpretationTemplate");
        
        return q.list();
    }
    
    public Test getTest(String description) {
        getTestQuery.setParameter("description", description);
        
        return (Test) getTestQuery.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DrugGeneric> getGenericDrugs() 
    {
        Query q = session.createQuery("from DrugGeneric");
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<DrugCommercial> getCommercialDrugs() 
    {
        Query q = session.createQuery("from DrugCommercial");
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<DrugClass> getClassDrugs() 
    {
        Query q = session.createQuery("from DrugClass");
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Protein> getProteins() {
        Query q = session.createQuery("from Protein protein");
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<ValueType> getValueTypes()
    {
        Query q = session.createQuery("from ValueType");
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<AttributeGroup> getAttributeGroups()
    {
        Query q = session.createQuery("from AttributeGroup");
        return q.list();
    }
    
    public Map<String, Protein> getProteinMap() {
        List<Protein> proteins = getProteins();
        Map<String, Protein> result = new HashMap<String, Protein>();
        
        for (Protein p:proteins)
            result.put(p.getAbbreviation(), p);
        

        return result;
    }
    
    /*
     * Dataset queries
     */

    /**
     * Obtain a list of all datasets in the database.
     */
    @SuppressWarnings("unchecked")
    public List<Dataset> getDatasets() {
        Query q = session.createQuery("from Dataset dataset");
        
        return q.list();
    }
    
    /**
     * Obtain a list of all datasets in the database which the current user can acces.
     */
    @SuppressWarnings("unchecked")
    public List<Dataset> getCurrentUsersDatasets() {
        return getCurrentUsersDatasets(Privileges.ANONYMOUS_READONLY);
    }
    
    /**
     * Obtain a list of all datasets in the database which the current user can acces for a minimum privillege.
     */
    @SuppressWarnings("unchecked")
    public List<Dataset> getCurrentUsersDatasets(Privileges privilege) 
    {
        Query q = session.createQuery("select dataset from Dataset dataset " +
                "join dataset.datasetAccesses as dataset_access " +
                "where dataset_access.permissions >= :privilege " +
                "and dataset_access.id.settingsUser.uid = :uid ");
        
        q.setParameter("privilege", privilege.getValue());
        q.setParameter("uid", login.getUid());
        
        return q.list();
    }
    
    public Dataset getDataset(String description) {
        Query q = session.createQuery("from Dataset dataset where dataset.description = :description");
        q.setParameter("description", description);
        
        return (Dataset) q.uniqueResult();
    }	

    /**
     * Returns patients in this data set according, checking access permissions.
     */
    
    @SuppressWarnings("unchecked")
    public List<Patient> getPatients(Dataset dataset) {
        Query q = session.createQuery(
                "select new net.sf.regadb.db.Patient(patient, max(access.permissions)) " +
                "from PatientImpl as patient " +
                "join patient.patientDatasets as patient_dataset " +
                "join patient_dataset.id.dataset as dataset " +
                "join dataset.datasetAccesses access " +
                "where dataset = :dataset " +
                "and access.permissions >= 1 " +
                "and access.settingsUser.uid = :uid " +
                "group by patient");
        q.setParameter("dataset", dataset);
        q.setParameter("uid", login.getUid());

        return q.list();
    }

    /**
     * Returns all patients, checking access permissions.
     */
    @SuppressWarnings("unchecked")
    public List<Patient> getPatients() {
        Query q = session.createQuery(
                "select new net.sf.regadb.db.Patient(patient, max(access.permissions)) " +
                getPatientsQuery() + 
                "group by patient");
        q.setParameter("uid", login.getUid());

        return q.list();
    }
    
    /**
     * Returns a Page of patients, checking access permissions,
     * checking all the filter constraints and grouped by the selected col.
     */
    @SuppressWarnings("unchecked")
    public List<Patient> getPatients(int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints) 
    {
        String queryString = 	"select new net.sf.regadb.db.Patient(patient, max(access.permissions)) " +
                				getPatientsQuery();
        if(!filterConstraints.clause_.equals(" "))
        {
        	queryString += "and" + filterConstraints.clause_;
        }
        queryString += " group by patient, " + sortField;
        queryString += " order by " + sortField + (ascending?" asc":" desc");
        
        Query q = session.createQuery(queryString);
        q.setParameter("uid", login.getUid());
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        
        return q.list();
    }
    
    public String getPatientsQuery()
    {
        return "from PatientImpl as patient " +
        "join patient.patientDatasets as patient_dataset " +
        "join patient_dataset.id.dataset as dataset " +
        "join dataset.datasetAccesses access " +
        "where access.permissions >= 1 " +
        "and access.id.settingsUser.uid = :uid ";
    }
    
    public long getPatientCount(HibernateFilterConstraint filterConstraints) 
    {
        String queryString =    "select count(patient) " +
                                getPatientsQuery();
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += "and" + filterConstraints.clause_;
        }
        
        Query q = session.createQuery(queryString);
        q.setParameter("uid", login.getUid());
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        return ((Long)q.uniqueResult()).longValue();
    }

    public Patient getPatient(Dataset dataset, String id) {
        getPatientQuery.setParameter("dataset", dataset);
        getPatientQuery.setParameter("uid", login.getUid());
        getPatientQuery.setParameter("patientId", id);
        
        return (Patient) getPatientQuery.uniqueResult();
    }
    
    /*
     * User settings queries
     */

    /**
     * Authenticated access to user settings.
     */
    public SettingsUser getSettingsUser(String uid) {
        Query q = session.createQuery("from SettingsUser user where user.uid = :uid");
        q.setParameter("uid", uid);
        
        return (SettingsUser) q.uniqueResult();
    }
    
    public SettingsUser changeUid(SettingsUser user, String newUid)
    {
        SettingsUser newSu = new SettingsUser(newUid, user.getChartWidth(), user.getChartHeight());
        newSu.setDataset(user.getDataset());
        newSu.setPassword(user.getPassword());
        newSu.setEmail(user.getEmail());
        newSu.setFirstName(user.getFirstName());
        newSu.setLastName(user.getLastName());
        newSu.setAdmin(user.getAdmin());
        newSu.setEnabled(user.getEnabled());
        
        delete(user);
        
        save(newSu);
        
        return newSu;
    }

    /**
     * Unauthenticated access to own settings.
     */
    public SettingsUser getSettingsUser() {
        Query q = session.createQuery("from SettingsUser user where user.uid = :uid");  
        q.setParameter("uid", login.getUid());

        return (SettingsUser) q.uniqueResult();
    }

    public void update(Patient patient) 
    {
        session.update(patient.getPatient());
    }
    
    public void update(Serializable object) 
    {
        session.update(object);
    }
    
    public void save(Patient patient) 
    {
        session.save(patient.getPatient());
    }
    
    public void save(Serializable object) 
    {
        session.save(object);
    }
        
    public void delete(Serializable object)
    {
        session.delete(object);
    }
    
    public void delete(Patient patient)
    {
        session.delete(patient.getPatient());
    }
    
    public void attach(Serializable o)
    {
        session.lock(o, LockMode.READ);
    }
    
    public void attach(Patient p)
    {
        session.lock(p.getPatient(), LockMode.READ);
    }
    
    public void refresh(Serializable s)
    {
        session.refresh(s);
    }

    /*
     * Patient queries
     */

    public Criteria createCriteria(Class c)
    {
        return session.createCriteria(c);
    }
    
    public boolean stillExists(Object obj)
    {
        if(obj instanceof Patient)
            obj = ((Patient)obj).getPatient();
        
        String className = obj.getClass().getName();
        int indexOfDollar = className.indexOf("$");
        if(indexOfDollar!=-1)
            className = className.substring(0, indexOfDollar);
        
        Query q = session.createQuery("from " + className + " obj where obj = :objParam");
        
        q.setParameter("objParam", obj);
    
        return q.uniqueResult() !=null;
    }
    
    //Get a limited, filtered and sorted list
    private List getLFS(String queryString, int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints, boolean and) {
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += (and ? " and" : " where" ) + filterConstraints.clause_;
        }
        queryString += " order by " + sortField + (ascending?" asc":" desc");
    
        Query q = session.createQuery(queryString);
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        
        return q.list();
    }
    
    /**
     * Returns a Page of TestResults,
     * checking all the filter constraints and grouped by the selected col.
     */
    @SuppressWarnings("unchecked")
    public List<TestResult> getNonViralIsolateTestResults(Patient patient, int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "from TestResult as testResult " +
                            "where testResult.patient.patientIi = " + patient.getPatientIi() + " " +
                            "and testResult.viralIsolate is null";
        return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, true);
    }
    
    public long getNonViralIsolateTestResultsCount(Patient patient, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "select count(testResult) " +
                            "from TestResult as testResult " +
                            "where testResult.patient.patientIi = " + patient.getPatientIi() + " " +
                            "and testResult.viralIsolate is null";
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += " and" + filterConstraints.clause_;
        }

        Query q = session.createQuery(queryString);
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        return ((Long)q.uniqueResult()).longValue();
    }
    
    /*
     * Returns all Events per patient
     */
    
    @SuppressWarnings("unchecked")
    public List<PatientEventValue> getPatientEvents(Patient p, int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints)
    {
    	String queryString = "FROM PatientEventValue AS patient_event_value " +
							" WHERE patient_ii = " + p.getPatientIi() + " ";
    	return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, true);
    }
    
    public long patientEventCount(Patient p, HibernateFilterConstraint filterConstraints) {
    	String queryString = "SELECT COUNT(patient_event_value) FROM PatientEventValue AS patient_event_value " +
								" WHERE patient_ii = " + p.getPatientIi() + " ";
    	
    	if( !filterConstraints.clause_.equals(" ") )
        {
            queryString += " AND " + filterConstraints.clause_;
        }
        
    	Query q = session.createQuery(queryString);
    	
    	for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
    	
    	return ((Long)q.uniqueResult()).longValue();
    }
    
    /*
     * Returns all Events
     */
    
    public List<Event> getEvents()
    {
    	String queryString = "FROM Event AS event ";
    	Query q = session.createQuery(queryString);
    	return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Event> getEvents(int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints)
    {
    	String queryString = "FROM Event AS event ";
    	return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, false);
    }
    
    public long eventCount(HibernateFilterConstraint filterConstraints) {
    	String queryString = "SELECT COUNT(event_ii) FROM Event as event ";
    	
    	if( !filterConstraints.clause_.equals(" ") )
        {
            queryString += " WHERE " + filterConstraints.clause_;
        }
        
    	Query q = session.createQuery(queryString);
    	
    	for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
    	
    	return ((Long)q.uniqueResult()).longValue();
    }
    
    /**
     * Returns a Page of Therapies,
     * checking all the filter constraints and grouped by the selected col.
     */
    @SuppressWarnings("unchecked")
    public List<Therapy> getTherapies(Patient patient, int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "from Therapy as therapy " +
                            "where therapy.patient.id = " + patient.getPatientIi();
        return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, true);
    }
    
    /**
     * Returns a Page of ViralIsolates,
     * checking all the filter constraints and grouped by the selected col.
     */
    @SuppressWarnings("unchecked")
    public List<ViralIsolate> getViralIsolates(Patient patient, int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "from ViralIsolate as viralIsolate " +
                            "where viralIsolate.patient.id = " + patient.getPatientIi();
        return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, true);
    }
    
    /**
     * Returns the count of Therapies,
     * for the given constraints.
     */
    @SuppressWarnings("unchecked")
    public long getTherapiesCount(Patient patient, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "select count(therapy)" +
                            "from Therapy as therapy " +
                            "where therapy.patient.id = " + patient.getPatientIi();
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += " and" + filterConstraints.clause_;
        }
      
        Query q = session.createQuery(queryString);
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        return ((Long)q.uniqueResult()).longValue();
    }
    
    /**
     * Returns the count of ViralIsolates,
     * for the given constraints.
     */
    @SuppressWarnings("unchecked")
    public long getViralIsolateCount(Patient patient, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "select count(viralIsolate)" +
                            "from ViralIsolate as viralIsolate " +
                            "where viralIsolate.patient.id = " + patient.getPatientIi();
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += " and" + filterConstraints.clause_;
        }
      
        Query q = session.createQuery(queryString);
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        return ((Long)q.uniqueResult()).longValue();
    }
    
    /**
     * Returns a Page of Attributes,
     * checking all the filter constraints and grouped by the selected col.
     */
    @SuppressWarnings("unchecked")
    public List<Attribute> getAttributes(int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "from Attribute as attribute ";
        
        return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, false);
    }
    
    /**
     * Returns the count of Attributes,
     * for the given constraints.
     */
    @SuppressWarnings("unchecked")
    public long getAttributeCount(HibernateFilterConstraint filterConstraints)
    {
        String queryString = "select count(attribute) from Attribute as attribute ";
        
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += "where " + filterConstraints.clause_;
        }
      
        Query q = session.createQuery(queryString);
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        return ((Long)q.uniqueResult()).longValue();
    }
        
    public long getAttributeUsage(Attribute attribute)
    {
        Query q = session.createQuery("select count(attributeValue.id.attribute) from PatientAttributeValue attributeValue where attributeValue.id.attribute = :idParam");

        q.setParameter("idParam", attribute);

        return ((Long)q.uniqueResult()).longValue();
    }
    
    /**
     * Returns a Page of AttributeGroups,
     * checking all the filter constraints and grouped by the selected col.
     */
    @SuppressWarnings("unchecked")
    public List<AttributeGroup> getAttributeGroups(int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "from AttributeGroup as attributeGroup ";
        
        return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, false);
    }
    
    /**
     * Returns the count of Attributes,
     * for the given constraints.
     */
    @SuppressWarnings("unchecked")
    public long getAttributeGroupCount(HibernateFilterConstraint filterConstraints)
    {
        String queryString = "select count(attributeGroup) from AttributeGroup as attributeGroup ";
        
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += "where " + filterConstraints.clause_;
        }
      
        Query q = session.createQuery(queryString);
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        return ((Long)q.uniqueResult()).longValue();
    }
    
    @SuppressWarnings("unchecked")
	public List<TestObject> getTestObjects() 
	{
		Query q=session.createQuery("from TestObject");
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public List<TestType> getTestTypes(int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints) 
	{
		String queryString = "from TestType as testType ";
        
        return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, false);
	}

	public long getTestTypeCount(HibernateFilterConstraint filterConstraints) 
	{
		 String queryString = "select count(testType) from TestType as testType ";
	        
	       	if(!filterConstraints.clause_.equals(" "))
	        {
	            queryString += "where " + filterConstraints.clause_;
	        }
	      
	        Query q = session.createQuery(queryString);
	        
	        for(Pair<String, Object> arg : filterConstraints.arguments_)
	        {
	            q.setParameter(arg.getKey(), arg.getValue());
	        }
	        
	        return ((Long)q.uniqueResult()).longValue();
	}
	
	public long getTestCount(HibernateFilterConstraint filterConstraints) 
	{
		 String queryString = "select count(description) from Test as test ";
	        
	        if(!filterConstraints.clause_.equals(" "))
	        {
	            queryString += "where " + filterConstraints.clause_;
	        }
	      
	        Query q = session.createQuery(queryString);
	        
	        for(Pair<String, Object> arg : filterConstraints.arguments_)
	        {
	            q.setParameter(arg.getKey(), arg.getValue());
	        }
	        
	        return ((Long)q.uniqueResult()).longValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<Test> getTests(int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints)
	{
		String queryString = "from Test as test ";
        
        return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, false);
	}
    
    public long getResRepTemplatesCount(HibernateFilterConstraint filterConstraints) 
    {
         String queryString = "select count(templateIi) from ResistanceInterpretationTemplate as resistanceInterpretationTemplate ";
            
            if(!filterConstraints.clause_.equals(" "))
            {
                queryString += "where " + filterConstraints.clause_;
            }
          
            Query q = session.createQuery(queryString);
            
            for(Pair<String, Object> arg : filterConstraints.arguments_)
            {
                q.setParameter(arg.getKey(), arg.getValue());
            }
            
            return ((Long)q.uniqueResult()).longValue();
    }
    
    @SuppressWarnings("unchecked")
    public List<ResistanceInterpretationTemplate> getResRepTemplates(int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "from ResistanceInterpretationTemplate as resistanceInterpretationTemplate ";
        
        return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, false);
    }
    
    public UserAttribute getUserAttribute(SettingsUser uid, String name)
    {
        Query q = session.createQuery("from UserAttribute ua " + 
        "where ua.settingsUser = :uid and ua.name = :name");

        q.setParameter("uid", uid);
        q.setParameter("name", name);

        return (UserAttribute)q.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<SettingsUser> getUsersByEnabled(int firstResult, int maxResults, String sortField, boolean ascending, boolean enabled, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "from SettingsUser as settingsUser ";
        
        queryString += "where not settingsUser.uid = :uid ";
        
        queryString += "and enabled " + (enabled?"is not null":"= null");
        
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += " and " + filterConstraints.clause_;
        }
        
        queryString += " order by " + sortField + (ascending?" asc":" desc");
    
      	Query q = session.createQuery(queryString);
        
        q.setParameter("uid", login.getUid());
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        
        return q.list();
    }
    
    public long getSettingsUserCountByEnabled(HibernateFilterConstraint filterConstraints, boolean enabled) 
    {
         String queryString = "select count(settingsUser) from SettingsUser as settingsUser ";
            
         queryString += "where not settingsUser.uid = :uid ";
         
         queryString += "and enabled " + (enabled?"is not null":"= null");
         
         if(!filterConstraints.clause_.equals(" "))
         {
             queryString += " and " + filterConstraints.clause_;
         }
         
         Query q = session.createQuery(queryString);
         
         q.setParameter("uid", login.getUid());
         
         for(Pair<String, Object> arg : filterConstraints.arguments_)
         {
             q.setParameter(arg.getKey(), arg.getValue());
         }
         
         return ((Long)q.uniqueResult()).longValue();
    }
    
    @SuppressWarnings("unchecked")
    public List<SettingsUser> getUsersWhitoutLoggedin(int firstResult, int maxResults, String sortField, HibernateFilterConstraint filterConstraints, String uid)
    {
        String queryString = "from SettingsUser as settingsUser ";
        
        queryString += "where not settingsUser.uid = :uid";
        
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += " and " + filterConstraints.clause_;
        }
        
        queryString += " order by " + sortField;
    
        Query q = session.createQuery(queryString);
        
        q.setParameter("uid", uid);
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        
        return q.list();
    }
    
    public long getSettingsUserCount(HibernateFilterConstraint filterConstraints) 
    {
         String queryString = "select count(settingsUser) from SettingsUser as settingsUser ";
            
         if(!filterConstraints.clause_.equals(" "))
         {
             queryString += "where " + filterConstraints.clause_;
         }
          
            Query q = session.createQuery(queryString);
            
            for(Pair<String, Object> arg : filterConstraints.arguments_)
            {
                q.setParameter(arg.getKey(), arg.getValue());
            }
            
            return ((Long)q.uniqueResult()).longValue();
    }
    
    public Test getTest(String testDescription, String testTypeDescription)
    {
        String queryString = "from Test as test where test.description = :testDescription and test.testType.description = :testTypeDescription";
        
        Query q = session.createQuery(queryString);
        q.setParameter("testDescription", testDescription);
        q.setParameter("testTypeDescription", testTypeDescription);
        
        return (Test)q.uniqueResult();
    }
    
    public Test getTest(int test_ii)
    {
        String queryString = "from Test as test where test.id = :id";
        
        Query q = session.createQuery(queryString);
        q.setParameter("id", test_ii);
        
        return (Test)q.uniqueResult();
    }

    public TestType getTestType(String description) {
        getTestTypeQuery.setParameter("description", description);
        
        return (TestType)getTestTypeQuery.uniqueResult();
       
    }

    public DrugGeneric getGenericDrug(String genericId) {
        getDrugGenericQuery.setParameter("genericId", genericId);
        
        return (DrugGeneric)getDrugGenericQuery.uniqueResult();
    }

    public DrugCommercial getCommercialDrug(String name) {
        getCommercialDrugQuery.setParameter("name", name);
        
        return (DrugCommercial)getCommercialDrugQuery.uniqueResult();
       
    }
    
    @SuppressWarnings("unchecked")
    public List<QueryDefinition> getQueryDefinitions(int firstResult, int maxResults, String sortField, HibernateFilterConstraint filterConstraints, boolean ascending)
    {
        String queryString = "from QueryDefinition as queryDefinition ";
        
        return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, false);
    }
    
    public long getQueryDefinitionCount(HibernateFilterConstraint filterConstraints) 
    {
         String queryString = "select count(queryDefinition) from QueryDefinition as queryDefinition ";
            
         if(!filterConstraints.clause_.equals(" "))
         {
             queryString += "where " + filterConstraints.clause_;
         }
          
         Query q = session.createQuery(queryString);
            
         for(Pair<String, Object> arg : filterConstraints.arguments_)
         {
        	 q.setParameter(arg.getKey(), arg.getValue());
         }
            
         return ((Long)q.uniqueResult()).longValue();
    }
    
    @SuppressWarnings("unchecked")
    public List<QueryDefinitionParameterType> getQueryDefinitionParameterTypes() 
    {
        Query q = session.createQuery("from QueryDefinitionParameterType");
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
	public List<Dataset> getDatasets(int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints)
	{
		String queryString = "from Dataset as dataset ";
        
        return getLFS(queryString, firstResult, maxResults, sortField, ascending, filterConstraints, false);
	}

	public long getDatasetCount(HibernateFilterConstraint filterConstraints) 
	{
		String queryString = "select count(description) from Dataset as dataset ";
        
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += "where " + filterConstraints.clause_;
        }
      
        Query q = session.createQuery(queryString);
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        return ((Long)q.uniqueResult()).longValue();
	}

	public String[] validateQuery(String query)
	{
		try
		{
			Query q = session.createQuery(query);
			
			return q.getNamedParameters();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
    public List<QueryDefinitionRun> getQueryDefinitionRuns(int firstResult, int maxResults, String sortField, HibernateFilterConstraint filterConstraints, boolean ascending, String uid)
    {
        String queryString = "from QueryDefinitionRun as queryDefinitionRun ";
        
        queryString += "where queryDefinitionRun.settingsUser.uid = :userId ";
        
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += "and " + filterConstraints.clause_;
        }
        
        queryString += " order by " + sortField + (ascending ? " asc" : " desc");
    
        Query q = session.createQuery(queryString);
        
        q.setParameter("userId", uid);
        
        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        
        return q.list();
    }
	
	public long getQueryDefinitionRunCount(HibernateFilterConstraint filterConstraints) 
    {
         String queryString = "select count(queryDefinitionRun) from QueryDefinitionRun as queryDefinitionRun ";
            
         if(!filterConstraints.clause_.equals(" "))
         {
             queryString += "where " + filterConstraints.clause_;
         }
          
         Query q = session.createQuery(queryString);
            
         for(Pair<String, Object> arg : filterConstraints.arguments_)
         {
        	 q.setParameter(arg.getKey(), arg.getValue());
         }
            
         return ((Long)q.uniqueResult()).longValue();
    }
	
    public DrugClass getDrugClass(String drugClassId)
    {
        Query q = session.createQuery("from DrugClass as drugclass where classId = :classId");
        
        q.setParameter("classId", drugClassId);
        
        return (DrugClass)q.uniqueResult();
    }
    
    public DrugGeneric getDrugGeneric(String drugGenericId)
    {
        Query q = session.createQuery("from DrugGeneric as druggeneric where genericId = :genericId");
        
        q.setParameter("genericId", drugGenericId);
        
        return (DrugGeneric)q.uniqueResult();
    }
    
    public DrugCommercial getDrugCommercial(String commercialName)
    {
        Query q = session.createQuery("from DrugCommercial as drugcommercial where name = :commercialName");
        
        q.setParameter("commercialName", commercialName);
        
        return (DrugCommercial)q.uniqueResult();
    }

    public TestObject getTestObject(String description) {
        getTestObjectQuery.setParameter("description", description);
        
        return (TestObject)getTestObjectQuery.uniqueResult();
    }

    public AttributeGroup getAttributeGroup(String groupName) {
        getAttributeGroupQuery.setParameter("groupName", groupName);
        
        return (AttributeGroup)getAttributeGroupQuery.uniqueResult();
    }

    public ValueType getValueType(String description) {
        getValueTypeQuery.setParameter("description", description);
        
        return (ValueType)getValueTypeQuery.uniqueResult();        
    }

    public AttributeNominalValue getAttributeNominalValue(Attribute attribute, String value) {
        getAttributeNominalValueQuery.setParameter("attribute", attribute);
        getAttributeNominalValueQuery.setParameter("value", value);
        
        try {
            return (AttributeNominalValue)getAttributeNominalValueQuery.uniqueResult();        
        } catch (RuntimeException e) {
            System.err.println("Exception for attribute value : " + attribute.getName() + " " + value);
            throw e;
        }
    }

    public TestNominalValue getTestNominalValue(TestType type, String value) {
        getTestNominalValueQuery.setParameter("type", type);
        getTestNominalValueQuery.setParameter("value", value);
        
        return (TestNominalValue)getTestNominalValueQuery.uniqueResult();        
    }

    public void clear() 
    {
        session.clear();
    }
    
    public Protein getProtein(int proteinId)
    {
        Query q = session.createQuery("from Protein as protein where id = :id");
        
        q.setParameter("id", proteinId);
        
        return (Protein)q.uniqueResult();
    }

    public ViralIsolate getViralIsolate(Integer vi_ii) 
    {
        Query q = session.createQuery("from ViralIsolate as vi where id = :id");
        
        q.setParameter("id", vi_ii);
        
        return (ViralIsolate)q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DrugClass> getDrugClassesSortedOnResistanceRanking() 
    {
        Query q = session.createQuery("from DrugClass as dc where dc.resistanceTableOrder is not null order by dc.resistanceTableOrder");
        
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<DrugGeneric> getDrugGenericSortedOnResistanceRanking(DrugClass drugClass) 
    {
        Query q = session.createQuery("from DrugGeneric as dg where dg.drugClass = :drugClass and dg.resistanceTableOrder is not null order by dg.resistanceTableOrder");
        
        q.setParameter("drugClass", drugClass);
        
        return q.list();
    }

    public void flush() 
    {
        session.flush();
    }
}
