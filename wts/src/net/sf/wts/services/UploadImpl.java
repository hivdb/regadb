package net.sf.wts.services;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import net.sf.wts.services.util.Service;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;
import net.sf.wts.services.util.Status;

import org.apache.commons.io.FileUtils;


public class UploadImpl 
{
    public void exec(String sessionTicket, String serviceName, String fileName, byte[] file) throws RemoteException
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
        if(!status.equals(Status.READY_FOR_JOB))
            throw new RemoteException("Service is runnning already");
        
        boolean found = false;
        for(String inputFileName : service.inputs_)
        {
            if(inputFileName.equals(fileName))
            {
                found = true;
                break;
            }
        }
        
        if(!found)
            throw new RemoteException("Service \"" + serviceName + "\" does not accept inputfile with name \""+ fileName +"\"");
        
        try 
        {
            FileUtils.writeByteArrayToFile(new File(sessionPath.getAbsolutePath()+File.separatorChar+"inputs"+File.separatorChar+fileName), file);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
