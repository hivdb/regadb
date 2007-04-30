package net.sf.wts.build.ant;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.Task;

public class GenerateCSharpProxyFiles  extends Task
{
    private File wsdlDirectory;
    private File csharpDirectory;
    private File wsdlExecutable;
    
    public void execute()
    {
        File [] files = wsdlDirectory.listFiles();
        
        for(File file : files)
        {
            if(file.getAbsolutePath().endsWith(".wsdl"))
            {
                String baseName = file.getName();
                int extensionIndex = baseName.lastIndexOf(".wsdl");
                baseName = baseName.substring(0, extensionIndex);
                String wsdlExec = wsdlExecutable.getAbsolutePath();
                File serviceDir = new File(csharpDirectory.getAbsolutePath()+File.separatorChar+baseName+File.separatorChar);
                serviceDir.mkdir();
                
                String arg1 = "/out:"+csharpDirectory.getAbsolutePath()+File.separatorChar+baseName+File.separatorChar+baseName+"Service.cs";
                String arg2 = "/namespace:net.sf.wts.client.proxyFiles."+baseName;
                String arg3 = file.getAbsolutePath();
                System.err.println(wsdlExec + " " + arg1+ " " + arg2+ " " + arg3);
                final ProcessBuilder pb = new ProcessBuilder(wsdlExec, arg1, arg2, arg3);

                try 
                {
                    pb.start();
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void main(String [] args)
    {
        GenerateCSharpProxyFiles antTask = new GenerateCSharpProxyFiles();
        antTask.setCsharpDirectory(new File("C:\\jvsant1\\wts\\csharp"));
        antTask.setWsdlDirectory(new File("C:\\jvsant1\\wts\\build"));
        antTask.setWsdlExecutable(new File("C:\\jvsant1\\wts\\wsdl.exe"));
        antTask.execute();
    }

    public File getCsharpDirectory() {
        return csharpDirectory;
    }

    public void setCsharpDirectory(File csharpDirectory) {
        this.csharpDirectory = csharpDirectory;
    }

    public File getWsdlDirectory() {
        return wsdlDirectory;
    }

    public void setWsdlDirectory(File wsdlDirectory) {
        this.wsdlDirectory = wsdlDirectory;
    }

    public File getWsdlExecutable() {
        return wsdlExecutable;
    }

    public void setWsdlExecutable(File wsdlExecutable) {
        this.wsdlExecutable = wsdlExecutable;
    }
}
