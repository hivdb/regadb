package net.sf.regadb.build.junit;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import junit.framework.TestCase;
import junit.framework.TestResult;

import org.apache.commons.io.FileUtils;

public class JUnitTest {
	public static void executeTests(String libPool) {
		try {
			URLClassLoader classLoader = loadJars(libPool);
			
			for (URL u : classLoader.getURLs()) {
				JUnitRapport.addTestSuite(u.getFile().substring(u.getFile().lastIndexOf(File.separatorChar) + 1));
				
				JarFile jf = new JarFile(u.getFile());
				
				//System.out.println(u.getFile());
				
				Enumeration entries = jf.entries();
				
				while (entries.hasMoreElements()) {
					JarEntry je = (JarEntry)entries.nextElement();
					
					if (je.getName().contains(".class")) {
						String className = je.getName();
						
						className = className.substring(0,className.indexOf(".class")).replaceAll("" + File.separatorChar, ".");
						
						try {
							Class cls = classLoader.loadClass(className);
							
							if (cls.getSuperclass() != null) {				
								if (cls.getSuperclass().getName().equals("junit.framework.TestCase")) {
									JUnitRapport.startTest();
									
									Method[] method = cls.getDeclaredMethods();
									
									for (Method m : method) {
										if (m.getName().contains("test")) {
											TestCase tc = (TestCase)cls.newInstance();
											
											tc.setName(m.getName());
											
											TestResult tr = tc.run();
											
											JUnitRapport.addRun(tr);
										}
									}
									
									JUnitRapport.closeTest();
								}
							}
						}
						catch (NoClassDefFoundError e) {
							//System.err.println(className);
						}
						catch (IllegalAccessError e) {
							//System.err.println(className);
						}
					}
				}
				
				JUnitRapport.closeTestSuite();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static URLClassLoader loadJars(String dir) {
		Collection jarFiles = FileUtils.listFiles(new File(dir), new String[] {"jar"}, true);
		
		List<URL> urlList = new ArrayList<URL>();
		
		for(Object o : jarFiles) {
			try {
				urlList.add(((File)o).toURL());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		URL[] urlArray = new URL[urlList.size()];
		
		int i = 0;
		
		for (URL u : urlList) {
			urlArray[i] = (URL)u;
			
			i++;
		}
		
		URLClassLoader classLoader = new URLClassLoader(urlArray);
		
		return classLoader;
	}
}
