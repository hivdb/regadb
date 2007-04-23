package net.sf.wts.services.util;

import java.io.File;
import java.util.ArrayList;

public class Session 
{
    private static ArrayList<String> sessions_ = new ArrayList<String>();
    
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
}
