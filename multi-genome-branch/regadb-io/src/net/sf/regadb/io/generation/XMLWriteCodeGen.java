package net.sf.regadb.io.generation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.sf.regadb.db.AaInsertionId;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Test;
import net.sf.regadb.util.hbm.InterpreteHbm;

public class XMLWriteCodeGen 
{
    private HashMap<String, String> methodString_;
    private HashMap<String, String> varNameList_;
    private int amounfOfMethod_;
    
    public XMLWriteCodeGen() {
        methodString_ = new HashMap<String, String>();
        varNameList_ = new HashMap<String, String> ();
        amounfOfMethod_ = 0;
    	
    }
    
    public String createString()
    {
        String id = "id" + amounfOfMethod_;
        String value = "";
        methodString_.put(id, value);
        amounfOfMethod_++;
        return id;
    }
    
    private void addString(String id, String addValue)
    {
        String original = methodString_.get(id);
        original += addValue;
        methodString_.put(id, original);
    }
    
    public void writeMethodSigEnd(String id)
    {
        String writeClassCode = "}";
        addString(id, writeClassCode);
    }
    
    public void writeTopMethod(Class toWrite, String id)
    {
        String writeClassCode="";
        
        writeClassCode += "public void writeTop"+toWrite.getSimpleName()+"("+toWrite.getSimpleName()+ " " + toWrite.getSimpleName()+"var, Element rootNode)";
        writeClassCode += "{";
        StringBuffer className = new StringBuffer(toWrite.getSimpleName());
        className.replace(0, 1, new String(className.charAt(0)+"").toLowerCase());
        writeClassCode += "Element elNode = new Element(\""+className.toString()+"s-el"+"\");";
        writeClassCode += "rootNode.addContent(elNode);";
        writeClassCode += "write"+toWrite.getSimpleName()+"("+toWrite.getSimpleName()+"var, elNode);";
        writeClassCode += "}";
        
        addString(id, writeClassCode);
    }
    
    public void writeMethodSig(Class toWrite, String id)
    {
        String writeClassCode="";
        
        writeClassCode += "public void write"+toWrite.getSimpleName()+"("+toWrite.getSimpleName()+ " " + toWrite.getSimpleName()+"var, Element rootNode)";
        writeClassCode += "{";
        writeClassCode += "Element parentNode = new Element(\""+toWrite.getSimpleName()+"\");";
        writeClassCode += "rootNode.addContent(parentNode);";
        writeClassCode += "if("+toWrite.getSimpleName()+"var==null)";
        writeClassCode += "{";
        writeClassCode += "return;";
        writeClassCode += "}";

        varNameList_.put(id, toWrite.getSimpleName()+"var");
        
        addString(id, writeClassCode);
    }
    
    public void writeSet(Class toWrite, String fieldName, String xmlParentNode, String id)
    {
        String writeClassCode="";
        
        String loopVarName = toWrite.getSimpleName() +"loopvar";
        writeClassCode += "Element "+ fieldName+"El = new Element(\""+fieldName+"\");";
        writeClassCode += ""+xmlParentNode+".addContent("+fieldName+"El);";
        writeClassCode += "for (" + toWrite.getSimpleName() + " " +loopVarName+ " : " + generateGetterConstruct(id,null,fieldName,toWrite) +"){";
        writeClassCode += "Element "+ fieldName+"_elEl = new Element(\""+fieldName+"-el\");";
        writeClassCode += ""+fieldName+"El"+".addContent("+fieldName+"_elEl);";
        //temporarly saving otherwise callClassWriteMethod does not have the new content
        addString(id, writeClassCode);
        callClassWriteMethod(null, toWrite, loopVarName, fieldName+"_elEl", id,loopVarName) ;
        writeClassCode = "}";
        
        addString(id, writeClassCode);
    }
    
