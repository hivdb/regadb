package net.sf.regadb.io.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.util.hbm.InterpreteHbm;


public class CsvWriteCodeGen {
    private static Map<String, String> contentMethod = new HashMap<String, String>();
    private static Map<String, String> headerMethod = new HashMap<String, String>();
    
    public static void methodSig(String id, Class classToWrite) {
        String sig = "public String getCsvContentLine(" + classToWrite.getSimpleName() + " " + classToWrite.getSimpleName()+"var) {\n";
        sig += "String " +classToWrite.getSimpleName() + "Line = \"\";\n";
        contentMethod.put(id, sig);
        sig = "public String getCsvHeaderLine(" + classToWrite.getSimpleName() + " " + classToWrite.getSimpleName()+"var) {\n";
        sig += "String " +classToWrite.getSimpleName() + "Line = \"\";\n";
        headerMethod.put(id, sig);
    }

    public static void methodEnd(String id, Class classToWrite) {
        String temp = contentMethod.get(id);
        temp += "return " + classToWrite.getSimpleName() + "Line;";
        temp += "\n}";
        contentMethod.put(id, temp);
        
        temp = headerMethod.get(id);
        temp += "return " + classToWrite.getSimpleName() + "Line;";
        temp += "\n}";
        headerMethod.put(id, temp);
    }
    
    public static void stringRepresentedValue(String id, String fieldName, Class toWrite, boolean composite, Class parentClass)
    {
        String stringRepField = GenerateIO.getStringRepValueName(toWrite.getName());
        stringRepField = Character.toUpperCase(stringRepField.charAt(0)) + stringRepField.substring(1);
        String var;
        var = XMLWriteCodeGen.generateGetterConstruct(id, composite?"id":null, fieldName);
        String var2 = var + ".get" + stringRepField+"()";
        
        String temp = contentMethod.get(id);
        temp += parentClass.getSimpleName() + "Line += " + var2 + ";\n";
        
        contentMethod.put(id, temp);
    }
    
    /*public static void stringRepresentedValue(String id, Class parentClass, String fieldName, Class toWrite) {
        String stringRepField = GenerateIO.getStringRepValueName(toWrite.getName());
        stringRepField = Character.toUpperCase(stringRepField.charAt(0)) + stringRepField.substring(1);
        String toAdd = parentClass.getSimpleName() + "Line += " + parentClass.getSimpleName()+"arg.get"+toWrite.getSimpleName()+"().get"+stringRepField+"() + \",\";";
        String temp = contentMethod.get(id);
        temp += toAdd;
        contentMethod.put(id, temp);
    }*/
    
    public static void writeClassToFile() {
        String total = "";
        
        //package declaration
        total = "package net.sf.regadb.io.exportCsv;\n";
        //package declaration
        
        //imports
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        String imports = "";
        for(String className : interpreter.getClassNames())
        {
            imports += "import "+className +";\n";
        }
        
        total+=imports+"\n";
        
        total+="public class ExportToCsv {\n";
        
        for(Map.Entry<String, String> e : contentMethod.entrySet()) {
            total += e.getValue() + "\n";
        }
        
        for(Map.Entry<String, String> e : headerMethod.entrySet()) {
            total += e.getValue() + "\n";
        }
        
        total += "\n}";
        
        total = total.replace("PatientImpl", "Patient");
        
        String srcDir = GenerateIO.getSrcPath("net.sf.regadb.io.exportCsv");
        File exportJavaCodeFile = new File(srcDir+File.separatorChar+"ExportToCsv.java");
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
