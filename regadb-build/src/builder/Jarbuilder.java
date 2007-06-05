package builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.regadb.build.ant.AntTools;
import net.sf.regadb.build.cvs.CvsTools;
import net.sf.regadb.build.eclipse.EclipseParseTools;
import net.sf.regadb.build.error.ErrorRapport;
import net.sf.regadb.build.junit.JUnitRapport;
import net.sf.regadb.build.junit.JUnitTest;
import net.sf.regadb.build.svn.SvnTools;
import net.sf.regadb.build.transform.XsltTransformer;

import org.apache.commons.io.FileUtils;
import org.tmatesoft.svn.core.io.SVNRepository;

public class Jarbuilder
{
	private final static String regadb_svn_url_ = "svn+ssh://zolder:3333/var/svn/repos";
    private final static String witty_cvs_url = ":pserver:anonymous@zolder:2401/cvsroot/witty";
    
    private static HashMap<String, ArrayList<String>> moduleJars_ = new HashMap<String, ArrayList<String>>();
    private static HashMap<String, ArrayList<String>> moduleDependencies_ = new HashMap<String, ArrayList<String>>();
	
	private static String buildDir_;
    private static String rapportDir_;
    private static String libPool_;

    public static void main (String args[])
    {
    	if (args.length == 2) {
    		buildDir_ = args[0] + File.separatorChar;
    		rapportDir_ = args[1] + File.separatorChar;
    		
    		libPool_ = buildDir_ + "libPool" + File.separatorChar;
    		
    		build();
    		
    		performTests();
    	}
    	else {
    		System.out.println("Wrong parameters");
    		System.out.println("First parameter for build dir");
    		System.out.println("Second parameter for report dir");
    	}
    }
    
    public static void build()
    {
        createLibPoolDir();
        
        try {
        	CvsTools.checkout(witty_cvs_url, buildDir_, "jwt/src", "jwt_src");
        }  
        
        catch (Exception e) {
        	handleError("jwt", e);
        }
        
        SVNRepository svnrepos = SvnTools.getSVNRepository(regadb_svn_url_, "jvsant1", "Kangoer1" );
        
        ArrayList<String> modules = SvnTools.getModules(svnrepos);
        
        modules = filterRegaDBSvnModules(modules);
        
        HashMap<String, ArrayList<String>> moduleDeps = new HashMap<String, ArrayList<String>>();
        
        for(String m : modules)
        {
        	try {
        		SvnTools.checkout(regadb_svn_url_, m, buildDir_, svnrepos);
            }
            catch (Exception e) {
            	handleError(m, e);
            }
            
            ArrayList<String> moduleDependencies = EclipseParseTools.getDependenciesFromClasspathFile(buildDir_ + m);
            moduleDependencies = filterRegaDBDependencies(moduleDependencies);
            moduleDeps.put(m, moduleDependencies);       
        }
        
        for(String m : modules)
        {
        	ArrayList<String> moduleDependencies = EclipseParseTools.getDependenciesFromClasspathFile(buildDir_ + m);
            moduleDependencies = filterRegaDBDependencies(moduleDependencies);
            moduleDependencies_.put(m, moduleDependencies);
        }
        
        buildModule(buildDir_, "jwt_src");
        
        buildRegaDBProjects(moduleDeps);
    }
    
