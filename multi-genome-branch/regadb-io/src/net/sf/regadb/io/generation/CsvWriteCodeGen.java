package net.sf.regadb.io.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientImplHelper;
import net.sf.regadb.io.datasetAccess.DatasetAccessSolver;
import net.sf.regadb.util.hbm.InterpreteHbm;


public class CsvWriteCodeGen {
    private static Map<String, String> contentMethod = new HashMap<String, String>();
    private static Map<String, String> headerMethod = new HashMap<String, String>();
    private static String contentCallMethod = "";
    private static String headerCallMethod = "";
    
    public static void methodSig(String id, Class classToWrite) {
        String sig = "public String getCsvContentLine(" + classToWrite.getSimpleName().replace("PatientImpl", "Patient") + " " + classToWrite.getSimpleName()+"var) {\n";
        sig += "String " +classToWrite.getSimpleName() + "Line = \"\";\n";
        contentMethod.put(id, sig);
        
        sig = "public String getCsvHeaderLine" + classToWrite.getSimpleName().replace("PatientImpl", "Patient") + "() {\n";
        sig += "String " +classToWrite.getSimpleName() + "Line = \"\";\n";
        headerMethod.put(id, sig);
        
        if(!classToWrite.getSimpleName().equals("PatientImpl")) {
        contentCallMethod += "else if(object instanceof " + classToWrite.getSimpleName() + ") {\n";
        contentCallMethod += "if(DatasetAccessSolver.getInstance().canAccess"+classToWrite.getSimpleName()+"(("+classToWrite.getSimpleName()+")object, datasets, accessiblePatients"+")){\n";
        contentCallMethod += "return getCsvContentLine((" + classToWrite.getSimpleName() + ")object);\n}\n";
        contentCallMethod += "else {\n return null;\n}\n}\n";
        
        
        headerCallMethod += "else if(object instanceof " + classToWrite.getSimpleName() + ") {\n";
        headerCallMethod += "return getCsvHeaderLine"+classToWrite.getSimpleName()+"();\n}\n";
        } else {
            patientHeaderContent();
        }
    }
    
    public static void patientHeaderContent() {
        contentCallMethod +=  "if(PatientImplHelper.isInstanceOfPatientImpl(object)) {\n" +
            "Patient p_casted = PatientImplHelper.castPatientImplToPatient(object, datasets);\n" +
            "if(DatasetAccessSolver.getInstance().canAccessPatient(p_casted, datasets, accessiblePatients)){\n" +
            "return getCsvContentLine(p_casted);\n" +
            "}\n" +
            "else {\n" +
             "return null;\n" +
            "}\n" +
        "}\n";
        
        headerCallMethod +=  "if(PatientImplHelper.isInstanceOfPatientImpl(object)) {";
        headerCallMethod += "return getCsvHeaderLine"+"Patient"+"();\n}\n";
    }

    public static void methodEnd(String id, Class classToWrite) {
        String temp = contentMethod.get(id);
        temp += "return " + classToWrite.getSimpleName() + "Line;";
        temp += "\n}\n";
        contentMethod.put(id, temp);
        
        temp = headerMethod.get(id);
        temp += "return " + classToWrite.getSimpleName() + "Line;";
        temp += "\n}\n";
        headerMethod.put(id, temp);
    }
    
    public static void stringRepresentedValue(String id, String fieldName, Class toWrite, boolean composite, Class parentClass)
    {
        String stringRepField = GenerateIO.getStringRepValueName(toWrite.getName());
        stringRepField = Character.toUpperCase(stringRepField.charAt(0)) + stringRepField.substring(1);
        String var;
        var = XMLWriteCodeGen.generateGetterConstruct(id, composite?"id":null, fieldName,toWrite);
        String var2 = var + ".get" + stringRepField+"()";
        
        String temp = contentMethod.get(id);
        temp += "if(" + var +"!=null) {\n";
        temp += parentClass.getSimpleName() + "Line += " + var2 + ";\n";
        temp += "}\n";
        temp += parentClass.getSimpleName() + "Line += " + "\",\";\n";
        contentMethod.put(id, temp);
        
        temp = headerMethod.get(id);
        if(composite) {
            temp += parentClass.getSimpleName() + "Line += \"" + parentClass.getSimpleName() + ".id." + fieldName + ",\";\n";
        }
        else {
            temp += parentClass.getSimpleName() + "Line += \"" + parentClass.getSimpleName() + "." + fieldName + ",\";\n";
        }
        headerMethod.put(id, temp);
    }
 
    public static void writePrimitiveVar(String grandFatherFieldName, Field field, String id, Class parentClass, boolean composite) {
    String writeClassCode="";
    
    String var = XMLWriteCodeGen.generateGetterConstruct(id, grandFatherFieldName, field.getName(),field.getType());
    
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
        
        temp += ";\n";
        
        if(fieldType.indexOf("class")>-1)
        {
            temp += "}\n";
        }
        
        temp += parentClass.getSimpleName() + "Line += ";
        temp += "\",\";\n";

        contentMethod.put(id, temp);
        
        temp = headerMethod.get(id);
        if(composite) {
            temp += parentClass.getSimpleName() + "Line += \"" + parentClass.getSimpleName() + ".id." + field.getName() + ",\";\n";
        }
        else {
            temp += parentClass.getSimpleName() + "Line += \"" + parentClass.getSimpleName() + "." + field.getName() + ",\";\n";
        }
        headerMethod.put(id, temp);
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
            imports += "import "+className.replace("PatientImpl", "Patient") +";\n";
        }
        imports += "import net.sf.regadb.db.PatientImplHelper;\n";
        imports += "import java.util.Set;\n";
        imports += "import net.sf.regadb.util.xml.XMLTools;\n";
        imports += "import net.sf.regadb.io.datasetAccess.DatasetAccessSolver;\n";
        
        total+=imports+"\n";
        
        total+="public class ExportToCsv {\n";
        
        for(Map.Entry<String, String> e : contentMethod.entrySet()) {
            total += e.getValue() + "\n";
        }
        
        for(Map.Entry<String, String> e : headerMethod.entrySet()) {
            total += e.getValue() + "\n";
        }
        
        total += "public String getCsvLineSwitch(Object object, Set<Dataset> datasets, Set<Integer> accessiblePatients) {\n" + contentCallMethod + "\n return null;\n}\n";

        total += "public String getCsvHeaderSwitch(Object object) {\n" + headerCallMethod + "\n return null;\n}\n";
        
        total += "\n}";
        
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
