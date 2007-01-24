package net.sf.regadb.io.relaxng.generation;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;

import net.sf.regadb.db.AaInsertionId;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Dataset;

public class XMLWriteCodeGen 
{
    private static HashMap<String, String> methodString_ = new HashMap<String, String>();
    private static HashMap<String, String> varNameList_ = new HashMap<String, String> ();
    private static int amounfOfMethod_ = 0;
    
    public static String createString()
    {
        String id = "id" + amounfOfMethod_;
        String value = "";
        methodString_.put(id, value);
        amounfOfMethod_++;
        return id;
    }
    
    private static void addString(String id, String addValue)
    {
        String original = methodString_.get(id);
        original += addValue;
        methodString_.put(id, original);
    }
    
    public static void writeMethodSigEnd(String id)
    {
        String writeClassCode = "}\n";
        addString(id, writeClassCode);
    }
    
    public static void writeMethodSig(Class toWrite, String id)
    {
        String writeClassCode="";
        
        writeClassCode += "public void write"+toWrite.getSimpleName()+"("+toWrite.getSimpleName()+ " " + toWrite.getSimpleName()+"var, Element parentNode)\n";
        writeClassCode += "{\n";
        writeClassCode += "\tif("+toWrite.getSimpleName()+"var==null)\n";
        writeClassCode += "\t{\n";
        writeClassCode += "\treturn;\n";
        writeClassCode += "\t}\n";
        
        varNameList_.put(id, toWrite.getSimpleName()+"var");
        
        addString(id, writeClassCode);
    }
    
    public static void writeSet(Class toWrite, String fieldName, String xmlParentNode, String id)
    {
        String writeClassCode="";
        
        String loopVarName = toWrite.getSimpleName() +"loopvar";
        writeClassCode += "for (" + toWrite.getSimpleName() + " " +loopVarName+ " : " + generateGetterConstruct(id,null,fieldName) +")\n{\n";
        writeClassCode += "\tElement "+ toWrite.getSimpleName()+"El = new Element(\""+fieldName+"\");\n";
        writeClassCode += "\t"+xmlParentNode+".addContent("+toWrite.getSimpleName()+"El);\n";
        //temporarly saving otherwise callClassWriteMethod does not have the new content
        addString(id, writeClassCode);
        callClassWriteMethod(null, toWrite, loopVarName, toWrite.getSimpleName()+"El", id,loopVarName) ;
        writeClassCode = "}\n";
        
        addString(id, writeClassCode);
    }
    
    private static String generateGetterConstruct(String id, String grandFatherFieldName, String fieldName)
    {
        String toReturn = "";
        
        String fatherFieldName = varNameList_.get(id);
        
        if(grandFatherFieldName==null&&fatherFieldName==null)
        {
            return fieldName;
        }
        
        if(grandFatherFieldName!=null)
        {
            char upperCase = Character.toUpperCase(grandFatherFieldName.charAt(0));
            String dotGettergrandFather = "get" + upperCase + grandFatherFieldName.substring(1) +"()";
            
            upperCase = Character.toUpperCase(fieldName.charAt(0));
            String dotGetterField = "get" + upperCase + fieldName.substring(1) +"()";
            
            toReturn += fatherFieldName + "." + dotGettergrandFather + "." + dotGetterField;
        }
        else
        {
            char upperCase = Character.toUpperCase(fieldName.charAt(0));
            String dotGetterField = "get" + upperCase + fieldName.substring(1) +"()";
            
            toReturn += fatherFieldName + "." + dotGetterField;
        }
        
        return toReturn;
    }
    
    public static void writePrimitiveVar(String grandFatherFieldName, Field field, String parentNode, String id)
    {
        String writeClassCode="";
        
        String var = generateGetterConstruct(id, grandFatherFieldName, field.getName());
        
            String fieldType = field.getType().toString();
            String startChar = "";
            if(fieldType.indexOf("class")>-1)
            {
                writeClassCode += "if("+var+"!=null)\n";
                writeClassCode += "{\n";
                startChar = "\t";
            }
            
            String primValEl = field.getName()+"primitiveValEl";
            writeClassCode += startChar + "Element "+primValEl+" = new Element(\""+field.getName()+"\");\n";
            if(fieldType.indexOf("class")>-1)
            {
                if(fieldType.indexOf("Date")>-1)
                {
                    writeClassCode += startChar + primValEl + ".addContent(XmlTools.dateToString("+ var + "));\n";
                }
                else
                {
                    writeClassCode += startChar + primValEl + ".addContent("+ var + ".toString());\n";
                }
            }
            else
            {
                writeClassCode += startChar + primValEl + ".addContent(String.valueOf("+ var+"));\n";
            }
            writeClassCode += startChar + parentNode+".addContent("+primValEl+");\n";         
            
            if(fieldType.indexOf("class")>-1)
            {
                writeClassCode += "}\n";
            }

            addString(id, writeClassCode);
    }
    
    public static void callClassWriteMethod(String grandFatherFieldName, Class toWrite, String fieldName, String parentNode, String id, String noGetter)
    {
        String var = generateGetterConstruct(id, grandFatherFieldName, fieldName);
        if(noGetter!=null)
        {
            var = noGetter;
        }
        
        String writeClassCode = "\twrite"+toWrite.getSimpleName()+"("+fieldName+","+parentNode+");\n";
        
        addString(id, writeClassCode);
    }
    
    public static void callClassWriteMethod(String grandFatherFieldName, Class toWrite, String fieldName, String parentNode, String id)
    {
        callClassWriteMethod(grandFatherFieldName, toWrite, fieldName, parentNode, id, null);
    }
    
    private static void printAndClear(String id)
    {
        System.out.println(methodString_.get(id));
        methodString_.put(id, "");
    }
    
    public static void main(String [] args)
    {
        String id = createString();
        writeMethodSig(AaSequence.class, id);
        //printAndClear();
        writeSet(AaSequence.class, "sequences", "parentNode", id);
        //printAndClear();
        
        Date today = new Date();
        System.err.println(today.toString());
        
        Class c = Dataset.class;
        Field [] fs = c.getDeclaredFields();
        System.err.println("test");
        
        writePrimitiveVar(null, fs[5], "parentNode", id);
        printAndClear(id);
        
        writePrimitiveVar(null, fs[3], "parentNode", id);
        printAndClear(id);
        
        c = AaInsertionId.class;
        fs = c.getDeclaredFields();
        
        writePrimitiveVar(null, fs[0], "parentNode", id);
        printAndClear(id);
        
        System.out.println(generateGetterConstruct(id, "patient", "currentDate"));
        System.out.println(generateGetterConstruct(id, null, "currentDate"));
    }
    
    public static void printAllMethods()
    {
        for(java.util.Map.Entry<String, String> entry : methodString_.entrySet())
        {
            System.out.println(entry.getValue()+"\n\n");
        }
    }
}
