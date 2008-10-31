package net.sf.wts.services.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.BASE64Encoder;

public class Encrypt {

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

	public static String decrypt(byte[] message, PrivateKey key){
		try {
			Cipher decryptCipher = getDecryptCipher(key);
			byte[] b = decryptCipher.doFinal(message);
			return new String(b);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			System.err.println("message too long");
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} 
		throw new Error();
	}
	
	public static byte[] encrypt(String message, PublicKey key){
		try {
			Cipher encryptCipher = getEncryptCipher(key);
			byte[] b = encryptCipher.doFinal(message.getBytes());
			return b;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			System.err.println("message too long");
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
}
