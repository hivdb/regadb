package net.sf.hivgensim;

import java.io.File;

import net.sf.regadb.build.builder.Jarbuilder;


public class Main {

	private final static String workDir = "/home/gbehey0/regadb";

	public static void main(String[] args){
		System.out.println("Clearing working directory");
		clearDirectory(new File(workDir));
		Jarbuilder.run(
				workDir + File.separator + "build",
				workDir + File.separator + "report",
				"/home/gbehey0/workspace",false);


	}

	public static void clearDirectory(File path) {
		if(path.exists()) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}		
	}

	public static void deleteDirectory(File path){
		clearDirectory(path);
		path.delete();
	}


}
