package net.sf.regadb.build.cvs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.netbeans.lib.cvsclient.commandLine.CVSCommand;

public class CvsTools 
{
    public static void checkout(String cvsroot, String localPath, String projectName, String localProjectName) throws Exception
    {
        System.out.println("Checking out cvs project: "+projectName);
        System.setProperty("cvs.root", cvsroot);
        
        String [] argsco = {"co", projectName}; 
        
        PrintStream ps_out = null;
        try 
        {
            ps_out = new PrintStream(new FileOutputStream(new File(localPath + File.separatorChar + "cvs_out.log")));
        } 
        catch (FileNotFoundException e) 
        {
            throw new Exception(e.getMessage());
        }
        PrintStream ps_err = null;
        try 
        {
            ps_err = new PrintStream(new FileOutputStream(new File(localPath + File.separatorChar + "cvs_err.log")));
        } 
        catch (FileNotFoundException e) 
        {
        	throw new Exception(e.getMessage());
        }
        
        boolean succes = (CVSCommand.processCommand(argsco, null, localPath, ps_out, ps_err));
        
        assert succes;
        
        try 
        {
            File newDir = new File(localPath + File.separatorChar + localProjectName);
            FileUtils.forceMkdir(newDir);
            FileUtils.copyDirectory(new File(localPath + File.separatorChar + projectName), newDir);
        } 
        catch (IOException e) 
        {
        	throw new Exception(e.getMessage());
        }
    }
    
    public static void localCheckout(String localProjectName, String destinationProjectName, String srcPath, String destPath)
    {
        try 
        {
            System.out.println("Copying module " + localProjectName);
            File destDir = new File(destPath+File.separatorChar+destinationProjectName);
            FileUtils.forceMkdir(destDir);
            FileUtils.copyDirectory(new File(srcPath + File.separatorChar + localProjectName), destDir);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
