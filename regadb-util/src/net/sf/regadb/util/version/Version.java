package net.sf.regadb.util.version;

import java.io.IOException;
import java.util.Properties;
import java.util.Map.Entry;

public class Version {
	private static final Properties properties;

	static {
		properties = new Properties();
		try { 
			properties.load(Version.class.getResourceAsStream("version.properties"));
		} catch(IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static String getCommitId(){
		return properties.getProperty("commit.id");
	}
	public static String getCommitAuthor(){
		return properties.getProperty("commit.author");
	}
	public static String getCommitTime(){
		return properties.getProperty("commit.time");
	}
	public static String getBuildTime(){
		return properties.getProperty("build.time");
	}
	public static String getBuildAuthor(){
		return properties.getProperty("build.author");
	}
	public static String getBuildSystem(){
		return properties.getProperty("build.system");
	}
	
	public static final Properties getProperties(){
		return properties;
	}
	
	public static void main(String args[]){
		for(Entry<Object, Object> e : properties.entrySet())
			System.out.println(e.getKey() +"="+ e.getValue());
	}
}
