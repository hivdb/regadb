package net.sf.regadb.io.relaxng.generation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.Patient;
import net.sf.regadb.util.hbm.InterpreteHbm;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
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
                    //if the field is a set >> zeroOrMore
                    if(field.getType() == Set.class)
                    {
                        Element zeroOrMore = new Element("zeroOrMore");
                        toAdd.addContent(zeroOrMore);
                        toAdd = zeroOrMore;
                    }
                    //if the field can be null >> optional
                    else if(!interpreter.isNotNull(field.getType().getName(), field.getName()))
                    {
                        Element optional = new Element("optional");
                        toAdd.addContent(optional);
                        toAdd = optional;
                    }
                    
                    Element fieldEl = new Element("element");
                    fieldEl.setAttribute("name", field.getName());
                    toAdd.addContent(fieldEl);
                    toAdd = fieldEl;
                    
                    grammarAlreadyWritten_.add(c);
                    
                    if(isRegaClass(bareClass))
                    {
                        addReference(toAdd, bareClass);
                        writeClassGrammar(bareClass);
                    }
                    else if(checkPrimitiveField(field))
                    {
                        //TODO also pass the length
                        addPrimitiveType(toAdd, field, null);
                    }
                    else
                    {
                        System.err.println("Ran into an unsupported primitive type!!!!" + field.getName());
                    }
                }
            }
        }
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
        
        return false;
    }
    
    private void addReference(Element parentNode, Class ref)
    {
        Element refEl = new Element("ref");
        refEl.setAttribute("name", ref.getName());
        parentNode.addContent(refEl);
    }
    
    private void addPrimitiveType(Element parentNode, Field field, Integer length)
    {
        
    }
    
    private boolean checkPrimitiveField(Field field)
    {
        if(field.getClass() == Integer.class)
        {
            return true;
        }
        else if(field.getClass() == Short.class)
        {
            return true;
        }
        else if(field.getClass() == String.class)
        {
            return true;
        }
        else if(field.getClass() == Double.class)
        {
            return true;
        }
        
        return false;
    }
    
    private void handleField()
    {
        
    }
    
    private boolean isClassToBeIgnored(Class classs)
    {
        if(true)
        {
            System.err.println("lala");
        }
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
