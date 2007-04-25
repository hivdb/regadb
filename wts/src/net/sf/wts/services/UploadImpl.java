package net.sf.wts.services;

import java.io.File;
import java.rmi.RemoteException;

import net.sf.wts.services.util.FileUtils;
import net.sf.wts.services.util.Service;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;


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
        
        FileUtils.writeByteArrayToFile(file, new File(sessionPath.getAbsolutePath()+File.separatorChar+"inputs"+File.separatorChar+fileName));
    }
}
