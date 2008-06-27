package net.sf.regadb.util.batch;

import java.io.File;
import java.io.IOException;

public class Batch {
    public static void runBatchScript(final String batchScriptPath, final String workingDir)
    {
        Thread jobRunningThread = new Thread(new Runnable()
        {
            public void run()
            {
                Process p = null;
                try 
                {
                    ProcessBuilder pb = new ProcessBuilder(batchScriptPath);
                    pb.directory(new File(workingDir));
                    p = pb.start();
                    p.waitFor();
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
}
