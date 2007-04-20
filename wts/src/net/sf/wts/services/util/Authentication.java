package net.sf.wts.services.util;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;


public class Authentication 
{
    private static SortedSet<Challenge> challenges_ = new TreeSet<Challenge>(new ChallengeComparator());
    
    public static String getRandomAsciiString()
    {
        Random generator = new Random();
        char[] asciiRandom = new char[100+generator.nextInt(50)];
        
        for(int i = 0; i<asciiRandom.length; i++)
        {
            asciiRandom[i] = (char)pickNumberInRange(33, 127);
        }
        
        return System.currentTimeMillis()+""+new String(asciiRandom);
    }
    
    private static int pickNumberInRange(int aLowerLimit, int aUpperLimit) 
    {
        Random generator = new Random();
        // get the range, casting to long to avoid overflow problems
        long range = (long)aUpperLimit - (long)aLowerLimit + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long)(range * generator.nextDouble());
        return (int)(fraction + aLowerLimit);
    }
    
    public static boolean isValidChallenge(String challenge)
    {
        synchronized(challenges_)
        {
            long currentTime = System.currentTimeMillis();
            Long creationTime = null;
            Challenge toRemove = null;
            for(Challenge c : challenges_)
            {
                if(c.challenge_.equals(challenge))
                {
                    toRemove = c;
                    creationTime = c.creationTime_;
                    break;
                }
            }
            
            if(toRemove!=null)
            {
                challenges_.remove(toRemove);
            }
            
            if(creationTime==null || currentTime>creationTime+5*1000*60)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    public static SortedSet<Challenge> getChallenges() 
    {
        return challenges_;
    }
}
