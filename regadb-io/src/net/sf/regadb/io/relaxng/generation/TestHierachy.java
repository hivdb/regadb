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

public class TestHierachy
{
	private static List<Class> classeslisted = new ArrayList<Class>();
	private static Element rootE1;
	private Class startclass;
	
	private static Class[] regaClasses_;
	TestHierachy(String strstartclass)
	{
		rootE1 = new Element("element");

		try
		{
			startclass = Class.forName(strstartclass);
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

	static void makexmlschema(Class c, Element parentnode)
	{
		InterpreteHbm interpreter = InterpreteHbm.getInstance();
		Element child = new Element("element");
		Field[] pfields = c.getDeclaredFields();
		String currentfieldname = new String();

		if (c.getName().equals("net.sf.regadb.db.PatientImpl")) //startclass.getname()
		{
			Namespace relaxng = Namespace.getNamespace("http://relaxng.org/ns/structure/1.0");
			parentnode.setNamespace(relaxng);
			parentnode.setAttribute("name", "Patients");
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
						makexmlschema(Class.forName(classstr), child);
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

		classeslisted.add(c.getClass());
	}

	static void printxmlschema()
	{

		try
		{
			Document n = new Document(rootE1);
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
			outputter.output(n, System.out);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	static boolean isRegaClass(Class searchclass)
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

	static boolean isClassListed(Class searchclass)
	{

		for (Class c : classeslisted)
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
		
		makexmlschema(startclass, rootE1);

	}

	public static void main(String[] args)
	{
		TestHierachy test = new TestHierachy("net.sf.regadb.db.PatientImpl");
		test.init();
		printxmlschema();
	}

}
