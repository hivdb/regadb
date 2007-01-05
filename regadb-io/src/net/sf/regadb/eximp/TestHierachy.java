package net.sf.regadb.eximp;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.sf.regadb.db.Patient;
import net.sf.regadb.hibernate.InterpreteHbm;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class TestHierachy {
	private static Class[] regaClasses_;
	private static List<Class> classeslisted=new ArrayList<Class>();
	private static Element rootE1=new Element("element");
	static
	{
		try 
		{
			regaClasses_ = getClasses(Patient.class.getPackage().getName());
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
 static void makexmlschema(Class c, Element parentnode)
  {
	
	 
	 InterpreteHbm interpreter = InterpreteHbm.getInstance();
	 //Element rootEl = new Element("element");
	 Element child = new Element("element");
	
	 //Element newnode =new Element("element"); 
	 Field [] pfields =c.getDeclaredFields();
	 String currentfieldname=new String();
	 
	 if(c.getName().equals("net.sf.regadb.db.PatientImpl"))
	 {
		 Namespace relaxng = Namespace.getNamespace("http://relaxng.org/ns/structure/1.0");
		 parentnode.setNamespace(relaxng);
		 parentnode.setAttribute("name", "Patients");
	 } 
	 else //Continue at the previous
	 {
		 parentnode.setAttribute("name", parentnode.getAttributeValue("name")); 
		 /*Element childnode = new Element("element");
		 newnode.setAttribute("name", currentfieldname);
		 childnode.addContent(newnode);*/
	 }
	 
	 child.setAttribute("name", c.getName().substring(c.getName().lastIndexOf(".")+1));
	 parentnode.addContent(child); 
	 //Element nextparent =child.getParentElement();
	
//
	 for (int m=0; m<pfields.length;m++)
	 {
		 currentfieldname =pfields[m].getName();
		if((pfields[m].getType() == Set.class))
		{	
			try
			{
				String setInfo = pfields[m].toGenericString();
				int startfrom=setInfo.indexOf('<')+1;
				int endat=setInfo.indexOf('>');
				String classstr=setInfo.substring( startfrom,endat);
				if(isRegaClass(Class.forName(classstr))) //create a new node on current parent
				{
					//newnode.setAttribute("name", currentfieldname);
					makexmlschema( Class.forName(classstr),child);
				}
			}
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		else //if not regaclass 
		 {				
			if(!interpreter.isId(c.getName(), currentfieldname)&& 
					!interpreter.isVersion(c.getName(), currentfieldname))
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
	catch(Exception e) 
	{
		e.printStackTrace();
	} 
}
 static boolean isRegaClass(Class searchclass) 
 {

	
	 
	 for(Class c : regaClasses_)
	 {
		 if(c.equals(searchclass))
		 {
			 return true;
		 }
	 }
	 
	 return false;
}
 
 static boolean isClassListed(Class searchclass) 
 {

	
	 for(Class c : classeslisted)
	 {
		 if(c.equals(searchclass))
		 {
			 return true;
		 }
	 }
	 
	 return false;
}
 
 /** 
  * list Classes inside a given package 
  * @param pckgname String name of a Package, EG "java.lang" 
  * @return Class[] classes inside the root of the given package 
  * @throws ClassNotFoundException if the Package is invalid 
  */ 
 public static Class[] getClasses(String pckgname) throws ClassNotFoundException { 
   ArrayList classes=new ArrayList(); 
   // Get a File object for the package 
   File directory=null; 
   try { 
	   //String packagePath = '/'+pckgname.replace('.', '/');
	   URL packageURL = Thread.currentThread().getContextClassLoader().getResource(
		pckgname.replace('.', '/'));
	   directory = new File(URLDecoder.decode(packageURL.getFile()));


     //directory=new File(r.getFile()); 
   } catch(NullPointerException x) { 
     throw new ClassNotFoundException(pckgname+" does not appear to be a valid package"); 
   } 
   if(directory.exists()) { 
	  //System.out.println(directory.getName()) ; //which directory
     // Get the list of the files contained in the package 
     String[] files=directory.list(); 
     for(int i=0; i<files.length; i++) { 
       // we are only interested in .class files 
       if(files[i].endsWith(".class")) { 
         // removes the .class extension 
         classes.add(Class.forName(pckgname+'.'+files[i].substring(0, files[i].length()-6))); 
       } 
     } 
   } else { 
     throw new ClassNotFoundException(pckgname+" does not appear to be a valid package"); 
   } 
   Class[] classesA=new Class[classes.size()]; 
   classes.toArray(classesA); 
   return classesA; 
 } 
 static void init ()
	{
		Class c = null;
		try {
			c = Class.forName("net.sf.regadb.db.PatientImpl");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		makexmlschema(c, rootE1);
		
			
	}
	public static void main(String[] args) 
	{		
		TestHierachy test=new TestHierachy();
		test.init();
		printxmlschema();
	}

}
