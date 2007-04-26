package net.sf.wts.client.meta;

import java.rmi.RemoteException;

import net.sf.wts.client.util.AxisClient;

public class WtsMetaClient 
{
    private String url_;
    private AxisClient axisService = new AxisClient();
    
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
    
    public byte[] getServiceDescription(String serviceName)
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "GetServiceDescription");
        
        byte [] result = null;
        
        axisService.addParameter(serviceName);
        
        try 
        {
            result = axisService.callAndGetByteArrayResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return result;
    }

    public String getUrl() 
    {
        return url_;
    }
    
    public static void main(String [] args)
    {
        WtsMetaClient client = new WtsMetaClient("http://localhost:8080/wts/services/");
        System.err.println(client.listServices());
        byte[] array = client.getServiceDescription("regadb-align");
        System.err.println(new String(array));
    }
}
