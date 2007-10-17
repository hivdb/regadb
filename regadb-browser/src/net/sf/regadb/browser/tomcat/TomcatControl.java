package net.sf.regadb.browser.tomcat;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class TomcatControl {
	private static TomcatControl instance_ = null;
	
	private TomcatControl() {
	}
	
	public static TomcatControl getInstance() {
		if(instance_==null) {
			instance_ = new TomcatControl();
		}
		return instance_;
	}
	public void runTomcatAntFile(String baseDir, String targetName, String logFile) {
        Project project = new Project();
        
        try
        { 
            project.init();
            project.setBasedir(baseDir);
            ProjectHelper.getProjectHelper().parse(project, this.getClass().getClassLoader().getResource("tomcat.xml"));
        	project.setProperty("tomcat.home", baseDir);
        	project.setProperty("regadb.run.logFile", logFile);
         	project.executeTarget(targetName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}
}
