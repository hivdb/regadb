package net.sf.regadb.install.wizard;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CustomFilter extends FileFilter {
	private String ext_;
	
	public CustomFilter(String ext) {
		ext_ = ext;
	}
	
	public boolean accept(File file) {
		String nm = file.getAbsolutePath();
		int idx = nm.lastIndexOf('.');
		return ( file.isDirectory() || ( idx != -1 && nm.substring(idx).equals("." + ext_) ) );
	}
	
	@Override
	public String getDescription() {
		return "*." + ext_;
	}
}
