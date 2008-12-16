package net.sf.regadb.csv;

class AttributeValue implements Comparable<AttributeValue> {
	String name;
	int count;
	int tie;

	AttributeValue(String name, int count, int tie) {
		this.name = name;
		this.count = count;
		this.tie = tie;
	}

	public int compareTo(AttributeValue other) {
		return (count - other.count) * 1000	+ (tie - other.tie);
	}
}