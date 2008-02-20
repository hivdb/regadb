package net.sf.regadb.io.generation;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Patient;
import net.sf.regadb.util.hbm.InterpreteHbm;
import net.sf.regadb.util.pair.Pair;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class GenerateIO
{
	private List<Class> classeslisted_ = new ArrayList<Class>();
	private Element rootE1_;
	private Class startclass_;
	
	private static Class[] regaClasses_;
    private static ArrayList<String> classToBeIgnored_ = new ArrayList<String>();
    private static String dbPackage = "net.sf.regadb.db.";
    private static ArrayList<String> stringRepresentedFields_ = new ArrayList<String>();
    private static ArrayList<String> pointerClasses_ = new ArrayList<String>();
    private static ArrayList<String> nominalValues_ = new ArrayList<String>();
    
    private static ArrayList<Pair<String, String>>  stringRepresentedFieldsRepresentationFields_ = new ArrayList<Pair<String, String>> ();
    private static ArrayList<Pair<String, String>>  fieldsToBeIgnored_ = new ArrayList<Pair<String, String>> ();
    
    private static ArrayList<Class>  idClasses_ = new ArrayList<Class> ();
      
    private ArrayList<Class> grammarAlreadyWritten_ = new ArrayList<Class>();
	
    static
    {
        try
        {
            regaClasses_ = net.sf.regadb.util.reflection.PackageUtils.getClasses(Patient.class.getPackage().getName());
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        
        classToBeIgnored_.add(dbPackage + "DatasetAccess");
        
        fieldsToBeIgnored_.add(new Pair<String, String>(dbPackage+"AttributeNominalValue", "attribute"));
        fieldsToBeIgnored_.add(new Pair<String, String>(dbPackage+"EventNominalValue", "event"));
        fieldsToBeIgnored_.add(new Pair<String, String>(dbPackage+"TestNominalValue", "testType"));
        fieldsToBeIgnored_.add(new Pair<String, String>(dbPackage+"Analysis", "tests"));
        fieldsToBeIgnored_.add(new Pair<String, String>(dbPackage+"AnalysisData", "analysis"));
        
        stringRepresentedFields_.add(dbPackage + "DrugGeneric");
        stringRepresentedFields_.add(dbPackage + "DrugCommercial");
        stringRepresentedFields_.add(dbPackage + "Protein");
        stringRepresentedFields_.add(dbPackage + "AnalysisType");
        stringRepresentedFields_.add(dbPackage + "TherapyMotivation");
        
        stringRepresentedFieldsRepresentationFields_.add(new Pair<String, String>(dbPackage + "DrugGeneric", "genericId"));
        stringRepresentedFieldsRepresentationFields_.add(new Pair<String, String>(dbPackage + "DrugCommercial", "name"));
        stringRepresentedFieldsRepresentationFields_.add(new Pair<String, String>(dbPackage + "Protein", "abbreviation"));
        stringRepresentedFieldsRepresentationFields_.add(new Pair<String, String>(dbPackage + "AnalysisType", "type"));
        stringRepresentedFieldsRepresentationFields_.add(new Pair<String, String>(dbPackage + "TherapyMotivation", "value"));
        
        pointerClasses_.add(dbPackage + "Test");
        pointerClasses_.add(dbPackage + "TestType");
        pointerClasses_.add(dbPackage + "ValueType");
        pointerClasses_.add(dbPackage + "TestObject");
        pointerClasses_.add(dbPackage + "TestNominalValue");
        
        pointerClasses_.add(dbPackage + "Attribute");
        pointerClasses_.add(dbPackage + "AttributeGroup");
        pointerClasses_.add(dbPackage + "AttributeNominalValue");
        
        pointerClasses_.add(dbPackage + "Event");
        pointerClasses_.add(dbPackage + "EventNominalValue");
        
        pointerClasses_.add(dbPackage + "Analysis");
        pointerClasses_.add(dbPackage + "AnalysisData");
        
        nominalValues_.add(dbPackage + "TestNominalValue");
        nominalValues_.add(dbPackage + "AttributeNominalValue");
        nominalValues_.add(dbPackage + "EventNominalValue");
    }
    
    public static String getStringRepValueName(String className)
    {
        for(Pair<String, String> c : stringRepresentedFieldsRepresentationFields_)
        {
            if(c.getKey().equals(className) || c.getKey().equals(dbPackage+className))
            {
                return c.getValue();
            }
        }
        
        return null;
    }
    
    private void setNs(Element el)
    {
        Namespace relaxng = Namespace.getNamespace("http://relaxng.org/ns/structure/1.0");
        el.setNamespace(relaxng);
    }
    
	public GenerateIO(String strstartclass,String rootnodename)
	{
		rootE1_ = new Element("grammar");
        setNs(rootE1_);
		//rootE1_.setAttribute("name", rootnodename);

		try
		{
			startclass_ = Class.forName(strstartclass);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
    private boolean alreadyWritten(Class c)
    {
        for(Class grammarC : grammarAlreadyWritten_)
        {
            if(grammarC.equals(c))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private Class replacePatientDatasetByDataset(Class cc)
    {       
       //Exception because we cannot acces PatientDataset directly 
       if(cc.getName().equals("net.sf.regadb.db.PatientDataset"))
       {
           try 
           {
               cc = Class.forName("net.sf.regadb.db.Dataset");
           } 
           catch (ClassNotFoundException e) 
           {
               e.printStackTrace();
           }
       }
       
       return cc;
    }
       
	private void writeClassGrammar(Class c)
	{
       if(alreadyWritten(c))
           return;
		
       if(c.getSimpleName().equals("Test"))
       {
           System.err.println("analysis");
       }
		c = replacePatientDatasetByDataset(c);
		
		InterpreteHbm interpreter = InterpreteHbm.getInstance();
        
        String id = XMLWriteCodeGen.createString();
        String id2 = XMLWriteCodeGen.createString();
        
        CsvWriteCodeGen.methodSig(id, c);
        DatasetAccessInterfaceCodeGen.methodSig(id, c);
        XMLWriteCodeGen.writeTopMethod(c, id2);
        XMLWriteCodeGen.writeMethodSig(c, id);
        
        XMLReadCodeGen.addObject(c, id);
		
        Field[] fields = c.getDeclaredFields();
        
        Element toAdd = rootE1_;
  
        Element define = new Element("define");
        setNs(define);
        String className = c.getName();
        //PatientImpl is not accessible for security reasons
        if(className.indexOf("PatientImpl")>-1)
        {
            className = className.replace("PatientImpl", "Patient");
        }
        define.setAttribute("name", className);
        toAdd.addContent(define);
        toAdd = define;
        
        Element startEl = toAdd;
        for(Field field : fields)
        {
            toAdd = startEl;
            
                Class bareClass;
                if(field.getType() == Set.class)
                {
                    bareClass = extractSetType(field);
                }
                else
                {
                    bareClass = field.getType();
                }
                
                if(!isFieldToBeIgnored(c, field, bareClass))
                {
                    handleField(field, toAdd, bareClass, c, id);
                }
        }
        
        CsvWriteCodeGen.methodEnd(id, c);
        XMLWriteCodeGen.writeMethodSigEnd(id);
	}
    
    private void handleField(Field field, Element toAdd, Class bareClass, Class c, String id)
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        boolean set = false;
        
        String fieldName = field.getName();
        
		//Exception because we cannot acces PatientDataset directly 
        if(field.getName().equals("patientDatasets"))
        {
			bareClass = replacePatientDatasetByDataset(bareClass);
            fieldName = "Datasets";
        }
        
        if(!interpreter.isComposite(c.getName(), field.getName()))
        {
            // if the field is a set >> zeroOrMore
            if (field.getType() == Set.class) 
            {
                Element setEl = new Element("element");
                setNs(setEl);
                setEl.setAttribute("name", fieldName);
                Element zeroOrMore = new Element("zeroOrMore");
                setNs(zeroOrMore);
                setEl.addContent(zeroOrMore);
                toAdd.addContent(setEl);
                toAdd = zeroOrMore;
                set = true;
            }
            // if the field can be null >> optional
            else if (!interpreter.isNotNull(field.getType().getName(), field.getName())) 
            {
                Element optional = new Element("optional");
                setNs(optional);
                toAdd.addContent(optional);
                toAdd = optional;
            }        
    
            Element fieldEl = new Element("element");
            setNs(fieldEl);
            if(!set)
            {
                fieldEl.setAttribute("name", field.getName());
            }
            else
            {
                fieldEl.setAttribute("name", fieldName+"-el");
            }
            toAdd.addContent(fieldEl);
            toAdd = fieldEl;
        }
        
        grammarAlreadyWritten_.add(c);
        
        if(isRegaClass(bareClass) && !isStringRepresentedField(bareClass) && !interpreter.isComposite(c.getName(), field.getName()) && !isPointer(bareClass))
        {
            addReference(toAdd, bareClass);
            if(set)
            {
                XMLWriteCodeGen.writeSet(bareClass, fieldName, "parentNode", id);
                XMLReadCodeGen.addSet(id, field.getName(), bareClass);
            }
            else
            {
                XMLWriteCodeGen.callClassWriteMethod(null,bareClass, field.getName(), "parentNode", id);
            }
            
            writeClassGrammar(bareClass);
        }
        else if(isStringRepresentedField(bareClass))
        {
            Element data = new Element("data");
            setNs(data);
            toAdd.addContent(handleStringField(data, null));
            XMLWriteCodeGen.writeStringRepresentedValue(id, field.getName(), bareClass, false, "parentNode");
            XMLReadCodeGen.addRepresentedValue(id, field.getName(), bareClass, false);
            CsvWriteCodeGen.stringRepresentedValue(id, field.getName(), bareClass, false, c);
        }
        else if(isPointer(bareClass))
        {
            handlePointer(toAdd, bareClass, set);
            
            if(!set)
            {
                XMLWriteCodeGen.writePointer(id, bareClass, field.getName(), "parentNode", false, c);
                XMLReadCodeGen.addPointer(id, field.getName(), bareClass, false);
            }
            else
            {
                XMLWriteCodeGen.writePointerSet(id, bareClass, field.getName(), "parentNode", c);
                XMLReadCodeGen.addPointerSet(id, field.getName(), bareClass);
            }
        }
        else if(interpreter.isComposite(c.getName(), field.getName()))
        {
            idClasses_.add(field.getType());
            for(Field compositeField : field.getType().getDeclaredFields())
            {
                if(isStringRepresentedField(compositeField.getType()))
                {
                    Element el = new Element("element");
                    setNs(el);
                    el.setAttribute("name", compositeField.getName());
                    toAdd.addContent(el);
                    Element data = new Element("data");
                    setNs(data);
                    el.addContent(handleStringField(data, null));
                    XMLWriteCodeGen.writeStringRepresentedValue(id, compositeField.getName(), compositeField.getType(), true, "parentNode");
                    XMLReadCodeGen.addRepresentedValue(id, compositeField.getName(), compositeField.getType(), true);
                    CsvWriteCodeGen.stringRepresentedValue(id, compositeField.getName(), compositeField.getType(), true, c);
                }
                else if(isPointer(compositeField.getType()))
                {
                    Element el = new Element("element");
                    setNs(el);
                    el.setAttribute("name", compositeField.getName());
                    toAdd.addContent(el);
                    handlePointer(el, compositeField.getType(), false);
                    XMLWriteCodeGen.writePointer(id, compositeField.getType(), compositeField.getName(), "parentNode", false, c);
                    XMLReadCodeGen.addPointer(id, compositeField.getName(), compositeField.getType(), true);
                }
                else 
                {
                    Integer length = interpreter.getLength(compositeField.getType().getName(), compositeField.getName());
                    Element data = addPrimitiveType(compositeField, length);
                    //This is a test to see wether it is really a primitive field
                    //otherwise it is ignored
                    if(data!=null)
                    {
                        Element el = new Element("element");
                        setNs(el);
                        el.setAttribute("name", compositeField.getName());
                        toAdd.addContent(el);
                        el.addContent(data);
                        XMLWriteCodeGen.writePrimitiveVar("id",compositeField, "parentNode", id);
                        XMLReadCodeGen.addPrimitive(id, compositeField.getName(), compositeField.getType(), true);
                        CsvWriteCodeGen.writePrimitiveVar("id", compositeField, id, c, true);
                    }
                }
            }
        }
        else //primitive field
        {
            if(field.getName().equals("data"))
            {
                System.err.println("stop");
            }
            XMLWriteCodeGen.writePrimitiveVar(null, field, "parentNode", id);
            XMLReadCodeGen.addPrimitive(id, field.getName(), bareClass, false);
            CsvWriteCodeGen.writePrimitiveVar(null, field, id, c, false);
            Integer length = interpreter.getLength(c.getName(), field.getName());
            Element primitive = addPrimitiveType(field, length);
            if(primitive==null)
            {
                try
                {
                    throw new Exception("Ran into an unsupported primitive type!!!!" + field.getName());
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }
            else
            {
                toAdd.addContent(primitive);
            }
        }
    }
    
    private void handlePointer(Element toAdd, Class bareClass, boolean set)
    {
        Element reference = new Element("element");
        setNs(reference);
        reference.setAttribute("name", "reference");
        Element data = new Element("data");
        setNs(data);
        data.setAttribute("type", "int");
        data.setAttribute(getDataTypeLib());
        reference.addContent(data);
        toAdd.addContent(reference);
        if(set || !isNominalClass(bareClass))
        {
            Element optional = new Element("optional");
            setNs(optional);
            Namespace relaxng = Namespace.getNamespace("http://relaxng.org/ns/structure/1.0");
            optional.setNamespace(relaxng);
            toAdd.addContent(optional);
            addReference(optional, bareClass);
            writeClassGrammar(bareClass);
        }
    }
    
    private boolean isNominalClass(Class classs)
    {
        for(String c : nominalValues_)
        {
            if(c.equals(classs.getName()) || c.equals(dbPackage+classs.getName()))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isStringRepresentedField(Class classs)
    {
        for(String c : stringRepresentedFields_)
        {
            if(c.equals(classs.getName()) || c.equals(dbPackage+classs.getName()))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isPointer(Class classs)
    {
        for(String c : pointerClasses_)
        {
            if(c.equals(classs.getName()) || c.equals(dbPackage+classs.getName()))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isFieldToBeIgnored(Class c, Field field, Class bareFieldClass)
    {
        if(field.getName().equals("testNominalValue"))
        {
            System.err.println("nominal");
        }
        
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        
        if(interpreter.isId(c.getName(), field.getName()))
        {
            return true;
        }
        
        if(interpreter.isVersion(c.getName(), field.getName()))
        {
            return true;
        }
        
        if(isClassToBeIgnored(bareFieldClass))
        {
            return true;
        }
        
        if(interpreter.isManyToOne(c.getName(), field.getName()) &&
                !isPointer(bareFieldClass) &&
                !isStringRepresentedField(bareFieldClass))
        {
            return true;
        }
        
        for(Pair<String, String> ign : fieldsToBeIgnored_)
        {
            if(ign.getKey().equals(c.getName()) && ign.getValue().equals(field.getName()))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private void addReference(Element parentNode, Class ref)
    {
        Element refEl = new Element("ref");
        setNs(refEl);
        refEl.setAttribute("name", ref.getName());
        parentNode.addContent(refEl);
    }
    
    private Attribute getDataTypeLib()
    {
        return new Attribute("datatypeLibrary", "http://www.w3.org/2001/XMLSchema-datatypes");
        //return new Attribute("datatypeLibrary", "lib");
    }
    
    private Element handleStringField(Element data, Integer length)
    {
        data.setAttribute("type", "string");
        data.setAttribute(getDataTypeLib());
        if(length!=null)
        {
            Element param = new Element("param");
            setNs(param);
            param.setAttribute("name", "maxLength");
            param.addContent(new Text(length.intValue()+""));
            data.addContent(param);
        }
        
        return data;
    }
    
    private Element addPrimitiveType(Field field, Integer length)
    {
        String fieldType = field.getType().toString();
        Element data = new Element("data");
        setNs(data);
        
        if(fieldType.indexOf("String")>-1)
        {
            handleStringField(data, length);
            return data;
        }
        else if(fieldType.toLowerCase().indexOf("short")>-1)
        {
            data.setAttribute("type", "short");
            data.setAttribute(getDataTypeLib());
            return data;
        }
        else if(fieldType.toLowerCase().indexOf("int")>-1)
        {
            data.setAttribute("type", "int");
            data.setAttribute(getDataTypeLib());
            return data;
        }
        else if(fieldType.toLowerCase().indexOf("double")>-1)
        {
            data.setAttribute("type", "double");
            data.setAttribute(getDataTypeLib());
            return data;
        }
        else if(fieldType.indexOf("Date")>-1)
        {
            data.setAttribute("type", "date");
            data.setAttribute(getDataTypeLib());
            return data;
        }
        else if(fieldType.toLowerCase().indexOf("boolean")>-1)
        {
            data.setAttribute("type", "boolean");
            data.setAttribute(getDataTypeLib());
            return data;
        }
        else if(fieldType.toLowerCase().indexOf("[b")>-1)
        {
            data.setAttribute("type", "base64Binary");
            data.setAttribute(getDataTypeLib());
            return data;
        }
        
        return null;
    }
    
    private boolean isClassToBeIgnored(Class classs)
    {
        for(String c : classToBeIgnored_)
        {
            if(c.equals(classs.getName()) || c.equals(dbPackage+classs.getName()))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private Class extractSetType(Field setField)
    {
        String setInfo = setField.toGenericString();
        int startfrom = setInfo.indexOf('<') + 1;
        int endat = setInfo.indexOf('>');
        String classStr = setInfo.substring(startfrom, endat);
        try 
        {
            return Class.forName(classStr);
        } 
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

	public void generate()
	{
		try
		{
            //relaxng schema
            //create start element
            Element start = new Element("start");
            setNs(start);
            Element el = new Element("element");
            setNs(el);
            el.setAttribute("name", "Patients");
            start.addContent(el);
            Element zeroOrMore = new Element("zeroOrMore");
            setNs(zeroOrMore);
            el.addContent(zeroOrMore);
            Element ref = new Element("ref");
            setNs(ref);
            zeroOrMore.addContent(ref);
            ref.setAttribute("name", "net.sf.regadb.db.Patient");
            rootE1_.addContent(start);

            Document n = new Document(rootE1_);
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
            String srcDir = getSrcPath("net.sf.regadb.io.relaxng");
            File relaxNgFile = new File(srcDir+File.separatorChar+"regadb-relaxng.xml");
            FileWriter fw = new FileWriter(relaxNgFile);
            outputter.output(n, fw);
            fw.flush();
            fw.close();
            //relaxng schema
            
            //export java code
            srcDir = getSrcPath("net.sf.regadb.io.exportXML");
            File exportJavaCodeFile = new File(srcDir+File.separatorChar+"ExportToXML.java");
            String exportCode = XMLWriteCodeGen.createClassCode(pointerClasses_);
            fw = new FileWriter(exportJavaCodeFile);
            fw.write(exportCode);
            fw.flush();
            fw.close();
            //export java code
            
            //import java code
            srcDir = getSrcPath("net.sf.regadb.io.importXML");
            File importJavaCodeFile = new File(srcDir+File.separatorChar+"ImportFromXML.java");
            fw = new FileWriter(importJavaCodeFile);
            XMLReadCodeGen.generate(fw);
            fw.flush();
            fw.close();
            //import java code
            
            CsvWriteCodeGen.writeClassToFile();
            DatasetAccessInterfaceCodeGen.writeInterfaceToFile();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
    
    public static String getSrcPath(String pckName)
    {
        URL packageURL = Thread.currentThread().getContextClassLoader().getResource(pckName.replace('.', '/'));
        File directory = new File(URLDecoder.decode(packageURL.getFile()));
        File srcDir = new File(directory.getAbsolutePath().replace(File.separatorChar+"bin"+File.separatorChar, File.separatorChar+"src"+File.separatorChar));
        
        return srcDir.getAbsolutePath();
    }

	private boolean isRegaClass(Class searchclass)
	{
		for (Class c : regaClasses_)
		{
			if (c.equals(searchclass))
			{
				return true;
			}
		}

		return false;
	}

	private boolean isClassListed(Class searchclass)
	{
		for (Class c : classeslisted_)
		{
			if (c.equals(searchclass))
			{
				return true;
			}
		}

		return false;
	}

	void init()
	{
        writeClassGrammar(startclass_);
	}

	public static void main(String[] args)
	{
        long start = System.currentTimeMillis();
        GenerateIO test = new GenerateIO("net.sf.regadb.db.PatientImpl", "Patients");
		test.init();
		test.generate();
        long duration = System.currentTimeMillis() -start;
        System.out.println("duration:"+duration);
        
        //check wether all classes are written
        for(Class c : regaClasses_)
        {
            boolean grammarWritten = false;
            if(test.grammarAlreadyWritten_.contains(c))
            {
                grammarWritten = true;
            }
            boolean isStringRep = false;
            for(Pair<String, String> stringRep : stringRepresentedFieldsRepresentationFields_)
            {
                if(stringRep.getKey().equals(c.getName()))
                {
                    isStringRep = true;
                    break;
                }
            }
            
            Class[] interfaces = c.getInterfaces();
            boolean isSeriazable = false;
            for(Class ii : interfaces)
            {
                if(ii.getName().equals("java.io.Serializable"))
                {
                    isSeriazable = true;
                }
            }
            
            boolean idClass = false;
            if(idClasses_.contains(c))
            {
                idClass = true;
            }
            
            if(!grammarWritten && !isStringRep && isSeriazable && !idClass)
                System.err.println("oeioei"+c);
            
        }
	}
}
