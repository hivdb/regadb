package net.sf.wts.services;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import net.sf.wts.services.util.Service;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;
import net.sf.wts.services.util.Status;

import org.apache.commons.io.FileUtils;

public class StartImpl 
{
    public void exec(String sessionTicket, final String serviceName) throws RemoteException
    {
        final File sessionPath = Sessions.getSessionPath(sessionTicket);
        if(sessionPath==null)
            throw new RemoteException("Your session ticket is not valid");
        Service service = Settings.getService(serviceName);
        if(service==null)
            throw new RemoteException("Service \"" + serviceName + "\" is not available");
        if(!Sessions.isSessionForService(sessionTicket, serviceName))
            throw new RemoteException("This ticket is not valid for service \"" + serviceName + "\"");
        
        final File startScript = Settings.getServiceStartScriptPath(serviceName);
        
        String status = Status.getStatus(sessionPath);
        if(status.equals("RUNNING"))
            throw new RemoteException("Service is runnning already");
            
        Thread jobRunningThread = new Thread(new Runnable()
        {
            public void run()
            {
                try 
                {
                    ProcessBuilder pb = new ProcessBuilder(startScript.getAbsolutePath(), sessionPath.getAbsolutePath());
                    Process p = pb.start();
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                } 
            }
        });
        
        jobRunningThread.start();
    }
}
