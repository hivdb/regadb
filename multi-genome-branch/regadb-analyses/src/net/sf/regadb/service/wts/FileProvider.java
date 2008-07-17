package net.sf.regadb.service.wts;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import net.sf.wts.client.WtsClient;

public class FileProvider 
{
    private static final String fileProviderServiceName = "get-file-provider-file";
    
    public FileProvider() 
    {

    }
    
    public void getFile(String fileProvider, String fileName, File placeToDownload) throws RemoteException
    {
        WtsClient client = new WtsClient(RegaDBWtsServer.getUrl());

        try
        {
        String challenge = client.getChallenge("public");
        String ticket = client.login("public", challenge, "public", fileProviderServiceName);
        
        client.upload(ticket, fileProviderServiceName, "file_provider_name", fileProvider.getBytes());
        client.upload(ticket, fileProviderServiceName, "file_name", fileName.getBytes());
        client.start(ticket, fileProviderServiceName);
        while(true)
        {
            if(!client.monitorStatus(ticket, fileProviderServiceName).equals("RUNNING"))
            {
                break;
            }
        }
        client.download(ticket, fileProviderServiceName, "file_provider_file", placeToDownload);
        
        client.closeSession(ticket, fileProviderServiceName);
        }
        catch(RemoteException re)
        {
            re.printStackTrace();
        } 
        catch (MalformedURLException e) 
        {
            e.printStackTrace();
        }
    }
}
