package net.sf.wts.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPMessage;

import net.sf.wts.client.util.AxisClient;
import net.sf.wts.client.util.Encrypt;
import net.sf.wts.client.util.EncryptedFileDataSource;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class WtsClient 
{
	private String url_;
	private AxisClient axisService = new AxisClient();
	private Key sessionKey;
	private PrivateKey privateKey; 

	public WtsClient(String url, PrivateKey privateKey)
	{
		this.url_ = url;
		this.privateKey = privateKey;
	}

	public String getChallenge(String userName) throws RemoteException, MalformedURLException
	{
		axisService.removeParameters();
		axisService.setServiceUrl(url_, "GetChallenge");
		axisService.addParameter(userName);

		String challenge = "";
		challenge = axisService.callAndGetStringResult();
		return challenge;
	}

	public String login(String userName, String challenge, String serviceName) throws RemoteException, MalformedURLException
	{
		//sign challenge
		try {
			Signature sign = Signature.getInstance("SHA1withRSA");
			sign.initSign(privateKey);
			sign.update(challenge.getBytes());
			byte[] signedChallenge = sign.sign();

			axisService.removeParameters();
			axisService.setServiceUrl(url_, "Login");

			axisService.addParameter(userName);
			axisService.addParameter(challenge);
			axisService.addParameter(signedChallenge);
			axisService.addParameter(serviceName);
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}		

		//send signedChallenge
		byte[] answer = axisService.callAndGetByteArrayResult();

		//decrypt answer
		String decryptedAnswer = Encrypt.decrypt(answer,privateKey);

		//retrieve sessionkey and store it
		String encodedSessionKey = decryptedAnswer.substring(decryptedAnswer.lastIndexOf("_")+1, decryptedAnswer.length());

		try {
			sessionKey = new SecretKeySpec((new BASE64Decoder()).decodeBuffer(encodedSessionKey),"AES");
			System.out.println((new BASE64Encoder()).encode(sessionKey.getEncoded()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		//return sessionTicket
		return decryptedAnswer.substring(0, decryptedAnswer.lastIndexOf("_"));
	}	

	@Deprecated //uses byte arrays
	public byte[] download(String sessionTicket, String serviceName, String fileName) throws RemoteException, MalformedURLException
	{
		axisService.removeParameters();
		axisService.setServiceUrl(url_, "Download");

		axisService.addParameter(sessionTicket);
		axisService.addParameter(serviceName);
		axisService.addParameter(fileName);

		byte[] array = null;
		try 
		{
			array = axisService.callAndGetByteArrayResult();
		} 
		catch (RemoteException e) 
		{
			e.printStackTrace();
		}
		return Encrypt.decrypt(sessionKey, array);
	}

	public void download(String sessionTicket, String serviceName, String fileName, File toWrite) throws RemoteException, MalformedURLException
	{
		//		byte[] array = download(sessionTicket, serviceName, fileName);
		//		
		//		if(array!=null)
		//		{
		//			try 
		//			{
		//				
		//				FileUtils.writeByteArrayToFile(toWrite, Encrypt.decrypt(sessionKey, array));
		//				
		//			} 
		//			catch (IOException e) 
		//			{
		//				e.printStackTrace();
		//			}
		//		}		

		axisService.removeParameters();
		axisService.setServiceUrl(url_, "Download");

		axisService.addParameter(sessionTicket);
		axisService.addParameter(serviceName);
		axisService.addParameter(fileName);

		SOAPMessage response = null;

		try 
		{
			response = axisService.callAndGetAttachment();
		} 
		catch (RemoteException e) 
		{
			e.printStackTrace();
		}

		Iterator iterator = response.getAttachments();

		while (iterator.hasNext()) {
			try {
				AttachmentPart ap = (AttachmentPart) iterator.next();
				System.out.println("attach");
				InputStream is = ap.getDataHandler().getDataSource().getInputStream();
//				OutputStream os = new CipherOutputStream(new FileOutputStream(toWrite),Encrypt.getDecryptCipher(sessionKey));
				OutputStream os = new FileOutputStream(toWrite);
				byte[] buffer = new byte[4096];
				int read = 0;
				while ((read = is.read(buffer)) > 0) {					
					os.write(buffer, 0, read);
					os.flush();
				}
				os.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void upload(String sessionTicket, String serviceName, String fileName, DataHandler dh) throws RemoteException, MalformedURLException
	{
		axisService.removeParameters();
		axisService.setServiceUrl(url_, "Upload");

		axisService.addParameter(sessionTicket);
		axisService.addParameter(serviceName);
		axisService.addParameter(fileName);
		axisService.addParameter(dh);

		try 
		{
			axisService.callAndGetStringResult();
		} 
		catch (RemoteException e) 
		{
			e.printStackTrace();
		}
	}

	//encrypt file[] or through data source
	public void upload(String sessionTicket, String serviceName, String fileName, byte[] file) throws RemoteException, MalformedURLException
	{
		//		axisService.removeParameters();
		//		axisService.setServiceUrl(url_, "Upload");
		//
		//		axisService.addParameter(sessionTicket);
		//		axisService.addParameter(serviceName);
		//		axisService.addParameter(fileName);
		//		axisService.addParameter(Encrypt.encrypt(sessionKey, file));
		//
		//		try 
		//		{
		//			axisService.call();
		//		} 
		//		catch (RemoteException e) 
		//		{
		//			e.printStackTrace();
		//		}


		DataSource ds = new ByteArrayDataSource(file, null);
		DataHandler dh = new DataHandler(ds);
		upload(sessionTicket, serviceName, fileName, dh);
	}

	public void upload(String sessionTicket, String serviceName, String fileName, File localLocation) throws RemoteException, MalformedURLException
	{
//		byte[] array = null;
//		try 
//		{
//			array = FileUtils.readFileToByteArray(localLocation);;
//		} 
//		catch (IOException e) 
//		{
//			e.printStackTrace();
//		}
//		upload(sessionTicket, serviceName, fileName, Encrypt.encrypt(sessionKey, array));

		DataSource ds = new FileDataSource(localLocation);
		DataHandler dh = new DataHandler(ds);
		upload(sessionTicket, serviceName, fileName, dh);
	}

	public byte[] monitorLogFile(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
	{
		axisService.removeParameters();
		axisService.setServiceUrl(url_, "MonitorLogFile");

		axisService.addParameter(sessionTicket);
		axisService.addParameter(serviceName);

		byte[] array = null;
		try 
		{
			array = axisService.callAndGetByteArrayResult();
		} 
		catch (RemoteException e) 
		{
			e.printStackTrace();
		}
		return Encrypt.decrypt(sessionKey, array);
	}

	public byte[] monitorLogTail(String sessionTicket, String serviceName, int numberOfLines) throws RemoteException, MalformedURLException
	{
		axisService.removeParameters();
		axisService.setServiceUrl(url_, "MonitorLogTail");

		axisService.addParameter(sessionTicket);
		axisService.addParameter(serviceName);
		axisService.addParameter(numberOfLines);

		byte[] array = null;
		try 
		{
			array = axisService.callAndGetByteArrayResult();
		} 
		catch (RemoteException e) 
		{
			e.printStackTrace();
		}
		return Encrypt.decrypt(sessionKey, array);
	}

	public String monitorStatus(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
	{
		axisService.removeParameters();
		axisService.setServiceUrl(url_, "MonitorStatus");

		axisService.addParameter(sessionTicket);
		axisService.addParameter(serviceName);

		String status = "";

		try 
		{
			status = axisService.callAndGetStringResult();
		} 
		catch (RemoteException e) 
		{
			e.printStackTrace();
		}

		return status;
	}

	public void start(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
	{
		axisService.removeParameters();
		axisService.setServiceUrl(url_, "Start");

		axisService.addParameter(sessionTicket);
		axisService.addParameter(serviceName);

		try 
		{
			axisService.call();
		} 
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}

	public void closeSession(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
	{
		axisService.removeParameters();
		axisService.setServiceUrl(url_, "CloseSession");

		axisService.addParameter(sessionTicket);
		axisService.addParameter(serviceName);

		try 
		{
			axisService.call();
		} 
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}

	public void stop(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
	{
		axisService.removeParameters();
		axisService.setServiceUrl(url_, "Stop");

		axisService.addParameter(sessionTicket);
		axisService.addParameter(serviceName);

		try 
		{
			axisService.call();
		} 
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}

	public void getServiceList() throws RemoteException, MalformedURLException{
		axisService.removeParameters();
		axisService.setServiceUrl(url_, "ListServices");
		try 
		{
			axisService.call();
		} 
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}

	public String getUrl() 
	{
		return url_;
	}

	public static void main(String[] args) {
		String url = "http://localhost:8080/wts/services/";
		String testPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMz1OALfr/CLLt20vxBJ/xvbwu9CUXY0CnV8YIigfIHUr7w5fPgBA4YavcVavqqqHeQgXRXV5luOLczBRnYNv6y3HBddZ5UvjLa/zr8/37OUMURhkqyB66lU8FOrV5ONslf/+1zs1Dpi83y0Yxhx0PRYub75JW7WyoVCpGz0qDELAgMBAAECgYBSk/ZmSgPUMe/HCfz1Lisn6UpIJfs2Wc9g+KTYR3kCwlOvzaXJMndd/8Y4DtDFaFc0w8ldc9olR0qytaiTBgUUc94UA+MtOM4aOjd0u9MrD59mGCG3MO1+ojjn9PMiPmXlj4QIdbu0CkWnwStrUkFr80sgUvHXSW09sM/YRj6x6QJBAOz8fO//IGO8xEnfhRIryvjHj/dnM7rYX2QMoYYvrd0Nvdxyr3t6qTEEkgNeimBmfZuG2ULn787V8fUoZUX2bS0CQQDdZuSHEbZ7GN+jq2QRh5fgsxcHSn460aM8Y9C5mN9r+w3Tq1j4qcvtrDu+ltwFInc8fEiSjQNx0jR712fi5yoXAkANp36LVWfIV1f36akBIwTO0LC60HdqjIzydsfXs2eRFPmbegAiXS7iZCEFkKzoYP9btqlN8Y8fm7QVK/6pyUkBAkEA0ImW3QY5DC88jqvjoINH8dSd7zciOIK3Ly2RLw+n+cxJlMMDFYzRUTd2Oqlb6dYx2x3xOWBrCy2EU9Vru5Qi1wJBAKPjEQW8ZSrVeys3p2x5kmcmGebz+M1u1dkEgNegBiglI3DnW3oxLD2JhdOHzFyZ1hEJDFfCEuOPhGaIkmaXJqA=";

		WtsClient wc = new WtsClient(url,Encrypt.restorePrivateKey(testPrivateKey));

		try {
			String serviceName = "regadb-align";

			String userName = "gbehey0";
			String password = "bla123";

			String inputFileName = "nt_sequences";
			String inputFileName2 = "region";
			String outputFileName = "aa_sequences";

			File localLocation = new File("/home/gbehey0/wts/input");
			File localLocation2 = new File("/home/gbehey0/wts/input2");
			File toWrite = new File("/home/gbehey0/wts/output");

			String challenge = wc.getChallenge(userName);
			String sessionTicket = wc.login(userName, challenge, serviceName);


			wc.upload(sessionTicket, serviceName, inputFileName, localLocation);
			wc.upload(sessionTicket, serviceName, inputFileName2, localLocation2);

			wc.start(sessionTicket, serviceName);
			String status = wc.monitorStatus(sessionTicket, serviceName);			
			while (!status.equals("ENDED_SUCCES")) {
				status = wc.monitorStatus(sessionTicket, serviceName);			
			}
			
			wc.download(sessionTicket, serviceName, outputFileName, toWrite);			
//			wc.closeSession(sessionTicket, serviceName);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
