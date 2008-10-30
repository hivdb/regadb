package net.sf.wts.services;

import java.rmi.RemoteException;

import net.sf.wts.services.util.Authentication;
import net.sf.wts.services.util.Encryption;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;

public class LoginImpl 
{
    public String exec(String userName, String challenge, String hashedChallenge, String serviceName) throws RemoteException
    {
        boolean valid = Authentication.authenticate(challenge, hashedChallenge, userName);
        if(valid)
        {
            String sessionTicket = Sessions.createNewSession(serviceName, userName);
            String sessionKey = new String(Sessions.getSessionKey(sessionTicket).getEncoded());
            return Encryption.encrypt(sessionTicket + "_" + sessionKey, Settings.getPublicKey(userName));
        }
        else
        {
            throw new RemoteException("Login failed");
        }
    }
}
