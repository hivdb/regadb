package net.sf.wts.services.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

public class Encrypt
{
	public static synchronized String encryptMD5(String plaintext)
	{
		return encrypt(plaintext, "MD5");
	}
	
	  private static synchronized String encrypt(String plaintext, String algo)
	  {
	    MessageDigest md = null;
	    try
	    {
	      md = MessageDigest.getInstance(algo);
	    }
	    catch(NoSuchAlgorithmException e)
	    {
	      e.printStackTrace();
	      return null;
	    }
	    try
	    {
	      md.update(plaintext.getBytes("UTF-8"));
	    }
	    catch(UnsupportedEncodingException e)
	    {
	    	e.printStackTrace();
	    	return null;
	    }
	    byte raw[] = md.digest();
	    String hash = (new BASE64Encoder()).encode(raw);
	    return hash;
	  }
}
