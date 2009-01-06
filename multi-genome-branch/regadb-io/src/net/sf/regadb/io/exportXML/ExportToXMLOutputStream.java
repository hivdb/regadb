package net.sf.regadb.io.exportXML;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;

import net.sf.regadb.db.Patient;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class ExportToXMLOutputStream {
	
	private PrintStream out;
	private ExportToXML xml = new ExportToXML();

	private XMLOutputter outputter;
	
	public ExportToXMLOutputStream(OutputStream out){
		this.out = new PrintStream(out);
		xml = new ExportToXML();
		outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
	}
	
	public void start(){
		this.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		this.out.println("<patients>");		
	}
	
	public void stop(){
		this.out.println("</patients>");
	}
	
	public void write(Patient p) throws IOException{
		Element el = new Element("patients-el");
		xml.writePatient(p, el);
		outputter.output(el, out);
	}
	
	public void write(Collection<Patient> ps) throws IOException{
		for(Patient p : ps)
			write(p);
	}
}
