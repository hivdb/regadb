package net.sf.wts.services.meta;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import net.sf.wts.services.util.Settings;

import org.apache.commons.io.FileUtils;

public class GetServiceDescriptionImpl 
{
    public byte[] exec(String serviceName) throws RemoteException
    {
        File descriptionFile = Settings.getDescription(serviceName);
        if(descriptionFile==null)
            throw new RemoteException("Service \"" + serviceName + "\" is not available");
        
        byte[] fileArray = null;
        
        try 
        {
            fileArray =  FileUtils.readFileToByteArray(descriptionFile);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        return fileArray;
    }
}
