package net.sf.regadb.ui.form.importTool.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class ImportDefinition {
	private String description;
	private File xmlFile;
	private List<Rule> rules = new ArrayList<Rule>();
	private ScriptDefinition script;

	public File getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}
	
	public ScriptDefinition getScript() {
		return script;
	}

	public void setScript(ScriptDefinition script) {
		this.script = script;
	}
	
	public static ImportDefinition getImportDefinition(File file){
		XStream xstream = new XStream();
		
		ImportDefinition id = null;
		try {
			id = (ImportDefinition)xstream.fromXML(new FileReader(file));
			id.setXmlFile(file);
			id.setDescription(id.getXmlFile().getName().substring(0, id.getXmlFile().getName().indexOf(".")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return id;
	}
}
