package net.sf.wts.services.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

import sun.misc.BASE64Encoder;

public class Encrypt {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static final String SECURITY_PROVIDER_STRING = "BC";
	public static final int LENGTH_OF_GENERATED_KEYS = 4096;
	
	private KeyPair keys;
	private Cipher ecipher;
	private Cipher dcipher;

	public Encrypt() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(LENGTH_OF_GENERATED_KEYS);
			keys = keyGen.generateKeyPair();
			ecipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			dcipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			ecipher.init(Cipher.ENCRYPT_MODE, keys.getPublic());
			dcipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public Encrypt(String filename, final String passphrase){
		try {
			PasswordFinder pf = new PasswordFinder(){

				public char[] getPassword() {
					return passphrase.toCharArray();
				}
				
			};
			PEMReader pr = new PEMReader(new FileReader(filename),pf,SECURITY_PROVIDER_STRING);
			keys = (KeyPair) pr.readObject();
			pr.close();
			ecipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			dcipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			ecipher.init(Cipher.ENCRYPT_MODE, keys.getPublic());
			dcipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			if(keys.getPrivate().getAlgorithm().equals("DSA")){
				System.err.println("You can't use DSA keys for encryption");
			}else{
				throw e;
			}			
		}
	}

	public Encrypt(KeyPair keys) {
		try {
			this.keys = keys;
			ecipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			dcipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			ecipher.init(Cipher.ENCRYPT_MODE, keys.getPublic());
			dcipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());
		} catch (javax.crypto.NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (java.security.InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public void encrypt(InputStream in, OutputStream out) {
		try {
			
			byte[] buf = new byte[245];
			int nb = 0;
			while ((nb = in.read(buf)) >= 0) {
				out.write(ecipher.doFinal(buf),0,nb);				
			}
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}
	
	public void encrypt(InputStream in, OutputStream out, PublicKey key) {
		try {
			byte[] buf = new byte[100];
			int nb = 0;
			while ((nb = in.read(buf)) >= 0) {
				byte[] bufpiece = new byte[nb];
				for (int i = 0; i < nb; i++) {
					bufpiece[i] = buf[i];
				}
				Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				encryptCipher.init(Cipher.ENCRYPT_MODE, key);
				out.write(ecipher.doFinal(bufpiece));
				out.flush();
			}
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public void decrypt(InputStream in, OutputStream out) {
		try {
			byte[] buf = new byte[128];
			int nb = 0;
			while ((nb = in.read(buf)) >= 0) {
				byte[] bufpiece = new byte[nb];
				for (int i = 0; i < nb; i++) {
					bufpiece[i] = buf[i];
				}
				out.write(dcipher.doFinal(bufpiece));
				out.flush();
			}
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeKey(String file, RSAKey key, boolean privateKey) {
		try {
			byte[] keyBytes;
			if (privateKey) {
				keyBytes = ((RSAPrivateKey) key).getEncoded();
			} else {
				keyBytes = ((RSAPublicKey) key).getEncoded();
			}
			FileWriter fw = new FileWriter(file);
			fw.write((new BASE64Encoder()).encode(keyBytes));
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writePublicKey(String file, RSAPublicKey key) {
		writeKey(file, key, false);
	}

	public static void writePrivateKey(String file, RSAPrivateKey key) {
		writeKey(file, key, false);
	}

	public static synchronized String encryptMD5(String plaintext) {
		return encrypt(plaintext, "MD5");
	}

	private static synchronized String encrypt(String plaintext, String algo) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algo);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		try {
			md.update(plaintext.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		byte raw[] = md.digest();
		String hash = (new BASE64Encoder()).encode(raw);
		return hash;
	}	

	public static void main(String[] args){
		try {
			Encrypt e = new Encrypt("/home/gbehey0/wts.test/sleutel","");
			//plain
			FileInputStream fis = new FileInputStream("/home/gbehey0/wts.test/plainin");
			FileOutputStream fos = new FileOutputStream("/home/gbehey0/wts.test/plainout");
			int nb;
			byte[] buffer = new byte[100];
			long start = System.currentTimeMillis();
			while((nb = fis.read()) != -1){
				fis.read(buffer);
				fos.write(buffer);
			}
			long stop = System.currentTimeMillis();
			fis.close();
			fos.close();
			System.out.println("Copy Time: "+(stop-start)+" ms");
			//encryption
			fis = new FileInputStream("/home/gbehey0/queries/database.zip");
			fos = new FileOutputStream("/home/gbehey0/wts.test/encrypted");
			start = System.currentTimeMillis();
		    e.encrypt(fis, fos);
			stop = System.currentTimeMillis();
			fis.close();
			fos.close();
			System.out.println("Encryption Time: "+(stop-start)+" ms");
			//decryption
//			fis = new FileInputStream("/home/gbehey0/wts.test/encrypted");
//			fos = new FileOutputStream("/home/gbehey0/wts.test/decrypted");
//			start = System.currentTimeMillis();
//		    e.decrypt(fis, fos);
//			stop = System.currentTimeMillis();
//			fis.close();
//			fos.close();
//			System.out.println("Decryption Time: "+(stop-start)+" ms");
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
}
