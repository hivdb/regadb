package net.sf.wts.services.test;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.spec.SecretKeySpec;

import net.sf.wts.services.CloseSessionImpl;
import net.sf.wts.services.GetChallengeImpl;
import net.sf.wts.services.LoginImpl;
import net.sf.wts.services.util.Encrypt;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class GetChallengeTest {

	public static String testUser = "gbehey0";
	public static String testPass = "bla123";
	public static String testPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMz1OALfr/CLLt20vxBJ/xvbwu9CUXY0CnV8YIigfIHUr7w5fPgBA4YavcVavqqqHeQgXRXV5luOLczBRnYNv6y3HBddZ5UvjLa/zr8/37OUMURhkqyB66lU8FOrV5ONslf/+1zs1Dpi83y0Yxhx0PRYub75JW7WyoVCpGz0qDELAgMBAAECgYBSk/ZmSgPUMe/HCfz1Lisn6UpIJfs2Wc9g+KTYR3kCwlOvzaXJMndd/8Y4DtDFaFc0w8ldc9olR0qytaiTBgUUc94UA+MtOM4aOjd0u9MrD59mGCG3MO1+ojjn9PMiPmXlj4QIdbu0CkWnwStrUkFr80sgUvHXSW09sM/YRj6x6QJBAOz8fO//IGO8xEnfhRIryvjHj/dnM7rYX2QMoYYvrd0Nvdxyr3t6qTEEkgNeimBmfZuG2ULn787V8fUoZUX2bS0CQQDdZuSHEbZ7GN+jq2QRh5fgsxcHSn460aM8Y9C5mN9r+w3Tq1j4qcvtrDu+ltwFInc8fEiSjQNx0jR712fi5yoXAkANp36LVWfIV1f36akBIwTO0LC60HdqjIzydsfXs2eRFPmbegAiXS7iZCEFkKzoYP9btqlN8Y8fm7QVK/6pyUkBAkEA0ImW3QY5DC88jqvjoINH8dSd7zciOIK3Ly2RLw+n+cxJlMMDFYzRUTd2Oqlb6dYx2x3xOWBrCy2EU9Vru5Qi1wJBAKPjEQW8ZSrVeys3p2x5kmcmGebz+M1u1dkEgNegBiglI3DnW3oxLD2JhdOHzFyZ1hEJDFfCEuOPhGaIkmaXJqA=";
	public static String testPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDM9TgC36/wiy7dtL8QSf8b28LvQlF2NAp1fGCIoHyB1K+8OXz4AQOGGr3FWr6qqh3kIF0V1eZbji3MwUZ2Db+stxwXXWeVL4y2v86/P9+zlDFEYZKsgeupVPBTq1eTjbJX//tc7NQ6YvN8tGMYcdD0WLm++SVu1sqFQqRs9KgxCwIDAQAB";

	public static void main(String [] args)
	{
		GetChallengeImpl gc = new GetChallengeImpl();

		String s=null;
		try {
			s = gc.exec(testUser);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.err.println(s);
		System.err.println(s.length());

		try {
			Signature sign = Signature.getInstance("SHA1withRSA");
			PrivateKey pk = Encrypt.restorePrivateKey(testPrivateKey); 
			sign.initSign(Encrypt.restorePrivateKey(testPrivateKey));
			sign.update(s.getBytes());
			byte[] signedChallenge = sign.sign();

			LoginImpl login = new LoginImpl();
			byte[] answer = login.exec(testUser, s, signedChallenge, "regadb-align");

			//decrypt answer
			String decryptedAnswer = Encrypt.decrypt(answer,pk);

			//retrieve sessionkey and store it
			String encodedSessionKey = decryptedAnswer.substring(decryptedAnswer.lastIndexOf("_")+1, decryptedAnswer.length());


			Key sessionKey = new SecretKeySpec((new BASE64Decoder()).decodeBuffer(encodedSessionKey),"AES");


			String sessionTicket = decryptedAnswer.substring(0, decryptedAnswer.lastIndexOf("_"));
//			byte[] file = FileUtils.readFileToByteArray(new File("/home/gbehey0/wts/input2"));
//			byte[] enc = Encrypt.encrypt(sessionKey, file);
//			(new UploadImpl()).exec(sessionTicket, "regadb-align", "region", enc);
			System.out.println((new BASE64Encoder()).encode(sessionKey.getEncoded()));
			
			(new CloseSessionImpl()).exec(sessionTicket,"regadb-align");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}


	}
}
