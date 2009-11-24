package net.sf.regadb.ui.form.importTool.data;

import java.io.File;

public class ImportDefinition {
	private String description;
	private File xmlFile;

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
}
