package org.apache.catalina.loader;

import java.io.File;
import java.util.List;

public interface ClasspathParser {
	public List<File> getClasspaths(File webAppDir);
}
