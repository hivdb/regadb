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
	private String wsddInputFile;
	private String wsddOutputFile;
	private String serviceName;
	private String servicePackage;

	public void execute()
	{
		SAXBuilder builder;
		Document doc = null;
		Element rootElement;
		
		builder = new SAXBuilder();
		
		try 
		{
			doc = builder.build(new File(wsddInputFile));
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
		
		Element service = new Element(serviceName);
		service.setName("service");
		service.setNamespace(rootElement.getNamespace());
		service.setAttribute("name", serviceName);
		service.setAttribute("provider", "java:RPC");
		rootElement.addContent(service);
		
		Element allowedMethods = new Element("allowedMethods");
		allowedMethods.setName("parameter");
		allowedMethods.setNamespace(rootElement.getNamespace());
		allowedMethods.setAttribute("name", "allowedMethods");
		allowedMethods.setAttribute("value", "*");
		service.addContent(allowedMethods);
		
		Element typeMappingVersion = new Element("typeMappingVersion");
		typeMappingVersion.setName("parameter");
		typeMappingVersion.setNamespace(rootElement.getNamespace());
		typeMappingVersion.setAttribute("name", "typeMappingVersion");
		typeMappingVersion.setAttribute("value", "1.2");
		service.addContent(typeMappingVersion);
		
		Element wsdlPortType = new Element("wsdlPortType");
		wsdlPortType.setName("parameter");
		wsdlPortType.setNamespace(rootElement.getNamespace());
		wsdlPortType.setAttribute("name", "wsdlPortType");
		wsdlPortType.setAttribute("value", serviceName);
		service.addContent(wsdlPortType);
		
		Element scope = new Element("scope");
		scope.setName("parameter");
		scope.setNamespace(rootElement.getNamespace());
		scope.setAttribute("name", "scope");
		scope.setAttribute("value", "Session");
		service.addContent(scope);
		
		Element className = new Element("className");
		className.setName("parameter");
		className.setNamespace(rootElement.getNamespace());
		className.setAttribute("name", "className");
		className.setAttribute("value", servicePackage + "." + serviceName + "SoapBindingSkeleton");
		service.addContent(className);
		
		Element wsdlServicePort = new Element("wsdlServicePort");
		wsdlServicePort.setName("parameter");
		wsdlServicePort.setNamespace(rootElement.getNamespace());
		wsdlServicePort.setAttribute("name", "wsdlServicePort");
		wsdlServicePort.setAttribute("value", serviceName);
		service.addContent(wsdlServicePort);
		
		Element wsdlTargetNamespace = new Element("wsdlTargetNamespace");
		wsdlTargetNamespace.setName("parameter");
		wsdlTargetNamespace.setNamespace(rootElement.getNamespace());
		wsdlTargetNamespace.setAttribute("name", "wsdlTargetNamespace");
		wsdlTargetNamespace.setAttribute("value", "urn:" + serviceName);
		service.addContent(wsdlTargetNamespace);
		
		Element wsdlServiceElement = new Element("wsdlServiceElement");
		wsdlServiceElement.setName("parameter");
		wsdlServiceElement.setNamespace(rootElement.getNamespace());
		wsdlServiceElement.setAttribute("name", "wsdlServiceElement");
		wsdlServiceElement.setAttribute("value", serviceName + "Service");
		service.addContent(wsdlServiceElement);
		
		try {
		    XMLOutputter outputter = new XMLOutputter();
		    outputter.outputString(doc);
		    
		    FileWriter writer = new FileWriter(wsddOutputFile);
			outputter.output(doc, writer);
			writer.close();
		} catch (java.io.IOException e) {
		    e.printStackTrace();
		}
	}
	
	public String getWsddInputFile() {
		return wsddInputFile;
	}
	
	public void setWsddInputFile(String wsddInputFile) {
		this.wsddInputFile = wsddInputFile;
	}
	
	public String getWsddOutputFile() {
		return wsddOutputFile;
	}
	
	public void setWsddOutputFile(String wsddOutputFile) {
		this.wsddOutputFile = wsddOutputFile;
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getServicePackage() {
		return servicePackage;
	}

	public void setServicePackage(String servicePackage) {
		this.servicePackage = servicePackage;
	}
}
