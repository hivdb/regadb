package net.sf.wts.services;

import java.rmi.RemoteException;

import net.sf.wts.services.util.Authentication;
import net.sf.wts.services.util.Session;

public class LoginImpl 
{
    public String exec(String userName, String challenge, String hashedChallenge, String serviceName) throws RemoteException
    {
        boolean valid = Authentication.authenticate(challenge, hashedChallenge, userName);
        if(valid)
        {
            return Session.createNewSession(serviceName, userName);
        }
        else
        {
            throw new RemoteException("Login failed");
        }
    }
}
