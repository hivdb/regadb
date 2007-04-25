package net.sf.wts.services.util;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Status 
{
    public final static String RUNNING = "RUNNING";
    public final static String READY_FOR_JOB = "READY_FOR_JOB";
    public final static String ENDED_SUCCES = "ENDED_SUCCES";
    public final static String ENDED_FAILED = "ENDED_FAILED";
    public final static String ENDED_KILLED = "ENDED_KILLED";
    
    public static String getStatus(File sessionPath) throws RemoteException
    {
        File endedFile = new File(sessionPath.getAbsolutePath()+File.separatorChar+".ended");
        if(endedFile.exists())
        {
            try 
            {
                List lines = FileUtils.readLines(endedFile);
                String line = (String)lines.get(0);
                if(line.equals(ENDED_SUCCES) || line.equals(ENDED_FAILED) || line.equals(ENDED_KILLED))
                {
                    return line;
                }
                else throw new RemoteException("Illegal .ended file; contact your wts server administrator.");
            } 
            catch (IOException e) 
            {
                throw new RemoteException("Illegal .ended file; contact your wts server administrator.");
            }
        }
        else if(new File(sessionPath.getAbsolutePath()+File.separatorChar+".running").exists())
        {
            return RUNNING;
        }
        else
        {
            return READY_FOR_JOB;
        }
    }
}
