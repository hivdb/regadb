package net.sf.wts.services.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

import javax.activation.FileDataSource;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

public class EncryptedFileDataSource extends FileDataSource {

	private Cipher encryptCipher;	

	public EncryptedFileDataSource(File file, Key key){
		super(file);
		encryptCipher = Encrypt.getEncryptCipher(key);		
	}

	public EncryptedFileDataSource(String filename, Key key){
		super(filename);
		encryptCipher = Encrypt.getEncryptCipher(key);	
	}
	
	public InputStream getInputStream() throws IOException{
		System.out.println("getInputStream");
		return new CipherInputStream(super.getInputStream(),encryptCipher);
	}

}
