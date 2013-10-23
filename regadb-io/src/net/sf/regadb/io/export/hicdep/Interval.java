package net.sf.regadb.io.export.hicdep;

 class Interval {
	Double start;
	boolean startIncluded;
	Double end;
	boolean endIncluded;
	
	boolean in(double v) {
		if (startIncluded && start == v)
			return true;
		else if (endIncluded && end == v)
			return true;
		else 
			return v > start && v < end;
	}
	
	public static Interval parse(String s) {
		s = s.replaceAll(" ", "");
		Interval i = new Interval();
		i.startIncluded = s.charAt(0) == '[';
		i.endIncluded = s.charAt(s.length() - 1) == ']';
		String [] values = s.substring(1, s.length() - 1).split(",");
		i.start = Double.parseDouble(values[0]);
		i.end = Double.parseDouble(values[1]);
		return i;
	}
}