    public String generateGetterConstruct(String id, String grandFatherFieldName, String fieldName, Class fieldClass)
    {
        String toReturn = "";
        
        String prefix;
        if(fieldClass == boolean.class){
            prefix = "is";
        }
        else{
            prefix = "get";
        }
        
        String fatherFieldName = varNameList_.get(id);
        
        if(grandFatherFieldName==null&&fatherFieldName==null)
        {
            return fieldName;
        }
        
        if(grandFatherFieldName!=null)
        {
            char upperCase = Character.toUpperCase(grandFatherFieldName.charAt(0));
            String dotGettergrandFather = prefix + upperCase + grandFatherFieldName.substring(1) +"()";
            
            upperCase = Character.toUpperCase(fieldName.charAt(0));
            String dotGetterField = prefix + upperCase + fieldName.substring(1) +"()";
            
            toReturn += fatherFieldName + "." + dotGettergrandFather + "." + dotGetterField;
        }
        else
        {
            char upperCase = Character.toUpperCase(fieldName.charAt(0));
            String dotGetterField = prefix + upperCase + fieldName.substring(1) +"()";
            
            toReturn += fatherFieldName + "." + dotGetterField;
        }
        
        return toReturn;
    }
    
    public void writePrimitiveVar(String grandFatherFieldName, Field field, String parentNode, String id)
    {
        String writeClassCode="";
        
        String var = generateGetterConstruct(id, grandFatherFieldName, field.getName(), field.getType());
        
            String fieldType = field.getType().toString();
            String startChar = "";
            if(fieldType.indexOf("class")>-1)
            {
                writeClassCode += "if("+var+"!=null)";
                writeClassCode += "{";
                startChar = "";
            }
            
            String primValEl = field.getName()+"primitiveValEl";
            writeClassCode += startChar + "Element "+primValEl+" = new Element(\""+field.getName()+"\");";
            if(fieldType.indexOf("class")>-1)
            {
                if(fieldType.indexOf("Date")>-1)
                {
                    writeClassCode += startChar + primValEl + ".addContent(XMLTools.dateToRelaxNgString("+ var + "));";
                }
                else if(fieldType.indexOf("[B")>-1)
                {
                    writeClassCode += startChar + primValEl + ".addContent(XMLTools.base64Encoding("+ var + "));";
                }
                else
                {
                    writeClassCode += startChar + primValEl + ".addContent("+ var + ".toString());";
                }
            }
            else
            {
                writeClassCode += startChar + primValEl + ".addContent(String.valueOf("+ var+"));";
            }
            writeClassCode += startChar + parentNode+".addContent("+primValEl+");";         
            
            if(fieldType.indexOf("class")>-1)
            {
                writeClassCode += "}";
            }

            addString(id, writeClassCode);
    }
    
    public void writePointer(String id, Class toWrite, String fieldName, String parentNode, boolean doNotTransformFieldName, Class parentClass)
    {
        //boolean doNotTransformFieldName is necessary when working from a loop (writePointerSet)
        String writeClassCode = "";
        
        String var;
        var = generateGetterConstruct(id, null, fieldName, toWrite);

        boolean foundVarInClass = false;
        for(Field f : parentClass.getDeclaredFields())
        {
            if(f.getName().equals(fieldName))
            {
                foundVarInClass = true;
                break;
            }
        }
        
        if(!foundVarInClass)
        {
            var = generateGetterConstruct(id, "id", fieldName, toWrite);
        }
        
        if(doNotTransformFieldName)
        {
            var = fieldName;
        }
        
        writeClassCode += "if("+var+"!=null)";
        writeClassCode += "{";
        writeClassCode += "Integer index" + fieldName+" = " + toWrite.getSimpleName() +"PMap.get(Ids.getUniqueId(" + var +"));";
        if(doNotTransformFieldName)
        {
            writeClassCode += "Element wrapper"+fieldName +" = " + parentNode+";";
        }
        else
        {
            writeClassCode += "Element wrapper"+fieldName+" = new Element(\""+fieldName+"\");";
            writeClassCode += parentNode + ".addContent(" + "wrapper"+fieldName+");";
        }
        writeClassCode += "if(index"+fieldName+"!=null)";
        writeClassCode += "{";
        writeClassCode += handlePointerRef(fieldName);
        //writeClassCode += "return;";
        writeClassCode += "}";
        writeClassCode += "else";
        writeClassCode += "{";
        writeClassCode += "index"+ fieldName+" = new Integer("+toWrite.getSimpleName() +"PMap.size());";
        writeClassCode += handlePointerRef(fieldName);
        writeClassCode += toWrite.getSimpleName() +"PMap.put(Ids.getUniqueId("+var+"),index"+fieldName+");";
        addString(id, writeClassCode);
        
        callClassWriteMethod(null, toWrite, var, "wrapper"+fieldName, id);
        
        writeClassCode = "}";
        writeClassCode += "}";
        addString(id, writeClassCode);
    }
    
