package net.sf.wts.build.ant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.tools.ant.Task;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class AddServiceToWSDD extends Task
{
	private String wsddFileName;
	private String serviceName;
	
	public void execute()
	{
		SAXBuilder builder;
		Document doc = null;
		Element rootElement;
		
		builder = new SAXBuilder();
		
		try 
		{
			doc = builder.build(new File(wsddFileName));
		} 
		catch (JDOMException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
		rootElement = doc.getRootElement();
		
		Element service = new Element("FileHandler");
		service.setName("service");
		service.setNamespace(rootElement.getNamespace());
		service.setAttribute("name", "FileHandler");
		service.setAttribute("provider", "java:RPC");
		rootElement.addContent(service);
		
		try {
		    XMLOutputter outputter = new XMLOutputter();
		    outputter.output(doc, System.out);
		    
		    FileWriter writer = new FileWriter("../wts/wts/WEB-INF/server-config2.wsdd");
			outputter.output(doc, writer);
			writer.close();
		} catch (java.io.IOException e) {
		    e.printStackTrace();
		}
	}

	public String getWsddFileName() {
		return wsddFileName;
	}

	public void setWsddFileName(String wsddFileName) {
		this.wsddFileName = wsddFileName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public static void main(String [] args)
	{
		AddServiceToWSDD astwsdd = new AddServiceToWSDD();
		astwsdd.setWsddFileName("../wts/wts/WEB-INF/server-config.wsdd");
		
		astwsdd.execute();
	}
}
