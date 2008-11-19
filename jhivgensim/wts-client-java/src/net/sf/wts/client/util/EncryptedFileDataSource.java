package net.sf.wts.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.activation.FileDataSource;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

public class EncryptedFileDataSource extends FileDataSource {

	private Cipher encryptCipher;
	private File file;
	

	public EncryptedFileDataSource(File file, Key key){
		super(file);
		this.file = file;
		encryptCipher = Encrypt.getEncryptCipher(key);		
	}

	public EncryptedFileDataSource(String filename, Key key){
		super(filename);
		this.file = new File(filename);
		encryptCipher = Encrypt.getEncryptCipher(key);	
	}
	
	public InputStream getInputStream() throws IOException{
		return new CipherInputStream(new FileInputStream(this.file),encryptCipher);	
	}
}

