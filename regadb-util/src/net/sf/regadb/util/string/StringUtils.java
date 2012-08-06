package net.sf.regadb.util.string;

public class StringUtils {

	public static String trimToLength(String original, int length){
		return trimToLength(original, length, "...");
	}
	
	public static String trimToLength(String original, int length, String suffix){
		original = original.trim();
		if(original.length() <= length)
			return original;
		else
			return original.substring(0,length - suffix.length()) + suffix;
	}
}
