package net.sf.wts.services.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Encryption {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static final String SECURITY_PROVIDER_STRING = "BC";
	public static final String ASYMMETRIC_ALGORITHM_NAME = "RSA";
	public static final String SYMMETRIC_ALGORITHM_NAME = "AES";
	public static final String ASYMMETRIC_CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	public static final String SYMMETRIC_CIPHER_TRANSFORMATION = "AES";
	public static final int LENGTH_OF_GENERATED_ASYMMETRIC_KEYS = 2048;
	public static final int LENGTH_OF_GENERATED_SYMMETRIC_KEYS = 128;
	
	public static final Key getNewSessionKey(){
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance(SYMMETRIC_ALGORITHM_NAME,SECURITY_PROVIDER_STRING);
			keyGen.init(LENGTH_OF_GENERATED_SYMMETRIC_KEYS);
			return keyGen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		//no arguments:
		//so exception will not occur for this code
		return null;	
	}
	
	private static final Cipher getEncryptCipher(Key k){
		Cipher c = null;
		try {
			if(k.getAlgorithm().equals(ASYMMETRIC_ALGORITHM_NAME)){
				c = Cipher.getInstance(ASYMMETRIC_ALGORITHM_NAME,SECURITY_PROVIDER_STRING);
				c.init(Cipher.ENCRYPT_MODE, k);
			}else if(k.getAlgorithm().equals(SYMMETRIC_ALGORITHM_NAME)){
				c = Cipher.getInstance(SYMMETRIC_CIPHER_TRANSFORMATION,SECURITY_PROVIDER_STRING);
				c.init(Cipher.ENCRYPT_MODE, k);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return c;
	}
	
	private static final Cipher getDecryptCipher(Key k){
		Cipher c = null;
		try {
			if(k.getAlgorithm().equals(ASYMMETRIC_ALGORITHM_NAME)){
				c = Cipher.getInstance(ASYMMETRIC_ALGORITHM_NAME,SECURITY_PROVIDER_STRING);
				c.init(Cipher.DECRYPT_MODE, k);
			}else if(k.getAlgorithm().equals(SYMMETRIC_ALGORITHM_NAME)){
				c = Cipher.getInstance(SYMMETRIC_CIPHER_TRANSFORMATION,SECURITY_PROVIDER_STRING);
				c.init(Cipher.DECRYPT_MODE, k);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public static final byte[] encrypt(String sessionTicket, byte[] input){
		try {
			return getEncryptCipher(Sessions.getSessionKey(sessionTicket)).doFinal(input);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		throw new Error();
	}
	
	public static final byte[] decrypt(String sessionTicket, byte[] input){
		try {
			return getDecryptCipher(Sessions.getSessionKey(sessionTicket)).doFinal(input);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		throw new Error();
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
	
	
	
	

	private KeyPair keys;
	private Cipher ecipher;
	private Cipher dcipher;

	public Encryption() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(LENGTH_OF_GENERATED_ASYMMETRIC_KEYS);
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

	public Encryption(String filename, final String passphrase){
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

	public Encryption(KeyPair keys) {
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

	public static String encrypt(String message, PublicKey key){
		try {
			Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] b = encryptCipher.doFinal(message.getBytes());
			return (new BASE64Encoder()).encode(b);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			System.err.println("message too long");
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} 
		throw new Error();

	}

	public static String decrypt(String message, PrivateKey key){
		try {
			Cipher decryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
			byte[] msg = (new BASE64Decoder()).decodeBuffer(message);
			byte[] dec = decryptCipher.doFinal(msg);
			return new String(dec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		throw new Error();

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

	

	



	public static void main(String[] args){
		
		
		//		try {
		//			Encrypt e = new Encrypt("/home/gbehey0/wts.test/sleutel","");
		//			//plain
		//			FileInputStream fis = new FileInputStream("/home/gbehey0/wts.test/plainin");
		//			FileOutputStream fos = new FileOutputStream("/home/gbehey0/wts.test/plainout");
		//			int nb;
		//			byte[] buffer = new byte[100];
		//			long start = System.currentTimeMillis();
		//			while((nb = fis.read()) != -1){
		//				fis.read(buffer);
		//				fos.write(buffer);
		//			}
		//			long stop = System.currentTimeMillis();
		//			fis.close();
		//			fos.close();
		//			System.out.println("Copy Time: "+(stop-start)+" ms");
		//			//encryption
		//			fis = new FileInputStream("/home/gbehey0/queries/database.zip");
		//			fos = new FileOutputStream("/home/gbehey0/wts.test/encrypted");
		//			start = System.currentTimeMillis();
		//		    e.encrypt(fis, fos);
		//			stop = System.currentTimeMillis();
		//			fis.close();
		//			fos.close();
		//			System.out.println("Encryption Time: "+(stop-start)+" ms");
		//decryption
		//			fis = new FileInputStream("/home/gbehey0/wts.test/encrypted");
		//			fos = new FileOutputStream("/home/gbehey0/wts.test/decrypted");
		//			start = System.currentTimeMillis();
		//		    e.decrypt(fis, fos);
		//			stop = System.currentTimeMillis();
		//			fis.close();
		//			fos.close();
		//			System.out.println("Decryption Time: "+(stop-start)+" ms");
		//			
		//		} catch (FileNotFoundException e1) {
		//			e1.printStackTrace();
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}

//				KeyPairGenerator keyGen;
//				try {
//					keyGen = KeyPairGenerator.getInstance("RSA");
//					keyGen.initialize(1024);
//					KeyPair keys = keyGen.generateKeyPair();
//					Cipher c = Cipher.getInstance("RSA");
//					String message = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345";
//					System.out.println(message);
//					String enc = encryptLongMessage(message,keys.getPublic());
//					System.out.println(enc);
//					String dec = decryptLongMessage(enc,keys.getPrivate());
//					System.out.println(dec);
//				} catch (NoSuchAlgorithmException e) {
//					e.printStackTrace();
//				} catch (NoSuchPaddingException e) {
//					e.printStackTrace();
//				}
//		String serviceName = "hierkomteenservice";
//		String userName = "gbehey0";
//		String test = serviceName + "_" + userName + "_" + System.currentTimeMillis();
//		
//		System.out.println(test.length());
//		
//		try {
//			//gen RSA keys
//			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA",SECURITY_PROVIDER_STRING);
//			keyPairGen.initialize(1024);
//			KeyPair rsaKeys = keyPairGen.generateKeyPair();
//			
//			//gen DES key
//			
//			
//			System.out.println(new String(skey.getEncoded()));
//			System.out.println((new BASE64Encoder()).encode(skey.getEncoded()));
//			//encrypt DES key
//			Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding",SECURITY_PROVIDER_STRING);
//			c.init(Cipher.ENCRYPT_MODE, rsaKeys.getPublic());
//			byte[] enc = c.doFinal(skey.getEncoded());
//			String senc = (new BASE64Encoder()).encode(enc);
//			
//			
//			//decrypt DES key
//			c.init(Cipher.DECRYPT_MODE, rsaKeys.getPrivate());
//			enc = (new BASE64Decoder()).decodeBuffer(senc);
//			byte[] dec = c.doFinal(enc);
//			String sdec = new String(dec);
//			System.out.println(sdec);
//			System.out.println((new BASE64Encoder()).encode(dec));
//			
//			//use SKEY
//			String message = "echt een prachtig voorbeeldje hier";
//			c = Cipher.getInstance("AES",SECURITY_PROVIDER_STRING);
//			c.init(Cipher.ENCRYPT_MODE, skey);
//			enc = c.doFinal(message.getBytes());
//			c.init(Cipher.DECRYPT_MODE, skey);
//			dec = c.doFinal(enc);
//			sdec = new String(dec);
//			System.out.println(sdec);
//			
//			
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (NoSuchProviderException e) {
//			e.printStackTrace();
//		} catch (NoSuchPaddingException e) {
//			e.printStackTrace();
//		} catch (InvalidKeyException e) {
//			e.printStackTrace();
//		} catch (IllegalBlockSizeException e) {
//			e.printStackTrace();
//		} catch (BadPaddingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}


	}
}
