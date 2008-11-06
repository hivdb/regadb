package net.sf.wts.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Iterator;

import javax.xml.soap.AttachmentPart;

import net.sf.wts.services.util.Encrypt;
import net.sf.wts.services.util.Service;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;
import net.sf.wts.services.util.Status;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.commons.io.FileUtils;


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
        
        String status = Status.getStatus(sessionPath);
        if(!status.equals(Status.READY_FOR_JOB))
            throw new RemoteException("Service is runnning already");
        
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
        
        try 
        {
        	byte[] decryptedFile = Encrypt.decrypt(sessionTicket, file);
            FileUtils.writeByteArrayToFile(new File(sessionPath.getAbsolutePath()+File.separatorChar+"inputs"+File.separatorChar+fileName), decryptedFile);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
//        MessageContext msgContext= MessageContext.getCurrentContext();
//        Message request = msgContext.getRequestMessage();
//        
//        Iterator iterator = request.getAttachments();
//        
//        while (iterator.hasNext()) {
//        	try {
//        		AttachmentPart ap = (AttachmentPart)iterator.next();
//        		
//        		InputStream is = ap.getDataHandler().getDataSource().getInputStream();
//        		
//        		OutputStream os = new FileOutputStream(toWrite);
//        		
//        		byte[] buffer = new byte[4096];
//        		
//        		int read = 0;
//        		
//        		while ((read = is.read(buffer)) > 0) {
//        			os.write(buffer, 0, read);
//        		}
//    		}
//        	catch (Exception e) {
//    			//e.printStackTrace();
//    		}
//        }
    }
}
