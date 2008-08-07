package cpp2java.cli;

import java.io.File;

import cpp2java.scripts.pretty.CheckAll;
import cpp2java.scripts.pretty.CreateAllScripts;

import ovid.preproc.PreprocCpp;

public class Cpp2Java {
	public static void main(String [] args) {
		PreprocCpp preproc = new PreprocCpp();
		
		if(args.length==0) { 
			System.err.println("Usage:");
			System.err.println("preproc [C++ file/header]");
			System.err.println("preproc-wt-src [wt-src-dir]");
			System.err.println("generate-wt-pretty-batch-scripts [wt-src-dir] [script-dir] [ccparse command]");
			System.err.println("check-wt-src [wt-src-dir] [report-dir]");
		} else {
			if(args[0].equals("preproc")) {
				preproc.performChangesOnFile(new File(args[1]));
			} else if(args[0].equals("preproc-wt-src")) {
				preproc.performChangesOnWitty(args[1]);
			} else if(args[0].equals("check-wt-src")) {
				CheckAll ca = new CheckAll();
				ca.run(args[1], args[2]);
			} else if(args[0].equals("generate-wt-pretty-batch-scripts")) {
				CreateAllScripts cas = new CreateAllScripts();
				cas.run(args[1], args[2], args[3]);
			} else {
				System.err.println("no such argument");
			}
		}
	}
}
