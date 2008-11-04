package net.sf.wts.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import net.sf.wts.client.util.AxisClient;
import net.sf.wts.client.util.Encrypt;

import org.apache.commons.io.FileUtils;

public class WtsClient implements IWtsClient
{
    private String url_;
    private AxisClient axisService = new AxisClient();
    
    public WtsClient(String url)
    {
        url_ = url;
    }
    
    public String getChallenge(String userName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "GetChallenge");
        
        axisService.addParameter(userName);
        
        String challenge = "";
        
        challenge = axisService.callAndGetStringResult();
        
        return challenge;
    }
    
    public String login(String userName, String challenge, String password, String serviceName) throws RemoteException, MalformedURLException
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
    
    public void upload(String sessionTicket, String serviceName, String fileName, byte[] file) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Upload");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        axisService.addParameter(fileName);
        axisService.addParameter(file);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
    }
    
    public byte[] download(String sessionTicket, String serviceName, String fileName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Download");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        axisService.addParameter(fileName);
        
        byte[] array = null;
        try 
        {
            array = axisService.callAndGetByteArrayResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return array;
    }
    
    public void download(String sessionTicket, String serviceName, String fileName, File toWrite) throws RemoteException, MalformedURLException
    {
        byte[] array = download(sessionTicket, serviceName, fileName);
        if(array!=null)
        {
            try 
            {
                FileUtils.writeByteArrayToFile(toWrite, array);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    public void upload(String sessionTicket, String serviceName, String fileName, File localLocation) throws RemoteException, MalformedURLException
    {
        byte[] array = null;
        try 
        {
            array = FileUtils.readFileToByteArray(localLocation);;
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        upload(sessionTicket, serviceName, fileName, array);
    }
    
    public byte[] monitorLogFile(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "MonitorLogFile");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);

        byte[] array = null;
        try 
        {
            array = axisService.callAndGetByteArrayResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return array;
    }
    
    public byte[] monitorLogTail(String sessionTicket, String serviceName, int numberOfLines) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "MonitorLogTail");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        axisService.addParameter(numberOfLines);

        byte[] array = null;
        try 
        {
            array = axisService.callAndGetByteArrayResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return array;
    }
    
    public String monitorStatus(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "MonitorStatus");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        String status = "";
        
        try 
        {
            status = axisService.callAndGetStringResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }

        return status;
    }
    
    public void start(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Start");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    
    public void closeSession(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "CloseSession");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    
    public void stop(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Stop");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public String getUrl() 
    {
        return url_;
    }
}
