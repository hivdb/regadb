package net.sf.wts.services;

import net.sf.wts.services.util.Authentication;
import net.sf.wts.services.util.Challenge;


public class GetChallengeImpl 
{
    public String exec(String user) throws java.rmi.RemoteException
    {
        synchronized(Authentication.getChallenges())
        {
        Challenge challenge = new Challenge();

        do
        {
            challenge.challenge_ = Authentication.getRandomAsciiString();
        }
        while(Authentication.getChallenges().contains(challenge));
        
        challenge.creationTime_ = System.currentTimeMillis();
        Authentication.getChallenges().add(challenge);
        
        return challenge.challenge_;
        }
    }
}
