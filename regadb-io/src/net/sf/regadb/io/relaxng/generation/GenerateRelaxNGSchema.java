package net.sf.regadb.io.relaxng.generation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Patient;
import net.sf.regadb.util.hbm.InterpreteHbm;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class GenerateRelaxNGSchema
{
	private List<Class> classeslisted_ = new ArrayList<Class>();
	private Element rootE1_;
	private Class startclass_;
	
	private static Class[] regaClasses_;
    private static ArrayList<String> classToBeIgnored_ = new ArrayList<String>();
    private static String dbPackage = "net.sf.regadb.db.";
    private static ArrayList<String> stringRepresentedFields_ = new ArrayList<String>();
    private static ArrayList<String> pointerClasses = new ArrayList<String>();
    
      
    private ArrayList<Class> grammarAlreadyWritten_ = new ArrayList<Class>();
	
    static
    {
        try
        {
            regaClasses_ = net.sf.regadb.util.reflection.Package.getInstance().getClasses(Patient.class.getPackage().getName());
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        
        classToBeIgnored_.add(dbPackage + "DatasetAccess");
        
        stringRepresentedFields_.add(dbPackage + "DrugGeneric");
        stringRepresentedFields_.add(dbPackage + "DrugCommercial");
        stringRepresentedFields_.add(dbPackage + "Protein");
        
   
    }
    
	public GenerateRelaxNGSchema(String strstartclass,String rootnodename)
	{
		rootE1_ = new Element("element");
		Namespace relaxng = Namespace.getNamespace("http://relaxng.org/ns/structure/1.0");
		rootE1_.setNamespace(relaxng);
		rootE1_.setAttribute("name", rootnodename);

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
    
	private void writeClassGrammar(Class c)
	{
       if(alreadyWritten(c))
           return;
        
		InterpreteHbm interpreter = InterpreteHbm.getInstance();
		
        Field[] fields = c.getDeclaredFields();
        
        Element toAdd = rootE1_;
  
        Element define = new Element("define");
        define.setAttribute("name", c.getName());
        toAdd.addContent(define);
        toAdd = define;
        
        Element startEl = toAdd;
        for(Field field : fields)
        {
            toAdd = startEl;
            
            if(!alreadyWritten(field.getType()))
            {
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
                    handleField(field, toAdd, bareClass, c);
                }
            }
        }
	}
    
    private void handleField(Field field, Element toAdd, Class bareClass, Class c)
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        
        if(!interpreter.isComposite(c.getName(), field.getName()))
        {
            // if the field is a set >> zeroOrMore
            if (field.getType() == Set.class) 
            {
                Element zeroOrMore = new Element("zeroOrMore");
                toAdd.addContent(zeroOrMore);
                toAdd = zeroOrMore;
            }
            // if the field can be null >> optional
            else if (!interpreter.isNotNull(field.getType().getName(), field.getName())) 
            {
                Element optional = new Element("optional");
                toAdd.addContent(optional);
                toAdd = optional;
            }        
    
            Element fieldEl = new Element("element");
            fieldEl.setAttribute("name", field.getName());
            toAdd.addContent(fieldEl);
            toAdd = fieldEl;
        }
        
        grammarAlreadyWritten_.add(c);
        
        if(isRegaClass(bareClass) && !isStringRepresentedField(bareClass) && !interpreter.isComposite(c.getName(), field.getName()))
        {
            addReference(toAdd, bareClass);
            writeClassGrammar(bareClass);
        }
        else if(isStringRepresentedField(bareClass))
        {
            toAdd.addContent(handleStringField(new Element("data"), null));
        }
        else if(interpreter.isComposite(c.getName(), field.getName()))
        {
            for(Field compositeField : field.getType().getDeclaredFields())
            {
                if(isStringRepresentedField(compositeField.getType()))
                {
                    Element el = new Element("element");
                    el.setAttribute("name", compositeField.getName());
                    toAdd.addContent(el);
                    el.addContent(handleStringField(new Element("data"), null));
                }
                else 
                {
                    Integer length = interpreter.getLength(compositeField.getType().getName(), compositeField.getName());
                    Element data = addPrimitiveType(compositeField, length);
                    if(data!=null)
                    {
                        Element el = new Element("element");
                        el.setAttribute("name", compositeField.getName());
                        toAdd.addContent(el);
                        el.addContent(data);
                    }
                }
            }
        }
        else //primitive field
        {
            Integer length = interpreter.getLength(c.getName(), field.getName());
            Element primitive = addPrimitiveType(field, length);
            if(primitive==null)
            {
                System.err.println("Ran into an unsupported primitive type!!!!" + field.getName());
            }
            else
            {
                toAdd.addContent(primitive);
            }
        }
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
        for(String c : this.pointerClasses)
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
        
        return false;
    }
    
    private void addReference(Element parentNode, Class ref)
    {
        Element refEl = new Element("ref");
        refEl.setAttribute("name", ref.getName());
        parentNode.addContent(refEl);
    }
    
    private Attribute getDataTypeLib()
    {
        //return new Attribute("datatypeLibrary", "http://www.w3.org/2001/XMLSchema-datatypes");
        return new Attribute("datatypeLibrary", "lib");
    }
    
    private Element handleStringField(Element data, Integer length)
    {
        data.setAttribute("type", "string");
        data.setAttribute(getDataTypeLib());
        if(length!=null)
        {
            Element param = new Element("param");
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

	public void printXmlSchema()
	{
		try
		{
			Document n = new Document(rootE1_);
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
			outputter.output(n, System.out);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
		GenerateRelaxNGSchema test = new GenerateRelaxNGSchema("net.sf.regadb.db.PatientImpl", "Patients");
		test.init();
		test.printXmlSchema();
	}

}
