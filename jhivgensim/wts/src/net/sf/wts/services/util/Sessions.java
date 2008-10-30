package net.sf.wts.services.util;

import java.io.File;
import java.security.Key;
import java.util.ArrayList;

public class Sessions 
{
    private static ArrayList<Session> sessions_ = new ArrayList<Session>();
    
    private static ArrayList<Job> processes_ = new ArrayList<Job>();
    
    public static String createNewSession(String serviceName, String userName)
    {
        String unique;
        
        synchronized(sessions_)
        {
            do
            {
                unique = serviceName + "_" + userName + "_" + System.currentTimeMillis();
            }
            while(!isUniqueSessionTicket(unique));
            
            
            sessions_.add(new Session(unique));
        }
        
        File sessionPath = new File(Settings.getWtsPath()+File.separatorChar+"sessions"+File.separatorChar+unique);
        File sessionInputPath = new File(Settings.getWtsPath()+File.separatorChar+"sessions"+File.separatorChar+unique+File.separatorChar+"inputs");
        File sessionOutputPath = new File(Settings.getWtsPath()+File.separatorChar+"sessions"+File.separatorChar+unique+File.separatorChar+"outputs");
        
        sessionPath.mkdir();
        sessionInputPath.mkdir();
        sessionOutputPath.mkdir();
        
        return unique;
    }
    
    public static File getSessionPath(String sessionTicket)
    {
        File sessionDir = new File(Settings.getWtsPath()+File.separatorChar+"sessions"+File.separatorChar+sessionTicket);
        if(sessionDir.exists() && sessionDir.isDirectory())
        {
            return sessionDir;
        }
        return null;
    }
    
    public static boolean isSessionForService(String sessionTicket, String serviceName)
    {
        return sessionTicket.startsWith(serviceName+"_");
    }
    
    public static void removeSessionTicket(String sessionTicket)
    {
        synchronized(sessions_)
        {
                sessions_.remove(new Session(sessionTicket));
        }
    }

    public static ArrayList<Job> getProcesses()
    {
        return processes_;
    }
    
    public static boolean isUniqueSessionTicket(String sessionTicket){
    	for(Session s : sessions_){
    		if(s.sessionTicket_.equals(sessionTicket)){
    			return false;
    		}
    	}
    	return true;
    }
    
    public static Key getSessionKey(String sessionTicket){
    	for(Session s : sessions_){
    		if(s.sessionTicket_.equals(sessionTicket)){
    			return s.getSessionKey();
    		}
    	}
    	return null;
    }
}
