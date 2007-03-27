package builder;

import java.io.File;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;

public class Jarbuilder
{
    public static Project project;
    public static String _baseDir = null;
    public static String _buildFile = null;
    
    public static void main (String args[])
    {
        // Create a new project, and perform some default initialization
        project = new Project();
        
        try
        { 
            project.init();
        }
        catch (BuildException e)
        {
            System.err.println("The default task list could not be loaded.");
        }
        
        // Set the base directory. If none is given, "." is used.
        if (_baseDir == null) 
        {
            _baseDir = "C:\\jvsant1\\build_dir\\regadb-util\\";
        }
        
        try
        {
            project.setBasedir(_baseDir);
        }
        catch (BuildException e)
        {
            System.err.println("The given basedir doesn't exist, or isn't a directory.");
        }

        // Parse the given buildfile. If none is given, "build.xml" is used.
        if (_buildFile == null) 
        {
            _buildFile = "build.xml";
        }
        try
        {
            ProjectHelper.getProjectHelper().parse(project, new File(_baseDir+_buildFile));
        }
        catch (BuildException e)
        {
            System.err.println("Configuration file "+_buildFile+" is invalid, or cannot be read.");
        }
        
        project.executeTarget("all");
    }
}
