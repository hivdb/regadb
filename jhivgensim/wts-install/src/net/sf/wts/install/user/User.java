package net.sf.wts.install.user;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import sun.misc.BASE64Encoder;

public class User {
	private String username;
	private String password;
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void writeUser(String fileName) {
		try {
			OutputStream output = new FileOutputStream(fileName);
			output.write((username + ":" + encrypt(password, "MD5") + "\n").getBytes());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private synchronized String encrypt(String plaintext, String algorithm) {
		MessageDigest md = null;
		
		try {
			md = MessageDigest.getInstance(algorithm);
			md.update(plaintext.getBytes("UTF-8"));
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		byte raw[] = md.digest();
		
		String hash = (new BASE64Encoder()).encode(raw);
		
		return hash;
	}
}
