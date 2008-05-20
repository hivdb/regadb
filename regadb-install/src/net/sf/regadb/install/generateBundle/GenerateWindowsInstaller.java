package net.sf.regadb.install.generateBundle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import net.sf.regadb.build.builder.Jarbuilder;

import org.apache.commons.io.FileUtils;

public class GenerateWindowsInstaller {
    public static void main(String [] args) {
    	if(args.length<1) {
    		System.err.println("Provide a workspace as argument!");
    		System.exit(0);
    	}
        File buildDir = null;
        File reportDir = null;
        File bundleDir = null;
        try {
            buildDir = createTempDir("installer", "regadb_installer_build");
            bundleDir = createTempDir("installer", "regadb_installer_bundle");
            reportDir = createTempDir("installer", "regadb_installer_report");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Jarbuilder builder = new Jarbuilder();
        builder.run(buildDir.getAbsolutePath(), reportDir.getAbsolutePath(), args[0], false);
        GenerateWindowsBundles.run(buildDir.getAbsolutePath(), bundleDir.getAbsolutePath());
        
        try {
            File nsisFile = new File(buildDir.getAbsolutePath() + File.separatorChar + "regadb-nsis" + File.separatorChar + "Regadb.nsi");
            byte[] array = FileUtils.readFileToByteArray(nsisFile);
            String nsisFileContent = new String(array);
            nsisFileContent = nsisFileContent.replaceAll("\\$FILE_SOURCE\\$", bundleDir.getAbsolutePath());
            FileUtils.writeByteArrayToFile(nsisFile, nsisFileContent.getBytes());
            
            System.err.println("start makensis");
            String [] nsisCommand = { "makensis",  nsisFile.getAbsolutePath() };
            Process child = Runtime.getRuntime().exec(nsisCommand);
            InputStream in = child.getInputStream();
            int c;
            char cc;
            while ((c = in.read()) != -1) {
                cc = (char)c;
            }
            in.close();
            System.err.println("stop makensis");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static File createTempDir(String prefix, String name) throws IOException {
        File tempFile = File.createTempFile(prefix, name);
        if (!tempFile.delete())
            throw new IOException();
        if (!tempFile.mkdir())
            throw new IOException();
        return tempFile;
    }
}
