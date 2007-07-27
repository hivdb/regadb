package net.sf.regadb.install.generateBundle;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.install.generateHsqldb.HsqldbDatabaseCreator;

import org.apache.commons.io.FileUtils;
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
        
        String tomcatDeployDir = bundlePath + replaceByPS("/tomcat/webapps/");
        try {
            FileUtils.copyFileToDirectory(new File(buildPath + replaceByPS("/regadb-ui/dist/regadb-ui-0.9.war")), new File(tomcatDeployDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            File templateTxt = new File(bundleDir + replaceByPS("/conf/template.txt"));
            templateTxt.createNewFile();
            FileUtils.copyDirectory(new File(buildDir + replaceByPS("/packages/regadb-install")), new File(bundleDir + replaceByPS("/regadb-install")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
