package net.sf.regadb.build.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntTools 
{
    public static void buildProject(String projectName, String localBuildDir) throws Exception
    {
        Project project = new Project();
        
        String buildFile = "build.xml";
        
        try
        { 
            project.init();
        }
        catch (BuildException e)
        {
            System.err.println("The default task list could not be loaded.");
        }
        
        try
        {
            project.setBasedir(localBuildDir + projectName);
        }
        catch (BuildException e)
        {
            System.err.println("The given basedir doesn't exist, or isn't a directory.");
        }

        try
        {
            ProjectHelper.getProjectHelper().parse(project, new File(localBuildDir + projectName + File.separatorChar + buildFile));
        }
        catch (BuildException e)
        {
            System.err.println("Configuration file " + buildFile + " is invalid, or cannot be read.");
        }
        
        System.out.println("Building project: " + projectName);
        
        try
        {
        	project.executeTarget("all");
        }
        catch (BuildException e)
        {
            throw new Exception(e.getMessage());
        }
    }
}
