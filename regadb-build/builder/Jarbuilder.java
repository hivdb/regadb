package builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class Jarbuilder
{
    private static Project project;
    private static String _baseDir = "C:/jvsant1/test_co/";
    private static String _buildFile = "build.xml";
    private static ArrayList<String> projects;
    private static ArrayList<String> removeProjects;
    private static ArrayList<String> dependendProjects;
    private static ArrayList<String> queue;
    private static String URL = "svn+ssh://zolder:3333/var/svn/repos";

    public static void main (String args[])
    {
        removeProjects = new ArrayList<String>();
        queue = new ArrayList<String>();
        
        checkOut(URL);
        
        for(String s : removeProjects)
        {
            projects.remove(s);
        }
        
        build("regadb-util");
        build("regadb-persist");
        
        for(String s : projects)
        {
            getDependendProjects(s);
            
            Iterator itr3 = dependendProjects.iterator();
            
            if(dependendProjects.size() > 0)
            {
                queue.add(s);
            }
            else
            {
                if(s.equals("regadb-io"))
                {
                    //
                }
                else
                {
                    build(s);
                }
            }
        }
        
        for(String s : queue)
        {
            //sort queue
        }
        
        for(String s : queue)
        {
            build(s);
        }
    }
    
    public static void checkOut(String url)
    {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        
        String name = "jvsant1";
        String password = "Kangoer1";
        SVNRepository repository = null;
        long latestRevision;
        
        try
        {
            repository = SVNRepositoryFactory.create( SVNURL.parseURIDecoded( url ) );
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( name , password );
            repository.setAuthenticationManager( authManager );
            System.out.println( "Getting repositories");
            SVNNodeKind nodeKind = repository.checkPath( "" ,  -1 );
            
            projects = getModules( repository, "" );
            for(String s : projects)
            {
                if(s.equals("regadb-sql") || s.equals("regadb-build") || s.equals("test_svn"))
                {
                    removeProjects.add(s);
                }
                else
                {
                    latestRevision = repository.getLatestRevision( );
                    SVNClientManager.newInstance().getUpdateClient().doCheckout(SVNURL.parseURIDecoded( url + "/" +s), new File(_baseDir + s),
                    SVNRevision.create(latestRevision), SVNRevision.create(latestRevision), true);
                }
                
                
            }          
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static ArrayList<String> getModules( SVNRepository repository, String path ) throws SVNException
    {
        ArrayList<String> modules = new ArrayList<String>(); 
        Collection entries = repository.getDir( path, -1 , null , (Collection) null );
        Iterator iterator = entries.iterator( );
        while ( iterator.hasNext( ) )
        {
            SVNDirEntry entry = ( SVNDirEntry ) iterator.next( );
            modules.add(entry.getName( ));
        }
        return modules;
    }
    
    public static void getDependendProjects(String projectDir)
    {
        ProjectFileReader pfr = new ProjectFileReader();
        dependendProjects = pfr.getDependencies(_baseDir + projectDir +"/.project");
    }
    
    public static void build(String projectName)
    {
        project = new Project();
        
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
            project.setBasedir(_baseDir);
        }
        catch (BuildException e)
        {
            System.err.println("The given basedir doesn't exist, or isn't a directory.");
        }

        try
        {
            ProjectHelper.getProjectHelper().parse(project, new File(_baseDir + projectName + "/" + _buildFile));
        }
        catch (BuildException e)
        {
            System.err.println("Configuration file " + _buildFile + " is invalid, or cannot be read.");
        }
        
        if(projectName.equals("regadb-persist/hibernate"))
        {
            System.out.println("Compiling " + projectName);
            project.executeTarget("compile");
        }
        else
        {
            System.out.println("Compiling " + projectName);
            project.executeTarget("all");
        }
    }
}
