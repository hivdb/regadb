package net.sf.wts.services;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import net.sf.wts.services.util.Service;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;
import net.sf.wts.services.util.Status;

import org.apache.commons.io.FileUtils;

public class CloseSessionImpl 
{
    public void exec(String sessionTicket, String serviceName) throws RemoteException
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
            throw new RemoteException("Service is still runnning, first stop the service");
        
        try 
        {
            FileUtils.deleteDirectory(sessionPath);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        Sessions.removeSessionTicket(sessionTicket);
    }
}
