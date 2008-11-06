package net.sf.wts.services;

import java.rmi.RemoteException;

import sun.misc.BASE64Encoder;

import net.sf.wts.services.util.Authentication;
import net.sf.wts.services.util.Encrypt;
import net.sf.wts.services.util.Sessions;
import net.sf.wts.services.util.Settings;

public class LoginImpl 
{
    public byte[] exec(String userName, String challenge, byte[] signedChallenge, String serviceName) throws RemoteException
    {
        boolean valid = Authentication.authenticate(challenge, signedChallenge, userName);
        if(valid)
        {
            String sessionTicket = Sessions.createNewSession(serviceName, userName);
            String sessionKey = (new BASE64Encoder()).encode(Sessions.getSessionKey(sessionTicket).getEncoded());
            return Encrypt.encrypt(sessionTicket + "_" + sessionKey, Settings.getPublicKey(userName));
        }
        else
        {
            throw new RemoteException("Login failed");
        }
    }
}
