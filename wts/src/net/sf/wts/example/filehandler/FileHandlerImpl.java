package net.sf.wts.example.filehandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class FileHandlerImpl {
	private final String file = "service.xml";
	
	public byte[] handleFile(byte[] in) {
		acceptFile(in);
		
		SAXBuilder builder = new SAXBuilder();
		
		Document doc = new Document();
		
		try 
		{
			doc = builder.build(new File(file));
		} 
		catch (JDOMException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
		Element rootElement = doc.getRootElement();
		
		Element test = new Element("test");
		test.setName("service");
		test.setNamespace(rootElement.getNamespace());
		test.setAttribute("name", "test");
		rootElement.addContent(test);
		
		try {
		    XMLOutputter outputter = new XMLOutputter();
		    outputter.outputString(doc);
		    
		    FileWriter writer = new FileWriter(file);
			outputter.output(doc, writer);
			writer.close();
		}
		catch (IOException e) {
		    e.printStackTrace();
		}
		
		return returnFile();
	}
	
	private void acceptFile(byte[] in) {
		try {
			FileOutputStream fout = new FileOutputStream(file);
			fout.write(in);
			fout.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private byte[] returnFile() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		try {
			FileInputStream fin = new FileInputStream(file);
			copy(fin, bout);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] out = bout.toByteArray();
		
		try {
			bout.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return out;
	}
	
	private void copy(InputStream in, OutputStream out) throws IOException {
		synchronized (in) {
			synchronized (out) {
				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					
					if (bytesRead == -1) {
						break;
					}
					
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	}
}
