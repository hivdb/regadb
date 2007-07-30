package net.sf.regadb.build.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    
    private static HashMap<String, List<String>> moduleJars_ = new HashMap<String, List<String>>();
    private static HashMap<String, List<String>> moduleDependencies_ = new HashMap<String, List<String>>();
	
	private static String buildDir_;
    private static String rapportDir_;
    private static String libPool_;
    private static String packageDir_;
    
    private static String localCheckoutDir_ = null;

    public static void main (String args[])
    {
    	if (args.length >= 2) {
    		
            if(args.length>2)
            {
                String localCheckoutArg = args[2];
                if(localCheckoutArg.startsWith("--localCheckout="))
                {
                    localCheckoutArg = localCheckoutArg.substring(localCheckoutArg.indexOf("--localCheckout=")+"--localCheckout=".length(), localCheckoutArg.length());
                    localCheckoutDir_ = localCheckoutArg;
                }
            }
            
            run(args[0], args[1], true);
    	}
    	else {
    		System.out.println("Wrong parameters");
    		System.out.println("First parameter for build dir");
    		System.out.println("Second parameter for report dir");
    	}
    }
    
    public static void run(String buildDir, String reportDir, boolean runTests) {
        buildDir_ = buildDir + File.separatorChar;
        rapportDir_ = reportDir + File.separatorChar;
        
        libPool_ = buildDir_ + "libPool" + File.separatorChar;
        
        packageDir_ = buildDir_ + "packages" + File.separatorChar;
        
        build();
        
        if(runTests)
            performTests();
    }
    
    public static void build()
    {
        createDirs();
        
        try {
            if(localCheckoutDir_==null)
                CvsTools.checkout(witty_cvs_url, buildDir_, "jwt/src", "jwt_src");
            else
                CvsTools.localCheckout("jwt", "jwt_src",  localCheckoutDir_, buildDir_);
        }  
        
        catch (Exception e) {
        	handleError("jwt", e);
        }
        
        SVNRepository svnrepos = SvnTools.getSVNRepository(regadb_svn_url_, "jvsant1", "Kangoer1" );
        
        List<String> modules;
        if(localCheckoutDir_==null)
            modules = SvnTools.getModules(svnrepos);
        else
            modules = SvnTools.getLocalModules(localCheckoutDir_);
        
        modules = filterRegaDBSvnModules(modules);
        
        HashMap<String, List<String>> moduleDeps = new HashMap<String, List<String>>();
        
        for(String m : modules)
        {
        	try {
                if(localCheckoutDir_==null)
                    SvnTools.checkout(regadb_svn_url_, m, buildDir_, svnrepos);
                else
                    SvnTools.localCheckout(m, localCheckoutDir_, buildDir_);
            }
            catch (Exception e) {
            	handleError(m, e);
            }
            
            File classPathFile = new File(buildDir_ + File.separatorChar + m + File.separatorChar + ".classpath");
            File buildXmlFile = new File(buildDir_ + File.separatorChar + m + File.separatorChar + "build.xml");
            if(classPathFile.exists() && buildXmlFile.exists()) {
                List<String> moduleDependencies = EclipseParseTools.getDependenciesFromClasspathFile(buildDir_ + m);
                moduleDependencies = filterRegaDBDependencies(moduleDependencies);
                moduleDeps.put(m, moduleDependencies);    
            }
        }
        
        for(String m : modules)
        {
            File classPathFile = new File(buildDir_ + File.separatorChar + m + File.separatorChar + ".classpath");
            File buildXmlFile = new File(buildDir_ + File.separatorChar + m + File.separatorChar + "build.xml");
            if(classPathFile.exists() && buildXmlFile.exists()) {
        	List<String> moduleDependencies = EclipseParseTools.getDependenciesFromClasspathFile(buildDir_ + m);
            moduleDependencies = filterRegaDBDependencies(moduleDependencies);
            moduleDependencies_.put(m, moduleDependencies);
            }
        }
        
        buildModule(buildDir_, "jwt_src");
        
        buildRegaDBProjects(moduleDeps);
    }
    
    private static void createDirs()
    {
        try 
        {
            FileUtils.forceMkdir(new File(libPool_));
            FileUtils.forceMkdir(new File(packageDir_));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    private static List<String> copyDistJarsToLibPool(String buildDir, String moduleName)
    {
    	List<String> dists = new ArrayList<String>();
    	
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
    
    private static void buildRegaDBProjects(HashMap<String, List<String>> moduleDeps)
    {
        int amountOfRoundTrips = moduleDeps.entrySet().size();

        List<String> projectsToRemove = new ArrayList<String>();
        
        for(int i = 0; i<amountOfRoundTrips; i++)
        {
            projectsToRemove.clear();
            if(moduleDeps.entrySet().size()==0)
            {
                break;
            }
            //System.err.println("roundTrips:"+i);
            for(Entry<String, List<String>> entry : moduleDeps.entrySet())
            {
            	if(entry.getValue().size()==0)
                {
                    buildModule(buildDir_, entry.getKey());
                    //System.err.println("building:"+entry.getKey());
                    projectsToRemove.add(entry.getKey());
                    
                    for(Entry<String, List<String>> entryDeeper : moduleDeps.entrySet())
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
    	List<String> deps = new ArrayList<String>();
    	
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
        	
        	List<String> dists = copyDistJarsToLibPool(buildDir, moduleName);
            
            deps.addAll(dists);
            
            moduleJars_.put(moduleName, deps);
        }
        catch (Exception e) {
        	e.printStackTrace();
        	handleError(moduleName, e);
        }
        
        createPackage(moduleName);
    }

	private static List<String> filterRegaDBSvnModules(List<String> modules)
    {
        List<String> filteredModules = new ArrayList<String>(); 
        
        for(String m : modules)
        {
            if(m.startsWith("regadb-") || m.startsWith("wts-"))
            {
                if(!m.equals("wts-build"))
                {
                    filteredModules.add(m);
                }
            }
        }
        
        return filteredModules;
    }
    
    private static List<String> filterRegaDBDependencies(List<String> moduleDependencies)
    {
        List<String> filteredDependencies = new ArrayList<String>();
        
        for(String md : moduleDependencies)
        {
            String dependency = md.substring(1);
            
            if((dependency.startsWith("regadb") || dependency.startsWith("wts")) && dependency.indexOf('/')==-1)
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
    
    private static Set<String> getForeignJarDependencies(String module, List<String> handledModules)
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
    
    private static Set<String> getDists(String module)
    {
    	Set<String> dists = new HashSet<String>();
    	
    	for(String s : moduleJars_.get(module))
    	{
    		if(s.contains("dist"))
    		{
    			dists.add(s.substring(s.lastIndexOf(File.separatorChar) + 1));
    		}
    	}
    	
    	return dists;
    }
    
    private static void createPackage(String moduleName)
    {
    	try 
        {
            FileUtils.forceMkdir(new File(packageDir_ + moduleName));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    	
    	Set<String> jars = new HashSet<String>();
    	
    	jars.addAll(getDists(moduleName));
    	
    	jars.addAll(getOwnJarDependencies(moduleName));
    	
    	if(!(moduleName.equals("jwt_src")))
    	{
    		jars.addAll(getForeignJarDependencies(moduleName, new ArrayList<String>()));
    	}
    	
    	Collection jarFilesFromLibPool = FileUtils.listFiles(new File(libPool_), new String[] { "jar" }, true);
        
        for(Object o : jarFilesFromLibPool)
        {
            if(jars.contains((((File)o).getAbsolutePath()).substring((((File)o).getAbsolutePath()).lastIndexOf(File.separatorChar) + 1)))
            {
            	try 
                {
                    File to = new File(packageDir_ + moduleName + File.separatorChar);
                    FileUtils.copyFileToDirectory((File)o, to);
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void performTests() {
    	System.out.println("Testing projects");
        
        JUnitRapport.startTesting();
        JUnitTest.executeTests(libPool_);
        JUnitRapport.endTesting(rapportDir_ + "testresult.xml");
        
        System.out.println("Generate testing report");
        XsltTransformer.transform(rapportDir_ + "testresult.xml", rapportDir_ + "testresult.html", "testresult.xsl");
	}
    
    private static void handleError(String moduleName, Exception e) {
    	ErrorRapport.handleError("testresult.xml", moduleName, e);
    	
    	XsltTransformer.transform(rapportDir_ + "testresult.xml", rapportDir_ + "testresult.html", "error.xsl");
    	
    	System.exit(1);
	}
}