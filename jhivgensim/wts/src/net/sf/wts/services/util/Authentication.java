package net.sf.wts.services.util;

import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Random;

import sun.misc.BASE64Encoder;


public class Authentication 
{
	private static ArrayList<Challenge> challenges_ = new ArrayList<Challenge>();

	public static String getRandomAsciiString()
	{
		Random generator = new Random();
		char[] asciiRandom = new char[100+generator.nextInt(50)];

		for(int i = 0; i<asciiRandom.length; i++)
		{
			asciiRandom[i] = (char)pickNumberInRange(33, 127, generator);
		}

		return System.currentTimeMillis()+""+new String(asciiRandom);
	}

	private static int pickNumberInRange(int aLowerLimit, int aUpperLimit, Random generator) 
	{
		// get the range, casting to long to avoid overflow problems
		long range = (long)aUpperLimit - (long)aLowerLimit + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long)(range * generator.nextDouble());
		return (int)(fraction + aLowerLimit);
	}

	public static boolean authenticate(String challenge, byte[] signedChallenge, String userName) throws java.rmi.RemoteException
	{
		synchronized(Settings.mutex_)
		{
			if(!Settings.isInitiated())
			{
				Settings.init();
			}
		}

		synchronized(challenges_)
		{
			long currentTime = System.currentTimeMillis();
			Challenge toRemove = null;
			for(Challenge c : challenges_)
			{
				if(c.challenge_.equals(challenge))
				{
					toRemove = c;
					break;
				}
			}

			if(toRemove!=null)
			{
				challenges_.remove(toRemove);
			}

			if(toRemove==null)
			{
				throw new RemoteException("Challenge does not exist"+"_"+challenges_.size()+challenges_.get(0).challenge_);
			}
			else if(currentTime>toRemove.creationTime_+Settings.getChallengeExpireTime()*1000)
			{
				throw new RemoteException("Challenge is too old");
			}
			else
			{
				PublicKey pk = Settings.getPublicKey(userName);
				if(pk == null)
					throw new RemoteException("User does not exist");
				try {
					Signature verify = Signature.getInstance("SHA1withRSA");

					verify.initVerify(pk);
					verify.update(challenge.getBytes());
					
					if(verify.verify(signedChallenge))
					{
						return true;
					}
					else
					{
						throw new RemoteException("Challenge was not solved correctly");
					}
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
					throw new RemoteException("problem with security key",e);
				} catch (SignatureException e) {
					e.printStackTrace();
					throw new RemoteException("problem with security key",e);
				} catch (InvalidKeyException e) {
					e.printStackTrace();
					throw new RemoteException("problem with security key",e);
				}				
			}
		}
	}

	public static ArrayList<Challenge> getChallenges() 
	{
		return challenges_;
	}
}
