package org.apache.catalina.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.sf.regadb.build.eclipse.EclipseParseTools;

public class EclipseClasspathParser implements ClasspathParser
{
    public List<File> getClasspaths(File webAppDir)
    {
    	File projectDir = webAppDir.getParentFile();
        
    	HashSet<String> dependencies = new HashSet<String>();
    	
    	dependencies = getDependencies(dependencies,projectDir);
        
    	ArrayList<File> files= new ArrayList<File>();
    	for(String s : dependencies){
    		//exclude servlet-api jars to avoid 'is not a Servlet' exception
    		if(s.contains("servlet-api"))
    			files.add(new File(s));
    	}
        return files; 
    }
    
    private HashSet<String> getDependencies(HashSet<String> dependencies, File projectDir){
    	log("Project Dir: "+ projectDir.getAbsolutePath());
    	List<String> al = EclipseParseTools.getDependenciesFromClasspathFile(projectDir.getAbsolutePath(),"src");
    	
    	for(String s : al){
    		if(!s.equals("") && !s.equals("src") && dependencies.add(s)){
    			log(s);
    			String f;
    			//TODO check windows compatibility
    			if(s.startsWith("/"))
    				f = projectDir.getParentFile().getAbsolutePath() + s;
    			else
    				f = projectDir.getAbsolutePath() + s;
    				
    			dependencies = getDependencies(dependencies, new File(f));
    		}
    	}
    	
    	al = EclipseParseTools.getDependenciesFromClasspathFile(projectDir.getAbsolutePath(),"lib");
    	for(String s : al)    	
    		dependencies.add(projectDir.getAbsolutePath() + File.separatorChar + s);
    	
    	al = EclipseParseTools.getDependenciesFromClasspathFile(projectDir.getAbsolutePath(),"output");
    	for(String s : al)
    		dependencies.add(projectDir.getAbsolutePath() + File.separatorChar + s);
    	
    	return dependencies;
    }
    
    private void log(String msg){
    	System.out.println("*** [Parser] "+ msg +" ***");
    }
}

