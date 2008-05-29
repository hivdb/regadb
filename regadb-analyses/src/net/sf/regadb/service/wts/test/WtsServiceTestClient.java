package net.sf.regadb.service.wts.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.wts.client.WtsClient;

public class WtsServiceTestClient extends TestCase{
    WtsServiceTestClient testClient_;

    private String url;
    private String userName;
    private String password;
    
    private String hiv1bRefSeq = ">AR06-275\ntcagaacagaccagaaccaacagccccaccagaagagagcttcaggtttggggaggagayaacaactccccctcmgaagcgggagacgggagacamggaaytgtatcctttaacctccctcagatcactctttggcaacgacccctcgtcacaataaagataggggggcaactaaaagaagctctattagatacaggagcagatgatacagtattagaagacatgaatttgccaggaagatggaaaccaaaaatgatagggggaattggaggttttatcaaagtaagacagtatgatcagatacccatagaartttgtgggcataaagctataggtacagtgttagtaggacctacacctgtcaacataattgggagaaatttgttgactaaaattggctgtactttaaattttcccattagtcctattgagactgtaccagtaaaattaaagccaggaatggatgggccaaaagttaaacaatggccattgacagaagaaaaaataaaagcattagtagaaatttgtacagaaatggaaaaggaaggaaagatttcaagaattgggcctgaaaatccatacaatactccaatatttgccataaagaaaaaagacagtactaaatggagaaaattagtagatttcagagaacttaacaagagaactcaagacttctgggaagttcaattaggaataccacatcccgcagggctaaaaaagaaaaaatcagtaacagtactggatgtgggtgatgcatatttttcagtgcccttagataaagacttcaggaagtatactgcatttaccatacctagtacaaacaatgagacaccaggaattagatatcagtacaatgtgcttccacagggatggaaaggatcaccagcaatatttcaaagtgccatgacaaaaatcttagagccttttagaaaacaaaatccagacatagttatctatcaatacatggatgatttatatgtaggatctgatttagaaatagggcaacacagaacaagagtagaggaactaagacaacatctgttaaagtggggatttaccacaccagacaaaaagcatcagaaagaccctccatttctttggatgggttatgaactccatcctgataaatggacagtgcagcctatagtgctgccagaaaaagacagctggactgtcaatgacatacagaagttagtgggaaaattgaattgggcaagtcagatttacccagggattaaaataaagcaattatgtaagctccttaggggagccaaagcactaacagaagtaataacaatgacagaagaagcagagctagaactggcagaaaacagggagattctaaaagaaccagtacatggagcgtattatgacccatcaaaagacttaatagcagaaata";
    private String hiv1MutSeq = ">test vi\ncctcagatcactctttggcaacgacccmtcgtcacaataaaggtaggggggcaactaaaggaagctctattagatacaggagcagatgatacagtattagaagaactaaatttgccaggaaaatggaaaccaaaaatratagggggaattggaggttttgtcaaagtaagrcagtatgatcaggtacccatagaaatctgtggacataaagtcctaagtacagtattagtaggacctacacctgccaacataattggaaggaatttgttgactcarcttggttgcactttaaattttcccattagtcctattgaaactgtaccagtaaaattaaagccaggaatggatggcccaaaagttaaacaatggccattgacagaagaaaaaataaaagcattagtggaaatttgtacagaattagaaaasgaaggaaaaatttcaaaaattgggcctgaaaatccatataatactccagtattcgccataaagaaaaaaracagtactaagtggagaaaattagtagatttcagagaacttaataagagaactcaagacttctgggaagttcaattaggaataccacatcccgcagggttaaagaagaaaaaatcagtaacagtactggatgtgggtgatgcatatttttcaattcccttagacaaagacttcaggaagtatactgcwtttaccatacctagtataaacaatgagacaccagggattagatatcagtacaatgtgcttccacagggatggaaaggatcaccatcaatattccaaagtagcatgacaaaaatcttagagccttttagaaaacaaaatccagacatagttatctatcagtatatggacgatctgtatgtaggatctgacttagagatagggcagcatagaacaaaaatagaggaactgagagaacatctatggaagtggggattttwcacaccagacaaaaaacatcagaaagaacctccattcctttggatgggttatgaactccaccctgataaatggacagtacagcctatagtgctgccagaaaaggacagctggactgtcaatgacatacagaagttagtgggaaaattgaattgggcaagtcaratttacccagggattaaagtmaggcagttatgcaaactccttaggggaaccaaagcactaacagaaataataccactaacaaaagaagcagagttagaactggcagaaaatagggaaattctaaaagaaccagtacatggagcatattatgacccatcaaaagacttaatagcagaaatacagaagcaggagctaggt";
    
    protected void setUp() throws Exception
    {
        String url = "http://virolab.med.kuleuven.be/wts/services/";
        String userName = "public";
        String password = "public";
        
        System.setProperty("http.proxyHost", "www-proxy");
        System.setProperty("http.proxyPort", "3128");
        
        testClient_ = new WtsServiceTestClient(url,userName,password);
        
        
    }
    
