package net.sf.wts.client.meta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import net.sf.wts.client.util.AxisClient;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

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
    
    private ArrayList<String> parseIONames(byte[] array, String main, String sub)
    {
        ArrayList<String> puts = new ArrayList<String>();
        InputStream is = new ByteArrayInputStream(array);
        
        SAXBuilder builder = new SAXBuilder();
        
        Document doc = new Document();
        
        try
        {
            doc = builder.build(is);
        }
        catch (JDOMException e1)
        {
            e1.printStackTrace();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        
        Element rootElement = doc.getRootElement();
        
        List<Element> childrenOfInput = (rootElement.getChild(main)).getChild(sub).getChildren();
        
        for(Element e : childrenOfInput)
        {
            if(e.getName().equals("name"))
            {
                puts.add(e.getValue());
            }
        }
        
        return puts;
    }
    
    public ArrayList<String> parseInputNames(byte[] array)
    {
        return parseIONames(array, "inputs", "input");
    }
    
    public ArrayList<String> parseOutputNames(byte[] array)
    {
        return parseIONames(array, "outputs", "output");
    }
    
    public static void main(String [] args)
    {
        WtsMetaClient client = new WtsMetaClient("http://zolder:8080/wts/services/");
        System.err.println(client.listServices());
        byte[] array = client.getServiceDescription("regadb-hiv-resist");
        System.err.println(new String(array));
    }
}
