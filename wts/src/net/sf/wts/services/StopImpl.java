package net.sf.wts.services;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import net.sf.wts.services.util.Service;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;
import net.sf.wts.services.util.Status;

import org.apache.commons.io.FileUtils;

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
//        File endedJob = new File(sessionPath.getAbsolutePath()+File.separatorChar+".ended");
        
        if(Sessions.removeProcess(sessionTicket)){
        	runningJob.delete();
            try 
            {
                FileUtils.writeByteArrayToFile(runningJob, "ENDED_KILLED".getBytes());
            } 
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
