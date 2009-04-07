package net.sf.wts.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import net.sf.wts.client.util.AxisClient;
import net.sf.wts.client.util.Encrypt;

import org.apache.commons.io.FileUtils;

public class WtsClient implements IWtsClient
{
    private String url_;
    private AxisClient axisService = new AxisClient();
    
    private int retryOnRemoteException;
    
    public WtsClient(String url, int retryOnRemoteException)
    {
        url_ = url;
        this.retryOnRemoteException = retryOnRemoteException;
    }
    
    public WtsClient(String url)
    {
    	this(url, 1);
    }
    
    public String getChallenge(String userName) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			return getChallengeImpl(userName);
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    	
    	return null;
    }
    
    private String getChallengeImpl(String userName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "GetChallenge");
        
        axisService.addParameter(userName);
        
        String challenge = "";
        
        challenge = axisService.callAndGetStringResult();
        
        return challenge;
    }
    
    public String login(String userName, String challenge, String password, String serviceName) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			return loginImpl(userName, challenge, password, serviceName);
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    	
    	return null;
    }
    
    private String loginImpl(String userName, String challenge, String password, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Login");
        
        axisService.addParameter(userName);
        axisService.addParameter(challenge);
        axisService.addParameter(Encrypt.encryptMD5(Encrypt.encryptMD5(password)+challenge));
        axisService.addParameter(serviceName);
        
        String sessionTicket = "";
        
        sessionTicket = axisService.callAndGetStringResult();
        
        return sessionTicket;
    }
    
    public void upload(String sessionTicket, String serviceName, String fileName, byte[] file) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			uploadImpl(sessionTicket, serviceName, fileName, file);
    			break;
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    }
    
    private void uploadImpl(String sessionTicket, String serviceName, String fileName, byte[] file) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Upload");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        axisService.addParameter(fileName);
        axisService.addParameter(file);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
    }
    
    public byte[] download(String sessionTicket, String serviceName, String fileName) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			return downloadImpl(sessionTicket, serviceName, fileName);
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    	
    	return null;
    }
    
    private byte[] downloadImpl(String sessionTicket, String serviceName, String fileName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Download");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        axisService.addParameter(fileName);
        
        byte[] array = null;
        try 
        {
            array = axisService.callAndGetByteArrayResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return array;
    }
    
    public void download(String sessionTicket, String serviceName, String fileName, File toWrite) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			downloadImpl(sessionTicket, serviceName, fileName, toWrite);
    			break;
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    }
    
    private void downloadImpl(String sessionTicket, String serviceName, String fileName, File toWrite) throws RemoteException, MalformedURLException
    {
        byte[] array = download(sessionTicket, serviceName, fileName);
        if(array!=null)
        {
            try 
            {
                FileUtils.writeByteArrayToFile(toWrite, array);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    public void upload(String sessionTicket, String serviceName, String fileName, File localLocation) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			uploadImpl(sessionTicket, serviceName, fileName, localLocation);
    			break;
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    }
    
    private void uploadImpl(String sessionTicket, String serviceName, String fileName, File localLocation) throws RemoteException, MalformedURLException
    {
        byte[] array = null;
        try 
        {
            array = FileUtils.readFileToByteArray(localLocation);;
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        upload(sessionTicket, serviceName, fileName, array);
    }
    
    public byte[] monitorLogFile(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			return monitorLogFileImpl(sessionTicket, serviceName);
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    	
    	return null;
    }
    
    private byte[] monitorLogFileImpl(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "MonitorLogFile");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);

        byte[] array = null;
        try 
        {
            array = axisService.callAndGetByteArrayResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return array;
    }
    
    public byte[] monitorLogTail(String sessionTicket, String serviceName, int numberOfLines) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			return monitorLogTail(sessionTicket, serviceName, numberOfLines);
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    	
    	return null;
    }
    
    private byte[] monitorLogTailImpl(String sessionTicket, String serviceName, int numberOfLines) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "MonitorLogTail");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        axisService.addParameter(numberOfLines);

        byte[] array = null;
        try 
        {
            array = axisService.callAndGetByteArrayResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return array;
    }
    
    public String monitorStatus(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			return monitorStatusImpl(sessionTicket, serviceName);
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    	
    	return null;
    }
    
    private String monitorStatusImpl(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "MonitorStatus");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        String status = "";
        
        try 
        {
            status = axisService.callAndGetStringResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }

        return status;
    }
    
    public void start(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			startImpl(sessionTicket, serviceName);
    			break;
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    }
    
    private void startImpl(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Start");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    
    public void closeSession(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			closeSessionImpl(sessionTicket, serviceName);
    			break;
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    }
    
    private void closeSessionImpl(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "CloseSession");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    
    public void stop(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException {
    	for(int i = 0; i<retryOnRemoteException+1; ++i) {
    		try {
    			stopImpl(sessionTicket, serviceName);
    			break;
    		} catch(RemoteException re) {
    			if(i==retryOnRemoteException) {
    				throw re;
    			}
    		}
    	}
    }
    
    private void stopImpl(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Stop");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public String getUrl() 
    {
        return url_;
    }
}