    public WtsServiceTestClient(){
        
    }
    
    public WtsServiceTestClient(String url, String userName, String password){
        setUrl(url);
        setUserName(userName);
        setPassword(password);
    }
    
    public void testSubtype(){
        Map<String,byte[]> inputs = new HashMap<String,byte[]>();
        inputs.put("nt_sequence", getHiv1bRefSeq().getBytes());
        inputs.put("species", "hiv".getBytes());
        
        Map<String,IOutputValidator> outputs = new HashMap<String,IOutputValidator>();
        outputs.put("subtype", new StringOutputValidator("HIV-1 Subtype B"));
        
        testClient_.testService("regadb-subtype", inputs, outputs);
    }
    
    public void testBlast(){
        Map<String,byte[]> inputs = new HashMap<String,byte[]>();
        inputs.put("nt_sequence", getHiv1bRefSeq().getBytes());
        
        Map<String,IOutputValidator> outputs = new HashMap<String,IOutputValidator>();
        outputs.put("species", new StringOutputValidator("B.FR.83.HXB2 ACC K03455\n"
                                                            +"0.0\n"
                                                            +"1139"));
        
        testClient_.testService("regadb-blast", inputs, outputs);
    }
    
    public void testHivType(){
        Map<String,byte[]> inputs = new HashMap<String,byte[]>();
        inputs.put("nt_sequence", getHiv1bRefSeq().getBytes());
        
        Map<String,IOutputValidator> outputs = new HashMap<String,IOutputValidator>();
        outputs.put("type", new StringOutputValidator("HIV 1"));
        
        testClient_.testService("regadb-hiv-type", inputs, outputs);
    }

    public void testHivSubtype(){
        Map<String,byte[]> inputs = new HashMap<String,byte[]>();
        inputs.put("nt_sequence", getHiv1bRefSeq().getBytes());
        
        Map<String,IOutputValidator> outputs = new HashMap<String,IOutputValidator>();
        outputs.put("subtype", new StringOutputValidator("HIV-1 Subtype B"));
        
        testClient_.testService("regadb-hiv-subtype", inputs, outputs);
    }
    
    public void testHiv1Align(){
        Map<String,byte[]> inputs = new HashMap<String,byte[]>();
        inputs.put("nt_sequences", getHiv1MutSeq().getBytes());
        inputs.put("region", "RT".getBytes());

        Map<String,IOutputValidator> outputs = new HashMap<String,IOutputValidator>();
        outputs.put("aa_mutations", new StringOutputValidator("seqid,status,score,frameshifts,begin,end,mutations\ntest,Success,4600,0,1,335,M41L K43KN D67DN V118I E122K A158S Q207E L210W R211K L214F T215FY V292I E297K V317A G333E Q334L"));
        
        testClient_.testService("regadb-align", inputs, outputs);
    }

    public void testService(String serviceName, Map<String,byte[]> inputs, Map<String,IOutputValidator> outputs){
        
        try{
            WtsClient wtsClient = new WtsClient(getUrl());
            String challenge = wtsClient.getChallenge(getUserName());
            String sessionTicket = wtsClient.login(getUserName(), challenge, getPassword(), serviceName);
            
            for(Map.Entry<String,byte[]> e : inputs.entrySet()){
                wtsClient.upload(sessionTicket, serviceName, e.getKey(), e.getValue());
            }
            wtsClient.start(sessionTicket, serviceName);
            
            while(!wtsClient.monitorStatus(sessionTicket, serviceName).startsWith("ENDED")){
                Thread.sleep(500);
            }
            
            for(Map.Entry<String, IOutputValidator> e : outputs.entrySet()){
                byte[] res = wtsClient.download(sessionTicket, serviceName, e.getKey());//,new File("/home/simbre1/"+ e.getKey()));
                e.getValue().validate(res);
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setUrl(String url) {
        this.url = url;
    }

    private String getUrl() {
        return url;
    }

    private void setUserName(String userName) {
        this.userName = userName;
    }

    private String getUserName() {
        return userName;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private String getPassword() {
        return password;
    }

    private void setHiv1bRefSeq(String hiv1bRefSeq) {
        this.hiv1bRefSeq = hiv1bRefSeq;
    }

    private String getHiv1bRefSeq() {
        return hiv1bRefSeq;
    }

    private void setHiv1MutSeq(String hiv1MutSeq) {
        this.hiv1MutSeq = hiv1MutSeq;
    }

    private String getHiv1MutSeq() {
        return hiv1MutSeq;
    }

    public interface IOutputValidator{
        void validate(byte[] output);
    }
    
    public class StringOutputValidator implements IOutputValidator{
        private String expected;
        
        public StringOutputValidator(String expected){
            this.expected = expected;
        }
        public void validate(byte[] output){
            assertEquals(expected, new String(output).trim());
        }
    }
}
