package net.sf.wts.client.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Encrypt {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static final String SECURITY_PROVIDER_STRING = "BC";
	public static final String ASYMMETRIC_ALGORITHM_NAME = "RSA";
	public static final String SYMMETRIC_ALGORITHM_NAME = "AES";
	public static final String ASYMMETRIC_CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	public static final String SYMMETRIC_CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
	public static final int LENGTH_OF_GENERATED_ASYMMETRIC_KEYS = 2048;
	public static final int LENGTH_OF_GENERATED_SYMMETRIC_KEYS = 128;
	
	public static PrivateKey restorePrivateKey(String key){
//	public static PrivateKey restorePrivateKey(String key, final String passphrase){
//		KeyPair keys = null;
//		try {
//			PasswordFinder pf = new PasswordFinder(){
//
//				public char[] getPassword() {
//					return passphrase.toCharArray();
//				}
//
//			};
//			PEMReader pr = new PEMReader(new StringReader(key),pf);
////			PEMReader pr = new PEMReader(new FileReader(filename),pf,SECURITY_PROVIDER_STRING);
//			keys = (KeyPair) pr.readObject();
//			pr.close();
//			return keys.getPrivate();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e){
//			if(keys.getPrivate().getAlgorithm().equals("DSA")){
//				System.err.println("You can't use DSA keys for encryption");
//			}else{
//				throw e;
//			}			
//		}
//		return null;
		
		PrivateKey k = null;
		try {
			byte[] encodedKey = (new BASE64Decoder()).decodeBuffer(key);
			k = (KeyFactory.getInstance("RSA")).generatePrivate(new PKCS8EncodedKeySpec(encodedKey));			
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return k;
	}
	
	public static PublicKey restorePublicKey(String key){
		PublicKey k = null;
		try {
			byte[] encodedKey = (new BASE64Decoder()).decodeBuffer(key);
			k = (KeyFactory.getInstance("RSA")).generatePublic(new X509EncodedKeySpec(encodedKey));			
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return k;
	}

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

	public static final Cipher getEncryptCipher(Key k){
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

	public static final Cipher getDecryptCipher(Key k){
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

	public static synchronized final byte[] encrypt(Key sessionKey, byte[] input){
		try {
			return getEncryptCipher(sessionKey).doFinal(input);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		throw new Error();
	}

	public static synchronized final byte[] decrypt(Key sessionKey, byte[] input){
		try {
			Cipher c = getDecryptCipher(sessionKey);
			InputStream is = new CipherInputStream(new ByteArrayInputStream(input),c);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int read = 0;
			while((read = is.read()) >= 0){
				os.write(read);
			}
			return os.toByteArray();
//			return getDecryptCipher(sessionKey).doFinal(input);
//		} catch (IllegalBlockSizeException e) {
//			e.printStackTrace();
//		} catch (BadPaddingException e) {
//			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new Error();
	}
	
	public static synchronized String decrypt(byte[] message, PrivateKey key){
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
	
	public static synchronized byte[] encrypt(String message, PublicKey key){
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
