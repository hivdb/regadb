/*
 * Created on Dec 14, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.login.DbException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.pair.Pair;
import net.sf.regadb.util.settings.Filter;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
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
    private final Query getTestTypeGenomeQuery;
    private final Query getTestTypeNoGenomeQuery;
    private final Query getAttributeQuery;
    private final Query getAmbiguousAttributeQuery;
    private final Query getPatientQuery;
    private final Query getDrugGenericQuery;
    private final Query getCommercialDrugQuery;
    private final Query getViralIsolateQuery;
    private final Query getEventQuery;
    private final Query getEventNominalValueQuery;

    private final Query getGenomeQuery;
    private final Query getOpenReadingFrameQuery;
    private final Query getProteinQuery;
    private final Query getSplicingPositionQuery;

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

        getTestTypeGenomeQuery   = session.createQuery("from TestType as testType where testType.description = :description and (testType.genome is not null) and (testType.genome.organismName = :organismName)");
        getTestTypeNoGenomeQuery = session.createQuery("from TestType as testType where testType.description = :description and testType.genome is null");

        getAttributeQuery = session.createQuery("from Attribute attribute where attribute.name = :name and attribute.attributeGroup.groupName = :groupName");
        getAmbiguousAttributeQuery = session.createQuery("from Attribute attribute where lower(attribute.name) = lower(:name)");
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
        
        getGenomeQuery = session.createQuery("from Genome g where g.organismName = :organismName");
        getOpenReadingFrameQuery = session.createQuery("from OpenReadingFrame orf where orf.genome = :genome and orf.name = :name");
        getProteinQuery = session.createQuery("from Protein p where p.openReadingFrame = :openReadingFrame and p.abbreviation = :abbreviation");
        getSplicingPositionQuery = session.createQuery("from SplicingPosition sp where sp.protein = :protein and sp.ntPosition = :ntPosition");
    }
    
    private void begin() {
        session.beginTransaction();
    }
    
    public void commit() throws DbException {
		try {
			this.session.getTransaction().commit();
		} catch (HibernateException he) {
			this.session.getTransaction().rollback();
			this.session.clear();
			throw new DbException("Could not commit to database: ", he);
		}
	}

    public void rollback() {
        session.getTransaction().rollback();
    }

    public void clearCache() {
        session.clear();
    }
    
    public void clearCache(Object o) {
    	session.evict(o);
    }

    public Query createQuery(String query)
    {
    	Query q = session.createQuery(query);
    	
    	return q;
    }
    
    public List<NtSequence> getSequences()
    {
    	Query q = session.createQuery("from NtSequence");
        
        return (List<NtSequence>)q.list();
    }
    
    public NtSequence getSequence(int id)
    {
        Query q = session.createQuery("from NtSequence where id = :id");
        
        q.setParameter("id", id);
        
        return (NtSequence)q.uniqueResult();
    }
    
    public List<ViralIsolate> getViralIsolates()
    {
    	Query q = session.createQuery("from ViralIsolate");
        
        return q.list();
    }
    
    public Long getViralIsolateCount()
    {
    	Query q = session.createQuery("select count(viral_isolate_ii) from ViralIsolate as viralIsolate");
        return (Long)q.uniqueResult();
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
    
    public Patient getPatientByIi(int ii)
    {
        Query q = session.createQuery(
                "select new net.sf.regadb.db.Patient(patient, max(access.permissions)) " +
                getPatientsQuery() + 
                "and patient.patientIi = :ii "+
                "group by patient");
        
        q.setParameter("uid", login.getUid());
        q.setParameter("ii", ii);

        return (Patient)q.uniqueResult();
    }
    
    public Patient getPatientBySampleId(String sampleId)
    {
        Query q = session.createQuery(
                "select new net.sf.regadb.db.Patient(patient, max(access.permissions)) from ViralIsolate vi join vi.patient as patient " +
        		"join patient.patientDatasets as patient_dataset " +
                "join patient_dataset.id.dataset as dataset " +
                "join dataset.datasetAccesses access " +
                "where ( access.permissions >= 1 " +
                "and access.id.settingsUser.uid = :uid ) and (vi.sampleId = :sampleId) group by patient order by patient.id");
        
        q.setParameter("uid", login.getUid());
        q.setParameter("sampleId", sampleId);

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
                "and access.id.settingsUser.uid = :uid ) and ( "+ filter.clause_ +" ) group by patient order by patient.id");
        
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
    
    @SuppressWarnings("unchecked")
    public List<Attribute> getAttributes(String name) {
        getAmbiguousAttributeQuery.setParameter("name", name);
        
        return (List<Attribute>) getAmbiguousAttributeQuery.list();
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
        Query q = session.createQuery("from Test test order by test.id");
        
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Test> getTests(TestType testType) 
    {
        Query q = session.createQuery("from Test test where test.testType.testTypeIi = :testTypeIdParam order by test.id");

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
    
    public PatientEventValue getNewestPatientEventValue(Event event, Patient p) {
        Query q = session.createQuery("from PatientEventValue pev where pev.event.name = :eventName and pev.patient.id = :patientId order by pev.startDate desc");

        q.setParameter("eventName", event.getName());
        q.setParameter("patientId", p.getPatientIi());
        q.setMaxResults(1);
        
        if(q.list().size()>0)
            return (PatientEventValue)q.list().get(0);
        else
            return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<TestType> getTestTypes() 
    {
        Query q = session.createQuery("from TestType tt order by tt.id");
        
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<AnalysisType> getAnalysisTypes() {
        Query q = session.createQuery("from AnalysisType analysistype order by analysistype.id");
        
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<TherapyMotivation> getTherapyMotivations() {
        Query q = session.createQuery("from TherapyMotivation tm order by tm.id");
        
        return q.list();
    }

    @SuppressWarnings("unchecked")
	public List<TestType> getUsedTestsTypes(){
    	Query q = session.createQuery("select distinct t.testType from Test t");
    	return q.list();
    }

    public ResistanceInterpretationTemplate getResRepTemplate(String name) {
        getResistanceInterpretationTemplateQuery.setParameter("name", name);
        
        return (ResistanceInterpretationTemplate) getResistanceInterpretationTemplateQuery.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<ResistanceInterpretationTemplate> getResRepTemplates() {
        Query q = session.createQuery("from ResistanceInterpretationTemplate rit order by rit.id");
        
        return q.list();
    }
    
    public Test getTest(String description) {
        getTestQuery.setParameter("description", description);
        
        return (Test) getTestQuery.uniqueResult();
    }
    public Test getTestByGenome(String description, String organismName) {
        Query q;
        if(organismName == null || organismName.length() == 0)
            q = session.createQuery("select t from Test t where t.description = :description and t.testType.genome is null order by t.description");
        else{
            q = session.createQuery("select t from Test t join t.testType tt where t.description = :description and tt.genome is not null and tt.genome.organismName = :organismName order by t.description");
            q.setParameter("organismName", organismName);
        }
        q.setParameter("description", description);
        
        return (Test) q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DrugGeneric> getGenericDrugs() 
    {
        Query q = session.createQuery("from DrugGeneric dg order by dg.id");
        return q.list();
    }
    @SuppressWarnings("unchecked")
    public List<DrugGeneric> getGenericDrugsSorted() 
    {
        Query q = session.createQuery("from DrugGeneric dg order by dg.genericName");
        return q.list();
    }
    @SuppressWarnings("unchecked")
    public List<DrugGeneric> getGenericDrugsSorted(Filter genomeFilter)
    {
        Query q = session.createQuery("select dg from DrugGeneric dg join dg.genomes g where g.organismName like :organismFilter group by dg.id, dg.version, dg.genericName, dg.drugClass.id, dg.genericId, dg.resistanceTableOrder, dg.atcCode order by dg.genericName");
        q.setString("organismFilter", genomeFilter.getHqlString());
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<DrugCommercial> getCommercialDrugs() 
    {
        Query q = session.createQuery("from DrugCommercial dc order by dc.id");
        return q.list();
    }
    @SuppressWarnings("unchecked")
    public List<DrugCommercial> getCommercialDrugsSorted() 
    {
        Query q = session.createQuery("from DrugCommercial dc order by dc.name");
        return q.list();
    }
    @SuppressWarnings("unchecked")
    public List<DrugCommercial> getCommercialDrugsSorted(Filter genomeFilter) 
    {
        Query q = session.createQuery("select dc from DrugCommercial dc join dc.drugGenerics dg join dg.genomes g where g.organismName like :organismFilter " +
        		"group by dc.commercialIi, dc.name, dc.atcCode, dc.version order by dc.name");
        q.setString("organismFilter", genomeFilter.getHqlString());
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<DrugClass> getClassDrugs() 
    {
        Query q = session.createQuery("from DrugClass dc order by dc.id");
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Protein> getProteins() {
        Query q = session.createQuery("from Protein protein order by protein.id");
        return q.list();
    }
    @SuppressWarnings("unchecked")
    public List<Protein> getProteins(Genome genome) {
        Query q = session.createQuery("from Protein protein where protein.openReadingFrame.genome.organismName = :organismName order by protein.id");
        q.setParameter("organismName", genome.getOrganismName());
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<ValueType> getValueTypes()
    {
        Query q = session.createQuery("from ValueType vt order by vt.id");
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<AttributeGroup> getAttributeGroups()
    {
        Query q = session.createQuery("from AttributeGroup ag order by ag.id");
        return q.list();
    }
    
    /*
     * Dataset queries
     */

    /**
     * Obtain a list of all datasets in the database.
     */
    @SuppressWarnings("unchecked")
    public List<Dataset> getDatasets() {
        Query q = session.createQuery("from Dataset dataset order by dataset.id");
        
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
    
    private Query getPatientsQuery(Dataset dataset){
        Query q = session.createQuery(
                "select new net.sf.regadb.db.Patient(patient, max(access.permissions)) " +
                "from PatientImpl as patient " +
                "join patient.patientDatasets as patient_dataset " +
                "join patient_dataset.id.dataset as dataset " +
                "join dataset.datasetAccesses access " +
                "where dataset = :dataset " +
                "and access.permissions >= 1 " +
                "and access.id.settingsUser.uid = :uid " +
                "group by patient order by patient");
        q.setParameter("dataset", dataset);
        q.setParameter("uid", login.getUid());
        return q;
    }
    @SuppressWarnings("unchecked")
    public List<Patient> getPatients(Dataset dataset) {
        Query q = getPatientsQuery(dataset);
        return q.list();
    }
    @SuppressWarnings("unchecked")
    public List<Patient> getPatients(Dataset dataset, int firstResult, int maxResult) {
        Query q = getPatientsQuery(dataset);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResult);
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
                "group by patient order by patient");
        q.setParameter("uid", login.getUid());

        return q.list();
    }
    
    /**
     * Returns all patients, checking access permissions.
     */
    @SuppressWarnings("unchecked")
    public ScrollableResults getPatientsScrollable() {
        Query q = session.createQuery(
                "select new net.sf.regadb.db.Patient(patient, max(access.permissions)) " +
                getPatientsQuery() + 
                "group by patient order by patient");
        q.setParameter("uid", login.getUid());

        return q.scroll();
    }
    
    public ScrollableResults getPatientsScrollable(int offset, int maxResults) {
        Query q = session.createQuery(
                "select new net.sf.regadb.db.Patient(patient, max(access.permissions)) " +
                getPatientsQuery() + 
                "group by patient order by patient");
        q.setParameter("uid", login.getUid());
        q.setFirstResult(offset);
        q.setMaxResults(maxResults);

        return q.scroll();
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
        queryString += " order by " + sortField + (ascending?" asc":" desc") +", patient ";
        
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
    
    private String getPatientFromWhereClause(HibernateFilterConstraint filterConstraints, List<Attribute> attributes){
        StringBuilder from = new StringBuilder(" from PatientImpl p ");
        
        int i=0;
        for(Attribute a : attributes){
            String av = "av"+i;
            
            from.append(" left outer join p.patientAttributeValues ");
            if(a.getAttributeNominalValues().size() == 0){
                from.append(av +" with "+ av +".attribute.attributeIi = "+ a.getAttributeIi() +" " );
                //from.append(av +" with ( p.patientIi = "+ av +".patientIi and "+ av +".attributeIi = "+ a.getAttributeIi() +") " );
            }
            else{
                String pav = "p"+ av;
                String att = "a"+i;
                from.append(pav +" with "+ pav +".attribute.attributeIi = "+ a.getAttributeIi() +" left outer join "+ pav +".attributeNominalValue "+ av +" ");
                //from.append(pav +" with p.patientIi = "+ pav +".patientIi left outer join AttributeNominalValue "+ av +" with "+ av +" = "+ pav  );
            }
            
            ++i;
        }
        
        String datasetQuery = " join p.patientDatasets as patient_dataset join patient_dataset.id.dataset as dataset join dataset.datasetAccesses access where access.permissions >= 1 and access.id.settingsUser.uid = :uid ";
        String queryString = from.toString() + datasetQuery+" ";
        
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += "and" + filterConstraints.clause_;
        }
        
        return queryString;
    }
    
    @SuppressWarnings("unchecked")
    public List<Object[]> getPatientWithAttributeValues(int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints, List<Attribute> attributes)
    {
        String fromWhere = getPatientFromWhereClause(filterConstraints, attributes);

        StringBuilder select = new StringBuilder("select p.patientIi");
        for(int i=0; i< attributes.size(); ++i)
            select.append(", av"+ i +".value");
        
        String queryString = select.toString() + fromWhere;
        queryString += " order by " + sortField + (ascending?" asc":" desc") +", p";
        
        Query q = session.createQuery(queryString);
        q.setParameter("uid", login.getUid());

        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);

        List<Object[]> l;
        if(attributes.size() == 0){
            l = new ArrayList<Object[]>();
            
            for(Object o : q.list())
                l.add(new Object[]{getPatientByIi((Integer)o)});
        }
        else{
            l = q.list();
            for(Object[] o : l)
                o[0] = getPatientByIi((Integer)o[0]);
        }
        return l;
    }
    
    public long getPatientWithAttributeValuesCount(HibernateFilterConstraint filterConstraints, List<Attribute> attributes){
        String select = "select count(p) ";
        String fromWhere = getPatientFromWhereClause(filterConstraints, attributes);
        String queryString = select + fromWhere;
        
        Query q = session.createQuery(queryString);
        q.setParameter("uid", login.getUid());

        for(Pair<String, Object> arg : filterConstraints.arguments_)
        {
            q.setParameter(arg.getKey(), arg.getValue());
        }
        return ((Long)q.uniqueResult()).longValue();
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
        newSu.setRole(user.getRole());
        
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
    
    //Get a limited, filtered and sorted list
    private List getLFS(String queryString, String uniqueField, int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints, boolean and) {
        if(!filterConstraints.clause_.equals(" "))
        {
            queryString += (and ? " and" : " where" ) + filterConstraints.clause_;
        }
        queryString += " order by " + sortField + (ascending?" asc":" desc") +", "+ uniqueField;
    
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
        String queryString = "select testResult from TestResult as testResult left outer join testResult.test.testType.genome as genome " +
                            "left outer join testResult.testNominalValue as testNominalValue "+
                            "where testResult.patient.patientIi = " + patient.getPatientIi() + " " +
                            "and testResult.viralIsolate is null " +
                            "and testResult.ntSequence is null";
        return getLFS(queryString, "testResult", firstResult, maxResults, sortField, ascending, filterConstraints, true);
    }
    
    public long getNonViralIsolateTestResultsCount(Patient patient, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "select count(testResult) " +
                            "from TestResult as testResult left outer join testResult.test.testType.genome as genome " +
                            "left outer join testResult.testNominalValue as testNominalValue "+
                            "where testResult.patient.patientIi = " + patient.getPatientIi() + " " +
                            "and testResult.viralIsolate is null " + 
                            "and testResult.ntSequence is null";
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
    	return getLFS(queryString, "patient_event_value", firstResult, maxResults, sortField, ascending, filterConstraints, true);
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
    	return getLFS(queryString, "event", firstResult, maxResults, sortField, ascending, filterConstraints, false);
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
        String queryString = "select therapy from Therapy as therapy " +
                            "where therapy.patient.id = " + patient.getPatientIi();
        return getLFS(queryString, "therapy", firstResult, maxResults, sortField, ascending, filterConstraints, true);
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
        return getLFS(queryString, "viralIsolate", firstResult, maxResults, sortField, ascending, filterConstraints, true);
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
        
        return getLFS(queryString, "attribute", firstResult, maxResults, sortField, ascending, filterConstraints, false);
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
        
        return getLFS(queryString, "attributeGroup", firstResult, maxResults, sortField, ascending, filterConstraints, false);
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
		Query q=session.createQuery("from TestObject tobj order by tobj");
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public List<TestType> getTestTypes(int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints) 
	{
		String queryString = "select testType from TestType as testType left outer join testType.genome as genome";
        
        return getLFS(queryString, "testType", firstResult, maxResults, sortField, ascending, filterConstraints, false);
	}

	public long getTestTypeCount(HibernateFilterConstraint filterConstraints) 
	{
		 String queryString = "select count(testType) from TestType as testType left outer join testType.genome as genome ";
	        
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
		 String queryString = "select count(test) from Test as test left outer join test.testType.genome as genome ";
	        
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
		String queryString = "select test from Test as test left outer join test.testType.genome as genome";
        
        return getLFS(queryString, "test", firstResult, maxResults, sortField, ascending, filterConstraints, false);
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
        
        return getLFS(queryString, "resistanceInterpretationTemplate", firstResult, maxResults, sortField, ascending, filterConstraints, false);
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
    public List<SettingsUser> getSettingsUsers(int firstResult, int maxResults, String sortField, boolean ascending, HibernateFilterConstraint filterConstraints)
    {
        String queryString = "from SettingsUser as settingsUser ";
        
        queryString += "where not settingsUser.uid = :uid ";
        
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
    
    public long getSettingsUsersCount(HibernateFilterConstraint filterConstraints) 
    {
         String queryString = "select count(settingsUser) from SettingsUser as settingsUser ";
            
         queryString += "where not settingsUser.uid = :uid ";
         
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

    //ambiguous
    public Test getTest(String testDescription, String testTypeDescription)
    {
        String queryString = "from Test as test where test.description = :testDescription and test.testType.description = :testTypeDescription";
        
        Query q = session.createQuery(queryString);
        q.setParameter("testDescription", testDescription);
        q.setParameter("testTypeDescription", testTypeDescription);
        
        return (Test)q.uniqueResult();
    }

    public Test getTest(String testDescription, String testTypeDescription, String organismName)
    {
        Query q;
        String queryString = "from Test as test where test.description = :testDescription and test.testType.description = :testTypeDescription and ";
        if(organismName != null && organismName.length() > 0){
            queryString += "(test.testType.genome is not null) and (test.testType.genome.organismName = :organismName)";
            q = session.createQuery(queryString);
            q.setParameter("organismName", organismName);
        }
        else{
            queryString += "(test.testType.genome is null)";
            q = session.createQuery(queryString);
        }
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

    public TestType getTestType(TestType t){
    	if (t == null)
    		return null;
    	
        return getTestType(t.getDescription(), (t.getGenome() != null ? t.getGenome().getOrganismName():null));
    }
    public TestType getTestType(String description, String organismName) {
        if(organismName != null && organismName.length() > 0){
            getTestTypeGenomeQuery.setParameter("description", description);
            getTestTypeGenomeQuery.setParameter("organismName", organismName);
            return (TestType)getTestTypeGenomeQuery.uniqueResult();
        }
        else{
            getTestTypeNoGenomeQuery.setParameter("description", description);
            return (TestType)getTestTypeNoGenomeQuery.uniqueResult();
        }
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
    public List<QueryDefinition> getQueryDefinitions(int firstResult, int maxResults, String sortField, HibernateFilterConstraint filterConstraints, boolean ascending, int queryType)
    {
        String queryString = "from QueryDefinition as queryDefinition where queryDefinition.queryTypeIi = " + queryType;
        
        return getLFS(queryString, "queryDefinition", firstResult, maxResults, sortField, ascending, filterConstraints, true);
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
        
        return getLFS(queryString, "dataset", firstResult, maxResults, sortField, ascending, filterConstraints, false);
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
        
        queryString += " order by " + sortField + (ascending ? " asc" : " desc") +", queryDefinitionRun";
    
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
    
    @SuppressWarnings("unchecked")
    public List<Genome> getGenomes(){
        return session.createQuery("from Genome").list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Genome> getGenomesSorted(){
        return session.createQuery("from Genome g order by g.organismName").list();
    }
    
    public Genome getGenome(String organismName){
        getGenomeQuery.setParameter("organismName", organismName);
        return (Genome)getGenomeQuery.uniqueResult();
    }
    
    public OpenReadingFrame getOpenReadingFrame(Genome genome, String name){
        getOpenReadingFrameQuery.setParameter("genome", genome);
        getOpenReadingFrameQuery.setParameter("name", name);
        return (OpenReadingFrame)getOpenReadingFrameQuery.uniqueResult();
    }
    
    public Protein getProtein(OpenReadingFrame orf, String abbreviation){
        getProteinQuery.setParameter("openReadingFrame", orf);
        getProteinQuery.setParameter("abbreviation", abbreviation);
        return (Protein)getProteinQuery.uniqueResult();
    }
    
    public SplicingPosition getSplicingPosition(Protein protein, int ntPosition){
        getSplicingPositionQuery.setParameter("protein", protein);
        getSplicingPositionQuery.setParameter("ntPosition", ntPosition);
        return (SplicingPosition)getSplicingPositionQuery.uniqueResult();
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
    
    public List<ViralIsolate> getViralIsolate(String sampleId) 
    {
        Query q = session.createQuery("from ViralIsolate as vi where vi.sampleId = :sampleId");
        
        q.setParameter("sampleId", sampleId);
        
        return q.list();
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
    
    @SuppressWarnings("unchecked")
    public List<ViralIsolate> getViralIsolatesSortedOnDate(Patient p) {
        String queryString = 
            " select vi " +
            " from ViralIsolate as vi " +
            " where vi.patient.id = " + p.getPatientIi() +
            " order by vi.sampleDate";
        
        Query q = session.createQuery(queryString);
        
        return q.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Therapy> getTherapiesSortedOnDate(Patient p) {
        String queryString = 
            " select t " +
            " from Therapy as t " +
            " where t.patient.id = " + p.getPatientIi() +
            " order by t.startDate";
        
        Query q = session.createQuery(queryString);
        
        return q.list();
    }
    
    public long getMaxAmountOfSequences() {
        String queryString = 
            " select count(ntseq) from NtSequence ntseq group by ntseq.viralIsolate.id order by count(ntseq) desc";
    
        Query q = session.createQuery(queryString);
        
        List result = q.list();
        
        if (result.size() == 0) {
        	return 0;
        } else {
        	return (Long)result.get(0);
        }
    }

    public void flush() 
    {
        session.flush();
    }
    
    public boolean isUsed(AttributeNominalValue anv){
    	if(anv != null && anv.getNominalValueIi() != null){
    		Query q = session.createQuery("select pav from PatientAttributeValue pav join pav.attributeNominalValue anv where anv = :anv");
    		q.setParameter("anv", anv);
    		q.setMaxResults(1);
    		return q.list().size() > 0;
    	}
    	return false;
    }
    public boolean isUsed(EventNominalValue env){
    	if(env != null && env.getNominalValueIi() != null){
    		Query q = session.createQuery("select pev from PatientEventValue pev join pev.eventNominalValue env where env = :env");
    		q.setParameter("env", env);
    		q.setMaxResults(1);
    		return q.list().size() > 0;
    	}
    	return false;
    }
    public boolean isUsed(TestNominalValue tnv){
    	if(tnv != null && tnv.getNominalValueIi() != null){
    		Query q = session.createQuery("select tnv from TestResult tr join tr.testNominalValue tnv where tnv = :tnv");
    		q.setParameter("tnv", tnv);
    		q.setMaxResults(1);
    		return q.list().size() > 0;
    	}
    	return false;
    }
    
    public boolean isUsedSampleId(String sampleId){
    	Query q = createQuery("select vi.id from ViralIsolate vi where vi.sampleId = :sampleId");
    	q.setParameter("sampleId", sampleId);
    	q.setMaxResults(1);
    	return q.list().size() > 0;
    }

    @SuppressWarnings("unchecked")
    public List<Test> getTests(TestObject to) 
    {
        Query q = session.createQuery("from Test test " +
        		"where test.testType.testObject.description = :testObject " +
        		"order by test.id");

        q.setParameter("testObject", to.getDescription());
        
        return q.list();
    }
    
    public boolean isActive() {
      return this.session.getTransaction().isActive();
    }
}