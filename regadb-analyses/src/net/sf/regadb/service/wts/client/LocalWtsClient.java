package net.sf.regadb.service.wts.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Random;

import net.sf.wts.client.IWtsClient;

import org.apache.commons.io.FileUtils;

public class LocalWtsClient implements IWtsClient {
	File inputs;
	File outputs;
	File myTmpDir;
	
	boolean ended = false;
	
	File wtsDir;
	
	public LocalWtsClient(File wtsDir) {
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		Random r = new Random(new Date().getTime());

		do{
			myTmpDir = new File(tmpDir.getAbsolutePath() + File.separator + "wts_local_client" + r.nextInt(Integer.MAX_VALUE));
		}while(myTmpDir.exists());
		
		System.err.println("tmpDir" + myTmpDir);
		myTmpDir.mkdir();
		
		
		inputs = new File(myTmpDir.getAbsolutePath()+File.separatorChar+"inputs");
		inputs.mkdir();
		outputs = new File(myTmpDir.getAbsolutePath()+File.separatorChar+"outputs");
		outputs.mkdir();
		
		this.wtsDir = wtsDir;
	}
	
    public String getChallenge(String userName) throws RemoteException, MalformedURLException {
    	return "";
    }
    
    public String login(String userName, String challenge, String password, String serviceName) throws RemoteException, MalformedURLException {
    	return "";
    }
    
    public void upload(String sessionTicket, String serviceName, String fileName, byte[] file) throws RemoteException, MalformedURLException {
    	File f = new File(inputs.getAbsolutePath()+File.separatorChar+fileName);
    	try {
			FileUtils.writeByteArrayToFile(f, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public byte[] download(String sessionTicket, String serviceName, String fileName) throws RemoteException, MalformedURLException {
    	File f = new File(outputs.getAbsolutePath()+File.separatorChar+fileName);
    	try {
			return FileUtils.readFileToByteArray(f);
		} catch (IOException e) {
			return null;
		}
    }
    
    public void download(String sessionTicket, String serviceName, String fileName, File toWrite) throws RemoteException, MalformedURLException {
    	byte[] b = download(sessionTicket, serviceName, fileName);
    	try {
			FileUtils.writeByteArrayToFile(toWrite, b);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void upload(String sessionTicket, String serviceName, String fileName, File localLocation) throws RemoteException, MalformedURLException {
    	try {
			upload(sessionTicket, serviceName, fileName, FileUtils.readFileToByteArray(localLocation));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public byte[] monitorLogFile(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException {
    	return null;
    }
    
    public byte[] monitorLogTail(String sessionTicket, String serviceName, int numberOfLines) throws RemoteException, MalformedURLException {
    	return null;
    }
    
    public String monitorStatus(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException {
    	if(ended) {
    		return "ENDED";
    	} else {
    		return "RUNNING";
    	}
    }
    
    public void start(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException {
        final File startScript = new File(wtsDir.getAbsolutePath()+File.separatorChar+"services"+File.separatorChar+serviceName+File.separatorChar+"startService");
    	Thread jobRunningThread = new Thread(new Runnable()
        {
            public void run()
            {
                Process p = null;
                try 
                {
                    ProcessBuilder pb = new ProcessBuilder(startScript.getAbsolutePath(), myTmpDir.getAbsolutePath(), wtsDir.getAbsolutePath());
                    p = pb.start();
                    p.waitFor();
                    ended = true;
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
                finally //anticipate java bug 6462165
                {
                    closeStreams(p);
                }
            }
            
            void closeStreams(Process p) 
            {
                try 
                {
                    p.getInputStream().close();
                    p.getOutputStream().close();
                    p.getErrorStream().close();
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        });
        
        jobRunningThread.start();
    }
    
    public void closeSession(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException {
    	try {
			FileUtils.deleteDirectory(myTmpDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void stop(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException {
    	
    }
}
