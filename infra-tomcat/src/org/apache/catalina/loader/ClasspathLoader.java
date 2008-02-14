package org.apache.catalina.loader;

import java.io.File;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;


/**
 * @author Stijn Imbrechts
 *
 */
public class ClasspathLoader extends WebappLoader {
	private ClasspathParser parser_=null;
 
    /**
     * Construct a new WebappLoader with no defined parent class loader (so that
     * the actual parent will be the system class loader).
     */
    public ClasspathLoader() {
        super();
    }

    /**
     * Construct a new WebappLoader with the specified class loader to be
     * defined as the parent of the ClassLoader we ultimately create.
     *
     * @param parent The parent class loader
     */
    public ClasspathLoader(ClassLoader parent) {
        super(parent);
    }
 
    @Override
    public void start() throws LifecycleException {
        // just add any jar/directory set in virtual classpath to the
        // repositories list before calling start on the standard WebappLoader
    	
    	addRepository(getClasspaths());
  	    super.start();
    }
    
    private void log(String s){
    	System.out.println("*** "+ s +" ***");
    }
    
    private void addRepository(File f){
    	if(f.exists()){
    		addRepository(f.toURI().toString());
    		log("addRepository: "+ f.toURI().toString());
    	}
    }
    
    private void addRepository(List<File> files){
    	for(File f : files){
    		addRepository(f);
    	}
    }
    
    protected List<File> getClasspaths(){
    	parser_ = new EclipseClasspathParser();
    	return parser_.getClasspaths(getWebAppDir());
    	
    }
    
    protected File getWebAppDir(){
    	//TODO check windows compatibility
    	return new File(((Context) getContainer()).getServletContext().getRealPath("/"));
    }
}
