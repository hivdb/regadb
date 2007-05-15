package net.sf.regadb.util.hbm;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.util.reflection.PackageUtils;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class InterpreteHbm
{
	private static InterpreteHbm instance_ = null;
	HashMap<String, Element> classHbms_ = new HashMap<String, Element>();
    HashMap<String, File> hbmsFiles_ = new HashMap<String, File>();
    HashMap<String, Document> xmlDocs_ = new HashMap<String, Document>();
    File [] xmlFiles_;
	
	//property attributes
	private boolean checkProperty(String parent, String property, String elName)
	{
		Element el = findProperty(parent, property);
		if(el == null)
		{
			System.err.println("Property doesn't exist");
			return false;
		}
		return el.getName().equals(elName);
	}
	
	public boolean isComposite(String parent, String property)
	{
		return checkProperty(parent, property, "composite-id");
	}
    
    public boolean isManyToOne(String parent, String property)
    {
        return checkProperty(parent, property, "many-to-one") || checkProperty(parent, property, "key-many-to-one");
    }
	
	public boolean isCompositeIndex(String parent, String compositeName, String property)
	{
		boolean composite = isComposite(parent, compositeName);
		String propertyLC = property.toLowerCase();
		boolean compositeIndex = propertyLC.charAt(propertyLC.length()-1)=='i' && propertyLC.charAt(propertyLC.length()-2)=='i';
		
		return composite && compositeIndex;
	}
	
	public boolean isVersion(String parent, String property)
	{
		return checkProperty(parent, property, "version");
	}
	
	public boolean isId(String parent, String property)
	{
		return checkProperty(parent, property, "id");
	}
	
	private String getPropertyAttribute(String parent, String property, String attrName)
	{
		Element el = findProperty(parent, property);
		if(el == null)
		{
			System.err.println("Property doesn't exist: " + parent + " " +property);
			return null;
		}
		
		Element col = el.getChild("column");
		if (col==null)
			return null;
		
		Attribute notNull = col.getAttribute(attrName);
		
		if(notNull!=null)
		return notNull.getValue();
		else
		return null;
	}
	
	public boolean isNotNull(String parent, String property)
	{
		String notNull = getPropertyAttribute(parent, property, "not-null");
		
		return notNull!=null && notNull.equals("true");
	}
	
	public Integer getLength(String parent, String property)
	{
		String length = getPropertyAttribute(parent, property, "length");
		
		if(length!=null)
		{
			return new Integer(length);
		}
		else
		{
			return null;
		}
	}
	
	public Double getPrecision(String parent, String property)
	{
		String length = getPropertyAttribute(parent, property, "precision");
		
		if(length!=null)
		{
			return new Double(length);
		}
		else
		{
			return null;
		}
	}
	
	public Double getScale(String parent, String property)
	{
		String length = getPropertyAttribute(parent, property, "scale");
		
		if(length!=null)
		{
			return new Double(length);
		}
		else
		{
			return null;
		}
	}
	
	private Element findProperty(String parent, String property)
	{
		Element classEl = classHbms_.get(parent);
		
		if(classEl!=null)
		{
			return findElementRecursively(classEl, property);
		}
		
		return null;
	}private
	
	Element findElementRecursively(Element root, String property)
	{
		for(Object oel : root.getChildren())
		{
			Element el = (Element)oel;
			if(el.getAttribute("name")!=null && el.getAttribute("name").getValue()!=null && el.getAttribute("name").getValue().equals(property))
			{
				return el;
			}
			else
			{
				Element toReturn = findElementRecursively(el, property);
				if(toReturn!=null)
				{
					return toReturn;
				}
			}
		}
		return null;
	}
	//property attributes
	
	private InterpreteHbm()
	{
		init();
	}
	
	public static InterpreteHbm getInstance()
	{
		if(instance_==null)
			instance_ = new InterpreteHbm();
		
		return instance_;
	}
	

    
	private void init()
	{
        File directory = new File(PackageUtils.getDirectoryPath("net.sf.regadb.db", "regadb-persist"));
  
        xmlFiles_ = directory.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				if(pathname.getAbsolutePath().indexOf(".xml")!=-1)
				{
					return true;
				}
				else
				{
					return false;	
				}
			}
		});
		

		
		SAXBuilder builder = new SAXBuilder();
		builder.setEntityResolver(new EntityResolver()
		{
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
			{
				File directory = new File(PackageUtils.getDirectoryPath("net.sf.regadb.db.hibernate.dtd", "regadb-persist"));
				File [] dtdFiles = directory.listFiles(new FileFilter()
				{
					public boolean accept(File pathname)
					{
						return pathname.getAbsolutePath().indexOf("hibernate-mapping-3.0.dtd")!=-1;
					}
				});
				
				FileReader fileReader = new FileReader(dtdFiles[0]);
				//return new InputSource(fileReader);
                return new InputSource(new StringBufferInputStream(""));
			}
		});
		
		for(File xmlFile : xmlFiles_)
		{
            Document doc = getDocFromXmlFile(xmlFile, builder);
			Element rootEl = doc.getRootElement();
			Element classEl = rootEl.getChild("class");
			String className = classEl.getAttribute("name").getValue();
			classHbms_.put(className, classEl);
            hbmsFiles_.put(className, xmlFile);
            xmlDocs_.put(className, doc);
		}
	}
	
	public Document getDocFromXmlFile(File xmlFile, SAXBuilder builder)
	{
		Document doc = null;
        
		try
		{
			doc = builder.build(xmlFile);
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}

		return doc;
	}
    
    public String[] getClassNames() 
    {
        String [] names = new String [classHbms_.entrySet().size()];
        int i = 0;
        
        for(Map.Entry<String, Element> className: classHbms_.entrySet())
        {
            names[i] = className.getKey();
            i++;
        }
        
        return names;
    }
    
	public static void main(String [] args)
	{
		InterpreteHbm interpreter = InterpreteHbm.getInstance();
		System.err.println("net.sf.regadb.db.PatientImpl "+"lastName " + "isComposite " +interpreter.isComposite("net.sf.regadb.db.PatientImpl", "lastName"));
		System.err.println("net.sf.regadb.db.PatientAttributeValue "+"id " + "isComposite " +interpreter.isComposite("net.sf.regadb.db.PatientAttributeValue", "id"));
		System.err.println("");
		
		System.err.println("net.sf.regadb.db.AaMutation "+"id position" + "isCompositeIndex " +interpreter.isCompositeIndex("net.sf.regadb.db.AaMutation", "id", "position"));
		System.err.println("net.sf.regadb.db.AaMutation "+"id aaSequenceIi" + "isCompositeIndex " +interpreter.isCompositeIndex("net.sf.regadb.db.AaMutation", "id", "aaSequenceIi"));
		System.err.println("");
		
		System.err.println("net.sf.regadb.db.PatientImpl "+"patientIi " + "isId " +interpreter.isId("net.sf.regadb.db.PatientImpl", "patientIi"));
		System.err.println("net.sf.regadb.db.PatientImpl "+"patientId " + "isId " +interpreter.isId("net.sf.regadb.db.PatientImpl", "patientId"));
		System.err.println("");
		
		System.err.println("net.sf.regadb.db.PatientImpl "+"patientIi " + "isVersion " +interpreter.isVersion("net.sf.regadb.db.PatientImpl", "patientIi"));
		System.err.println("net.sf.regadb.db.PatientImpl "+"version " + "isVersion " +interpreter.isVersion("net.sf.regadb.db.PatientImpl", "version"));
		System.err.println("");
		
		System.err.println("net.sf.regadb.db.PatientImpl "+"patientId " + "isNotNull " +interpreter.isNotNull ("net.sf.regadb.db.PatientImpl", "patientId"));
		System.err.println("net.sf.regadb.db.PatientImpl "+"version " + "isNotNull " +interpreter.isNotNull ("net.sf.regadb.db.PatientImpl", "version"));
		System.err.println("");
		
		System.err.println("net.sf.regadb.db.DrugClass "+"className " + "getLength " +interpreter.getLength ("net.sf.regadb.db.DrugClass", "className"));
		System.err.println("net.sf.regadb.db.DrugClass "+"drugGenerics " + "getLength " +interpreter.getLength ("net.sf.regadb.db.DrugClass", "drugGenerics"));
		System.err.println("");
		
		System.err.println("net.sf.regadb.db.TherapyGeneric "+"dayDosageMg " + "getPrecision " +interpreter.getPrecision ("net.sf.regadb.db.TherapyGeneric", "dayDosageMg"));
		System.err.println("net.sf.regadb.db.TherapyGeneric "+"therapy " + "getPrecision " +interpreter.getPrecision ("net.sf.regadb.db.TherapyGeneric", "therapy"));
		System.err.println("");
		
		System.err.println("net.sf.regadb.db.TherapyGeneric "+"dayDosageMg " + "getScale " +interpreter.getScale ("net.sf.regadb.db.TherapyGeneric", "dayDosageMg"));
		System.err.println("net.sf.regadb.db.TherapyGeneric "+"therapy " + "getScale " +interpreter.getScale ("net.sf.regadb.db.TherapyGeneric", "therapy"));
		System.err.println("");
	}
}
