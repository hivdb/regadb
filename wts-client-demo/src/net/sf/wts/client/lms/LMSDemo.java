package net.sf.wts.client.lms;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import net.sf.wts.client.WtsClient;
import net.sf.wts.client.meta.WtsMetaClient;

public class LMSDemo 
{
    public static void main(String [] args)
    {
        //handling arguments
        String proxySettings = null;
        String inp = null;
        String outp = null;
        boolean registry  = false;
        
        for(String a : args)
        {
            if(a.startsWith("--proxy=") && a.length()>"--proxy=".length())
            {
                proxySettings = a.split("=")[1];
            }
            else if(a.startsWith("--inp=") && a.length()>"--inp=".length())
            {
                inp = a.split("=")[1];
            }
            else if(a.startsWith("--outp=") && a.length()>"--outp=".length())
            {
                outp = a.split("=")[1];
            }
            else if(a.startsWith("--meta"))
            {
                registry = true;
            }
        }
        
        if(!registry)
        {
            if(inp==null)
                System.err.println("Please provide an input file");
            if(outp==null)
                System.err.println("Please provide an output file location");
            
            if(inp==null||outp==null)
                System.exit(1);
        }
        //end handling arguments
        
        if(proxySettings!=null && proxySettings.contains(":"))
        {
            System.setProperty("http.proxyHost", proxySettings.split(":")[0]);
            System.setProperty("http.proxyPort", proxySettings.split(":")[1]);
        }
        
        if(registry)
        {
            handleRegistry();
        }
        else
        {
            handleTrussStatRun(inp, outp);
        }
    }
    
    public static void handleRegistry()
    {
        WtsMetaClient client = new WtsMetaClient("http://virolab.med.kuleuven.be/wts/services/");
        try {
            System.err.println(client.listServices());
        } catch (RemoteException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        byte[] array=null;
        try {
            array = client.getServiceDescription("truss_stat");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.err.println(new String(array));
    }
    
    public static void handleTrussStatRun(String inputFileName, String outputFileName)
    {
        WtsClient client = new WtsClient("http://virolab.med.kuleuven.be/wts/services/");
        final String serviceName = "truss_stat";
        try 
        {
            System.err.println("Negotiating acces protocol...");
            String challenge = client.getChallenge("public");
            String ticket = client.login("public", challenge, "public", serviceName);

            System.err.println("Uploading input file...");
            client.upload(ticket, serviceName, "input", new File(inputFileName));
            
            System.err.println("Starting analysis...");
            client.start(ticket, serviceName);

            System.err.println("Monitoring the analysis run...");
            boolean finished = false;
            while(!finished)
            {
                try 
                {
                    Thread.sleep(200);
                } 
                catch (InterruptedException ie) 
                {
                    ie.printStackTrace();
                }
                if(client.monitorStatus(ticket, serviceName).startsWith("ENDED"))
                {
                    finished = true;
                }
            }
            
            System.err.println("Downloading the result file...");
            client.download(ticket, serviceName, "output", new File(outputFileName));
            
            System.err.println("Closing the session...");
            client.closeSession(ticket, serviceName);
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
}
