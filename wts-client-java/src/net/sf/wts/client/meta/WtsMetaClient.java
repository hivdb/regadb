package net.sf.wts.client.meta;

import java.rmi.RemoteException;

public class WtsMetaClient 
{
    private String url_;
    private AxisService axisService = new AxisService();
    
    public WtsMetaClient(String url)
    {
        url_ = url;
    }
    
    public String listServices()
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "ListServices");
        
        String services = "";
        
        try 
        {
            services = axisService.callAndGetStringResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return services;
    }

    public String getUrl() 
    {
        return url_;
    }
    
    public static void main(String [] args)
    {
        WtsMetaClient client = new WtsMetaClient("http://localhost:8080/wts/services/");
        System.err.println(client.listServices());
    }
}
