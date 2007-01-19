package net.sf.regadb.io.relaxng.generation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
		
	private void makeXmlSchema(Class c, Element parentnode)
	{
		InterpreteHbm interpreter = InterpreteHbm.getInstance();
		Element child = new Element("element");
		Field[] pfields = c.getDeclaredFields();
		String currentfieldname;
        
		child.setAttribute("name", c.getName().substring(c.getName().lastIndexOf(".") + 1));
		parentnode.addContent(child);
		
		for (int m = 0; m < pfields.length; m++)
		{
			currentfieldname = pfields[m].getName();
			if ((pfields[m].getType() == Set.class))
			{
                handleSet(child, pfields[m], c.getName());
			}
			else if (!interpreter.isId(c.getName(), currentfieldname)
						&& !interpreter.isVersion(c.getName(), currentfieldname) 
                        && !isClassToBeIgnored(c.getName()) 
                        && !isClassListed(c))
			{
			    Element toAdd = handleOptionalFields(child, pfields[m], c.getName());
				Element childnode = new Element("element");
				childnode.setAttribute("name", currentfieldname);
                toAdd.addContent(childnode);
			}
		}

		classeslisted_.add(c.getClass());
	}
    
    private boolean isClassToBeIgnored(String className)
    {
        for(String c : classToBeIgnored_)
        {
            if(c.equals(className) || c.equals(dbPackage+className))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private void handleSet(Element child, Field field, String parentClassStr)
    {
        try
        {
            String setInfo = field.toGenericString();
            int startfrom = setInfo.indexOf('<') + 1;
            int endat = setInfo.indexOf('>');
            String classStr = setInfo.substring(startfrom, endat);
            if (isRegaClass(Class.forName(classStr)) && !isClassToBeIgnored(classStr) && !isClassListed(Class.forName(classStr))) 
            {
                Element zeroOrMore = new Element("zeroOrMore");
                child.addContent(zeroOrMore);
                makeXmlSchema(Class.forName(classStr), zeroOrMore);
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
    //the returned Element is the Element where you should put your stuff
    private Element handleOptionalFields(Element child, Field field, String parentClassStr)
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        Element toAdd;
        if(!interpreter.isNotNull(parentClassStr, field.getName()))
        {
            Element optional = new Element("optional");
            child.addContent(optional);
            toAdd = optional;
        }
        else
        {
            toAdd = child;
        }
        
        return toAdd;
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
		makeXmlSchema(startclass_, rootE1_);
	}

	public static void main(String[] args)
	{
		GenerateRelaxNGSchema test = new GenerateRelaxNGSchema("net.sf.regadb.db.PatientImpl", "Patients");
		test.init();
		test.printXmlSchema();
	}

}
