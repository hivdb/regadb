package net.sf.wts.services.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
/**
 * openssl rsa -in sleutel -outform PEM -out private
 * openssl rsa -in sleutel -outform PEM -pubout -out public
 */
public class EncryptionTest {
	
	
	public static void main(String[] args) {
		File encDir = new File(System.getProperty("user.home") + File.separator + ".wts");
		if(!encDir.exists()){
			encDir.mkdir();
		}
		File key = new File(encDir.getAbsolutePath() + File.separator + "key");
		if(!key.exists()){
			
		}
		
		KeyPair keys = null;
		try {
			PasswordFinder pf = new PasswordFinder(){

				public char[] getPassword() {
					return "iets".toCharArray();
				}

			};
//			PEMReader pr = new PEMReader(new StringReader(key),pf);
			PEMReader pr = new PEMReader(new FileReader(key),pf,"BC");
			keys = (KeyPair) pr.readObject();
			pr.close();
			keys.getPrivate().getEncoded();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			if(keys.getPrivate().getAlgorithm().equals("DSA")){
				System.err.println("You can't use DSA keys for encryption");
			}else{
				throw e;
			}			
		}
		
	}

}
