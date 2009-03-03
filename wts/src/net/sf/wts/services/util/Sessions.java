package net.sf.wts.services.util;

import java.io.File;
import java.util.ArrayList;

public class Sessions 
{
    private static ArrayList<String> sessions_ = new ArrayList<String>();
    
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
            while(sessions_.contains(unique));
            
            
            sessions_.add(unique);
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
                sessions_.remove(sessionTicket);
        }
    }

    public static ArrayList<Job> getProcesses()
    {
        return processes_;
    }
    
    public static boolean removeProcess(String sessionTicket)
    {
    	synchronized(processes_)
    	{
    		Job toDestroy = null;
            for(Job j : processes_)
            {
                if(j.sessionTicket_.equals(sessionTicket))
                {
                    toDestroy = j;
                    break;
                }
            }
            if(toDestroy!=null)
            {
            	try{
    	            toDestroy.process_.destroy();
    	            processes_.remove(toDestroy);
    	            
    	            return true;
            	}
            	catch(Exception e){
            		e.printStackTrace();
            	}
            }
    	}
    	return false;
    }
}
