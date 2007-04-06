package net.sf.regadb.build.cvs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.netbeans.lib.cvsclient.commandLine.CVSCommand;

public class CvsTools 
{
    public static void checkout(String cvsroot, String localPath, String projectName, String localProjectName)
    {
        System.setProperty("cvs.root", cvsroot);
        
        String [] argsco = {"co", projectName}; 
        
        boolean succes = (CVSCommand.processCommand(argsco, null, localPath, System.out, System.err));
        
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
