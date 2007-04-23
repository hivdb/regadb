package net.sf.wts.services.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;


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
    
    public static boolean authenticate(String challenge, String hashedChallenge, String userName) throws java.rmi.RemoteException
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
                String password = Settings.getHashedPassword(userName);
                if(password==null)
                    throw new RemoteException("User does not exist");

                String checkChallenge = Encrypt.encryptMD5(password+challenge);
                
                if(checkChallenge.equals(hashedChallenge))
                {
                    return true;
                }
                else
                {
                    throw new RemoteException("Challenge was not solved correctly");
                }
            }
        }
    }

    public static ArrayList<Challenge> getChallenges() 
    {
        return challenges_;
    }
}
