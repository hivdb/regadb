package net.sf.wts.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.wts.install.config.ConfigFile;
import net.sf.wts.install.user.User;

import org.apache.commons.io.FileUtils;

public class Installer {
	private final static String configFile = "wts.xml";
	
	private static String configDirectory;
	
	private static String wtsDir;
	
	public static void main(String[] args) {
		String os = System.getProperty("os.name");
		
		if (os.toLowerCase().contains("linux")) {
			configDirectory = "/etc/wt/wts/";
		}
		
		if (os.toLowerCase().contains("windows")) {
			configDirectory = "C:/Program Files/wt/wts/";
		}
		
		//show error messqge
		if (configDirectory != null || !"".equals(configDirectory)) {
			createConfiguration();
			
			install();
		}
	}
	
	private static void createConfiguration() {
		try {
			FileUtils.forceMkdir(new File(configDirectory));
		}
		catch (IOException ioe) {
			showError("Run the installer as root");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void install() {
		readWtsDir();
		
		File wts = new File(wtsDir);
		
		try {
			FileUtils.forceMkdir(wts);
			
			if (wts.listFiles().length != 0) {
				showError("Directory is not empty");
			}
			else {
				FileUtils.forceMkdir(new File(wtsDir + File.separatorChar + "services"));
				FileUtils.forceMkdir(new File(wtsDir + File.separatorChar + "sessions"));
				FileUtils.forceMkdir(new File(wtsDir + File.separatorChar + "users"));
				
				Map<String, String> challengeSettings = readChallengeSettings();
				
				ConfigFile config = new ConfigFile(configDirectory + configFile, wtsDir, challengeSettings);
				config.createConfigFile();
				
				createUser();
			}
		}
		catch (Exception e) {
			//shoz error
			e.printStackTrace();
		}
	}
	
	private static void readWtsDir() {
		System.out.println("Install Directory");
		wtsDir = readInput() + File.separatorChar;
	}
	
	private static Map<String, String> readChallengeSettings() {
		Map<String, String> challengeSettings = new HashMap<String, String>();
		
		System.out.println("Expire Time");
		challengeSettings.put("expire-time", readInput());
		
		return challengeSettings;
	}
	
	private static void createUser() {
		System.out.println("Username");
		String username = readInput();
		
		System.out.println("Password");
		String password = readInput();
		
		User user = new User(username, password);
		user.writeUser(wtsDir + File.separatorChar + "users" + File.separatorChar + "users.pwd");
	}
	
	private static String readInput() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		String input = new String();
		
		try {
			input = reader.readLine();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return input;
	}
	
	private static void showError(String error) {
		System.out.println(error);
		
		System.exit(0);
	}
}
