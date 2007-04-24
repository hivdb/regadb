package net.sf.wts.services.meta;

import java.io.File;
import java.rmi.RemoteException;

import net.sf.wts.services.util.FileUtils;
import net.sf.wts.services.util.Settings;

public class GetServiceDescriptionImpl 
{
    public byte[] exec(String serviceName) throws RemoteException
    {
        File descriptionFile = Settings.getDescription(serviceName);
        if(descriptionFile==null)
            throw new RemoteException("Service \"" + serviceName + "\" is not available");
        return FileUtils.getByteArrayFromFile(descriptionFile);
    }
}
