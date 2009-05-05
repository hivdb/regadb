package net.sf.regadb.util.settings;


public class ConfigTest {

	public static void main(String args[]){
		RegaDBSettings set = RegaDBSettings.getInstance();
		set.writeConfFileSkeleton(new java.io.File("/dev/null"));
	}
}
