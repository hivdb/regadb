package builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import net.sf.regadb.build.ant.AntTools;
import net.sf.regadb.build.cvs.CvsTools;
import net.sf.regadb.build.eclipse.EclipseParseTools;
import net.sf.regadb.build.svn.SvnTools;

import org.apache.commons.io.FileUtils;
import org.tmatesoft.svn.core.io.SVNRepository;

public class Jarbuilder
{
    private final static String buildDir_ = "/home/plibin0/regadb_build/";
    
    private final static String regadb_svn_url_ = "svn+ssh://zolder:3333/var/svn/repos";
    private final static String witty_cvs_url = ":pserver:anonymous@zolder:2401/cvsroot/witty";

    public static void main (String args[])
    {
        createLibPoolDir();
        
        CvsTools.checkout(witty_cvs_url, buildDir_, "jwt/src", "jwt_src");
        
        buildModule(buildDir_, "jwt_src");
        
        SVNRepository svnrepos = SvnTools.getSVNRepository(regadb_svn_url_, "jvsant1", "Kangoer1" );
        
        ArrayList<String> modules = SvnTools.getModules(svnrepos);
        
        modules = filterRegaDBSvnModules(modules);
        
        HashMap<String, ArrayList<String>> moduleDeps = new HashMap<String, ArrayList<String>>();
        
        for(String m : modules)
        {
            SvnTools.checkout(regadb_svn_url_, m, buildDir_, svnrepos);
            ArrayList<String> moduleDependencies = EclipseParseTools.getDependenciesFromClasspathFile(buildDir_ + File.separatorChar + m + File.separatorChar);
            moduleDependencies = filterRegaDBDependencies(moduleDependencies);
            moduleDeps.put(m, moduleDependencies);
        }
        
        buildRegaDBProjects(moduleDeps);
    }
    
    private static void createLibPoolDir()
    {
        try 
        {
            FileUtils.forceMkdir(new File(buildDir_ + File.separatorChar + "libPool"));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    private static void copyDistJarsToLibPool(String buildDir, String moduleName)
    {
        File distDir = new File(buildDir + File.separatorChar + moduleName + File.separatorChar + "dist");
        
        Collection jarFiles = FileUtils.listFiles(distDir, new String[] { "jar" }, false);
        for(Object o : jarFiles)
        {
            try 
            {
                FileUtils.copyFileToDirectory((File)o, new File(buildDir_ + File.separatorChar + "libPool"));
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    private static void buildRegaDBProjects(HashMap<String, ArrayList<String>> moduleDeps)
    {
        int amountOfRoundTrips = moduleDeps.entrySet().size();

        ArrayList<String> projectsToRemove = new ArrayList<String>();
        
        for(int i = 0; i<amountOfRoundTrips; i++)
        {
            projectsToRemove.clear();
            if(moduleDeps.entrySet().size()==0)
            {
                break;
            }
            //System.err.println("roundTrips:"+i);
            for(Entry<String, ArrayList<String>> entry : moduleDeps.entrySet())
            {
                if(entry.getValue().size()==0)
                {
                    buildModule(buildDir_, entry.getKey());
                    //System.err.println("building:"+entry.getKey());
                    projectsToRemove.add(entry.getKey());
                    
                    for(Entry<String, ArrayList<String>> entryDeeper : moduleDeps.entrySet())
                    {
                        if(entryDeeper.getValue().remove(entry.getKey()))
                        {
                            //System.err.println("remove:"+entry.getKey()+"from:"+entryDeeper.getKey());
                        }
                    }
                    break;
                }
            }
            for(String ptr : projectsToRemove)
            {
                moduleDeps.remove(ptr);
            }
        }
    }
    
    private static void buildModule(String buildDir, String moduleName)
    {
        Collection jarFiles = FileUtils.listFiles(new File(buildDir + File.separatorChar + moduleName), new String[] { "jar" }, true);
        for(Object o : jarFiles)
        {
            try 
            {
                FileUtils.copyFileToDirectory((File)o, new File(buildDir_ + File.separatorChar + "libPool"));
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        
        Collection jarFilesFromLibPool = FileUtils.listFiles(new File(buildDir_ + File.separatorChar + "libPool"), new String[] { "jar" }, false);
        
        for(Object o : jarFilesFromLibPool)
        {
            try 
            {
                File to = new File(buildDir + File.separatorChar + moduleName + File.separatorChar + "lib");
                FileUtils.copyFileToDirectory((File)o, to);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        
        AntTools.buildProject(moduleName, buildDir_);
        
        copyDistJarsToLibPool(buildDir, moduleName);
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
