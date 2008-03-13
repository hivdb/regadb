package net.sf.regadb.util.hbm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;

import net.sf.regadb.util.reflection.PackageUtils;

public class FixJavaFiles {
    private File[] javaFiles_;
    
    public static void main(String[] args)
    {
        FixJavaFiles fp = new FixJavaFiles();
        fp.run();
        System.out.println("Finished fixing Java files.");
    }
    
    public FixJavaFiles(){
        
    }
    
    public void run(){
        init();
        String deleteline = "// Generated";
        
        for(File f : javaFiles_){
            replaceLine(f,deleteline,"");
            
            if(f.getName().equals("PatientImpl.java")){
                replaceLine(f,"class PatientImpl implements","class PatientImpl implements java.io.Serializable {");
            }
        }
    }
    
    public void replaceLine(File f, String oldline, String newline){
        if(f.canWrite()){
            try{
                String orifilestr = f.getAbsolutePath();
                String backupfilestr = orifilestr +"~";

                File fixedfile = new File(backupfilestr);
                
                BufferedReader fr = new BufferedReader(new FileReader(f));
                BufferedWriter fw = new BufferedWriter(new FileWriter(fixedfile));
                
                String line;
                boolean replaced=false;
                while((line = fr.readLine()) != null){
                    if(!replaced && (line.indexOf(oldline) != -1)){
                        replaced = true;
                        if(newline.length() > 0)
                            fw.write(newline +"\n");
                    }
                    else{
                        fw.write(line +"\n");
                    }
                }
                
                fw.close();
                fr.close();
                
                f.delete();
                fixedfile.renameTo(f);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void init()
    {
        File directory = new File(PackageUtils.getDirectoryPath("net.sf.regadb.db", "regadb-persist"));
  
        javaFiles_ = directory.listFiles(new FileFilter()
        {
            public boolean accept(File pathname)
            {
                if(pathname.getAbsolutePath().indexOf(".java")!=-1)
                {
                    return true;
                }
                else
                {
                    return false;   
                }
            }
        });
    }
}
