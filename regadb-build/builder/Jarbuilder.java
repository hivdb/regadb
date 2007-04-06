package builder;

import java.io.File;
import java.util.ArrayList;

import net.sf.regadb.build.ant.AntTools;
import net.sf.regadb.build.cvs.CvsTools;
import net.sf.regadb.build.eclipse.EclipseParseTools;
import net.sf.regadb.build.svn.SvnTools;

import org.tmatesoft.svn.core.io.SVNRepository;

public class Jarbuilder
{
    private final static String buildDir_ = "/home/plibin0/regadb_build/";
    
    private final static String regadb_svn_url_ = "svn+ssh://zolder:3333/var/svn/repos";
    private final static String witty_cvs_url = ":pserver:anonymous@zolder:2401/cvsroot/witty";

    public static void main (String args[])
    {
        CvsTools.checkout(witty_cvs_url, buildDir_, "jwt/src", "jwt_src");
        
        AntTools.buildProject("jwt_src", buildDir_);
        
        SVNRepository svnrepos = SvnTools.getSVNRepository(regadb_svn_url_, "jvsant1", "Kangoer1" );
        
        ArrayList<String> modules = SvnTools.getModules(svnrepos);
        
        modules = filterRegaDBSvnModules(modules);
        
        for(String m : modules)
        {
            SvnTools.checkout(regadb_svn_url_, m, buildDir_, svnrepos);
            ArrayList<String> moduleDependencies = EclipseParseTools.getDependenciesFromClasspathFile(buildDir_ + File.separatorChar + m + File.separatorChar);
            moduleDependencies = filterRegaDBDependencies(moduleDependencies);
            for(String md : moduleDependencies)
            {
                System.err.println(md);
            }
        }
    }
    
    private static ArrayList<String> filterRegaDBSvnModules(ArrayList<String> modules)
    {
        ArrayList<String> filteredModules = new ArrayList<String>(); 
        
        for(String m : modules)
        {
            if(m.startsWith("regadb-"))
            {
                if(!m.equals("regadb-sql") && !m.equals("regadb-build"))
                {
                    filteredModules.add(m);
                }
            }
        }
        
        return filteredModules;
    }
    
    private static ArrayList<String> filterRegaDBDependencies(ArrayList<String> moduleDependencies)
    {
        ArrayList<String> filteredDependencies = new ArrayList<String>();
        
        for(String md : moduleDependencies)
        {
            String dependency = md.substring(1);
            
            if(dependency.startsWith("regadb-") && dependency.indexOf('/')==-1)
            {
                filteredDependencies.add(dependency);
            }
        }
        
        return filteredDependencies;
    }
    
    /*public static void getDependendProjects(String projectDir)
    {
        ProjectFileReader pfr = new ProjectFileReader();
        dependendProjects = pfr.getDependencies(_baseDir + projectDir +"/.project");
    }*/
    
}
