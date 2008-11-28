package net.sf.wts.services.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.activation.DataSource;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;



public class EncryptedFileDataSource implements DataSource {

	private Cipher encryptCipher;
	private File file;
	

	public EncryptedFileDataSource(File file, Key key){
		this.file = file;
		encryptCipher = Encrypt.getEncryptCipher(key);		
	}

	public EncryptedFileDataSource(String filename, Key key){
		this.file = new File(filename);
		encryptCipher = Encrypt.getEncryptCipher(key);	
	}
	
	public InputStream getInputStream() throws IOException{
		return new CipherInputStream(new FileInputStream(this.file),encryptCipher);	
	}

	public String getContentType() {
		return "application/octet-stream";
	}

	public String getName() {
		return "encrypted file";
	}

	public OutputStream getOutputStream() throws IOException {
		return null;
	}
}
