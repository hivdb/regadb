package net.sf.wts.client;

import java.rmi.RemoteException;

import net.sf.wts.client.meta.AxisService;

public class WtsClient 
{
    private String url_;
    private AxisService axisService = new AxisService();
    
    public WtsClient(String url)
    {
        url_ = url;
    }
    
    public String getChallenge(String userName) throws RemoteException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "GetChallenge");
        
        axisService.addParameter(userName);
        
        String challenge = "";
        
        challenge = axisService.callAndGetStringResult();
        
        return challenge;
    }
    
    public String login(String userName, String challenge, String password, String serviceName) throws RemoteException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Login");
        
        axisService.addParameter(userName);
        axisService.addParameter(challenge);
        axisService.addParameter(Encrypt.encryptMD5(Encrypt.encryptMD5(password)+challenge));
        axisService.addParameter(serviceName);
        
        String sessionTicket = "";
        
        sessionTicket = axisService.callAndGetStringResult();
        
        return sessionTicket;
    }

    public String getUrl() 
    {
        return url_;
    }
    
    public static void main(String [] args)
    {
        WtsClient client = new WtsClient("http://localhost:8080/wts/services/");
        try 
        {
            String challenge = client.getChallenge("kdforc0");
            System.err.println(challenge);
            String ticket = client.login("kdforc0", challenge, "Vitabis1", "regadb-align");
            System.err.println(ticket);
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
    }
}
