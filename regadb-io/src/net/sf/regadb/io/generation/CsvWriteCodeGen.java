package net.sf.regadb.io.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.util.hbm.InterpreteHbm;


public class CsvWriteCodeGen {
    private static Map<String, String> contentMethod = new HashMap<String, String>();
    private static Map<String, String> headerMethod = new HashMap<String, String>();
    private static String contentCallMethod = "";
    
    public static void methodSig(String id, Class classToWrite) {
        String sig = "public String getCsvContentLine(" + classToWrite.getSimpleName() + " " + classToWrite.getSimpleName()+"var) {\n";
        sig += "String " +classToWrite.getSimpleName() + "Line = \"\";\n";
        contentMethod.put(id, sig);
        sig = "public String getCsvHeaderLine(" + classToWrite.getSimpleName() + " " + classToWrite.getSimpleName()+"var) {\n";
        sig += "String " +classToWrite.getSimpleName() + "Line = \"\";\n";
        headerMethod.put(id, sig);
        contentCallMethod += "else if(object instanceof " + classToWrite.getSimpleName() + ") {\n";
        contentCallMethod += "return getCsvContentLine((" + classToWrite.getSimpleName() + ")object);\n}\n";
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
        temp += parentClass.getSimpleName() + "Line += " + var2 + "+\",\";\n";
        
        contentMethod.put(id, temp);
    }
 
    public static void writePrimitiveVar(String grandFatherFieldName, Field field, String id, Class parentClass) {
    String writeClassCode="";
    
    String var = XMLWriteCodeGen.generateGetterConstruct(id, grandFatherFieldName, field.getName());
    
        String fieldType = field.getType().toString();
        String startChar = "";
        if(fieldType.indexOf("class")>-1)
        {
            writeClassCode += "if("+var+"!=null) ";
            writeClassCode += "{\n";
            startChar = "";
        }
        
        String temp = contentMethod.get(id);
        temp += writeClassCode;
        temp += parentClass.getSimpleName() + "Line += ";
        if(fieldType.indexOf("class")>-1)
        {
            if(fieldType.indexOf("Date")>-1)
            {
                temp += "XMLTools.dateToRelaxNgString("+ var + ")";
            }
            else if(fieldType.indexOf("[B")>-1)
            {
                temp += "XMLTools.base64Encoding("+ var + ")";
            }
            else
            {
                temp += var + ".toString()";
            }
        }
        else
        {
            temp += "String.valueOf("+ var+")";
        }
        
        temp += "+\",\";\n";
        if(fieldType.indexOf("class")>-1)
        {
            temp += "}\n";
        }
        
        contentMethod.put(id, temp);
    }
    
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
        imports += "import net.sf.regadb.util.xml.XMLTools;";
        
        total+=imports+"\n";
        
        total+="public class ExportToCsv {\n";
        
        for(Map.Entry<String, String> e : contentMethod.entrySet()) {
            total += e.getValue() + "\n";
        }
        
        for(Map.Entry<String, String> e : headerMethod.entrySet()) {
            total += e.getValue() + "\n";
        }
        
        contentCallMethod = contentCallMethod.replaceFirst("else ", "");
        total += "public String getCsvLineSwitch(Object object) {\n" + contentCallMethod + "\n return null;\n}\n";
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
