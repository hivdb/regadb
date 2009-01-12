package net.sf.regadb.io.exportXML;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public abstract class ExportToXMLOutputStream<T> {
	
	private PrintStream out;
	private ExportToXML xml = new ExportToXML();

	private XMLOutputter outputter;
	
	private String rootElementName;
	
	public ExportToXMLOutputStream(OutputStream out, String rootElementName){
		this.out = new PrintStream(out);
		xml = new ExportToXML();
		outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		
		this.rootElementName = rootElementName;
	}
	
	public void start(){
		this.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		this.out.println('<'+ getRootElementName() +'>');		
	}
	
	public String getRootElementName(){
	    return rootElementName;
	}
	
	public void setRootElementName(String rootElementName){
	    this.rootElementName = rootElementName;
	}
	
	public void stop(){
		this.out.println("</"+ getRootElementName() +">");
	}
	
	public abstract void exportPatient(Patient p);
	public abstract Element toXML(T t);
	
	protected ExportToXML getExportToXml(){
	    return xml;
	}
	
	public void write(T t) throws IOException{
		outputter.output(toXML(t), out);
	}
	
	public void write(Collection<T> ts) throws IOException{
		for(T t : ts)
			write(t);
	}
	
	public static class PatientXMLOutputStream extends ExportToXMLOutputStream<Patient>{
	    public PatientXMLOutputStream(OutputStream out) {
            super(out, "patients");
        }

        @Override
	    public Element toXML(Patient p){
	        Element el = new Element("patients-el");
	        getExportToXml().writePatient(p, el);
	        return el;
	    }

        @Override
        public void exportPatient(Patient p) {
            try {
                write(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	public static class ViralIsolateXMLOutputStream extends ExportToXMLOutputStream<ViralIsolate>{

        public ViralIsolateXMLOutputStream(OutputStream out) {
            super(out, "viralIsolates");
        }

        @Override
        public Element toXML(ViralIsolate t) {
            Element el = new Element("viralIsolates-el");
            getExportToXml().writeViralIsolate(t, el);
            return el;
        }

        @Override
        public void exportPatient(Patient p) {
            try {
                write(p.getViralIsolates());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	    
	}
}
