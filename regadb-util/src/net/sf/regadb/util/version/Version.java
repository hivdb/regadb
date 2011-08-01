package net.sf.regadb.util.version;

import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

public class Version {
	private static Properties properties = null;

	static {
		try {
			InputStream in = Version.class.getResourceAsStream("version.properties");
			if(in != null){
				properties = new Properties();
				properties.load(in);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static String getCommitId(){
		return properties == null ? "n/a" :
				properties.getProperty("commit.id");
	}
	public static String getCommitAuthor(){
		return properties == null ? "n/a" :
			 properties.getProperty("commit.author");
	}
	public static String getCommitTime(){
		return properties == null ? "n/a" :
			 properties.getProperty("commit.time");
	}
	public static String getBuildTime(){
		return properties == null ? "n/a" :
			 properties.getProperty("build.time");
	}
	public static String getBuildAuthor(){
		return properties == null ? "n/a" :
			 properties.getProperty("build.author");
	}
	public static String getBuildSystem(){
		return properties == null ? "n/a" :
			 properties.getProperty("build.system");
	}
	
	public static final Properties getProperties(){
		return properties;
	}
	
	public static void main(String args[]){
		for(Entry<Object, Object> e : properties.entrySet())
			System.out.println(e.getKey() +"="+ e.getValue());
	}
}
