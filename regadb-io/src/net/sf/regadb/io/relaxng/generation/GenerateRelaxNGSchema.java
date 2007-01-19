package net.sf.regadb.io.relaxng.generation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Patient;
import net.sf.regadb.io.hibernate.mapping.InterpreteHbm;

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
	}

	private void makeXmlSchema(Class c, Element parentnode)
	{
		InterpreteHbm interpreter = InterpreteHbm.getInstance();
		Element child = new Element("element");
		Field[] pfields = c.getDeclaredFields();
		String currentfieldname = new String();

		if (c.getName().equals(startclass_.getName())) //startclass.getname()
		{
			
		}
		else
		{
			parentnode.setAttribute("name", parentnode.getAttributeValue("name"));
		}

		child.setAttribute("name", c.getName().substring(c.getName().lastIndexOf(".") + 1));
		parentnode.addContent(child);
		
		for (int m = 0; m < pfields.length; m++)
		{
			currentfieldname = pfields[m].getName();
			if ((pfields[m].getType() == Set.class))
			{
				try
				{
					String setInfo = pfields[m].toGenericString();
					int startfrom = setInfo.indexOf('<') + 1;
					int endat = setInfo.indexOf('>');
					String classstr = setInfo.substring(startfrom, endat);
					if (isRegaClass(Class.forName(classstr))) 
					{
						makeXmlSchema(Class.forName(classstr), child);
					}
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				if (!interpreter.isId(c.getName(), currentfieldname)
						&& !interpreter.isVersion(c.getName(), currentfieldname))
				{
					Element childnode = new Element("element");
					childnode.setAttribute("name", currentfieldname);
					child.addContent(childnode);
				}

			}
		}

		classeslisted_.add(c.getClass());
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
