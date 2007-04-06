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
    public static void checkout(String cvsroot, String localPath, String projectName, String localProjectName)
    {
        System.out.println("Checking out cvs project: "+projectName);
        System.setProperty("cvs.root", cvsroot);
        
        String [] argsco = {"co", projectName}; 
        
        PrintStream ps_out = null;
        try 
        {
            ps_out = new PrintStream(new FileOutputStream(new File(localPath + File.separatorChar + "cvs_out.log")));
        } 
        catch (FileNotFoundException e1) 
        {
            e1.printStackTrace();
        }
        PrintStream ps_err = null;
        try 
        {
            ps_err = new PrintStream(new FileOutputStream(new File(localPath + File.separatorChar + "cvs_err.log")));
        } 
        catch (FileNotFoundException e1) 
        {
            e1.printStackTrace();
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
            e.printStackTrace();
        }
    }
    
    public static void main(String [] args)
    {
        checkout(":pserver:anonymous@zolder:2401/cvsroot/witty", "/home/plibin0/regadb_build", "jwt/src", "jwt_src");
    }
}
