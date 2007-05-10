package net.sf.regadb.build.junit;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import junit.framework.TestResult;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class JUnitRapport {
	private static String file = "testresult.xml";
	
	private static Document doc;
	
	private static Element rootElement;
	private static Element currentSuite;
	private static Element currentTest;
	
	public static String getFile() {
		return file;
	}
	
	public static void startTesting() {
		doc = new Document();
		
		rootElement = new Element("test");
		rootElement.setName("test");
		
		rootElement.setAttribute("suites", "" + 0);
		rootElement.setAttribute("tests", "" + 0);
		rootElement.setAttribute("runs", "" + 0);
		rootElement.setAttribute("errors", "" + 0);
		rootElement.setAttribute("failures", "" + 0);
		
		doc.addContent(rootElement);
	}
	
	public static void endTesting() {
		List children = rootElement.getChildren();
		
		rootElement.setAttribute("suites", children.size() + "");
		
		for (Object o : children) {
			rootElement.setAttribute("tests" , "" + (Integer.parseInt(rootElement.getAttributeValue("tests")) + Integer.parseInt(((Element)o).getAttributeValue("tests"))));
			rootElement.setAttribute("runs" , "" + (Integer.parseInt(rootElement.getAttributeValue("runs")) + Integer.parseInt(((Element)o).getAttributeValue("runs"))));
			rootElement.setAttribute("errors" , "" + (Integer.parseInt(rootElement.getAttributeValue("errors")) + Integer.parseInt(((Element)o).getAttributeValue("errors"))));
			rootElement.setAttribute("failures" , "" + (Integer.parseInt(rootElement.getAttributeValue("failures")) + Integer.parseInt(((Element)o).getAttributeValue("failures"))));
		}
		
		try {
		    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		    outputter.outputString(doc);
		    
		    FileWriter writer = new FileWriter(file);
			outputter.output(doc, writer);
			writer.close();
		}
		catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	public static void addTestSuite(String name) {
		currentSuite = new Element("testsuite");
		currentSuite.setName("testsuite");
		
		currentSuite.setAttribute("name", name);
		currentSuite.setAttribute("tests", "" + 0);
		currentSuite.setAttribute("runs", "" + 0);
		currentSuite.setAttribute("errors", "" + 0);
		currentSuite.setAttribute("failures", "" + 0);
		
		rootElement.addContent(currentSuite);
	}
	
	public static void closeTestSuite() {
		List children = currentSuite.getChildren();
		
		currentSuite.setAttribute("tests", children.size() + "");
		
		for (Object o : children) {
			currentSuite.setAttribute("runs", "" + (Integer.parseInt(currentSuite.getAttributeValue("runs")) + Integer.parseInt(((Element)o).getAttributeValue("runs"))));
			currentSuite.setAttribute("errors", "" + (Integer.parseInt(currentSuite.getAttributeValue("errors")) + Integer.parseInt(((Element)o).getAttributeValue("errors"))));
			currentSuite.setAttribute("failures", "" + (Integer.parseInt(currentSuite.getAttributeValue("failures")) + Integer.parseInt(((Element)o).getAttributeValue("failures"))));
		}
		
		if (Integer.parseInt(currentSuite.getAttributeValue("tests")) == 0) {
			rootElement.removeContent(currentSuite);
		}
	}
	
	public static void addTest() {
		currentTest = new Element("testcase");
		currentTest.setName("testcase");
		
		currentTest.setAttribute("runs", "" + 0);
		currentTest.setAttribute("errors", "" + 0);
		currentTest.setAttribute("failures", "" + 0);
	}
	
	public static void addRun(TestResult result) {
		currentTest.setAttribute("runs", "" + (Integer.parseInt(currentTest.getAttributeValue("runs")) + result.runCount()));
		currentTest.setAttribute("errors", "" + (Integer.parseInt(currentTest.getAttributeValue("errors")) + result.errorCount()));
		currentTest.setAttribute("failures", "" + (Integer.parseInt(currentTest.getAttributeValue("failures")) + result.failureCount()));
		
		Enumeration fail = result.failures();
		
		while (fail.hasMoreElements()) {
			String f = fail.nextElement().toString();
			String failureClass = f.substring(f.indexOf("(") + 1, f.indexOf(")"));
			String failurePackage = failureClass.substring(0, failureClass.lastIndexOf("."));
			String failureClassName = failureClass.substring(failureClass.lastIndexOf(".") + 1);
			String failureMethod = f.substring(0, f.indexOf("("));
			String failure = f.substring(f.indexOf(":") + 2);
			
			Element currentFailure = new Element("failure");
			currentFailure.setName("failure");
			
			currentFailure.setAttribute("package", failurePackage);
			currentFailure.setAttribute("class", failureClassName);
			currentFailure.setAttribute("method", failureMethod);
			currentFailure.setAttribute("failure", failure);
			
			currentTest.addContent(currentFailure);
		}
		
		Enumeration err = result.errors();
		
		while (err.hasMoreElements()) {
			String e = err.nextElement().toString();
			String errorClass = e.substring(e.indexOf("(") + 1, e.indexOf(")"));
			String errorPackage = errorClass.substring(0, errorClass.lastIndexOf("."));
			String errorClassName = errorClass.substring(errorClass.lastIndexOf(".") + 1);
			String errorMethod = e.substring(0, e.indexOf("("));
			String error = e.substring(e.indexOf(":") + 2);
			
			Element currentError = new Element("error");
			currentError.setName("error");
			
			currentError.setAttribute("package", errorPackage);
			currentError.setAttribute("class", errorClassName);
			currentError.setAttribute("method", errorMethod);
			currentError.setAttribute("error", error);
			
			currentTest.addContent(currentError);
		}
		
		currentSuite.addContent(currentTest);
	}
}