    private String  handlePointerRef(String fieldName)
    {
        String writeClassCode = "";
        
        writeClassCode += "Element refElement"+fieldName+"= new Element(\"reference\");";
        writeClassCode += "wrapper"+fieldName + ".addContent(" + "refElement"+fieldName+");";
        writeClassCode += "refElement"+fieldName + ".addContent(index"+fieldName+".toString());";
        
        return writeClassCode;
    }
    
    public void writePointerSet(String id, Class toWrite, String fieldName, String parentNode, Class parentClass)
    {
        if(fieldName.equals("patientDatasets")){
            toWrite = GenerateIO.replacePatientDatasetByDataset(toWrite);
            fieldName = "datasets";
        }
        String var = generateGetterConstruct(id, null, fieldName, toWrite);
        String writeClassCode = "";
        writeClassCode += "Element forParent"+fieldName+" = new Element(\""+fieldName+"\");";
        writeClassCode += parentNode+".addContent(forParent"+fieldName+");";
        writeClassCode += "if("+var+".size()!=0)";
        writeClassCode += "{";
        writeClassCode += "Element forParentLoopVar;"; 
        writeClassCode += "for("+toWrite.getSimpleName() +" " +fieldName +"loopvar :" +var+")";
        writeClassCode += "{";
        writeClassCode += "forParentLoopVar = new Element(\""+fieldName+"-el\");";
        writeClassCode += "forParent"+fieldName+".addContent(forParentLoopVar);";
        addString(id, writeClassCode);
        
        writePointer(id, toWrite, fieldName +"loopvar", "forParentLoopVar", true, parentClass);
        
        writeClassCode = "}";
        writeClassCode += "}";
        addString(id, writeClassCode);
    }
    
    public void callClassWriteMethod(String grandFatherFieldName, Class toWrite, String fieldName, String parentNode, String id, String noGetter)
    {
        String var = generateGetterConstruct(id, grandFatherFieldName, fieldName, toWrite);
        if(noGetter!=null)
        {
            var = noGetter;
        }
        
        String writeClassCode = "write"+toWrite.getSimpleName()+"("+fieldName+","+parentNode+");";
        
        addString(id, writeClassCode);
    }
    
    public void callClassWriteMethod(String grandFatherFieldName, Class toWrite, String fieldName, String parentNode, String id)
    {
        callClassWriteMethod(grandFatherFieldName, toWrite, fieldName, parentNode, id, null);
    }
    
    private void printAndClear(String id)
    {
        System.out.println(methodString_.get(id));
        methodString_.put(id, "");
    }
    
    public void writeStringRepresentedValue(String id, String fieldName, Class toWrite, String stringRepField, boolean composite, String parentNode)
    {
        stringRepField = Character.toUpperCase(stringRepField.charAt(0)) + stringRepField.substring(1);
        String var;
        var = generateGetterConstruct(id, composite?"id":null, fieldName, toWrite);
        String var2 = var + ".get" + stringRepField+"()";
        String writeClassCode = "";
        writeClassCode += "if("+var+"!=null &&" +var2+"!=null)";
        writeClassCode += "{";
        writeClassCode += "Element "+fieldName+"var = new Element(\""+fieldName+"\");";
        writeClassCode += parentNode + ".addContent(" + fieldName+"var);";
        writeClassCode += fieldName+"var.addContent(" + var2 +");";
        writeClassCode += "}";
         
        addString(id, writeClassCode);
    }
    
