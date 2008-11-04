package net.sf.wts.client;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

public interface IWtsClient {
    public String getChallenge(String userName) throws RemoteException, MalformedURLException;
    
    public String login(String userName, String challenge, String password, String serviceName) throws RemoteException, MalformedURLException;
    
    public void upload(String sessionTicket, String serviceName, String fileName, byte[] file) throws RemoteException, MalformedURLException;
    
    public byte[] download(String sessionTicket, String serviceName, String fileName) throws RemoteException, MalformedURLException;
    
    public void download(String sessionTicket, String serviceName, String fileName, File toWrite) throws RemoteException, MalformedURLException;
    
    public void upload(String sessionTicket, String serviceName, String fileName, File localLocation) throws RemoteException, MalformedURLException;
    
    public byte[] monitorLogFile(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException;
    
    public byte[] monitorLogTail(String sessionTicket, String serviceName, int numberOfLines) throws RemoteException, MalformedURLException;
    
    public String monitorStatus(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException;
    
    public void start(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException;
    
    public void closeSession(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException;
    
    public void stop(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException;
}
