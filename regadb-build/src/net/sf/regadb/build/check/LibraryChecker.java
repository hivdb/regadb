package net.sf.regadb.build.check;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class LibraryChecker {
	private static Set<File> doubleFiles;
	private static Set<String> doubleFileNames;
	
	private static List<File> jarFiles;
	
	public static void main(String[] args) {
		doubleFiles = new HashSet<File>();
		doubleFileNames = new HashSet<String>();
		
		jarFiles = new ArrayList<File>();
		
		Collection workspace = FileUtils.listFiles(new File("/home/bddeck0/Workspace/"), new String[] { "jar" }, true);
		
		for (Object o : workspace) {
			if (!((File)o).getAbsolutePath().contains("regadb-build"))
			{
				jarFiles.add((File)o);
			}
		}
		
		checkDoubles();
		
		print();
	}
	
	private static void checkDoubles() {
		for (File f : jarFiles) {
			String fileName = f.getName();
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
			
			String subFileName = fileName.substring(checkForwards(fileName), checkBackwards(fileName));
			
			for (File jf : jarFiles) {
				String checkName = jf.getName();
				checkName = checkName.substring(0, checkName.lastIndexOf('.'));
				
				String subCheckName = checkName.substring(checkForwards(checkName), checkBackwards(checkName));
				
				if (subFileName.equals(subCheckName) && jarFiles.indexOf(f) != jarFiles.indexOf(jf)) {
					doubleFiles.add(f);
					doubleFileNames.add(f.getName());
				}
			}
		}
	}
	
	private static void print() {
		for (File f : doubleFiles) {
			System.out.println(f.getAbsolutePath());
		}
		
		for (String s : doubleFileNames) {
			System.out.println(s);
		}
	}
	
	private static int checkForwards(String fileName) {
		int start = 0;
		
		for (int i = 0; i < fileName.length(); i++) {
			if (!Character.isDigit(fileName.charAt(i))) {
				start = i;
				
				break;
			}
		}
		
		return start;
	}
	
	private static int checkBackwards(String fileName) {
		int end = fileName.length();
		
		for (int i = fileName.length() -1 ; i >= 0; i--) {
			if (!(Character.isDigit(fileName.charAt(i)) || fileName.charAt(i) == '.' || fileName.charAt(i) == '-' || fileName.charAt(i) == '_')) {
				end = i + 1;
				
				break;
			}
		}
		
		return end;
	}
}
