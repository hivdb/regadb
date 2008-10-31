package net.sf.wts.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.spec.SecretKeySpec;

import net.sf.wts.client.util.AxisClient;
import net.sf.wts.client.util.Encrypt;

import org.apache.commons.io.FileUtils;

import sun.misc.BASE64Decoder;

public class WtsClient 
{
	private String url_;
	private AxisClient axisService = new AxisClient();
	private Key sessionKey;
	private PrivateKey privateKey; 

	public WtsClient(String url)
	{
		url_ = url;
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

	public String login(String userName, String challenge, String password, String serviceName) throws RemoteException, MalformedURLException
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
			axisService.addParameter(new String(signedChallenge));
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//return sessionTicket
		return decryptedAnswer.substring(0, decryptedAnswer.lastIndexOf("_"));
	}

	public void upload(String sessionTicket, String serviceName, String fileName, byte[] file) throws RemoteException, MalformedURLException
	{
		axisService.removeParameters();
		axisService.setServiceUrl(url_, "Upload");

		axisService.addParameter(sessionTicket);
		axisService.addParameter(serviceName);
		axisService.addParameter(fileName);
		axisService.addParameter(Encrypt.encrypt(sessionKey, file));

		try 
		{
			axisService.call();
		} 
		catch (RemoteException e) 
		{
			e.printStackTrace();
		}
	}

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
		byte[] array = download(sessionTicket, serviceName, fileName);
		
		if(array!=null)
		{
			try 
			{
				FileUtils.writeByteArrayToFile(toWrite, Encrypt.decrypt(sessionKey, array));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void upload(String sessionTicket, String serviceName, String fileName, File localLocation) throws RemoteException, MalformedURLException
	{
		byte[] array = null;
		try 
		{
			array = FileUtils.readFileToByteArray(localLocation);;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		upload(sessionTicket, serviceName, fileName, Encrypt.encrypt(sessionKey, array));
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

	public String getUrl() 
	{
		return url_;
	}
}
