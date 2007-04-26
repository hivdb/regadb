package net.sf.wts.services.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class Settings 
{
    private static String wtsPath_;
    private static long challengeExpireTime_;
    
    private static ArrayList<User> userSettings_ = new ArrayList<User>();
    
    private static ArrayList<Service> services_ = new ArrayList<Service>(); 
    
    private static String WTS_CONFIG_FILE = null;
    
    private static boolean initiated_ = false;
    
    public final static String mutex_ = "mutex";
    
    static
    {
        synchronized(Settings.mutex_)
        {
            if(!Settings.isInitiated())
            {
                Settings.init();
            }
        }
    }
    
    public static void init()
    {
        String osName = System.getProperty("os.name");
        osName = osName.toLowerCase();
        
        if(osName.startsWith("windows"))
            WTS_CONFIG_FILE = "C:\\Program files\\wt\\wts\\wts.xml";
        else
            WTS_CONFIG_FILE = "/etc/wt/wts/wts.xml";
        
        SAXBuilder builder = new SAXBuilder();
        
        Document doc = new Document();
        
        try 
        {
            doc = builder.build(new File(WTS_CONFIG_FILE));
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
        Element wts_dir = rootElement.getChild("wts-dir");
        wtsPath_ = wts_dir.getTextTrim();
        Element challenge_settings = rootElement.getChild("challenge-settings");
        challengeExpireTime_ = Long.parseLong(challenge_settings.getChildTextTrim("expire-time"));
        
        String thisLine;
        int indexOfSemiCol;
        try
        {
            InputStream is = new FileInputStream(new File(wtsPath_+File.separatorChar+"users"+File.separatorChar+"users.pwd"));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((thisLine = br.readLine()) != null)
            {
                User u = new User();
                indexOfSemiCol = thisLine.indexOf(':');
                u.userName_ = thisLine.substring(0, indexOfSemiCol);
                u.password_ = thisLine.substring(indexOfSemiCol+1);
                userSettings_.add(u);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        File servicesDir = new File(wtsPath_+File.separatorChar+"services"+File.separatorChar);
        for(File serviceDir : servicesDir.listFiles())
        {
            if(serviceDir.isDirectory())
            {
                parseService(new File(serviceDir.getAbsolutePath()+File.separatorChar+"service.xml"), serviceDir.getName());
            }
        }
        
        initiated_ = true;
    }
    
    private static void parseService(File serviceXmlFile, String serviceName)
    {
        System.err.println("parseService"+serviceXmlFile.getAbsolutePath());
        if(serviceXmlFile.exists())
        {
            SAXBuilder builder = new SAXBuilder();
            
            Document doc = new Document();
            
            try 
            {
                doc = builder.build(serviceXmlFile);
            } 
            catch (JDOMException e1) 
            {
                System.err.println("wrong services.xml file");
            } 
            catch (IOException e1) 
            {
                e1.printStackTrace();
            }
            
            Service service = new Service();
            Element rootElement;
            if(doc==null)
                return;
            rootElement = doc.getRootElement();
            if(rootElement!=null)
            {
                System.err.println("inputs");
                Element inputs = rootElement.getChild("inputs");
                if(inputs==null)
                    return;
                List inputsList = inputs.getChildren("input");
                if(inputsList.size()==0)
                    return;
                Element name;
                for(Object inp : inputsList)
                {
                    name = ((Element)inp).getChild("name");
                    if(name==null)
                        return;
                    service.inputs_.add(name.getTextTrim());
                }
                
                System.err.println("outputs");
                Element outputs = rootElement.getChild("outputs");
                if(inputs==null)
                    return;
                List outputsList = outputs.getChildren("output");
                if(inputsList.size()==0)
                    return;
                for(Object outp : outputsList)
                {
                    name = ((Element)outp).getChild("name");
                    if(name==null)
                        return;
                    service.outputs_.add(name.getTextTrim());
                }
                
                System.err.println("description");
                Element description = rootElement.getChild("description");
                if(description == null)
                    return;
                service.description_ = description.getTextTrim();
                
                System.err.println("version");
                Element version = rootElement.getChild("version");
                if(version == null)
                    return;
                service.version_ = version.getTextTrim();
                
                System.err.println("name");
                service.name_ = serviceName;
                
                System.err.println("add");
                services_.add(service);
            }
        }
    }

    public static long getChallengeExpireTime() 
    {
        return challengeExpireTime_;
    }

    public static String getWtsPath() 
    {
        return wtsPath_;
    }
    
    public static boolean isInitiated()
    {
        return initiated_;
    }
    
    public static String getHashedPassword(String userName)
    {
        for(User u : userSettings_)
        {
            if(u.userName_.equals(userName))
            {
                return u.password_;
            }
        }
        
        return null;
    }
    
    public static String[] getServicesList()
    {
        synchronized(services_)
        {
            String [] services = new String[services_.size()];
            
            System.err.println("size:"+services_.size());
            
            for(int i = 0; i < services_.size(); i++)
            {
                System.err.println("name:"+services_.get(i).name_);
                services[i] = services_.get(i).name_;
            }
            return services;
        }
    }
    
    public static File getDescription(String serviceName)
    {
        File servicesDir = new File(wtsPath_+File.separatorChar+"services"+File.separatorChar+serviceName);
        File serviceFile = new File(wtsPath_+File.separatorChar+"services"+File.separatorChar+serviceName+File.separatorChar+"service.xml");
        if(servicesDir.isDirectory() && servicesDir.exists() && serviceFile.isFile() && serviceFile.exists())
        {
            return serviceFile;
        }
        else return null;
    }
    
    public static Service getService(String serviceName)
    {
        for(Service s : services_)
        {
            if(s.name_.equals(serviceName))
            {
                return s;
            }
        }
        return null;
    }
    
    public static File getServiceStartScriptPath(String serviceName)
    {
        return new File(wtsPath_+File.separatorChar+"services"+File.separatorChar+serviceName+File.separatorChar+"startService");
    }
}
