package net.sf.regadb.eximp;

//package net.sf.regadb.eximp;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Patient;

import org.jdom.Element;
import org.jdom.Namespace;


public class TestHierachy {
	private static Class[] regaClasses_;
	 
	 
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
	
	public static void main(String[] args) 
	{		
		Class c= Patient.class;
		List classeslisted=new ArrayList();
		classeslisted.add("Patient");
		printClassInfo(c);
		
	}

 static void printClassInfo(Class c)
  {
	 Namespace relaxng = Namespace.getNamespace("http://relaxng.org/ns/structure/1.0");
	 Element patientEl = new Element("Patient");
	 patientEl.setNamespace(relaxng);
	 Element nameEl = new Element("name");
	 nameEl.addContent(c.getName());
	 patientEl.addContent(nameEl);
	
	 
	 	 //System.out.println("Current class :"+ c.getName());
	 Field [] pfields =c.getDeclaredFields();
		for (int m=0; m<pfields.length;m++){
			String currentfieldname =pfields[m].getName();
			//System.out.println("Field Name: " + currentfieldname);
			 //XmlSchemaStr=XmlSchemaStr +"<attribute name=\"" + currentfieldname +"\">\n";
			patientEl.setAttribute("name", currentfieldname);
			
			if(pfields[m].getType() == Set.class)
			{
				//Class<Set> s = (Class<Set>)pfields[m].getType();
				String setInfo = pfields[m].toGenericString();
				int startfrom=setInfo.indexOf('<')+1;
				int endat=setInfo.indexOf('>');
				String classstr=setInfo.substring( startfrom,endat);
				try 
				{
					if (isRegaClass(Class.forName(classstr)))
							{
						//Check if class is already printed by checking an array
						//System.out.println(classstr);
						//printClassInfo(Class.forName(classstr));
							}
				} 
				catch (ClassNotFoundException e) 
				{
					e.printStackTrace();
				}
				//System.out.println (classstr.substring(classstr.lastIndexOf('.')+1));
			}
			}	 
		 //XmlSchemaStr=XmlSchemaStr+"</element>\n</element>";
		//System.out.println (XmlSchemaStr);
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

}
