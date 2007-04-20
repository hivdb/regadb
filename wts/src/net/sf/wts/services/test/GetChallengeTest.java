package net.sf.wts.services.test;

import java.rmi.RemoteException;

import net.sf.wts.services.GetChallengeImpl;
import net.sf.wts.services.util.Authentication;

public class GetChallengeTest {
    public static void main(String [] args)
    {
        GetChallengeImpl gc = new GetChallengeImpl();
        
        String s=null;
        try {
            s = gc.exec("pieter");
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
        System.err.println(Authentication.isValidChallenge(s));
        System.err.println(Authentication.isValidChallenge(s));
    }
}
