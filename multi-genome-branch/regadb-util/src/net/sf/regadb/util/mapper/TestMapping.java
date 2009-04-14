package net.sf.regadb.util.mapper;

import org.jdom.Element;

public class TestMapping extends Mapping implements Mapper<TestResultMapping>{
    private String organismName;
    private String testTypeDescription;
    private String testDescription;
    
    private DefaultMapper<TestResultMapping> testResultMapper = new DefaultMapper<TestResultMapping>();
    
//    private static Map<String, TKVMap<Genome, TKVMap<TestType, Test>>> gtttMap = new HashMap<String, TKVMap<Genome,TKVMap<TestType, Test>>>();
//    static{
//        for(Test t : StandardObjects.getTests()){
//            addTest(t);
//        }
//    }
//    
//    private static void addTest(Test t){
//        Genome g = t.getTestType().getGenome();
//        
//        TKVMap<Genome, TKVMap<TestType, Test>> tttMap = gtttMap.get(g == null ? null : g.getOrganismName());
//        if(tttMap == null){
//            tttMap = new TKVMap<Genome, TKVMap<TestType, Test>>(g);
//            gtttMap.put(g.getOrganismName(), tttMap);
//        }
//
//        TKVMap<TestType, Test> tMap = tttMap.get(t.getTestType().getDescription());
//        if(tMap == null){
//            tMap = new TKVMap<TestType, Test>(t.getTestType());
//            tttMap.put(t.getTestType().getDescription(), tMap);
//        }
//        tMap.put(t.getDescription(), t);
//    }

    @Override
    protected void parseMapping(Element e) {
        setOrganismName(e.getAttributeValue("organism"));
        setTestTypeDescription(e.getAttributeValue("testtype"));
        setTestDescription(e.getAttributeValue("description"));

        Element ee = e.getChild("results");
        if(ee != null){
            for(Object o : ee.getChildren()){
                Element eee = (Element)o;
                TestResultMapping trm = new TestResultMapping();
                trm.parseXml(eee);
                testResultMapper.add(trm);
            }
        }
    }
    
    public String getOrganismName(){
        return organismName;
    }
    public void setOrganismName(String organismName){
        this.organismName = organismName;
    }
    
    public String getTestTypeDescription(){
        return testTypeDescription;
    }
    public void setTestTypeDescription(String testTypeDescription){
        this.testTypeDescription = testTypeDescription;
    }
    
    public String getTestDescription(){
        return testDescription;
    }
    public void setTestDescription(String testDescription){
        this.testDescription = testDescription;
    }

    public TestResultMapping get(String description) {
        return testResultMapper.get(description);
    }
    
    public String toString(){
        return getOrganismName() +":"+ getTestTypeDescription() +":"+ getTestDescription();
    }
}