    public static void main(String [] args)
    {
    	XMLWriteCodeGen xmlWriteCodeGen = new XMLWriteCodeGen();
        String id = xmlWriteCodeGen.createString();
        xmlWriteCodeGen.writeMethodSig(AaSequence.class, id);
        //printAndClear();
        xmlWriteCodeGen.writeSet(AaSequence.class, "sequences", "parentNode", id);
        //printAndClear();
        
        Date today = new Date();
        System.err.println(today.toString());
        
        Class c = Dataset.class;
        Field [] fs = c.getDeclaredFields();
        System.err.println("test");
        
        xmlWriteCodeGen.writePrimitiveVar(null, fs[5], "parentNode", id);
        xmlWriteCodeGen.printAndClear(id);
        
        xmlWriteCodeGen.writePrimitiveVar(null, fs[3], "parentNode", id);
        xmlWriteCodeGen.printAndClear(id);
        
        c = AaInsertionId.class;
        fs = c.getDeclaredFields();
        
        xmlWriteCodeGen.writePrimitiveVar(null, fs[0], "parentNode", id);
        xmlWriteCodeGen.printAndClear(id);
        
        System.out.println(xmlWriteCodeGen.generateGetterConstruct(id, "patient", "currentDate", java.util.Date.class));
        System.out.println(xmlWriteCodeGen.generateGetterConstruct(id, null, "currentDate", java.util.Date.class));
        
        xmlWriteCodeGen.writePointer(id, Test.class,"testField", "parentNode", false, null);
        
        //System.out.println(createClassCode());
    }
    
    public String createClassCode(ArrayList<String> pointerClasses)
    {
        String total = "";
        
        //package declaration
        total = "package net.sf.regadb.io.exportXML;\n";
        //package declaration
        
        //imports
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        String imports = "";
        for(String className : interpreter.getClassNames())
        {
            imports += "import "+className +";";
        }
        imports += "import net.sf.regadb.util.xml.XMLTools;";
        imports += "import org.jdom.Element;";
        imports += "import java.util.HashMap;";
        imports += "import net.sf.regadb.db.meta.Ids;";
        
        total += imports +"\n";
        //imports
        
        //class definition
        total += "public class ExportToXML {";
        //class definition
        
        //pointer hashmaps
        for(String pointerClass : pointerClasses)
        {
            String pointer = pointerClass.substring(pointerClass.lastIndexOf('.')+1);
            String line = "HashMap<" +"String"+", Integer> " + pointer +"PMap = new HashMap<"+"String"+", Integer>();";
            total += line;
        }
        //pointer hashmaps
        
        //methods
        for(java.util.Map.Entry<String, String> entry : methodString_.entrySet())
        {
            total += entry.getValue();
        }
        //methods
        
        //end class definition
        total += "}";
        //end class definition
         
        //replace PatientImpl by Patient
        //PatientImpl is for security reasons not accessible
        total = total.replace("PatientImpl", "Patient");
        
        String formatted = beautifyCode(total);
        
        return formatted;
    }
    
    private static String beautifyCode(String code)
    {
        StringBuffer toReturn = new StringBuffer(1000);
        int amountOfBrackets = 0;
        String startChars = "";
        
        char c;
        for(int i = 0; i<code.length(); i++)
        {
            c = code.charAt(i);
            
            if(c=='{')
            {
                amountOfBrackets++;
                toReturn.append('\n');                            
                
                toReturn.append(startChars);
                toReturn.append(c);
                toReturn.append('\n');

                startChars += '\t';
                toReturn.append(startChars);
            }
            else if(c==';')
            {
                toReturn.append(c);
                toReturn.append('\n');
                toReturn.append(startChars);
            }
            else if(c=='}')
            {
                amountOfBrackets--;
                
                startChars = startChars.substring(0,startChars.length()-1);
                toReturn.deleteCharAt(toReturn.length()-1);
                
                toReturn.append(c);
                toReturn.append('\n');
                toReturn.append(startChars);
            }
            else 
            {
                toReturn.append(c);
            }
        }
        return toReturn.toString();
    }
}
