package net.sf.wts.services;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.commons.io.FileUtils;

import net.sf.wts.services.util.Job;
import net.sf.wts.services.util.Service;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;
import net.sf.wts.services.util.Status;

public class StopImpl 
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
        
        String status = Status.getStatus(sessionPath);
        if(!status.equals(Status.RUNNING))
            throw new RemoteException("Service is no longer runnning");
        
        File runningJob = new File(sessionPath.getAbsolutePath()+File.separatorChar+".running");
        File endedJob = new File(sessionPath.getAbsolutePath()+File.separatorChar+".ended");
        
        Job toDestroy = null;
        for(Job j : Sessions.getProcesses())
        {
            if(j.sessionTicket_.equals(sessionTicket))
            {
                toDestroy = j;
                break;
            }
        }
        if(toDestroy!=null)
        {
            runningJob.delete();
            try 
            {
                FileUtils.writeByteArrayToFile(runningJob, "ENDED_KILLED".getBytes());
            } 
            catch (IOException e)
            {
                e.printStackTrace();
            }
            toDestroy.process_.destroy();
            Sessions.getProcesses().remove(toDestroy);
        }
    }
}
