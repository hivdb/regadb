package net.sf.regadb.install.generateBundle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.regadb.install.generateHsqldb.HsqldbDatabaseCreator;
import net.sf.regadb.util.pair.Pair;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.GUnzip;
import org.apache.tools.ant.taskdefs.Untar;

public class GenerateWindowsBundles {
    public void unpackJavaTomcat(String buildPath, String bundlePath) {
        String javaTomcatLocation = buildPath + replaceByPS("/regadb-install/src/net/sf/regadb/install/generateBundle/winResources/");
        tarxzvf(javaTomcatLocation + "jre.tgz", bundlePath);
        tarxzvf(javaTomcatLocation + "tomcat.tgz", bundlePath);
    }
    
    public void deployRegaDB(String buildPath, String bundlePath) {
        try {
            FileUtils.forceMkdir(new File(bundlePath + File.separatorChar + "tmp" + File.separatorChar));
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        
        ArrayList<Pair<String, String>> properties = new ArrayList<Pair<String, String>>();
        properties.add(new Pair<String, String>("tomcat.home", bundlePath + File.separatorChar + "tomcat"));
        properties.add(new Pair<String, String>("warfile", buildPath + replaceByPS("/regadb-ui/dist/regadb-ui-0.9.war")));
        String tomcatDir = bundlePath + replaceByPS("/tomcat/bin/");

        runBatchScript(tomcatDir + File.separatorChar + "startup.bat", tomcatDir);
        System.err.println("Tomcat started...");
        System.err.println("Waiting for 5 seconds to make sure it is started successfully");

        String buildFile = buildPath + replaceByPS("regadb-install/src/net/sf/regadb/install/generateBundle/");
        
        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }*/
        
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis()-start<(5*60*1000)) {
            try {
                runBuildFile(buildFile + "tomcat-deploy.xml",
                            bundlePath, 
                            "tomcat-deploy",
                            properties);
                start = -1;
                break;
            } catch (BuildException e) {
            }
        }
        if(start!=-1){
            System.err.println("Something went wrong when deploying the war, exiting.");
            System.exit(1);
        } else {
        System.err.println("War was deployed succesfully,\nwaiting 10 seconds before shutting down tomcat");
        }
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        runBatchScript(tomcatDir + File.separatorChar + "shutdown.bat", tomcatDir);
    }
    
    private void tarxzvf(String tarGzFile, String destPath) {
        GUnzip gunzip = new GUnzip();
        gunzip.init();
        gunzip.setSrc(new File(tarGzFile));
        File tarFile = new File(tarGzFile.substring(0, tarGzFile.lastIndexOf('.'))+".tar");
        gunzip.setDest(tarFile);
        gunzip.execute();
        
        Untar untar = new Untar();
        untar.init();
        untar.setSrc(tarFile);
        untar.setDest(new File(destPath+File.separatorChar));
        untar.execute();
        
        tarFile.delete();
    }
    
    private static String replaceByPS(String path) {
        String result = "";
        for(int i = 0; i < path.length(); i++) {
            if(path.charAt(i)=='/')
                result += File.separatorChar;
            else
                result += path.charAt(i);
        }
        return result;
    }
    
    public static void main(String [] args) {
        GenerateWindowsBundles gen = new GenerateWindowsBundles();
        
        if(args.length<2) {
            System.err.println("Please provide the builddir and the bundledir as program arguments");
            System.exit(1);
        }
        
        String buildDir = args[0];
        String bundleDir = args[1];
        
        gen.unpackJavaTomcat(buildDir, bundleDir);
        
        gen.deployRegaDB(buildDir, bundleDir);
        try {
            FileUtils.forceMkdir(new File(bundleDir + File.separatorChar + "hsqldb"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        HsqldbDatabaseCreator hsqldb = new HsqldbDatabaseCreator(   bundleDir + File.separatorChar + "hsqldb", 
                                                                    "regadb", 
                                                                    "regadb", 
                                                                    "regadb", 
                                                                    buildDir + File.separatorChar + replaceByPS("regadb-install/src/net/sf/regadb/install/ddl/schema/hsqldbSchema.sql"));
        hsqldb.run();
        
        try {
            FileUtils.forceMkdir(new File(bundleDir + replaceByPS("/conf/")));
            FileUtils.copyDirectory(new File(buildDir + replaceByPS("/packages/regadb-install")), new File(bundleDir + replaceByPS("install")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void runBuildFile(String buildFile, String bundlePath, String target, ArrayList<Pair<String, String>> properties) throws BuildException {
        Project project = new Project();
        
        project.setName("generate-bundle");
        project.init();
        project.setBasedir(bundlePath + File.separatorChar + "tmp");
        ProjectHelper.getProjectHelper().parse(project, new File(buildFile));
        for(Pair<String, String> p : properties) {
            project.setProperty(p.getKey(), p.getValue());
        }
        System.err.println("Start of target:" + target);
        project.executeTarget(target);
        System.err.println("End of target:" + target);
    }
    
    public void runBatchScript(final String batchScriptPath, final String workingDir)
    {
        Thread jobRunningThread = new Thread(new Runnable()
        {
            public void run()
            {
                Process p = null;
                try 
                {
                    ProcessBuilder pb = new ProcessBuilder(batchScriptPath);
                    pb.directory(new File(workingDir));
                    p = pb.start();
                    p.waitFor();
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
                finally //anticipate java bug 6462165
                {
                    closeStreams(p);
                }
            }
            
            void closeStreams(Process p) 
            {
                try 
                {
                    p.getInputStream().close();
                    p.getOutputStream().close();
                    p.getErrorStream().close();
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        });
        
        jobRunningThread.start();
    }
}