    private static void createLibPoolDir()
    {
        try 
        {
            FileUtils.forceMkdir(new File(libPool_));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    private static ArrayList<String> copyDistJarsToLibPool(String buildDir, String moduleName)
    {
    	ArrayList<String> dists = new ArrayList<String>();
    	
        File distDir = new File(buildDir + moduleName + File.separatorChar + "dist");
        
        Collection jarFiles = FileUtils.listFiles(distDir, new String[] { "jar" }, false);
        for(Object o : jarFiles)
        {
            try 
            {
                FileUtils.copyFileToDirectory((File)o, new File(libPool_));
                dists.add(((File)o).getAbsolutePath());
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        
        return dists;
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
    	ArrayList<String> deps = new ArrayList<String>();
    	
        Collection jarFiles = FileUtils.listFiles(new File(buildDir + moduleName), new String[] { "jar" }, true);
        for(Object o : jarFiles)
        {
            try 
            {
                FileUtils.copyFileToDirectory((File)o, new File(libPool_));
                deps.add(((File)o).getAbsolutePath());
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        
        moduleJars_.put(moduleName, deps);
        
        Collection jarFilesFromLibPool = FileUtils.listFiles(new File(libPool_), new String[] { "jar" }, false);
        
        for(Object o : jarFilesFromLibPool)
        {
            try 
            {
                File to = new File(buildDir + moduleName + File.separatorChar + "lib");
                FileUtils.copyFileToDirectory((File)o, to);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        try {
        	String jarDeps = getJardependenciesString(getOwnJarDependencies(moduleName));
        	
        	if(!(moduleName.equals("jwt_src")))
        	{
        		jarDeps = jarDeps.concat(" " + getJardependenciesString(getForeignJarDependencies(moduleName, new ArrayList<String>())));
        	}
        	
        	AntTools.buildProject(moduleName, buildDir_, jarDeps);
        	
        	ArrayList<String> dists = copyDistJarsToLibPool(buildDir, moduleName);
            
            deps.addAll(dists);
            
            moduleJars_.put(moduleName, deps);
        }
        catch (Exception e) {
        	e.printStackTrace();
        	handleError(moduleName, e);
        }
    }

	private static ArrayList<String> filterRegaDBSvnModules(ArrayList<String> modules)
    {
        ArrayList<String> filteredModules = new ArrayList<String>(); 
        
        for(String m : modules)
        {
            if(m.startsWith("regadb-") || m.startsWith("wts-client-java"))
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
            
            if((dependency.startsWith("regadb-") || dependency.startsWith("wts")) && dependency.indexOf('/')==-1)
            {
                filteredDependencies.add(dependency);
            }
        }
        
        return filteredDependencies;
    }
    
    private static Set<String> getOwnJarDependencies(String module)
    {
    	Set<String> jarDependencies = new HashSet<String>();
    	
    	for(String s : moduleJars_.get(module))
    	{
    		if(!(s.contains("dist")))
    		{
    			jarDependencies.add(s.substring(s.lastIndexOf(File.separatorChar) + 1));
    		}
    	}
    	
    	return jarDependencies;
    }
    
    private static Set<String> getForeignJarDependencies(String module, ArrayList<String> handledModules)
    {
    	Set<String> jarDependencies = new HashSet<String>();
    	
    	for(String s : moduleDependencies_.get(module))
    	{
    		for(String str : moduleJars_.get(s))
    		{
    			jarDependencies.add(str.substring(str.lastIndexOf(File.separatorChar) + 1));
    		}
    		
    		if(!handledModules.contains(s))
    		{
    			jarDependencies.addAll(getForeignJarDependencies(s, handledModules));
    			handledModules.add(s);
    		}
    	}
    	
    	return jarDependencies;
    }
    
    private static String getJardependenciesString(Set<String> jarDependenciesSet)
    {
    	String jarDependencies = new String();
    	
    	for(String s : jarDependenciesSet)
    	{
    		jarDependencies = jarDependencies.concat(s + " ");
    	}
    	
    	return jarDependencies.trim();
    }

    private static void performTests() {
    	System.out.println("Testing projects");
        
        JUnitRapport.startTesting();
        JUnitTest.executeTests(libPool_);
        JUnitRapport.endTesting("testresult.xml");
        
        System.out.println("Generate testing report");
        XsltTransformer.transform("testresult.xml", rapportDir_ + "testresult.html", "testresult.xsl");
	}
    
    private static void handleError(String moduleName, Exception e) {
    	ErrorRapport.handleError("testresult.xml", moduleName, e);
    	
    	XsltTransformer.transform("testresult.xml", rapportDir_ + "testresult.html", "error.xsl");
    	
    	System.exit(1);
	}
}