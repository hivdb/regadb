package net.sf.regadb.ui.form.importTool.data;

public class Rule {
	public enum Type {
		AttributeValue("Attribute value"),
		TestDate("Test date"),
		TestValue("Test value"),
		EventStartDate("Event start date"),
		EventEndDate("Event end date"),
		EventValue("Event value"),
		TherapyRegimen("Therapy regimen"),
		TherapyStartDate("Therapy start date"),
		TherapyEndDate("Therapy end date"),
		TherapyMotivation("Therapy motivation"),
		TherapyComment("Therapy comment");
		
		private String name;
		
		private Type(String name) {
			this.name = name;
		}
		
		private Type(String name, Details details) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
	private String column;
	private Type type;
	private String typeName;
	private int number;
	private Details details;
	
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
	
	public Details getDetails() {
		return details;
	}
	
	public void setDetails(Details details) {
		this.details = details;
	}
}