package net.sf.wts.services;

import java.io.File;
import java.rmi.RemoteException;

import net.sf.wts.services.util.Service;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;
import net.sf.wts.services.util.Tail;

public class MonitorLogTailImpl 
{
    public byte[] exec(String sessionTicket, String serviceName, int numberLines) throws RemoteException
    {
        File sessionPath = Sessions.getSessionPath(sessionTicket);
        if(sessionPath==null)
            throw new RemoteException("Your session ticket is not valid");
        Service service = Settings.getService(serviceName);
        if(service==null)
            throw new RemoteException("Service \"" + serviceName + "\" is not available");
        if(!Sessions.isSessionForService(sessionTicket, serviceName))
            throw new RemoteException("This ticket is not valid for service \"" + serviceName + "\"");
        
        File monitorFile = new File(sessionPath.getAbsolutePath()+File.separatorChar+".monitor");
        if(!monitorFile.exists())
            throw new RemoteException("There is no monitor file available");
        
        return Tail.getTail(monitorFile, numberLines);
    }
}
