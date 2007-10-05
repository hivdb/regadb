package net.sf.regadb.io.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.regadb.util.hbm.InterpreteHbm;


public class DatasetAccessInterfaceCodeGen {
    public static StringBuffer content = new StringBuffer();
    
    public static void methodSig(String id, Class classToWrite) {
        String sig = "public boolean canAccess" + classToWrite.getSimpleName() +
            "(" + classToWrite.getSimpleName() + " " + classToWrite.getSimpleName()+"var, Set<DatasetAccess> datasets);\n";
        content.append(sig);
    }
    
    public static void writeInterfaceToFile() {
        String total = "";
        
        //package declaration
        total = "package net.sf.regadb.io.datasetAccess;\n";
        //package declaration
        
        //imports
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        String imports = "";
        for(String className : interpreter.getClassNames())
        {
            imports += "import "+className +";\n";
        }
        imports += "import java.util.Set;\n";
        
        total+=imports+"\n";
        
        total+="public interface IDatasetAccess {\n";
        
        total+=content.toString();
        
        total += "}";
        
        total = total.replace("PatientImpl", "Patient");
        
        String srcDir = GenerateIO.getSrcPath("net.sf.regadb.io.datasetAccess");
        File exportJavaCodeFile = new File(srcDir+File.separatorChar+"IDatasetAccess.java");
        FileWriter fw;
        try {
            fw = new FileWriter(exportJavaCodeFile);
            fw.write(total);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
