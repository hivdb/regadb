package net.sf.regadb.build.error;

import java.io.FileWriter;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class ErrorRapport {
	private static Document doc;
	
	private static Element rootElement;
	private static Element currentError;
	
	public static void handleError(String fileName, String projectName, Exception e) {
		doc = new Document();
		
		rootElement = new Element("test");
		rootElement.setName("test");
		
		doc.addContent(rootElement);
		
		currentError = new Element("error");
		currentError.setName("error");
		
		currentError.setAttribute("projectname", projectName);
		currentError.setAttribute("exception", e.getMessage());
		
		rootElement.addContent(currentError);
		
		try {
		    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		    outputter.outputString(doc);
		    
		    FileWriter writer = new FileWriter(fileName);
			outputter.output(doc, writer);
			writer.close();
		}
		catch (IOException ioe) {
		    ioe.printStackTrace();
		}
	}
}
