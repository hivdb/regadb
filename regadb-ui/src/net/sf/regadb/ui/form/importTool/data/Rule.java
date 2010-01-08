package net.sf.regadb.ui.form.importTool.data;

import net.sf.regadb.db.Test;

public class Rule {
	public enum Type {
		PatientId("Patient id"),
		AttributeValue("Attribute value"),
		TestDate("Test date", true),
		TestValue("Test value"),
		EventStartDate("Event start date", true),
		EventEndDate("Event end date", true),
		EventValue("Event value"),
		TherapyRegimen("Therapy regimen"),
		TherapyStartDate("Therapy start date", true),
		TherapyEndDate("Therapy end date", true),
		TherapyStopMotivation("Therapy stop motivation"),
		TherapyComment("Therapy comment"),
		ViralIsolateSampleId("Viral isolate sample id"),
		ViralIsolateSampleDate("Viral isolate sample date", true),
		ViralIsolateSampleSequence1("Viral isolate sequence"),
		ViralIsolateSampleManualSubtype("Viral isolate manual subtype");
		
		private String name;
		private boolean date;
		
		private Type(String name, boolean date) {
			this.name = name;
			this.date = date;
		}
		
		private Type(String name) {
			this(name, false);
		}
		
		public String getName() {
			return name;
		}
		
		public boolean isDate() {
			return date;
		}
	}
	
	public Rule() {
		number = 1;
	}
	
	private String column;
	private Type type;
	private String typeName;
	private int number;
	private MappingDetails mappingDetails;
	private RegimenDetails regimenDetails;
	private DateDetails dateDetails;
	private SequenceDetails sequenceDetails;

	public String getColumn() {
		return column;
	}
	
	public void setColumn(String column) {
		this.column = column;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public MappingDetails getMappingDetails() {
		return mappingDetails;
	}

	public void setMappingDetails(MappingDetails mappingDetails) {
		this.mappingDetails = mappingDetails;
	}
	
	public RegimenDetails getRegimenDetails() {
		return regimenDetails;
	}

	public void setRegimenDetails(RegimenDetails regimenDetails) {
		this.regimenDetails = regimenDetails;
	}
	
	public DateDetails getDateDetails() {
		return dateDetails;
	}

	public void setDateDetails(DateDetails dateDetails) {
		this.dateDetails = dateDetails;
	}
	
	public SequenceDetails getSequenceDetails() {
		return sequenceDetails;
	}

	public void setSequenceDetails(SequenceDetails sequenceDetails) {
		this.sequenceDetails = sequenceDetails;
	}
	
	public static String getTestName(Test t) {
		String testName = t.getDescription();
		if (t.getTestType().getGenome() != null)
			testName += "(" + t.getTestType().getGenome().getOrganismName() + ")";
		return testName;
	}
}