package net.sf.wts.services;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;

import net.sf.wts.services.util.EncryptedFileDataSource;
import net.sf.wts.services.util.Service;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;
import net.sf.wts.services.util.Status;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;

public class DownloadImpl 
{
    public byte[] exec(String sessionTicket, String serviceName, String fileName) throws RemoteException
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
            throw new RemoteException("Service is still running");
        
        boolean found = false;
        for(String outputFileName : service.outputs_)
        {
            if(outputFileName.equals(fileName))
            {
                found = true;
                break;
            }
        }
        
        if(!found)
            throw new RemoteException("Service \"" + serviceName + "\" doesn't support outputfiles with name \""+ fileName +"\"");
        
        try 
        {
            File outputFile = new File(sessionPath.getAbsolutePath()+File.separatorChar+"outputs"+File.separatorChar+fileName);
            if(outputFile.exists()){
            	DataSource ds = new EncryptedFileDataSource(outputFile,Sessions.getSessionKey(sessionTicket));
            	DataHandler dh = new DataHandler(ds);
            	
            	MessageContext msgContext= MessageContext.getCurrentContext();
            	Message response = msgContext.getResponseMessage();
            	AttachmentPart ap = response.createAttachmentPart();
            	ap.setDataHandler(dh);
            	
            	response.addAttachmentPart(ap);
            	response.saveChanges();
//            	byte[] temp = FileUtils.readFileToByteArray(outputFile);            	
//            	return Encrypt.encrypt(Sessions.getSessionKey(sessionTicket), temp);
            }
            else throw new RemoteException("Service \"" + serviceName + "\" doesn't have outputfiles with name \""+ fileName +"\"");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SOAPException e) {
			e.printStackTrace();
		}        
        return new byte[0];
    }
}
