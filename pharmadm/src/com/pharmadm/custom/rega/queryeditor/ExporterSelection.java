package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;

public class ExporterSelection extends OutputSelection implements Serializable{
	private FieldExporter exporter;

	public ExporterSelection(OutputVariable ovar) {
		super(ovar);
	}

	public ExporterSelection(OutputVariable ovar, boolean selected) {
		this(ovar);
		setSelected(selected);
	}

	@Override
	public Object getObject(Object objectSpec) {
		return super.getObjectSpec();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	public void setExporter(FieldExporter exporter) {
		this.exporter = exporter;
	}

	public FieldExporter getExporter() {
		return exporter;
	}

}
