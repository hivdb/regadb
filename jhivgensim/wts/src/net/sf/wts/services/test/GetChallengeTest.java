package net.sf.wts.services.test;

import java.rmi.RemoteException;

import net.sf.wts.services.GetChallengeImpl;
import net.sf.wts.services.LoginImpl;
import net.sf.wts.services.util.Authentication;
import net.sf.wts.services.util.Encrypt;

public class GetChallengeTest {
    public static void main(String [] args)
    {
        GetChallengeImpl gc = new GetChallengeImpl();
        
        String s=null;
        try {
            s = gc.exec("kdforc0");
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        /*try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        
        System.err.println(s);
        System.err.println(s.length());
        String hashedPw = Encrypt.encryptMD5("Vitabis1");
        try {
            //System.err.println("a" + Authentication.authenticate(s, Encrypt.encryptMD5(hashedPw+s), "kdforc0"));
            
            LoginImpl login = new LoginImpl();
            login.exec("kdforc0", s, Encrypt.encryptMD5(hashedPw+s), "regadb-align");
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
        }
        
        
        gc = new GetChallengeImpl();
        
        s=null;
        try {
            s = gc.exec("kdforc0");
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            System.err.println("b" + Authentication.authenticate(s, Encrypt.encryptMD5("Vitabis1"+s), "kdforc0"));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
        }
        
        gc = new GetChallengeImpl();
        
        s=null;
        try {
            s = gc.exec("kdforc0");
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            System.err.println("c" + Authentication.authenticate(s, Encrypt.encryptMD5(hashedPw+s), "ishmael"));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
        }
        //System.err.println(Authentication.isValidChallenge(s));
    }
}
