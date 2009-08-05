package net.sf.regadb.tools;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MutationStringComparator implements Comparator<String> {

	/*
	 *  REGION + POSITION + AA|*
	 */
	 public int compare(String o1, String o2) {
		 Pattern p = Pattern.compile("([A-Z]+)([0-9]+)([A-Z*])");
		 Matcher m1 = p.matcher(o1);
		 Matcher m2 = p.matcher(o2);
		 if(m1.matches() && m2.matches()){
			 String region1 = m1.group(1);
			 String region2 = m2.group(1);

			 String pos1 = m1.group(2);
			 String pos2 = m2.group(2);

			 String aa1 = m1.group(3);
			 String aa2 = m2.group(3);

			 if(!region1.equals(region2)){
				 return region1.compareTo(region2);
			 }
			 if(!pos1.equals(pos2)){
				 return ((Integer) Integer.parseInt(pos1)).compareTo((Integer) Integer.parseInt(pos2));
			 }
			 return aa1.compareTo(aa2);
		 }
		 return o1.compareTo(o2);
	 }
}
