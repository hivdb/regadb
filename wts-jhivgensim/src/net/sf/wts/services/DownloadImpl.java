package net.sf.wts.services;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import net.sf.wts.services.util.Service;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;
import net.sf.wts.services.util.Status;

import org.apache.commons.io.FileUtils;

public class DownloadImpl 
{
    public byte[] exec(String sessionTicket, String serviceName, String fileName) throws RemoteException
    {
        File sessionPath = Sessions.getSessionPath(sessionTicket);
        if(sessionPath==null)
            throw new RemoteException("Your session ticket is not valid");
        Service service = Settings.getService(serviceName);
        if(service==null)
            throw new RemoteException("Service \"" + serviceName + "\" is not available");
        if(!Sessions.isSessionForService(sessionTicket, serviceName))
            throw new RemoteException("This ticket is not valid for service \"" + serviceName + "\"");
        
        String status = Status.getStatus(sessionPath);
        if(status.equals(Status.RUNNING))
            throw new RemoteException("Service is still running");
        
        boolean found = false;
        for(String outputFileName : service.outputs_)
        {
            if(outputFileName.equals(fileName))
            {
                found = true;
                break;
            }
        }
        
        if(!found)
            throw new RemoteException("Service \"" + serviceName + "\" doesn't support outputfiles with name \""+ fileName +"\"");
        
        try 
        {
            File outputFile = new File(sessionPath.getAbsolutePath()+File.separatorChar+"outputs"+File.separatorChar+fileName);
            if(outputFile.exists())
            return FileUtils.readFileToByteArray(outputFile);
            else throw new RemoteException("Service \"" + serviceName + "\" doesn't have outputfiles with name \""+ fileName +"\"");
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        return new byte[0];
    }
}
